package be.jeffcheasey88.peeratcode.routes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Player;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Router;
import be.jeffcheasey88.peeratcode.webserver.User;

public class Register implements Response {

	private DatabaseRepository databaseRepo;
	private Router router;

	public Register(DatabaseRepository databaseRepo, Router router) {
		this.databaseRepo = databaseRepo;
		this.router = router;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		if(user != null){
			HttpUtil.responseHeaders(writer, 403,"Access-Control-Allow-Origin: *");
			return;
		}
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
				int id;
				if ((id = databaseRepo.register(pseudo, email, password, firstname, lastname, description, group,
						avatar)) >= 0) {
					HttpUtil.responseHeaders(writer, 200,
							"Access-Control-Allow-Origin: *",
							"Access-Control-Expose-Headers: Authorization",
							"Authorization: Bearer " + this.router.createAuthUser(id));
					createFolderToSaveSourceCode(pseudo);
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

	private void createFolderToSaveSourceCode(String pseudo) throws IOException {
		Files.createDirectories(Paths.get(String.format(Player.PATH_TO_CODE, pseudo)));
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/register$");
	}

	@Override
	public String getType() {
		return "POST";
	}

}
