package be.jeffcheasey88.peeratcode.routes;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepo;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register implements Response {

	private final DatabaseRepo databaseRepo;

	public Register(DatabaseRepo databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		JSONObject informations = (JSONObject) HttpUtil.readJson(reader);
		if (informations != null) {
			String pseudo = (String) informations.get("pseudo");
			String email = (String) informations.get("email");
			String password = (String) informations.get("passwd");
			String firstname = (String) informations.get("firstname");
			String lastname = (String) informations.get("lastname");
			String description = (String) informations.get("description");
			String group = (String) informations.get("group");
			String avatar = (String) informations.get("avatar");

			boolean pseudoAvailable = databaseRepo.checkPseudoAvailability(pseudo);
			boolean emailAvailable = databaseRepo.checkEmailAvailability(email);
			if (pseudoAvailable && emailAvailable) {
				boolean wellRegistered = databaseRepo.register(pseudo, email, password, firstname, lastname, description, group, avatar);
				if (!wellRegistered) {
					HttpUtil.responseHeaders(writer, 400, "Access-Control-Allow-Origin: *");
					writer.write("Error while registering");
				} else {
					HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
					writer.write("OK");
				}
			} else {
				HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
				JSONObject error = new JSONObject();
				error.put("username_valid", pseudoAvailable);
				error.put("email_valid", emailAvailable);
				writer.write(error.toJSONString());
			}
		}
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/register$");
	}
}
