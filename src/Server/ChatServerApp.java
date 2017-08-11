package Server;

import java.io.UnsupportedEncodingException;
//import Modules.Message;
//import Modules.Message.*;

public class ChatServerApp {

	public static void main(String[] args) throws UnsupportedEncodingException {
		//TestMessage();
		
		ChatServer chatServer = new ChatServer();
		chatServer.Run();		
	}

//	private static void TestMessage() {
//		try
//		{
//			Message message = new Message(Command.JOIN, Ack.REQ, "", "Test messgae 한글 : ㅉ**90");
//			System.out.println(message);
//			System.out.println(message.serialize());
//			byte[] bytes = message.getBytes();
//			
//			Message message2 = new Message(bytes);
//			System.out.println(message2);
//			
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
}
