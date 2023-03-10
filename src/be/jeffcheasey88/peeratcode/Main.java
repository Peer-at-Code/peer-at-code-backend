package be.jeffcheasey88.peeratcode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

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
import be.jeffcheasey88.peeratcode.webserver.Client;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
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
			public Pattern getPattern(){
				return null;
			}

			@Override
			public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
				HttpUtil.responseHeaders(writer, 404, "Access-Control-Allow-Origin: *");
				writer.write("404 not Found.\n");
				writer.flush();
				writer.close();
			}
		});
		
		router.register(new Response(){
			@Override
			public Pattern getPattern() {
				return Pattern.compile("^(.*)$");
			}
			@Override
			public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
				HttpUtil.responseHeaders(writer, 200,
						"Access-Control-Allow-Origin: *",
						"Access-Control-Allow-Methods: *",
						"Access-Control-Allow-Headers: *");
			}
			
			@Override
			public String getType() {
				return "OPTIONS";
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
