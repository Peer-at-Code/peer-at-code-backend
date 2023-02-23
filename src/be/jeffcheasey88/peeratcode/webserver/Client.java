package be.jeffcheasey88.peeratcode.webserver;

import java.net.Socket;
import java.util.Arrays;

import be.jeffcheasey88.peeratcode.model.User;

public class Client extends Thread{
	
	private HttpReader reader;
	private HttpWriter writer;
	private Router router;
	
	public Client(Socket socket, Router router) throws Exception{
		this.reader = new HttpReader(socket);
		this.writer = new HttpWriter(socket);
		this.router = router;
	}
	
	@Override
	public void run(){
		try {
			String[] headers = reader.readLine().split("\\s");
			System.out.println(Arrays.toString(headers));

			User user = new User(HttpUtil.readAutorization(reader));
			router.exec(headers[0], headers[1], user, reader, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}