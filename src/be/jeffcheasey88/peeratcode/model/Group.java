package be.jeffcheasey88.peeratcode.model;

import org.json.simple.JSONObject;

public class Group {
	private String name;
	private int linkToChapter;
	private int linkToPuzzle;
	
	public Group(JSONObject json){
		this.name = (String)json.get("name");
		this.linkToChapter = ((Number)json.get("chapter")).intValue();
		this.linkToPuzzle = ((Number)json.get("puzzle")).intValue();
	}
	
	public Group(String name, int initChap, int initPuzz) {
		this.name = name;
		this.linkToChapter = initChap;
		this.linkToPuzzle = initPuzz;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLinkToChapter() {
		return linkToChapter;
	}

	public void setLinkToChapter(int linkToChapter) {
		this.linkToChapter = linkToChapter;
	}

	public int getLinkToPuzzle() {
		return linkToPuzzle;
	}

	public void setLinkToPuzzle(int linkToPuzzle) {
		this.linkToPuzzle = linkToPuzzle;
	}

	public JSONObject toJson() {
		JSONObject groupJSON = new JSONObject();
		groupJSON.put("name", name);
		if (linkToChapter > 0) groupJSON.put("chapter", linkToChapter);
		if (linkToPuzzle > 0) groupJSON.put("puzzle", linkToPuzzle);
		return groupJSON;
	}
}
