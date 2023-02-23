package be.jeffcheasey88.peeratcode.routes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.User;

public class Result implements Response{
	
	private DatabaseRepository repo;
	
	public Result(DatabaseRepository repo){
		this.repo = repo;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		int puzzle = Integer.parseInt(matcher.group(1));
		
		int score = this.repo.getScore(0, puzzle);
		if(score < 0) {
			HttpUtil.responseHeaders(writer, 425, "Access-Control-Allow-Origin: *");
		}else{
			HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
			writer.write(score+"");
			writer.flush();
			writer.close();
		}
	}

	@Override
	public Pattern getPattern(){
		return Pattern.compile("^\\/result\\/(\\d+)$");
	}
	

}
