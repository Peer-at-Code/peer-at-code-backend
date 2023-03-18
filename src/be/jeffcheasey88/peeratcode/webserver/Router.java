package be.jeffcheasey88.peeratcode.webserver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;

public class Router{
	
	private Map<Response, Route> responses;
	private Map<Response, Pattern> patterns;
	private Response noFileFound;
	private RsaJsonWebKey rsaJsonWebKey;
	private DatabaseRepository repo;
	private String token_issuer;
	private int token_expiration;
	
	public Router(DatabaseRepository repo, String token_issuer, int token_expiration) throws Exception{
		this.repo = repo;
		this.token_issuer =  token_issuer;
		this.token_expiration = token_expiration;
		this.responses = new HashMap<>();
		this.patterns = new HashMap<>();
		this.rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
	}
	
	public DatabaseRepository getDataBase(){
		return this.repo;
	}
	
	public void register(Response response){
		try {
			Method method = response.getClass().getDeclaredMethod("exec");
			Route route = method.getAnnotation(Route.class);
			
			this.responses.put(response, route);
			this.patterns.put(response, Pattern.compile(route.path()));
		} catch (Exception e){
			throw new IllegalArgumentException(e);
		}
	}
	
	public void setDefault(Response response){
		this.noFileFound = response;
	}

	public void exec(String type, String path, User user, HttpReader reader, HttpWriter writer) throws Exception {
		for(Entry<Response, Route> routes : this.responses.entrySet()){
			if(routes.getValue().type().equals(type)){
				Matcher matcher = this.patterns.get(routes.getKey()).matcher(path);
				if(matcher.matches()){
					if(user == null && routes.getValue().needLogin()) return;
					routes.getKey().exec(matcher, user, reader, writer);
					return;
				}
			}
		}
		if(noFileFound != null) noFileFound.exec(null, user, reader, writer);
	}
	
	public RsaJsonWebKey getWebKey(){
		return this.rsaJsonWebKey;
	}
	
	public String getTokenIssuer(){
		return this.token_issuer;
	}
	
	public String createAuthUser(int id) throws JoseException{
		JwtClaims claims = new JwtClaims();
	    claims.setIssuer(token_issuer);  // who creates the token and signs it
	    claims.setExpirationTimeMinutesInTheFuture(token_expiration);
	    claims.setGeneratedJwtId(); // a unique identifier for the token
	    claims.setIssuedAtToNow();  // when the token was issued/created (now)
	    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
	    
	    claims.setClaim("id", id);
	    
	    claims.setSubject("Nani ???");
	    
	    JsonWebSignature jws = new JsonWebSignature();
	    jws.setPayload(claims.toJson());
	    jws.setKey(rsaJsonWebKey.getPrivateKey());
	    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
	    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
	    return jws.getCompactSerialization();
	}
}