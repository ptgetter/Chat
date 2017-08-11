package Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import Modules.Message;

public class Client {
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	
	public Client(Socket socket) {
		this.socket = socket;
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
			this.printWriter = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(Message message) {
		printWriter.println(message.serialize());
		printWriter.flush();
		ChatServer.ServerLog(message.toString());
	}
	
	public Message getMessage() {
		try {
			String line = this.bufferedReader.readLine();
			return new Message(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
