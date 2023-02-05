package be.jeffcheasey88.peeratcode.routes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;

public class PuzzleList implements Response{

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		
		JSONObject json = new JSONObject();
		json.put("Theo", "GL HF");
		writer.write(json.toJSONString());
		writer.flush();
		writer.close();
	}

	@Override
	public Pattern getPattern(){
		return Pattern.compile("^\\/puzzle\\/?$");
	}

}
