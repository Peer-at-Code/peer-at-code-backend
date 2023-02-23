package be.jeffcheasey88.peeratcode.webserver;

import org.jose4j.jwt.JwtClaims;

public class User {

	private String username;
	private int id;
	
	public User(JwtClaims jwtClaims){
		this.username = (String) jwtClaims.getClaimValue("username");
		this.id = (int) jwtClaims.getClaimValue("id");
	}
	
	public String getUsername(){
		return username;
	}
	
	public int getId(){
		return this.id;
	}
}