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

import java.util.Base64;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Leaderboard implements Response {

	private final DatabaseRepository databaseRepo;

	public Leaderboard(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		SortedSet<Player> allPlayers = databaseRepo.getAllPlayerForLeaderboard();
		JSONArray playersJSON = new JSONArray();
		if (allPlayers != null) {
			for (Player player : allPlayers) {
				JSONObject playerJSON = new JSONObject();
				playerJSON.put("pseudo", player.getPseudo());
				playerJSON.put("group", player.getGroup());
				if(player.getAvatar() != null) playerJSON.put("avatar", new String(Base64.getEncoder().encode(player.getAvatar())));
				playerJSON.put("score", player.getTotalScore());
				playerJSON.put("completions", player.getTotalCompletion());
				playerJSON.put("tries", player.getTotalTries());
				playersJSON.add(playerJSON);
			}
		}
		writer.write(playersJSON.toJSONString());
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/leaderboard$");
	}
}
