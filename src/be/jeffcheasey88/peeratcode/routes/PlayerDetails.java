package be.jeffcheasey88.peeratcode.routes;

import java.util.Base64;
import java.util.regex.Matcher;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Player;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.User;

public class PlayerDetails implements Response {

	private final DatabaseRepository databaseRepo;

	public PlayerDetails(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Route(path = "^\\/player\\/?(.+)?$")
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
			if (player.getGroups() != null) playerJSON.put("groups", player.getJsonGroups());
			playerJSON.put("rank", player.getRank());
			playerJSON.put("score", player.getTotalScore());
			playerJSON.put("completions", player.getTotalCompletion());
			playerJSON.put("tries", player.getTotalTries());
			if (player.getBadges() != null) playerJSON.put("badges", player.getJsonBadges());
			if(player.getAvatar() != null) playerJSON.put("avatar", Base64.getEncoder().encodeToString(player.getAvatar()));
			writer.write(playerJSON.toJSONString().replace("\\", ""));
		} else {
			HttpUtil.responseHeaders(writer, 400, "Access-Control-Allow-Origin: *");
		}
	}

}
