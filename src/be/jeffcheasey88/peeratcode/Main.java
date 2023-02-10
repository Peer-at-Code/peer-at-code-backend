package be.jeffcheasey88.peeratcode;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.routes.ChapterElement;
import be.jeffcheasey88.peeratcode.routes.ChapterList;
import be.jeffcheasey88.peeratcode.routes.PuzzleElement;
import be.jeffcheasey88.peeratcode.webserver.Client;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Router;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Configuration config = new Configuration("config.txt");
		config.load();
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://"+config.getDbHost()+":"+config.getDbPort()+"/"+config.getDbDatabase()+"",config.getDbUser(), config.getDbPassword());
		
		Router router = new Router();
		
		router.setDefault(new Response(){
			
			@Override
			public Pattern getPattern(){return null;}
			
			@Override
			public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
				HttpUtil.responseHeaders(writer, 404, "Access-Control-Allow-Origin: *");
				writer.write("404 not Found.\n");
				writer.flush();
				writer.close();
			}
		});
		
		initRoutes(router, new DatabaseRepository(con));
		
		
		ServerSocket server = new ServerSocket(80);
		
		while(!server.isClosed()){
			Socket socket = server.accept();
			Client client = new Client(socket, router);
			client.start();
		}
		
	}	
	private static void initRoutes(Router router, DatabaseRepository repo){
		router.register(new ChapterElement(repo));
		router.register(new ChapterList(repo));
		router.register(new PuzzleElement(repo));
	}

}
