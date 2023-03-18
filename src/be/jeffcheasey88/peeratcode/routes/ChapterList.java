package be.jeffcheasey88.peeratcode.routes;

import java.util.List;
import java.util.regex.Matcher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Chapter;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.User;

public class ChapterList implements Response {

	private final DatabaseRepository databaseRepo;

	public ChapterList(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Route(path = "^\\/chapters$")
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
				if (chapter.getStartDate() != null) chapterJSON.put("startDate", chapter.getStartDate());
				if (chapter.getEndDate() != null) chapterJSON.put("endDate", chapter.getEndDate());
				chaptersJSON.add(chapterJSON);
			}
			writer.write(chaptersJSON.toJSONString());
		}
	}

}
