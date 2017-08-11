package Server;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

import Modules.Message;
import Modules.Message.Ack;
import Modules.Message.Command;

public class ChatServerThread extends Thread {
	private static Map<String, Client> clientMap = new HashMap<String, Client>();
	private Client client;	
	
	public ChatServerThread(Socket socket) {
		this.client = new Client(socket);
		
		InetSocketAddress inetSocketAddress = ( InetSocketAddress )socket.getRemoteSocketAddress();
		ChatServer.ServerLog( "connected from " + inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort() );
	}

	@Override
	public void run() {
		doJoin();
		doChat();
	}
	
	private void doChat() {
		Message message = null;
		while(true) {
			message = client.getMessage();
			switch(message.getCommand()) {				
				case JOIN:
					break;
				case List:
					String list = "[현재 대화방에 있는 사람들]\r\n";
					int count = 0;
					synchronized (ChatServerThread.clientMap) {
						Set<String> keys = ChatServerThread.clientMap.keySet();
						for(String key : keys) {
							list += key + "\r\n";
						}
						count = keys.size();
					}
					list += String.format("==========%s명============", count);
					client.sendMessage(new Message(Command.List, Ack.OK, "", list));					
					break;
				case MSG:
					message.setAck(Ack.OK);
					broadCast(message);
					break;
				case NOTY:
					break;
				case QUIT:
					String name = message.getName();
					synchronized (ChatServerThread.clientMap) {
						ChatServerThread.clientMap.remove(name);
					}
					broadCast(new Message(Command.NOTY, Ack.REQ, name, String.format("%s님 퇴장!!!!!!", name)));
					this.client = null;
					this.interrupt();
					break;
				default:
					break;
			}
		}
	}

	private void doJoin() {
		Message message = null;
		Message joinMessage = new Message(Command.JOIN, Ack.REQ, "", "이름을 넣어라:");
		while (true) {

			client.sendMessage(joinMessage);

			message = client.getMessage();
			if (message.getCommand() == Command.JOIN && message.getAck() == Ack.OK) {
				String name = message.getMessage();
				if(isExistName(name)) {
					client.sendMessage(new Message(Command.JOIN, Ack.REQ, name, "대화명이 존재합니다."));
				}
				else {
					client.sendMessage(new Message(Command.JOIN, Ack.OK, name, String.format("%s 님 환영합니다.", name)));
					broadCast(new Message(Command.NOTY, Ack.REQ, name, String.format("%s 님이 등장!!!!", name)));
					ChatServerThread.clientMap.put(name, client);
					break;
				}
			}			
		}
	}

	private boolean isExistName(String name) {
		Set<String> keys = clientMap.keySet();
		return keys.contains(name);
	}

	private void broadCast(Message message) {
		ChatServer.ServerLog("broadCast " + message);
		synchronized (ChatServerThread.clientMap) {
			for(String key : ChatServerThread.clientMap.keySet()) {
				Client client = clientMap.get(key);
				client.sendMessage(message);
			}			
		}		
	}
}
