package be.jeffcheasey88.peeratcode.webserver;

import java.net.Socket;
import java.util.Arrays;

import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class Client extends Thread{
	
	private HttpReader reader;
	private HttpWriter writer;
	private Router router;
	private RsaJsonWebKey key;
	
	public Client(Socket socket, Router router, RsaJsonWebKey key) throws Exception{
		this.reader = new HttpReader(socket);
		this.writer = new HttpWriter(socket);
		this.router = router;
		this.key = key;
	}

	@Override
	public void run(){
		try {
			String[] headers = reader.readLine().split("\\s");
			System.out.println(Arrays.toString(headers));

			router.exec(headers[0], headers[1], isLogin(reader), reader, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private User isLogin(HttpReader reader){
		try {
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
		            .setRequireExpirationTime()
		            .setAllowedClockSkewInSeconds(30)
		            .setRequireSubject()
		            .setExpectedIssuer("Issuer")
		            .setExpectedAudience("Audience")
		            .setVerificationKey(this.router.getWebKey().getKey())
		            .setJwsAlgorithmConstraints(
		                    ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
		            .build();
			
	        JwtClaims jwtClaims = jwtConsumer.processToClaims(HttpUtil.readAutorization(reader));
	        return new User(jwtClaims);
		}catch(Exception e){}
		return null;
	}
}