package be.jeffcheasey88.peeratcode.repository;

import be.jeffcheasey88.peeratcode.model.Chapter;
import be.jeffcheasey88.peeratcode.model.Puzzle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRepo {
	private static final String SPECIFIC_PUZZLE_QUERY = "SELECT * FROM puzzle WHERE id_puzzle = ?";
	private static final String SPECIFIC_CHAPTER_QUERY = "SELECT * FROM chapter WHERE id_chapter = ?";
	private static final String PUZZLES_IN_CHAPTER_QUERY = "SELECT * FROM puzzle WHERE fk_chapter = ?";
	public static final String ALL_CHAPTERS_QUERY = "SELECT * FROM chapter";
	private final Connection con;

	public DatabaseRepo(Connection con) {
		this.con = con;
	}

	private Puzzle makePuzzle(ResultSet puzzleResult) throws SQLException {
		return new Puzzle(puzzleResult.getInt("id_puzzle"), puzzleResult.getString("name"), puzzleResult.getString("content"));
	}

	private Chapter makeChapter(ResultSet chapterResult) throws SQLException {
		return new Chapter(chapterResult.getInt("id_chapter"), chapterResult.getString("name"));
	}

	private List<Puzzle> getPuzzlesInChapter(int id) throws SQLException {
		List<Puzzle> puzzles = new ArrayList<>();
		PreparedStatement puzzleStmt = con.prepareStatement(PUZZLES_IN_CHAPTER_QUERY);
		puzzleStmt.setInt(1, id);
		ResultSet puzzleResult = puzzleStmt.executeQuery();
		while (puzzleResult.next()) {
			puzzles.add(makePuzzle(puzzleResult));
		}
		return puzzles;
	}

	/**
	 * Get a specific puzzle
	 *
	 * @param id The id of the puzzle
	 * @return The puzzle or null if an error occurred
	 */
	public Puzzle getPuzzle(int id) {
		try {
			PreparedStatement puzzleStmt = con.prepareStatement(SPECIFIC_PUZZLE_QUERY);
			puzzleStmt.setInt(1, id);
			ResultSet puzzleResult = puzzleStmt.executeQuery();
			if (puzzleResult.next()) {
				return makePuzzle(puzzleResult);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get a specific chapter
	 *
	 * @param id The id of the chapter
	 * @return The chapter or null if an error occurred
	 */
	public Chapter getChapter(int id) {
		try {
			PreparedStatement chapterStmt = con.prepareStatement(SPECIFIC_CHAPTER_QUERY);
			chapterStmt.setInt(1, id);
			ResultSet chapterResult = chapterStmt.executeQuery();
			if (chapterResult.next()) {
				Chapter chapter = makeChapter(chapterResult);
				List<Puzzle> puzzles = getPuzzlesInChapter(id);
				chapter.setPuzzles(puzzles);
				return chapter;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get all chapters in the database
	 *
	 * @return List of all chapters or null if an error occurred
	 */
	public List<Chapter> getAllChapters() {
		try {
			List<Chapter> chapterList = new ArrayList<>();
			PreparedStatement chapterStmt = con.prepareStatement(ALL_CHAPTERS_QUERY);
			ResultSet chapterResult = chapterStmt.executeQuery();
			while (chapterResult.next()) {
				Chapter chapter = makeChapter(chapterResult);
				chapter.setPuzzles(getPuzzlesInChapter(chapter.getId()));
				chapterList.add(chapter);
			}
			return chapterList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}