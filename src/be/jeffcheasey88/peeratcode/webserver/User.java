package be.jeffcheasey88.peeratcode.webserver;

import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

public class User {

	private final String username;
	
	public User(final RsaJsonWebKey rsaJsonWebKey, final String jwt) throws InvalidJwtException {
		    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
		            .setRequireExpirationTime()
		            .setAllowedClockSkewInSeconds(30)
		            .setRequireSubject()
		            .setExpectedIssuer("Issuer")
		            .setExpectedAudience("Audience")
		            .setVerificationKey(rsaJsonWebKey.getKey())
		            .setJwsAlgorithmConstraints(
		                    ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
		            .build();
			
	        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
	        username = (String) jwtClaims.getClaimValue("username");
	}
	
	public String getUsername() {
		return username;
	}
}