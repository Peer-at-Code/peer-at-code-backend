package be.jeffcheasey88.peeratcode.webserver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;

public class Router{
	
	private List<Response> responses;
	private Response noFileFound;
	private RsaJsonWebKey rsaJsonWebKey;
	private DatabaseRepository repo;
	private String token_issuer;
	private int token_expiration;
	
	public Router(DatabaseRepository repo, String token_issuer, int token_expiration) throws Exception{
		this.repo = repo;
		this.token_issuer =  token_issuer;
		this.token_expiration = token_expiration;
		this.responses = new ArrayList<>();
		this.rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
	}
	
	public DatabaseRepository getDataBase(){
		return this.repo;
	}
	
	public void register(Response response){
		this.responses.add(response);
	}
	
	public void setDefault(Response response){
		this.noFileFound = response;
	}

	public void exec(String type, String path, User user, HttpReader reader, HttpWriter writer) throws Exception {
		for(Response response : this.responses){
			if(type.equals(response.getType())){
				Matcher matcher = response.getPattern().matcher(path);
				if(matcher.matches()){
					if(user == null && response.needLogin()) return;
					response.exec(matcher, user, reader, writer);
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