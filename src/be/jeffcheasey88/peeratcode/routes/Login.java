package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepo;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login implements Response {
	private final DatabaseRepo databaseRepo;

	public Login(DatabaseRepo databaseRepo) {
		this.databaseRepo = databaseRepo;
	}
	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		JSONObject informations = (JSONObject) HttpUtil.readJson(reader);
		if (informations != null) {
			String pseudo = (String) informations.get("pseudo");
			String password = (String) informations.get("passwd");
			boolean wellLogged = databaseRepo.login(pseudo, password);
			if (!wellLogged) {
				HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
			} else {
				HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
			}
		}
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/login$");
	}
}
