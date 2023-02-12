package be.jeffcheasey88.peeratcode.routes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;

public class Register implements Response{
	
	private DatabaseRepository repo;
	
	public Register(DatabaseRepository repo){
		this.repo = repo;
	}

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		JSONObject json = (JSONObject) HttpUtil.readJson(reader);
		
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
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
