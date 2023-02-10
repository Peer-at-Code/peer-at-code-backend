package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.model.Puzzle;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PuzzleElement implements Response {

	private final DatabaseRepository databaseRepo;

	public PuzzleElement(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		Puzzle puzzle = databaseRepo.getPuzzle(extractId(matcher));
		if (puzzle != null) {
			JSONObject puzzleJSON = new JSONObject();
			puzzleJSON.put("id", puzzle.getId());
			puzzleJSON.put("name", puzzle.getName());
			puzzleJSON.put("content", puzzle.getContent());
			writer.write(puzzleJSON.toJSONString());
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
