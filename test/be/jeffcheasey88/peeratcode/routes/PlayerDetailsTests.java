package be.jeffcheasey88.peeratcode.routes;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import be.jeffcheasey88.peeratcode.Main;
import be.jeffcheasey88.peeratcode.webclient.WebClient;

@TestInstance(Lifecycle.PER_CLASS)
class PlayerDetailsTests {

	private Thread server;
	private WebClient client;
	
	@BeforeAll
	void init(){
		server = new Thread(new Runnable() {
			@Override
			public void run(){
				try {
					Main.main(null);
				} catch (Exception e) {
					e.printStackTrace();
				};
			}
		});
		server.start();
		client = new WebClient("localhost", 80);
	}
	
	@AfterAll
	void close(){
		server.interrupt();
	}
	
	@Test
	void test(){
		try {
			client.auth("JeffCheasey88", "TheoPueDesPieds");
			client.route("/player/","GET");
			
			client.assertResponseCode(200);
		} catch (Exception e){
			fail(e);
		}
	}

}
