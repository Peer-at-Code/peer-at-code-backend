package be.jeffcheasey88.peeratcode.model;

public class Puzzle {
	
	private int id;
	private String name;
	private String content;

	public Puzzle(int id, String name, String content) {
		this.id = id;
		this.name = name;
		this.content = content;
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
