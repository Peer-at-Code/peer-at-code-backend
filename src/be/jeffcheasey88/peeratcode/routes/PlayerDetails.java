package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.model.Player;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerDetails implements Response {

	private final DatabaseRepository databaseRepo;

	public PlayerDetails(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		int id = user.getId();
		if (matcher.groupCount() > 0) {
			id = Integer.parseInt(matcher.group(1));
		}
		Player player = databaseRepo.getPlayerDetails(id);
		JSONObject playerJSON = new JSONObject();
		if (player != null) {
			playerJSON.put("pseudo", player.getPseudo());
			playerJSON.put("email", player.getEmail());
			playerJSON.put("firstname", player.getFirstname());
			playerJSON.put("lastname", player.getLastname());
			playerJSON.put("description", player.getDescription());
			playerJSON.put("group", player.getGroup());
			playerJSON.put("score", player.getTotalScore());
			playerJSON.put("completions", player.getTotalCompletion());
			playerJSON.put("tries", player.getTotalTries());
			playerJSON.put("badges", player.getBadges());
			playerJSON.put("avatar", player.getAvatar());
		}
		writer.write(playerJSON.toJSONString());
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/player\\/(.+)?$");
	}
}
