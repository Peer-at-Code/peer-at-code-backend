package be.jeffcheasey88.peeratcode.webserver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;

public class Router{
	
	private List<Response> responses;
	private Response noFileFound;
	private RsaJsonWebKey rsaJsonWebKey;
	
	public Router() throws Exception{
		this.responses = new ArrayList<>();
		this.rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
	}
	
	public void register(Response response){
		this.responses.add(response);
	}
	
	public void setDefault(Response response){
		this.noFileFound = response;
	}

	public void exec(String type, String path, User user, HttpReader reader, HttpWriter writer) throws Exception {
		for(Response response : this.responses){
			if(type.equals(response.getType())){
				Matcher matcher = response.getPattern().matcher(path);
				if(matcher.matches()){
					response.exec(matcher, user, reader, writer);
					return;
				}
			}
		}
		if(noFileFound != null) noFileFound.exec(null, user, reader, writer);
	}
	
	public RsaJsonWebKey getWebKey(){
		return this.rsaJsonWebKey;
	}
}