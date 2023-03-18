package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.model.Chapter;
import be.jeffcheasey88.peeratcode.model.Puzzle;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;

public class ChapterElement implements Response {

	private final DatabaseRepository databaseRepo;

	public ChapterElement(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Route(path = "^\\/chapter\\/([0-9]+)$")
	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		Chapter chapter = databaseRepo.getChapter(extractId(matcher));
		if (chapter != null) {
			JSONObject chapterJSON = new JSONObject();
			chapterJSON.put("id", chapter.getId());
			chapterJSON.put("name", chapter.getName());
			if (chapter.getStartDate() != null) chapterJSON.put("startDate", chapter.getStartDate().toString());
			if (chapter.getEndDate() != null) chapterJSON.put("endDate", chapter.getEndDate().toString());
			JSONArray puzzles = new JSONArray();
			for (Puzzle puzzle : chapter.getPuzzles()) {
				JSONObject puzzleJSON = new JSONObject();
				puzzleJSON.put("id", puzzle.getId());
				puzzleJSON.put("name", puzzle.getName());
				if (puzzle.getTags() != null) puzzleJSON.put("tags", puzzle.getJsonTags());
				puzzles.add(puzzleJSON);
			}
			chapterJSON.put("puzzles", puzzles);
			writer.write(chapterJSON.toJSONString());
		}
	}

	private int extractId(Matcher matcher) {
		return Integer.parseInt(matcher.group(1));
	}
}
