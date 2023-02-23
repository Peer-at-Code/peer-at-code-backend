package be.jeffcheasey88.peeratcode.model;

import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class User {

	private final String username;
	
	public User(String jwt) {
		new RsaJsonWebKey();
		// Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
	    // be used to validate and process the JWT.
	    // The specific validation requirements for a JWT are context dependent, however,
	    // it is typically advisable to require a (reasonable) expiration time, a trusted issuer, and
	    // an audience that identifies your system as the intended recipient.
	    // If the JWT is encrypted too, you need only provide a decryption key or
	    // decryption key resolver to the builder.
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

	    try
	    {
	        //  Validate the JWT and process it to the Claims
	        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
	        System.out.println("JWT validation succeeded! " + jwtClaims);
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
	            System.out.println("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
	        }

	        // Or maybe the audience was invalid
	        if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
	        {
	            System.out.println("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
	        }
	    }
		
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
}