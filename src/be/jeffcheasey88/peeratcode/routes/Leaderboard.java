package be.jeffcheasey88.peeratcode.routes;

import java.util.Base64;
import java.util.SortedSet;
import java.util.regex.Matcher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Player;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.User;

public class Leaderboard implements Response {

	private final DatabaseRepository databaseRepo;

	public Leaderboard(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Route(path = "^\\/leaderboard$")
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
				if(player.getAvatar() != null) playerJSON.put("avatar", Base64.getEncoder().encodeToString(player.getAvatar()));
				playerJSON.put("score", player.getTotalScore());
				playerJSON.put("completions", player.getTotalCompletion());
				playerJSON.put("tries", player.getTotalTries());
				playersJSON.add(playerJSON);
			}
		}
		writer.write(playersJSON.toJSONString().replace("\\", ""));
	}
}
