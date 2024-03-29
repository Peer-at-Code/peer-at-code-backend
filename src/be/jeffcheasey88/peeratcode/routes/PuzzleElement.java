package be.jeffcheasey88.peeratcode.routes;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jose4j.json.internal.json_simple.JSONArray;
import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Puzzle;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.User;

public class PuzzleElement implements Response {

	private final DatabaseRepository databaseRepo;

	public PuzzleElement(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Route(path = "^\\/puzzle\\/([0-9]+)$")
	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *", "Content-Type: application/json");
		Puzzle puzzle = databaseRepo.getPuzzle(extractId(matcher));
		if (puzzle != null) {
			JSONObject puzzleJSON = new JSONObject();
			puzzleJSON.put("id", puzzle.getId());
			puzzleJSON.put("name", puzzle.getName());
			puzzleJSON.put("content", puzzle.getContent());
			if (puzzle.getTags() != null) puzzleJSON.put("tags", puzzle.getJsonTags());
			if (puzzle.getDepend() > 0)	puzzleJSON.put("depend", puzzle.getDepend());
			writer.write(puzzleJSON.toJSONString());
		}
	}

	private int extractId(Matcher matcher) {
		return Integer.parseInt(matcher.group(1));
	}
}
