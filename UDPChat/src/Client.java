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
			System.out.println("received , client57");

			// switch-case for the type of message
			// 1 = private message
			// 2 = list of clients
			int i = Integer.parseInt(parts[0]);
			switch (i) {
			case 1:
				m_GUI.displayMessage(parts[1]);
				break;
			case 2:
				m_GUI.displayParticipants(parts[1]);
				break;

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
		String message = m_GUI.getInput();
		if (message.startsWith("/tell ")) {
			message = message.substring(6);

			// private message
			String[] parts = message.split(", ");
			if(parts.length != 1) {
				String dest_name = message.substring(0, parts[0].length());
				message = message.substring(parts[0].length()+1);
				m_connection.sendChatMessage("1-" + m_name + "-" + dest_name + "-" + message);
			}
		} else {
			// broadcast
			m_connection.sendChatMessage("2-" + m_name + "-" + message);
			
		}
		m_GUI.clearInput();
	}
}
