package be.jeffcheasey88.peeratcode;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.jeffcheasey88.peeratcode.routes.PuzzleList;
import be.jeffcheasey88.peeratcode.webserver.Client;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Router;

public class Main {
	
	public static void main(String[] args) throws Exception {
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
		
		initRoutes(router);
		
		ServerSocket server = new ServerSocket(80);
		
		while(!server.isClosed()){
			Socket socket = server.accept();
			Client client = new Client(socket, router);
			client.start();
		}
	}
	
	private static void initRoutes(Router router){
		router.register(new PuzzleList());
	}

}
