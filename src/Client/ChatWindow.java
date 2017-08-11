package Client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import Modules.Message;
import Modules.Message.Ack;
import Modules.Message.Command;

public class ChatWindow {
	private static final int port = 5000;
	private static final String ip = "192.168.1.14";

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private Thread clientThread;
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	private boolean isOnline;
	private String name;

	public ChatWindow() {
		frame = new Frame("대화방");
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		clientThread = new ChatClientThread();
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(ip, port));
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
			this.printWriter = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isOnline = false;
	}
		
	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});
		

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener( new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
		frame.pack();
		
		clientThread.start();
	}
	
	private void sendMessage() {
		String line = textField.getText();
		Message message = null;
		if(line.toLowerCase().startsWith("/quit")) {
			message = new Message(Command.QUIT, Ack.REQ, this.name, "Bye.");
			this.printWriter.println(message.serialize());
			this.printWriter.flush();
			this.clientThread.interrupt();
			System.exit(0);
		}
		if(line.toLowerCase().startsWith("/list")) {
			message = new Message(Command.List, Ack.REQ, this.name, "list 요청");
		}
		else if(isOnline) {
			message = new Message(Command.MSG, Ack.REQ, this.name, line);
		}
		else {
			message = new Message(Command.JOIN, Ack.OK, this.name, line);
		}
		this.printWriter.println(message.serialize());
		this.printWriter.flush();

		textField.setText("");
		textField.requestFocus();		
	}
	
	public class ChatClientThread extends Thread {
	
		@Override
		public void run() {
			System.out.println("run");
			Message message = null;
			
			String showMessage = "";
			while(true) {
				message = getMessage();
				System.out.println(message);
				switch(message.getCommand()) {
					case JOIN:						
						showMessage = join(message);
						break;
					case List:
						showMessage = message.getMessage();
						break;
					case MSG:
						showMessage = message.getNameMessage();
						break;
					case NOTY:
						showMessage = message.getMessage();
						break;
					case QUIT:
						isOnline = false;
						showMessage = message.getMessage();						
						break;
					default:
						showMessage = message.getMessage();
						break;
				}
				
				textArea.append(showMessage + "\r\n");
			}
		}

		private String join(Message message) {
			String showMessage = "";
			switch(message.getAck()) {
				case FAIL:
					break;
				case OK:
					isOnline = true;
					name = message.getName();
					showMessage = message.getMessage();
					break;
				case REQ:
					showMessage = message.getMessage();
					break;
				default:
					showMessage = message.getMessage();
					break;
			}
			
			return showMessage;
		}

		private Message getMessage() {
			Message message = null;
			String line;
			try {
				while(true) {
					line = bufferedReader.readLine();
					if(line != null && line.length() > 0) {
						break;
					}
				}
				message = new Message(line);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return message;
		}
	}
}
