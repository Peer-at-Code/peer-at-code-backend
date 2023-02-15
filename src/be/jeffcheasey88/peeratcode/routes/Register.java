package be.jeffcheasey88.peeratcode.routes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;

public class Register implements Response {

	private final DatabaseRepository databaseRepo;

	public Register(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		JSONObject informations = (JSONObject) HttpUtil.readJson(reader);
		if (informations != null) {
			boolean allFieldsFilled = informations.containsKey("pseudo") && informations.containsKey("email")
					&& informations.containsKey("passwd") && informations.containsKey("firstname")
					&& informations.containsKey("lastname") && informations.containsKey("description")
					&& informations.containsKey("sgroup") && informations.containsKey("avatar");
			if (!allFieldsFilled) {
				HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
				return;
			}
			String pseudo = (String) informations.get("pseudo");
			String email = (String) informations.get("email");
			String password = (String) informations.get("passwd");
			String firstname = (String) informations.get("firstname");
			String lastname = (String) informations.get("lastname");
			String description = (String) informations.get("description");
			String group = (String) informations.get("sgroup");
			String avatar = (String) informations.get("avatar");

			boolean pseudoAvailable = databaseRepo.checkPseudoAvailability(pseudo);
			boolean emailAvailable = databaseRepo.checkEmailAvailability(email);
			if (pseudoAvailable && emailAvailable) {
				if (databaseRepo.register(pseudo, email, password, firstname, lastname, description, group, avatar)){
					HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
					return;
				}
			} else {
				HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
				JSONObject error = new JSONObject();
				error.put("username_valid", pseudoAvailable);
				error.put("email_valid", emailAvailable);
				writer.write(error.toJSONString());
				return;
			}
		}
		HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/register$");
	}
	
	@Override
	public String getType(){
		return "POST";
	}

}