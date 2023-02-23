package be.jeffcheasey88.peeratcode.webserver;

import org.jose4j.jwt.JwtClaims;

public class User {

	private int id;
	
	public User(JwtClaims jwtClaims){
		this.id = (int) jwtClaims.getClaimValue("id");
	}
	
	public int getId(){
		return this.id;
	}
}