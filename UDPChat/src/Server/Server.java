package Server;

import java.io.IOException;

//
// Source file for the server side. 
//
// Created by Sanny Syberfeldt
// Maintained by Marcus Brohede
//

import java.net.*;
//import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

	private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
	private DatagramSocket m_socket;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java Server portnumber");
			System.exit(-1);
		}
		try {
			Server instance = new Server(Integer.parseInt(args[0]));
			instance.listenForClientMessages();
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Server(int portNumber) {
		// TODO: create a socket, attach it to port based on portNumber, and assign it
		// to m_socket
		try {
			m_socket = new DatagramSocket(portNumber);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void listenForClientMessages() {
		System.out.println("Waiting for client messages... ");

		do {
			byte[] buffer = new byte[256];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			try {
				System.out.println("server receives Server54");
				m_socket.receive(p);
				String received = new String(p.getData(), 0, p.getLength());
				String[] parts = received.split("-");
				System.out.println("server has received , Server58");
				// switch-case for the type of message
				// 0 = Client wants to connect
				// 1 = Client Sends private message
				// 2 = Client Sends broadcast
				// 3 = Client wants list of Clients
				for(int i = 0; i < parts.length; i++) {
					System.out.print(parts[i]+" ");
				}
				
				int i = Integer.parseInt(parts[0]);
				switch (i) {
				case 0:
					newClient(parts[1], p);
					break;
				case 1:
					// 1.source 2.destination 3.message
					clientPrivateMessage(parts[1], parts[2], parts[3]);
					break;
				case 2:
					// 1.source 2.message
					clientBroadcast(parts[1], parts[2]);
					break;
				case 3:
					updateClientList(parts[1]);
					break;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO: Listen for client messages.
			// On reception of message, do the following:
			// * Unmarshal message
			// * Depending on message type, either
			// - Try to create a new ClientConnection using addClient(), send
			// response message to client detailing whether it was successful
			// - Broadcast the message to all connected users using broadcast()
			// - Send a private message to a user using sendPrivateMessage()
		} while (true);
	}

	public boolean addClient(String name, InetAddress address, int port) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				return false; // Already exists a client with this name
			}
		}
		m_connectedClients.add(new ClientConnection(name, address, port));
		// sendPrivateMessage("connected", name);
		return true;
	}

	public void sendPrivateMessage(String message, String name) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				c.sendMessage(message, m_socket);
			}
		}
	}

	public void broadcast(String message) {
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			itr.next().sendMessage(message, m_socket);
		}
	}

	private void updateClientList(String name) {
		sendPrivateMessage("2-" + getListOfClients(), name);
	}

	private String getListOfClients() {
		String list = "";
		for (ClientConnection c : m_connectedClients) {
			list.concat(c.getName() + "\n");
		}
		return list;
	}

	private void newClient(String name, DatagramPacket p) {
		if (addClient(name, p.getAddress(), p.getPort())) {
			sendPrivateMessage("0-1", name);
			System.out.println("Has sent connected!");
		} else {
			sendPrivateMessage("0-0", name);
			System.out.println("Has sent not connected");
		}
	}

	private void clientPrivateMessage(String source, String destination, String msg) {
		sendPrivateMessage("1-" + source + ": " + msg, destination);
	}

	private void clientBroadcast(String source, String msg) {
		broadcast("1-" + source + ": " + msg);
	}
}
