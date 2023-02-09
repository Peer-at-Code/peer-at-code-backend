package be.jeffcheasey88.peeratcode.model;

import java.util.List;

public class Chapter {
	private int id;
	private String name;
	private List<Puzzle> puzzles;

	public Chapter(int id, String name) {
		this.id = id;
		this.name = name;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Chapter chapter = (Chapter) o;

		return id == chapter.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
