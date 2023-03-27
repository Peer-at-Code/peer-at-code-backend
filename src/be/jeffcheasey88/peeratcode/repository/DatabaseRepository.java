package be.jeffcheasey88.peeratcode.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.password4j.Hash;
import com.password4j.Password;

import be.jeffcheasey88.peeratcode.Configuration;
import be.jeffcheasey88.peeratcode.model.Badge;
import be.jeffcheasey88.peeratcode.model.Chapter;
import be.jeffcheasey88.peeratcode.model.Completion;
import be.jeffcheasey88.peeratcode.model.Group;
import be.jeffcheasey88.peeratcode.model.Player;
import be.jeffcheasey88.peeratcode.model.Puzzle;

public class DatabaseRepository {

	private Connection con;
	private Configuration config;

	public DatabaseRepository(Configuration config) {
		this.config = config;
	}

	private void ensureConnection() throws SQLException {
		if (con == null || (!con.isValid(5))) {
			this.con = DriverManager.getConnection(
					"jdbc:mysql://" + config.getDbHost() + ":" + config.getDbPort() + "/" + config.getDbDatabase() + "",
					config.getDbUser(), config.getDbPassword());
		}
	}

	private Puzzle makePuzzle(ResultSet puzzleResult) throws SQLException {
		return new Puzzle(puzzleResult.getInt("id_puzzle"), puzzleResult.getString("name"),
				puzzleResult.getString("content"), null, "", 0, puzzleResult.getString("tags"),
				hasColumn(puzzleResult, "origin") ? puzzleResult.getInt("origin") : -1);
	}

	private Chapter makeChapter(ResultSet chapterResult) throws SQLException {
		return new Chapter(chapterResult.getInt("id_chapter"), chapterResult.getString("name"),
				chapterResult.getTimestamp("start_date"), chapterResult.getTimestamp("end_date"));
	}

	private Completion makeCompletion(int playerId, int puzzleId, ResultSet completionResult) throws SQLException {
		return new Completion(playerId, puzzleId, completionResult.getInt("id_completion"),
				completionResult.getInt("tries"), completionResult.getString("fileName"),
				completionResult.getInt("score"), null);
	}

	private Player makePlayer(ResultSet playerResult) throws SQLException {
		Player p = new Player(playerResult.getString("pseudo"), playerResult.getString("email"),
				playerResult.getString("firstName"), playerResult.getString("lastName"),
				playerResult.getString("description"));
		if (hasColumn(playerResult, "avatar")) {
			p.setAvatar(playerResult.getBytes("avatar"));
		}
		if (hasColumn(playerResult, "score")) {
			p.setRank(playerResult.getInt("rank"));
			p.setTotalScore(playerResult.getInt("score"));
			p.setTotalCompletion(playerResult.getInt("completions"));
			p.setTotalTries(playerResult.getInt("tries"));
		}
		// Manage groups
		String groupName = playerResult.getString("name");
		if (groupName != null) {
			p.addGroup(makeGroup(playerResult));
		}

		return p;
	}

	private Group makeGroup(ResultSet result) throws SQLException {
		return new Group(result.getString("name"), result.getInt("fk_chapter"), result.getInt("fk_puzzle"));
	}

	private Badge makeBadge(ResultSet rs) throws SQLException {
		return new Badge(rs.getString("name"), rs.getBytes("logo"), rs.getInt("level"));
	}

	private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		// Found on StackOverflow
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}

	private List<Puzzle> getPuzzlesInChapter(int id) throws SQLException {
		List<Puzzle> puzzles = new ArrayList<>();
		ensureConnection();
		PreparedStatement puzzleStmt = DatabaseQuery.PUZZLES_IN_CHAPTER_QUERY.prepare(this.con);
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
			PreparedStatement puzzleStmt = DatabaseQuery.SPECIFIC_PUZZLE_QUERY.prepare(this.con);
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
			ensureConnection();
			PreparedStatement stmt = DatabaseQuery.SCORE.prepare(this.con);
			stmt.setInt(1, user);
			stmt.setInt(2, puzzle);

			ResultSet result = stmt.executeQuery();
			if (result.next())
				result.getInt("score");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public Completion getCompletion(int playerId, int puzzleId) {
		try {
			PreparedStatement completionsStmt = DatabaseQuery.GET_COMPLETION.prepare(this.con);
			completionsStmt.setInt(1, puzzleId);
			completionsStmt.setInt(2, playerId);
			ResultSet result = completionsStmt.executeQuery();
			if (result.next()) {
				return makeCompletion(playerId, puzzleId, result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Player getPlayer(int idPlayer) {
		try {
			PreparedStatement completionsStmt = DatabaseQuery.GET_PLAYER_SIMPLE.prepare(this.con);
			completionsStmt.setInt(1, idPlayer);
			ResultSet result = completionsStmt.executeQuery();
			if (result.next()) {
				return makePlayer(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Player getPlayerDetails(int idPlayer) {
		return getPlayerDetails(idPlayer, null);
	}

	public Player getPlayerDetails(String pseudoPlayer) {
		return getPlayerDetails(-1, pseudoPlayer);
	}

	private Player getPlayerDetails(int id, String pseudo) {
		try {
			ensureConnection();
			PreparedStatement completionsStmt;
			if (pseudo != null) {
				completionsStmt = DatabaseQuery.GET_PLAYER_DETAILS_BY_PSEUDO.prepare(this.con);
				completionsStmt.setString(1, pseudo);
			} else {
				completionsStmt = DatabaseQuery.GET_PLAYER_DETAILS_BY_ID.prepare(this.con);
				completionsStmt.setInt(1, id);
			}
			ResultSet result = completionsStmt.executeQuery();
			Player player = null;
			while (result.next()) {
				if (player == null) {
					player = makePlayer(result);
					completionsStmt = DatabaseQuery.GET_BADGES_OF_PLAYER.prepare(this.con);
					completionsStmt.setInt(1, result.getInt("id_player"));
					ResultSet resultBadges = completionsStmt.executeQuery();
					while (resultBadges.next()) {
						player.addBadge(makeBadge(resultBadges));
					}
				} else {
					player.addGroup(makeGroup(result));
				}
			}
			return player;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SortedSet<Player> getAllPlayerForLeaderboard() {
		try {
			ensureConnection();
			PreparedStatement playersStmt = DatabaseQuery.ALL_PLAYERS_FOR_LEADERBOARD.prepare(this.con);
			ResultSet result = playersStmt.executeQuery();
			ArrayList<Player> players = new ArrayList<Player>();
			Player tmpPlayer;
			while (result.next()) {
				tmpPlayer = makePlayer(result);
				if (!players.contains(tmpPlayer)) {
					players.add(tmpPlayer);
				} else {
					players.get(players.indexOf(tmpPlayer)).addGroup(makeGroup(result));
				}
			}
			return new TreeSet<Player>(players);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Badge getBadge(int badgeId) {
		try {
			ensureConnection();
			PreparedStatement completionsStmt = DatabaseQuery.GET_BADGE.prepare(this.con);
			completionsStmt.setInt(1, badgeId);
			ResultSet result = completionsStmt.executeQuery();
			if (result.next()) {
				return makeBadge(result);
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
			ensureConnection();
			PreparedStatement chapterStmt = DatabaseQuery.SPECIFIC_CHAPTER_QUERY.prepare(this.con);
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
			PreparedStatement chapterStmt = DatabaseQuery.ALL_CHAPTERS_QUERY.prepare(this.con);
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
	
	public List<Group> getAllGroups(){
		try {
			List<Group> list = new ArrayList<>();
			PreparedStatement stmt = DatabaseQuery.ALL_GROUPS.prepare(this.con);
			ResultSet groupResult = stmt.executeQuery();
			while(groupResult.next()) list.add(makeGroup(groupResult));
			return list;
		}catch(Exception e){
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
		return checkAvailability(pseudo, DatabaseQuery.CHECK_PSEUDO_AVAILABLE_QUERY.toString());
	}

	/**
	 * Check if an email is available
	 *
	 * @param email The email to check
	 * @return True if the email is available, false if it's already taken
	 */
	public boolean checkEmailAvailability(String email) {
		return checkAvailability(email, DatabaseQuery.CHECK_EMAIL_AVAILABLE_QUERY.toString());
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
	 * @param sgroup      The group of the user
	 * @param avatar      The avatar of the user
	 * @return True if the user was registered, false if an error occurred
	 */
	public int register(String pseudo, String email, String password, String firstname, String lastname,
			String description, String sgroup, String avatar) {
		Hash hash = Password.hash(password).withArgon2();
		try {
			ensureConnection();
			PreparedStatement statement = con.prepareStatement(DatabaseQuery.REGISTER_QUERY.toString(), Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, pseudo);
			statement.setString(2, email);
			statement.setString(3, hash.getResult());
			statement.setString(4, firstname);
			statement.setString(5, lastname);
			statement.setString(6, description);
			statement.setString(7, sgroup);
			statement.setString(8, avatar);
			if (statement.executeUpdate() == 1) {
				ResultSet inserted = statement.getGeneratedKeys();
				if (inserted.next())
					return inserted.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Login a user
	 *
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return id the id of the user, -1 if not login successefuly
	 */
	public int login(String username, String password) {
		try {
			ensureConnection();
			PreparedStatement statement = con.prepareStatement(DatabaseQuery.CHECK_PASSWORD.toString());DatabaseQuery.PUZZLES_IN_CHAPTER_QUERY.prepare(this.con);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String hashedPassword = result.getString("passwd");
				if (Password.check(password, hashedPassword).withArgon2())
					return result.getInt("id_player");
			}
		} catch (SQLException e) {
		}
		return -1;
	}

	public Completion insertOrUpdatePuzzleResponse(int puzzleId, int userId, String fileName, byte[] code) {
		try {
			Puzzle currentPuzzle = getPuzzle(puzzleId);
			Completion completion = getCompletion(userId, puzzleId);
			if (completion == null) {
				insertCompletion(new Completion(userId, puzzleId, fileName, currentPuzzle.getScoreMax()));
			} else {
				completion.addTry();
				updateCompletion(completion);
			}
			return completion;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void insertCompletion(Completion newCompletion) throws SQLException {
		// Insert completions
		PreparedStatement statement = DatabaseQuery.INSERT_COMPLETION.prepare(this.con);
		statement.setInt(1, newCompletion.getPuzzleId());
		statement.setInt(2, newCompletion.getPlayerId());
		statement.setInt(3, newCompletion.getTries());
		statement.setBytes(4, newCompletion.getCode());
		statement.setString(5, newCompletion.getFileName());
		statement.setInt(6, newCompletion.getScore());
		statement.executeUpdate();
	}

	private void updateCompletion(Completion completionToUpdate) throws SQLException {
		// Update completions
		PreparedStatement statement = DatabaseQuery.UPDATE_COMPLETION.prepare(this.con);
		statement.setInt(1, completionToUpdate.getTries());
		statement.setString(2, completionToUpdate.getFileName());
		statement.setInt(3, completionToUpdate.getScore());
		statement.setInt(4, completionToUpdate.getPuzzleId());
		statement.setInt(5, completionToUpdate.getPlayerId());
		statement.executeUpdate();
	}
}