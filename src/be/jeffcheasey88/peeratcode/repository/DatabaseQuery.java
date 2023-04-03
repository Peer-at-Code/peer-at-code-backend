package be.jeffcheasey88.peeratcode.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseQuery {
	
	SPECIFIC_PUZZLE_QUERY("SELECT p.*, np.origin, GROUP_CONCAT(t.name) AS tags FROM puzzles p LEFT JOIN nextPart np ON p.id_puzzle = np.next LEFT JOIN containsTags ct ON ct.fk_puzzle = p.id_puzzle LEFT JOIN tags t ON t.id_tag = ct.fk_tag WHERE p.id_puzzle = ? GROUP BY p.id_puzzle"),
	SPECIFIC_CHAPTER_QUERY("SELECT * FROM chapters WHERE id_chapter = ?"),
	PUZZLES_IN_CHAPTER_QUERY("SELECT p.*, GROUP_CONCAT(t.name) AS tags FROM puzzles p LEFT JOIN containsTags ct ON ct.fk_puzzle = p.id_puzzle LEFT JOIN tags t ON t.id_tag = ct.fk_tag WHERE fk_chapter = ? GROUP BY p.id_puzzle"),
	ALL_CHAPTERS_QUERY("SELECT * FROM chapters WHERE id_chapter > 0"),
	ALL_GROUPS("SELCT * FROM groups"),
	ALL_PLAYERS_FOR_LEADERBOARD("select p.*, scores.*, g.* from players p ,(SELECT fk_player, SUM(c.score) AS score, COUNT(c.id_completion) AS completions, SUM(c.tries) AS tries, rank() over(ORDER BY score DESC) AS rank FROM completions c GROUP BY c.fk_player) AS scores LEFT JOIN containsGroups cg ON scores.fk_player = cg.fk_player LEFT JOIN groups g ON cg.fk_group = g.id_group  WHERE p.id_player = scores.fk_player ORDER BY g.fk_chapter, g.fk_puzzle"),
	CHECK_PSEUDO_AVAILABLE_QUERY("SELECT * FROM players WHERE pseudo = ?"),
	CHECK_EMAIL_AVAILABLE_QUERY("SELECT * FROM players WHERE email = ?"),
	REGISTER_QUERY("INSERT INTO players (pseudo, email, passwd, firstname, lastname, description, avatar) VALUES (?, ?, ?, ?, ?, ?, ?)"),
	REGISTER_PLAYER_IN_EXISTING_GROUP("INSERT INTO containsGroups (fk_player, fk_group) VALUES (?, (SELECT id_group FROM groups WHERE name = ?));"),
	CHECK_PASSWORD("SELECT id_player, passwd FROM players WHERE pseudo=?"),
	SCORE("SELECT score FROM completions WHERE fk_player = ? AND fk_puzzle = ?"),
	GET_COMPLETION("SELECT id_completion, tries, fileName, score FROM completions WHERE fk_puzzle = ? AND fk_player = ?"),
	GET_PLAYER_SIMPLE("SELECT pseudo, email, firstname, lastname, description FROM players WHERE id_player = ?"),
	GET_PLAYER_DETAILS("SELECT p.*, scores.score, scores.completions, scores.tries, scores.rank, g.* FROM players p, (SELECT fk_player, SUM(c.score) AS score, COUNT(c.id_completion) AS completions, SUM(c.tries) AS tries, rank() over(ORDER BY score DESC) AS rank FROM completions c GROUP BY c.fk_player) AS scores LEFT JOIN containsGroups cg ON scores.fk_player = cg.fk_player LEFT JOIN groups g ON cg.fk_group = g.id_group WHERE p.id_player = scores.fk_player AND "),
	GET_PLAYER_DETAILS_BY_ID(GET_PLAYER_DETAILS," p.id_player = ? ORDER BY g.fk_chapter, g.fk_puzzle;"),
	GET_PLAYER_DETAILS_BY_PSEUDO(GET_PLAYER_DETAILS,"p.pseudo = ? ORDER BY g.fk_chapter, g.fk_puzzle;"),
	GET_BADGE("SELECT * FROM badges WHERE id_badge = ?"),
	GET_BADGES_OF_PLAYER("SELECT * FROM badges b LEFT JOIN containsBadges cb ON cb.fk_badge = b.id_badge WHERE cb.fk_player = ?"),
	INSERT_COMPLETION("INSERT INTO completions (fk_puzzle, fk_player, tries, code, fileName, score) values (?, ?, ?, ?, ?, ?)"),
	INSERT_GROUP("INSERT INTO groups (name, fk_chapter, fk_puzzle) VALUES (?,?,?)"),
	UPDATE_COMPLETION("UPDATE completions SET tries = ?, filename = ?, score = ? WHERE fk_puzzle = ? AND fk_player = ?");
	
	private String request;
	
	DatabaseQuery(DatabaseQuery parent, String request){
		this.request = parent.request+request;
	}
	
	DatabaseQuery(String request){
		this.request = request;
	}
	
	public PreparedStatement prepare(Connection con) throws SQLException{
		return con.prepareStatement(this.request);
	}
	
	@Override
	public String toString(){
		return this.request;
	}
}
