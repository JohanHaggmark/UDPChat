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
			instance.listenToClientStatus();
			instance.listenForClientMessages();
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Server(int portNumber) {
		try {
			m_socket = new DatagramSocket(portNumber);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void listenForClientMessages() {
		System.out.println("Waiting for client messages... ");
		//new Thread(new ConnectionStatus(m_connectedClients)).start();
		do {
			byte[] buffer = new byte[256];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			try {
				m_socket.receive(p);
				String received = new String(p.getData(), 0, p.getLength());
				String[] parts = received.split("-");
				// switch-case for the type of message
				// 0 = Client wants to connect
				// 1 = Client Sends private message
				// 2 = Client Sends broadcast
				// 3 = Client wants list of Clients
				for (int i = 0; i < parts.length; i++) {
					System.out.print(parts[i] + " ");
				}
				System.out.println(" ");

				int i = Integer.parseInt(parts[0]);
				switch (i) {
				case 0:
					newClient(parts[1], p);
					break;
				case 1:
					// 1.source 2.destination 3.message
					clientPrivateMessage(parts[1], parts[2], parts[3]);
					clientPrivateMessage(parts[1], parts[1], parts[3]);
					break;
				case 2:
					// 1.source 2.message
					clientBroadcast(parts[1], parts[2]);
					break;
				case 3:
					updateClientList(parts[1]);
					break;
				case 4:
					disconnectUser(parts[1]);
					break;
				case 5:
					updateClientList(parts[1]);
					clientList(parts[1]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		updateClientStatus(name);
		sendPrivateMessage("2-" + getListOfClients(), name);
	}

	private String getListOfClients() {
		String list = "";
		for (ClientConnection c : m_connectedClients) {
			list += c.getName() + "\n";
		}
		return list;
	}

	private void newClient(String name, DatagramPacket p) {
		if (addClient(name, p.getAddress(), p.getPort())) {
			sendPrivateMessage("0-1", name);
		} else {
			sendPrivateMessage("0-0", name);
		}
	}

	private void clientPrivateMessage(String source, String destination, String msg) {
		sendPrivateMessage("1-" + source + ": " + msg, destination);
	}

	private void clientBroadcast(String source, String msg) {
		broadcast("1-" + source + ": " + msg);
	}
	private void disconnectUser(String name) {
		for(int i = 0; i < m_connectedClients.size(); i++) {
			if(m_connectedClients.get(i).hasName(name)) {
				m_connectedClients.get(i).sendMessage("3-", m_socket);
				m_connectedClients.remove(i);		
				break;
			}
		}
	}
	private void clientList(String name) {
		sendPrivateMessage("1-"+getListOfClients(), name);
	}	
	private void updateClientStatus(String name) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				c.isAlive = true;
			}
		}
	}
	private void listenToClientStatus() {
		new Status(m_connectedClients);
	}

}