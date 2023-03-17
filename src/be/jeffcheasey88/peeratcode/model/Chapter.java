package be.jeffcheasey88.peeratcode.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class Chapter {
	
	private int id;
	private String name;
	private List<Puzzle> puzzles;
	private Timestamp startDate;
	private Timestamp endDate;

	public Chapter(int id, String name, Timestamp startDate, Timestamp endDate) {
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
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

	public List<Puzzle> getPuzzles() {
		return puzzles;
	}

	public void setPuzzles(List<Puzzle> puzzles) {
		this.puzzles = puzzles;
	}
	
	public Timestamp getStartDate() {
		return startDate;
	}
	
	public Timestamp getEndDate() {
		return endDate;
	}

	@Override
	public boolean equals(Object object){
		if(this == object) return true;
		if(!(object instanceof Chapter)) return false;
		return this.id == (((Chapter)object).id);
	}

	@Override
	public int hashCode() {
		return id;
	}
}
