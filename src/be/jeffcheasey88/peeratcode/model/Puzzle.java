package be.jeffcheasey88.peeratcode.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Puzzle {
	
	private int id;
	private String name;
	private String content;
	private byte[] soluce;
	private String verify;
	private int scoreMax;
	private Set<String> tags;
	private int depend;

	public Puzzle(int id, String name, String content, byte[] soluce, String verify, int scoreMax, String tags){
		this(id, name, content, soluce, verify, scoreMax, tags, -1);	
	}
	public Puzzle(int id, String name, String content, byte[] soluce, String verify, int scoreMax, String tags, int depend){
		this.id = id;
		this.name = name;
		this.content = content;
		this.soluce = soluce;
		this.verify = verify;
		this.scoreMax = scoreMax;
		setTags(tags);
		this.depend = depend;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public byte[] getSoluce(){
		return this.soluce;
	}
	
	public void setSoluce(byte[] array){
		this.soluce = array;
	}
	
	public String getVerify(){
		return this.verify;
	}
	
	public void setVerify(String regex){
		this.verify = regex;
	}
	
	public int getScoreMax(){
		return this.scoreMax;
	}
	
	public void setScoreMax(int max){
		this.scoreMax = max;
	}
	
	public Set<String> getTags(){
		return this.tags;
	}
	
	/**
	 * DO NOT EVER EVER SHOW TO MISTER LUDWIG XD
	 * @return DEATH
	 */
	public JSONArray getJsonTags() {
		if (tags == null)
			return null;
		JSONArray tagsJSON = new JSONArray();
		for (String tag: tags) {
			JSONObject tagJSON = new JSONObject();
			tagJSON.put("name", tag);
			tagsJSON.add(tagJSON);
		}
		return tagsJSON;
	}
	
	public void setTags(String tags){
		if (tags == null || tags.isEmpty())
			this.tags = null;
		else
			this.tags = new HashSet<String>(Arrays.asList(tags.split(",")));
	}
	
	public int getDepend(){
		return this.depend;
	}
	
	public void setDepend(int depend){
		this.depend = depend;
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;
		if(!(object instanceof Puzzle)) return false;
		return this.id == (((Puzzle)object).id);
	}

	@Override
	public int hashCode() {
		return id;
	}
}
