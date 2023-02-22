package be.jeffcheasey88.peeratcode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.routes.ChapterElement;
import be.jeffcheasey88.peeratcode.routes.ChapterList;
import be.jeffcheasey88.peeratcode.routes.Login;
import be.jeffcheasey88.peeratcode.routes.PuzzleElement;
import be.jeffcheasey88.peeratcode.routes.Register;
import be.jeffcheasey88.peeratcode.webserver.Client;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Router;

public class Main {
	// Define SSL Protocol parameters
	public static void main(String[] args) throws Exception {
		Configuration config = new Configuration("config.txt");
		config.load();
		Class.forName("com.mysql.cj.jdbc.Driver");

		Router router = new Router();

		router.setDefault(new Response(){

			@Override
			public Pattern getPattern(){
				return null;
			}

			@Override
			public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
				HttpUtil.responseHeaders(writer, 404, "Access-Control-Allow-Origin: *");
				writer.write("404 not Found.\n");
				writer.flush();
				writer.close();
			}
		});

		initRoutes(router, new DatabaseRepository(config));

		startWebServer(config, router);
	}

	private static void initRoutes(Router router, DatabaseRepository repo) {
		router.register(new ChapterElement(repo));
		router.register(new ChapterList(repo));
		router.register(new PuzzleElement(repo));
		router.register(new Register(repo));
		router.register(new Login(repo));
	}

	private static void startWebServer(Configuration config, Router router) throws IOException {
		if (config.useSsl()) {
			SSLServerSocket server = null;
			try {
				System.setProperty("javax.net.ssl.keyStore", config.getSslKeystore());
				System.setProperty("javax.net.ssl.keyStorePassword", config.getSslKeystorePasswd());

				SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				server = (SSLServerSocket) ssf.createServerSocket(config.getTcpPort());

				while (!server.isClosed()) {
					Socket socket = server.accept();
					Client client = new Client(socket, router);
					client.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (server != null) {
					server.close();
				}
			}
		}
		else {
			try (ServerSocket server = new ServerSocket(config.getTcpPort())){
				while(!server.isClosed()){
					Socket socket = server.accept();
					Client client = new Client(socket, router);
					client.start();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
