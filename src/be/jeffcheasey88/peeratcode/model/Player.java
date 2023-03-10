package be.jeffcheasey88.peeratcode.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Player implements Comparable<Player> {
	public static final String PATH_TO_CODE = "/home/%s/peer-at-source/";

	private String pseudo;
	private String email;
	private String firstname;
	private String lastname;
	private String description;
	private String sgroup;
	private byte[] avatar;

	private int totalScore;
	private int totalCompletion;
	private int totalTries;
	
	private String badges; // To change to a set of model

	public Player(String pseudo, String email, String firstname, String lastname, String description, String sgroup) {
		this(pseudo, email, firstname, lastname, description, sgroup, null);
	}

	public Player(String pseudo, String email, String firstname, String lastname, String description, String sgroup,
			byte[] avatar) {
		this(pseudo, email, firstname, lastname, description, sgroup, avatar, null);
	}
	public Player(String pseudo, String email, String firstname, String lastname, String description, String sgroup,
			byte[] avatar, String badges) {
		this.pseudo = pseudo;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.description = description;
		this.sgroup = sgroup;
		this.avatar = avatar;

		totalScore = 0;
		totalCompletion = 0;
		totalTries = 0;
		
		this.badges = null;
	}

	public String getPseudo() {
		return this.pseudo;
	}

	public String getEmail() {
		return this.email;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public String getDescription() {
		return this.description;
	}

	public String getGroup() {
		return this.sgroup;
	}

	public byte[] getAvatar() {
		return this.avatar;
	}
	
	public String getPathToSourceCode() {
		return String.format(PATH_TO_CODE, pseudo);
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getTotalCompletion() {
		return totalCompletion;
	}

	public void setTotalCompletion(int totalCompletion) {
		this.totalCompletion = totalCompletion;
	}

	public int getTotalTries() {
		return totalTries;
	}

	public void setTotalTries(int totalTries) {
		this.totalTries = totalTries;
	}
	
	public String getBadges() {
		return badges;
	}
	public void setBadges(String initBadges) {
		badges = initBadges;	
	}

	@Override
	public int compareTo(Player other) {
		if (this == other)
			return 0;
		if (other == null)
			return -1;
		int compare = Integer.compare(other.getTotalScore(), totalScore);
		if (compare == 0) {
			compare =  Integer.compare(other.getTotalCompletion(), totalCompletion);
			if (compare == 0) {
				compare =  Integer.compare(totalTries, other.getTotalTries());
				if(compare == 0) compare = other.getPseudo().compareTo(pseudo);
			}
		}

		return compare;
	}
}
