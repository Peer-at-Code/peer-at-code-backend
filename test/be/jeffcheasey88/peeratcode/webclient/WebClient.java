package be.jeffcheasey88.peeratcode.webclient;

import static org.junit.Assert.fail;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;

public class WebClient {
	
	private static Pattern AUTORIZATION = Pattern.compile("Authorization: Bearer (.*)");
	
	private Socket socket;
	private HttpReader reader;
	private HttpWriter writer;
	
	private String token;
	private int responseCode;
	private List<String> headers;
	
	private String host;
	private int port;
	
	public WebClient(String host, int port){
		this.host = host;
		this.port = port;
		this.headers = new ArrayList<>();
	}
	
	private void ensureConnection() throws Exception{
		this.socket = new Socket(this.host, this.port);
		this.reader = new HttpReader(socket);
		this.writer = new HttpWriter(socket);
		this.responseCode = -1;
		this.headers.clear();
	}
	
	public void auth(String user, String password) throws Exception{
		JSONObject login = new JSONObject();
		login.put("pseudo", user);
		login.put("passwd", password);
		route("/login", "POST", login.toJSONString());
		
		for(String line : this.headers){
			Matcher matcher = AUTORIZATION.matcher(line);
			if(matcher.matches()){
				this.token = matcher.group(1);
				break;
			}
		}
	}
	
	public void route(String route, String type, String... content) throws Exception{
		ensureConnection();
		this.writer.write(type+" "+route+" HTTP/1.1\n");
		if(this.token != null) this.writer.write("Authorization: Bearer "+this.token+"\n");
		
		this.writer.write("\n");
		for(String value : content) this.writer.write(value+"\n");
		this.writer.flush();
		
		this.responseCode = Integer.parseInt(this.reader.readLine().split("\\s+")[1]);
		String line;
		while(((line = reader.readLine()) != null) && line.length() > 0) this.headers.add(line);
	}
	
	public void assertResponseCode(int expected){
		try {
			if(expected != responseCode) fail("Expected http reponse code <"+expected+"> but found <"+responseCode+">");
		} catch (Exception e){
			fail("Failed to get the response code: "+e.getMessage());
		}
	}
	
}
