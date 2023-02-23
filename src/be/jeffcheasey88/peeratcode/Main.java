package be.jeffcheasey88.peeratcode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.routes.ChapterElement;
import be.jeffcheasey88.peeratcode.routes.ChapterList;
import be.jeffcheasey88.peeratcode.routes.Login;
import be.jeffcheasey88.peeratcode.routes.PuzzleElement;
import be.jeffcheasey88.peeratcode.routes.Register;
import be.jeffcheasey88.peeratcode.routes.Result;
import be.jeffcheasey88.peeratcode.webserver.Client;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Router;
import be.jeffcheasey88.peeratcode.webserver.User;

public class Main {
	// Define SSL Protocol parameters
	public static void main(String[] args) throws Exception {		
		Configuration config = new Configuration("config.txt");
		config.load();
		
		/*try
	    {
			JwtClaims claims = new JwtClaims();
		    claims.setIssuer("Issuer");  // who creates the token and signs it
		    claims.setAudience("Audience"); // to whom the token is intended to be sent
		    claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
		    claims.setGeneratedJwtId(); // a unique identifier for the token
		    claims.setIssuedAtToNow();  // when the token was issued/created (now)
		    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		    
		    claims.setClaim("username","USERNAME");

		    JsonWebSignature jws = new JsonWebSignature();

		    jws.setPayload(claims.toJson());

		    jws.setKey(rsaJsonWebKey.getPrivateKey());

		    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

		    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		    String jwt = jws.getCompactSerialization();

			System.out.println("jwt token = " + jwt);
			
			
		    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
		            .setRequireExpirationTime() // the JWT must have an expiration time
		            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
		            .setRequireSubject() // the JWT must have a subject claim
		            .setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
		            .setExpectedAudience("Audience") // to whom the JWT is intended for
		            .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
		            .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
		                    ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256) // which is only RS256 here
		            .build(); // create the JwtConsumer instance
			
	        //  Validate the JWT and process it to the Claims
	        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
	        System.out.println("JWT validation =  " + jwtClaims);
	    }
	    catch (InvalidJwtException e)
	    {
	        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
	        // Hopefully with meaningful explanations(s) about what went wrong.
	        System.out.println("Invalid JWT! " + e);

	        // Programmatic access to (some) specific reasons for JWT invalidity is also possible
	        // should you want different error handling behavior for certain conditions.

	        // Whether or not the JWT has expired being one common reason for invalidity
	        if (e.hasExpired())
	        {
	            System.out.println("JWT expired");
	        }

	        // Or maybe the audience was invalid
	        if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
	        {
	            System.out.println("JWT had wrong audience");
	        }
	    } catch (JoseException e) {
			e.printStackTrace();
		}*/
		
		
		Class.forName("com.mysql.cj.jdbc.Driver");

		Router router = new Router();

		router.setDefault(new Response(){

			@Override
			public Pattern getPattern(){
				return null;
			}

			@Override
			public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
				HttpUtil.responseHeaders(writer, 404, "Access-Control-Allow-Origin: *");
				writer.write("404 not Found.\n");
				writer.flush();
				writer.close();
			}
		});

		initRoutes(router, new DatabaseRepository(config));

		startWebServer(config, router);
	}

	private static void initRoutes(Router router, DatabaseRepository repo) {
		router.register(new ChapterElement(repo));
		router.register(new ChapterList(repo));
		router.register(new PuzzleElement(repo));
		router.register(new Register(repo));
		router.register(new Login(repo));
		router.register(new Result(repo));
	}

	private static void startWebServer(Configuration config, Router router) throws IOException {
		if (config.useSsl()) {
			SSLServerSocket server = null;
			try {
				System.setProperty("javax.net.ssl.keyStore", config.getSslKeystore());
				System.setProperty("javax.net.ssl.keyStorePassword", config.getSslKeystorePasswd());

				SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				server = (SSLServerSocket) ssf.createServerSocket(config.getTcpPort());

				while (!server.isClosed()) {
					Socket socket = server.accept();
					Client client = new Client(socket, router, RsaJwkGenerator.generateJwk(2048));
					client.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (server != null) {
					server.close();
				}
			}
		}
		else {
			try (ServerSocket server = new ServerSocket(config.getTcpPort())){
				while(!server.isClosed()){
					Socket socket = server.accept();
					RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
					Client client = new Client(socket, router, rsaJsonWebKey);
					client.start();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
