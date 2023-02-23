package be.jeffcheasey88.peeratcode.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.password4j.Hash;
import com.password4j.Password;

import be.jeffcheasey88.peeratcode.Configuration;
import be.jeffcheasey88.peeratcode.model.Chapter;
import be.jeffcheasey88.peeratcode.model.Puzzle;

public class DatabaseRepository {
	private static final String SPECIFIC_PUZZLE_QUERY = "SELECT * FROM puzzles WHERE id_puzzle = ?";
	private static final String SPECIFIC_CHAPTER_QUERY = "SELECT * FROM chapters WHERE id_chapter = ?";
	private static final String PUZZLES_IN_CHAPTER_QUERY = "SELECT * FROM puzzles WHERE fk_chapter = ?";
	private static final String ALL_CHAPTERS_QUERY = "SELECT * FROM chapters WHERE id_chapter > 0";
	private static final String CHECK_PSEUDO_AVAILABLE_QUERY = "SELECT * FROM players WHERE pseudo = ?";
	private static final String CHECK_EMAIL_AVAILABLE_QUERY = "SELECT * FROM players WHERE email = ?";
	private static final String REGISTER_QUERY = "INSERT INTO players (pseudo, email, passwd, firstname, lastname, description, sgroup, avatar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String CHECK_PASSWORD = "SELECT id_player, passwd FROM players WHERE pseudo=?";
	private static final String SCORE = "SELECT score FROM completions WHERE fk_player = ? AND fk_puzzle = ?";
	private static final String GET_PUZZLE_SOLUTION = "SELECT soluce FROM puzzles WHERE id_puzzle=?";
	private static final String GET_PUZZLE_SCORE_MAX = "SELECT score_max FROM puzzles WHERE id_puzzle=?";
	private static final String GET_PLAYER_ID_BY_PSEUDO = "SELECT id_player FROM players WHERE pseudo=?";
	private static final String GET_PUZZLE_NB_TRIES_AND_SCORE_BY_PLAYER = "SELECT tries, score FROM completions WHERE fk_puzzle = ? AND fk_player = ?";
	private static final String INSERT_COMPLETION = "INSERT INTO completions (fk_puzzle, fk_player, tries, code, fileName, score) values (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_COMPLETION = "UPDATE completions SET tries = ?, code = ?, filename = ?, score = ?";

	private Connection con;
	private Configuration config;

	public DatabaseRepository(Configuration config){
		this.config = config;
	}
	
	private void ensureConnection() throws SQLException{
		if(con == null || (!con.isValid(5))){
			this.con = DriverManager.getConnection("jdbc:mysql://"+config.getDbHost()+":"+config.getDbPort()+"/"+config.getDbDatabase()+"",config.getDbUser(), config.getDbPassword());
		}
	}

	private Puzzle makePuzzle(ResultSet puzzleResult) throws SQLException {
		return new Puzzle(puzzleResult.getInt("id_puzzle"), puzzleResult.getString("name"), puzzleResult.getString("content"), null,"",0);
	}

	private Chapter makeChapter(ResultSet chapterResult) throws SQLException {
		return new Chapter(chapterResult.getInt("id_chapter"), chapterResult.getString("name"));
	}

	private List<Puzzle> getPuzzlesInChapter(int id) throws SQLException {
		List<Puzzle> puzzles = new ArrayList<>();
		ensureConnection();
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
			ensureConnection();
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
	
	public int getScore(int user, int puzzle){
		try {
			PreparedStatement stmt = this.con.prepareStatement(SCORE);
			stmt.setInt(1, user);
			stmt.setInt(2, puzzle);
			
			ResultSet result = stmt.executeQuery();
			if(result.next()) result.getInt("score");
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Get a specific chapter
	 *
	 * @param id The id of the chapter
	 * @return The chapter or null if an error occurred
	 */
	public Chapter getChapter(int id) {
		try {
			ensureConnection();
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
			ensureConnection();
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

	/**
	 * Check if a pseudo is available
	 *
	 * @param pseudo The pseudo to check
	 * @return True if the pseudo is available, false if it's already taken
	 */
	public boolean checkPseudoAvailability(String pseudo) {
		return checkAvailability(pseudo, CHECK_PSEUDO_AVAILABLE_QUERY);
	}

	/**
	 * Check if an email is available
	 *
	 * @param email The email to check
	 * @return True if the email is available, false if it's already taken
	 */
	public boolean checkEmailAvailability(String email) {
		return checkAvailability(email, CHECK_EMAIL_AVAILABLE_QUERY);
	}

	private boolean checkAvailability(String queriedString, String correspondingQuery) {
		try {
			ensureConnection();
			PreparedStatement statement = con.prepareStatement(correspondingQuery);
			statement.setString(1, queriedString);
			ResultSet result = statement.executeQuery();
			return !result.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Register a new user
	 *
	 * @param pseudo      The pseudo of the user
	 * @param email       The email of the user
	 * @param password    The password of the user
	 * @param firstname   The firstname of the user
	 * @param lastname    The lastname of the user
	 * @param description The description of the user
	 * @param sgroup       The group of the user
	 * @param avatar      The avatar of the user
	 * @return True if the user was registered, false if an error occurred
	 */
	public int register(String pseudo, String email, String password, String firstname, String lastname, String description, String sgroup, String avatar) {
		Hash hash = Password.hash(password).withArgon2();
		try {
			ensureConnection();
			PreparedStatement statement = con.prepareStatement(REGISTER_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, pseudo);
			statement.setString(2, email);
			statement.setString(3, hash.getResult());
			statement.setString(4, firstname);
			statement.setString(5, lastname);
			statement.setString(6, description);
			statement.setString(7, sgroup);
			statement.setString(8, avatar);
			if(statement.executeUpdate() == 1){
				ResultSet inserted = statement.getGeneratedKeys();
				if(inserted.next()) return inserted.getInt("id_player");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Login a user
	 *
	 * @param username    The username of the user
	 * @param password The password of the user
	 * @return id the id of the user, -1 if not login successefuly
	 */
	public int login(String username, String password) {
		try {
			ensureConnection();
			PreparedStatement statement = con.prepareStatement(CHECK_PASSWORD);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String hashedPassword = result.getString("passwd");
				if(Password.check(password, hashedPassword).withArgon2()) return result.getInt("id_player");
			}
		} catch (SQLException e) {
		}
		return -1;
	}

	public byte[] getPuzzleSolution(int puzzleId) {
		try {
			PreparedStatement puzzleStmt = con.prepareStatement(GET_PUZZLE_SOLUTION);
			puzzleStmt.setInt(1, puzzleId);
			ResultSet result = puzzleStmt.executeQuery();
			if (result.next()) {
				return result.getBytes("soluce");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int insertOrUpdatePuzzleResponse(int puzzleId, String pseudo, String fileName, byte[] code) {
		try {
			ensureConnection();
			int[] triesAndScore = getPuzzleNbTriesAndScore(puzzleId, pseudo);
			int playerId = getPlayerIdByPseudo(pseudo);
			int puzzleScoreMax = getPuzzleScoreMax(puzzleId);
			if (triesAndScore[0] < 0) {
				// Insert completions
				PreparedStatement statement = con.prepareStatement(INSERT_COMPLETION);
				statement.setInt(1, puzzleId);
				statement.setInt(2, playerId);
				statement.setInt(3, 0);
				statement.setBytes(4, code);
				statement.setString(5, fileName);
				statement.setInt(6, puzzleScoreMax);
				return puzzleScoreMax;
			} else {
				// Update completions
				int score = puzzleScoreMax * (((triesAndScore[0]-1)*10)/100);
				PreparedStatement statement = con.prepareStatement(UPDATE_COMPLETION);
				statement.setInt(1, triesAndScore[0]+1);
				statement.setBytes(2, code);
				statement.setString(3, fileName);
				statement.setInt(4, score);
				return score;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public int getPuzzleScoreMax(int puzzleId) {
		try {
			PreparedStatement puzzleStmt = con.prepareStatement(GET_PUZZLE_SCORE_MAX);
			puzzleStmt.setInt(1, puzzleId);
			ResultSet result = puzzleStmt.executeQuery();
			if (result.next()) {
				return result.getInt("score_max");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public int[] getPuzzleNbTriesAndScore(int puzzleId, String pseudo) {
		try {
			PreparedStatement puzzleStmt = con.prepareStatement(GET_PUZZLE_NB_TRIES_AND_SCORE_BY_PLAYER);
			puzzleStmt.setInt(1, puzzleId);
			ResultSet result = puzzleStmt.executeQuery();
			int[] res = new int[2];
			if (result.next()) {
				res[0] = result.getInt("tries");
				res[1] = result.getInt("score");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	private int getPlayerIdByPseudo(String pseudo) {
		try {
			PreparedStatement puzzleStmt = con.prepareStatement(GET_PLAYER_ID_BY_PSEUDO);
			puzzleStmt.setString(1, pseudo);
			ResultSet result = puzzleStmt.executeQuery();
			if (result.next()) {
				return result.getInt("id_player");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
}