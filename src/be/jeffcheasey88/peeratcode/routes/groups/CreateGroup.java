package be.jeffcheasey88.peeratcode.routes.groups;

import java.util.regex.Matcher;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Group;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.User;

public class CreateGroup implements Response{
	
	private DatabaseRepository repo;
	
	public CreateGroup(DatabaseRepository repo){
		this.repo = repo;
	}

	@Route(path = "^\\/groupCreate$", type = "POST", needLogin = true)
	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		
		if(this.repo.insertGroup(new Group((JSONObject)HttpUtil.readJson(reader)))){
			HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		}else{
			HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
		}
	}

}
