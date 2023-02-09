package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.jeffcheasey88.peeratcode.repository.DatabaseQueries.SPECIFIC_PUZZLE_QUERY;

public class PuzzleElement implements Response {
	private final Connection con;

	public PuzzleElement(Connection con) {
		this.con = con;
	}

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");

		JSONObject puzzle = new JSONObject();

		PreparedStatement puzzleStmt = con.prepareStatement(SPECIFIC_PUZZLE_QUERY);
		puzzleStmt.setInt(1, extractId(matcher));

		ResultSet puzzleResult = puzzleStmt.executeQuery();
		if (puzzleResult.next()) {
			puzzle.put("id", puzzleResult.getString("id_puzzle"));
			puzzle.put("name", puzzleResult.getString("name"));
			puzzle.put("content", puzzleResult.getString("content"));
			puzzle.put("chapter", puzzleResult.getString("fk_chapter"));

			writer.write(puzzle.toJSONString());
		}
		writer.flush();
		writer.close();
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/puzzle\\/([0-9]+)$");
	}

	private int extractId(Matcher matcher) {
		return Integer.parseInt(matcher.group(1));
	}
}
