package Client;

import java.awt.event.*;
//import java.io.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Client implements ActionListener {

	private String m_name = null;
	private final ChatGUI m_GUI;
	private ServerConnection m_connection = null;

	public static void main(String[] args) {
		System.out.println("starting client");

		if (args.length < 3) {
			System.err.println("Usage: java Client serverhostname serverportnumber username");
			System.exit(-1);
		}

		try {
			Client instance = new Client(args[2]);
			instance.connectToServer(args[0], Integer.parseInt(args[1]));
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Client(String userName) {
		m_name = userName;

		// Start up GUI (runs in its own thread)
		m_GUI = new ChatGUI(this, m_name);
	}

	private void connectToServer(String hostName, int port) {
		m_GUI.displayMessage("hello");
		// Create a new server connection
		m_connection = new ServerConnection(hostName, port);
		m_GUI.displayMessage("handshake with server");
		if (m_connection.handshake(m_name)) {
			m_GUI.displayMessage("Connected!");
			//UpdateParticipantList upl = new UpdateParticipantList(m_name, m_connection);
			new Thread(new UpdateParticipantList(m_name, m_connection)).start();		
			listenForServerMessages();
		} else {
			m_GUI.displayMessage("Not Connected");
			System.err.println("Unable to connect to server");
		}
	}

	private void listenForServerMessages() {
		// Use the code below once m_connection.receiveChatMessage() has been
		// implemented properly.
		do {
			String[] parts = m_connection.receiveChatMessage();
			for(int i = 0; i < parts.length;i++) {
				System.out.print(parts[i]);
			}

			// switch-case for the type of message
			// 1 = private message
			// 2 = list of clients
			// 
			int i = Integer.parseInt(parts[0]);
			switch (i) {
			case 1:
				m_GUI.displayMessage(parts[1]);
				break;
			case 2:
				m_GUI.displayParticipants(parts[1]);
				break;
			case 3:		
				m_connection = null;
				m_GUI.displayMessage("disconnected!");
				m_GUI.clearClientList();
			}
		} while (true);
	}

	// Sole ActionListener method; acts as a callback from GUI when user hits enter
	// in input field
	@Override
	public void actionPerformed(ActionEvent e) {
		// Since the only possible event is a carriage return in the text input field,
		// the text in the chat input field can now be sent to the server.
		// m_connection.sendChatMessage(m_GUI.getInput());
		Message msg = new Message(m_name);
		m_connection.sendChatMessage(msg.getTranslattionFromInputToMessage(m_GUI.getInput()));
		m_GUI.clearInput();
	}
}
