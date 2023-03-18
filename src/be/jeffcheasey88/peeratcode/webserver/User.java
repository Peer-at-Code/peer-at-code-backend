package be.jeffcheasey88.peeratcode.webserver;

import org.jose4j.jwt.JwtClaims;

public class User {

	private int id;
	
	public User(JwtClaims jwtClaims){
		this.id = ((Long)jwtClaims.getClaimValue("id")).intValue();
	}
	
	public int getId(){
		return this.id;
	}
}