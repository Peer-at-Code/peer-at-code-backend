package be.jeffcheasey88.peeratcode.webserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpWriter{
	
	private OutputStream out;
	private BufferedWriter writer;
	
	public HttpWriter(Socket socket) throws Exception{
		this.out = socket.getOutputStream();
		this.writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
	}
	
	public void write(byte[] buffer) throws IOException{
		this.out.write(buffer);
		this.out.flush();
	}

	public void write(String message) throws IOException{
		this.writer.write(message);
	}
	
	public void flush() throws IOException{
		this.writer.flush();
	}
	
	public void close() throws IOException{
		this.writer.close();
	}
}