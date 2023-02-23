package be.jeffcheasey88.peeratcode.routes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.User;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;

public class Login implements Response {

	private final DatabaseRepository databaseRepo;

	public Login(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		JSONObject informations = (JSONObject) HttpUtil.readJson(reader);
		if (informations != null) {
			String pseudo = (String) informations.get("pseudo");
			String password = (String) informations.get("passwd");
			if (databaseRepo.login(pseudo, password)) {
				HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
				return;
			}
		}
		HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/login$");
	}

	@Override
	public String getType(){
		return "POST";
	}

}
