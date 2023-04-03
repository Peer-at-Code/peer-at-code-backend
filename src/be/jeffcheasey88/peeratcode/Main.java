package be.jeffcheasey88.peeratcode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.routes.BadgeDetails;
import be.jeffcheasey88.peeratcode.routes.ChapterElement;
import be.jeffcheasey88.peeratcode.routes.ChapterList;
import be.jeffcheasey88.peeratcode.routes.Leaderboard;
import be.jeffcheasey88.peeratcode.routes.Login;
import be.jeffcheasey88.peeratcode.routes.PlayerDetails;
import be.jeffcheasey88.peeratcode.routes.PuzzleElement;
import be.jeffcheasey88.peeratcode.routes.PuzzleResponse;
import be.jeffcheasey88.peeratcode.routes.Register;
import be.jeffcheasey88.peeratcode.routes.Result;
import be.jeffcheasey88.peeratcode.routes.groups.CreateGroup;
import be.jeffcheasey88.peeratcode.routes.groups.GroupList;
import be.jeffcheasey88.peeratcode.webserver.Client;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.Route;
import be.jeffcheasey88.peeratcode.webserver.Router;
import be.jeffcheasey88.peeratcode.webserver.User;

public class Main {
	public static void main(String[] args) throws Exception {
		Configuration config = new Configuration("config.txt");
		config.load();

		Class.forName("com.mysql.cj.jdbc.Driver");

		Router router = new Router(new DatabaseRepository(config), config.getTokenIssuer(), config.getTokenExpiration());

		router.setDefault(new Response(){
			@Override
			public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
				HttpUtil.responseHeaders(writer, 404, "Access-Control-Allow-Origin: *");
				writer.write("404 not Found.\n");
				writer.flush();
				writer.close();
			}
		});
		
		router.register(new Response(){
			@Route(path = "^(.*)$", type = "OPTIONS")
			@Override
			public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
				HttpUtil.responseHeaders(writer, 200,
						"Access-Control-Allow-Origin: *",
						"Access-Control-Allow-Methods: *",
						"Access-Control-Allow-Headers: *");
			}
		});

		initRoutes(router);

		startWebServer(config, router);
	}

	private static void initRoutes(Router router) {
		router.register(new ChapterElement(router.getDataBase()));
		router.register(new ChapterList(router.getDataBase()));
		router.register(new PuzzleElement(router.getDataBase()));
		router.register(new Register(router.getDataBase(), router));
		router.register(new Login(router.getDataBase(), router));
		router.register(new Result(router.getDataBase()));
		router.register(new PuzzleResponse(router.getDataBase()));
		router.register(new Leaderboard(router.getDataBase()));
		router.register(new PlayerDetails(router.getDataBase()));
		router.register(new BadgeDetails(router.getDataBase()));
		
		router.register(new GroupList(router.getDataBase()));
		router.register(new CreateGroup(router.getDataBase()));
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
					Client client = new Client(socket, router, RsaJwkGenerator.generateJwk(2048));
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
					RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
					Client client = new Client(socket, router, rsaJsonWebKey);
					client.start();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
