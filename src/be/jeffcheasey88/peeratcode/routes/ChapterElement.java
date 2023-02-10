package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.model.Chapter;
import be.jeffcheasey88.peeratcode.model.Puzzle;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterElement implements Response {

	private final DatabaseRepository databaseRepo;

	public ChapterElement(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		Chapter chapter = databaseRepo.getChapter(extractId(matcher));
		if (chapter != null) {
			JSONObject chapterJSON = new JSONObject();
			chapterJSON.put("id", chapter.getId());
			chapterJSON.put("name", chapter.getName());
			JSONArray puzzles = new JSONArray();
			for (Puzzle puzzle : chapter.getPuzzles()) {
				JSONObject puzzleJSON = new JSONObject();
				puzzleJSON.put("id", puzzle.getId());
				puzzleJSON.put("name", puzzle.getName());
				puzzles.add(puzzleJSON);
			}
			chapterJSON.put("puzzles", puzzles);
			writer.write(chapterJSON.toJSONString());
		}
		writer.flush();
		writer.close();
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/chapter\\/([0-9]+)$");
	}

	private int extractId(Matcher matcher) {
		return Integer.parseInt(matcher.group(1));
	}
}
