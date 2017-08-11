package Modules;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Message {
	public enum Command { JOIN, NOTY, MSG, List, QUIT }
	public enum Ack { REQ, OK, FAIL }
	
	private Command command;
	private Ack ack;
	private String name;
	private String message;
	public Command getCommand() {
		return command;
	}
	public void setCommand(Command command) {
		this.command = command;
	}
	public Ack getAck() {
		return ack;
	}
	public void setAck(Ack ack) {
		this.ack = ack;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getNameMessage() {
		return String.format("[%s] %s", this.name, this.message);
	}
	
	@Override
	public String toString() {
		return String.format("Command[%s] Ack[%s] Name[%s] Message[%s]", command, ack, name, message);
	}
	
	public String serialize() {
		try {
			String encodedMessage = Base64.getEncoder().encodeToString(this.message.getBytes("utf-8"));
			return String.format("%s:%s:%s:%s", this.command, this.ack, this.name, encodedMessage);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public byte[] getBytes() throws UnsupportedEncodingException {
		return this.serialize().getBytes("utf-8");
	}
	
	public Message() {}
	
	public Message(Command command, Ack ack, String name, String message) {
		this.command = command;
		this.ack = ack;
		this.name = name;
		this.message = message;
	}
	
	public Message(String val)
	{
		setAll(val);
	}
	private void setAll(String val) {
		String[] tokens = val.split(":");
		this.command = Command.valueOf(tokens[0]);
		this.ack = Ack.valueOf(tokens[1]);
		this.name = tokens[2];
		byte[] bytes = Base64.getDecoder().decode(tokens[3]);
		try {
			this.message = new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.message = "";
		}
	}
	
	public Message(byte[] bytes) throws UnsupportedEncodingException {
		String val = new String(bytes, "utf-8");
		setAll(val);
	}
	
}
