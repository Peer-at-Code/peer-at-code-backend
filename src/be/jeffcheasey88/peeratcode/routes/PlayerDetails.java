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

import java.util.Arrays;
import java.util.Base64;
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
		Player player;
		if (matcher.group(1) != null){
			player = databaseRepo.getPlayerDetails(matcher.group(1));
		} else {
			player = databaseRepo.getPlayerDetails(user.getId());
		}
		JSONObject playerJSON = new JSONObject();
		if (player != null) {
			HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
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
			if(player.getAvatar() != null) playerJSON.put("avatar", Base64.getEncoder().encodeToString(player.getAvatar()));
			writer.write(playerJSON.toJSONString().replace("\\", ""));
		} else {
			HttpUtil.responseHeaders(writer, 400, "Access-Control-Allow-Origin: *");
		}
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/player\\/?(.+)?$");
	}
}
