package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.model.Badge;
import be.jeffcheasey88.peeratcode.model.Player;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Base64;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BadgeDetails implements Response {

	private final DatabaseRepository databaseRepo;

	public BadgeDetails(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		if (matcher.groupCount() > 0) {
			int badgeId = Integer.parseInt(matcher.group(1));
			Badge badge = databaseRepo.getBadge(badgeId);
			JSONObject badgeJSON = new JSONObject();
			if (badge != null) {
				badgeJSON.put("name", badge.getName());
				if(badge.getLogo() != null) badgeJSON.put("logo", Base64.getEncoder().encodeToString(badge.getLogo()));
				badgeJSON.put("level", badge.getLevel());
			}
			writer.write(badgeJSON.toJSONString().replace("\\", ""));
		}
		else {
			HttpUtil.responseHeaders(writer, 400, "Access-Control-Allow-Origin: *");
		}
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/badge\\/([0-9]+)$");
	}
}
