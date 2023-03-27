package be.jeffcheasey88.peeratcode.routes.groups;

import java.util.regex.Matcher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Group;
import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.User;

public class GroupList implements Response{
	
	private DatabaseRepository repo;
	
	public GroupList(DatabaseRepository repo){
		this.repo = repo;
	}

	@Route(path = "^\\/groups$", needLogin = true)
	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		JSONArray result = new JSONArray();
		for(Group group : this.repo.getAllGroups()) result.add(group.toJson());
		writer.write(result.toJSONString());
	}

}
