package be.jeffcheasey88.peeratcode.routes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Router;
import be.jeffcheasey88.peeratcode.webserver.User;

public class Login implements Response {

	private DatabaseRepository databaseRepo;
	private Router router;

	public Login(DatabaseRepository databaseRepo, Router router){
		this.databaseRepo = databaseRepo;
		this.router = router;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		JSONObject informations = (JSONObject) HttpUtil.readJson(reader);
		if (informations != null) {
			String pseudo = (String) informations.get("pseudo");
			String password = (String) informations.get("passwd");
			int id;
			if ((id = databaseRepo.login(pseudo, password)) >= 0){
				HttpUtil.responseHeaders(writer, 200,
						"Access-Control-Allow-Origin: *",
						"Authorization: Bearer "+this.router.createAuthUser(id));
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
