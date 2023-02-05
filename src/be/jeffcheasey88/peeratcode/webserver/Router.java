package be.jeffcheasey88.peeratcode.webserver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Router{
	
	private List<Response> responses;
	private Response noFileFound;
	
	public Router(){
		this.responses = new ArrayList<>();
	}
	
	public void register(Response response){
		this.responses.add(response);
	}
	
	public void setDefault(Response response){
		this.noFileFound = response;
	}
	
	public void exec(String type, String path, HttpReader reader, HttpWriter writer) throws Exception {
		for(Response response : this.responses){
			if(type.equals(response.getType())){
				Matcher matcher = response.getPattern().matcher(path);
				if(matcher.matches()){
					response.exec(matcher, reader, writer);
					return;
				}
			}
		}
		if(noFileFound != null) noFileFound.exec(null, reader, writer);
	}
	
}