package Server;

import java.io.IOException;
import java.net.*;

public class ChatServer {
	private static final int port = 5000;
	private static final String ip = "192.168.1.14";
		
	public ChatServer() {}

	public void Run() {
		ChatServer.ServerLog("시작한다.");
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket();
			
			//String localhost = InetAddress.getLocalHost().getHostAddress();
			//serverSocket.bind( new InetSocketAddress(localhost, port));
			serverSocket.bind( new InetSocketAddress(ip, port));
			
			while(true) {
				Socket socket = serverSocket.accept();				
				new ChatServerThread(socket).start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public static void ServerLog(String logMessage) {
		System.out.println("[Server] " + logMessage);
	}
}
