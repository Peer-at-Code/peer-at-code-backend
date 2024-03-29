package be.jeffcheasey88.peeratcode.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Player implements Comparable<Player> {
	public static final String PATH_TO_CODE = "/home/%s/peer-at-source/";

	private String pseudo;
	private String email;
	private String firstname;
	private String lastname;
	private String description;
	private Set<Group> groups;
	private byte[] avatar;

	private int rank;
	private int totalScore;
	private int totalCompletion;
	private int totalTries;

	private Set<Badge> badges;

	public Player(String pseudo, String email, String firstname, String lastname, String description) {
		this.pseudo = pseudo;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.description = description;

		totalScore = 0;
		totalCompletion = 0;
		totalTries = 0;
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

	public Set<Group> getGroups() {
		return groups;
	}

	/**
	 * SEE SET_TAGS IN PUZZLE
	 * 
	 * @return DEATH
	 */
	public JSONArray getJsonGroups() {
		if (groups != null) {
			JSONArray groupsJSON = new JSONArray();
			for (Group group : groups) {
				groupsJSON.add(group.toJson());
			}
			return groupsJSON;
		}
		return null;
	}

	public void addGroup(Group newGroup) {
		if (newGroup != null) {
			if (this.groups == null)
				this.groups = new HashSet<Group>();
			this.groups.add(newGroup);
		}
	}

	public byte[] getAvatar() {
		return this.avatar;
	}

	public void setAvatar(byte[] newAvatar) {
		avatar = newAvatar;
	}

	public String getPathToSourceCode() {
		return String.format(PATH_TO_CODE, pseudo);
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int newRank) {
		rank = newRank;
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

	public Set<Badge> getBadges() {
		return badges;
	}

	/**
	 * SEE SET_TAGS IN PUZZLE
	 * 
	 * @return DEATH
	 */
	public JSONArray getJsonBadges() {
		if (badges == null)
			return null;
		JSONArray badgesJSON = new JSONArray();
		for (Badge badge : badges) {
			JSONObject badgeJSON = new JSONObject();
			badgeJSON.put("name", badge.getName());
			byte[] logo = badge.getLogo();
			if (logo != null)
				badgeJSON.put("logo", Base64.getEncoder().encodeToString(logo));
			badgeJSON.put("level", badge.getLevel());
			badgesJSON.add(badgeJSON);
		}
		return badgesJSON;
	}

	public void addBadge(Badge newBadge) {
		if (newBadge != null) {
			if (badges == null)
				badges = new HashSet<Badge>();
			badges.add(newBadge);
		}
	}

	@Override
	public int compareTo(Player other) {
		if (this == other)
			return 0;
		if (other == null)
			return -1;
		int compare = Integer.compare(other.getTotalScore(), totalScore);
		if (compare == 0) {
			compare = Integer.compare(other.getTotalCompletion(), totalCompletion);
			if (compare == 0) {
				compare = Integer.compare(totalTries, other.getTotalTries());
				if (compare == 0)
					compare = other.getPseudo().compareTo(pseudo);
			}
		}

		return compare;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, pseudo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		return Objects.equals(email, other.email) && Objects.equals(pseudo, other.pseudo);
	}

}
