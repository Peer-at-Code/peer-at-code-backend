package be.jeffcheasey88.peeratcode.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class HttpReader {
	
	private Socket socket;
	private InputStream in;
	private BufferedReader reader;
	
	public HttpReader(Socket socket) throws Exception{
		this.socket = socket;
		this.in = socket.getInputStream();
		this.reader = new BufferedReader(new InputStreamReader(in));
	}
	
	public boolean isClosed(){
		return this.socket.isClosed();
	}
	
	public int read(byte[] buffer) throws IOException{
		return this.in.read(buffer);
	}
	
	public int read(char[] buffer) throws IOException {
		return this.reader.read(buffer);
	}
	
	public String readLine() throws IOException{
		return this.reader.readLine();
	}
	
	public boolean ready() throws IOException{
		return this.reader.ready();
	}
	
}