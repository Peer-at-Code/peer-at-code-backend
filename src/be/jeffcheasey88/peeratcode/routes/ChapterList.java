package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.model.Chapter;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterList implements Response {

	private final DatabaseRepository databaseRepo;

	public ChapterList(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		List<Chapter> allChapters = databaseRepo.getAllChapters();
		if (allChapters != null) {
			JSONArray chaptersJSON = new JSONArray();
			for (Chapter chapter : allChapters) {
				JSONObject chapterJSON = new JSONObject();
				chapterJSON.put("id", chapter.getId());
				chapterJSON.put("name", chapter.getName());
				chaptersJSON.add(chapterJSON);
			}
			writer.write(chaptersJSON.toJSONString());
		}
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/chapters$");
	}
}
