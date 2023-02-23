package be.jeffcheasey88.peeratcode.model;

public class Player {
	
	private String pseudo;
	private String email;
	private String firstname;
	private String lastname;
	private String description;
	private String sgroup;
	
	public Player(String pseudo, String email, String firstname, String lastname, String description, String sgroup){
		this.pseudo = pseudo;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.description = description;
		this.sgroup = sgroup;
	}
	
	public String getPseudo(){
		return this.pseudo;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public String getFirstname(){
		return this.firstname;
	}
	
	public String getLastname(){
		return this.lastname;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getGroup(){
		return this.sgroup;
	}

}
