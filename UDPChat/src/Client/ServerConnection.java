/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 *
 * @author brom
 */
public class ServerConnection {

	// Artificial failure rate of 30% packet loss
	static double TRANSMISSION_FAILURE_RATE = 0.0;

	private DatagramSocket m_socket = null;
	private InetAddress m_serverAddress = null;
	private int m_serverPort = -1;

	public ServerConnection(String hostName, int port) {
		m_serverPort = port;

		// TODO:
		// * get address of host based on parameters and assign it to m_serverAddress
		// * set up socket and assign it to m_socket
		try {
			m_serverAddress = InetAddress.getByName(hostName);
			m_socket = new DatagramSocket();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean handshake(String name) {
		// TODO:
		// * marshal connection message containing user name
		// * send message via socket
		// * receive response message from server
		// * unmarshal response message to determine whether connection was successful
		// * return false if connection failed (e.g., if user name was taken)
		System.out.println(m_socket.getLocalPort());
		sendChatMessage("0-" + name);
		String parts[] = receiveChatMessage();
		if (parts[1].equals("1")) {
			return true;
		}
		return false;
	}

	public String[] receiveChatMessage() {
		// TODO:
		byte[] buffer = new byte[256];
		DatagramPacket p = new DatagramPacket(buffer, buffer.length);
		try {
			System.out.println("receive");
			m_socket.receive(p);
			System.out.println("has received");
			String[] parts = unmarshal(p);

			return parts;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Note that the main thread can block on receive here without
		// problems, since the GUI runs in a separate thread

		// Update to return message contents
		return null;
	}

	public void sendChatMessage(String message) {
		Random generator = new Random();
		double failure = generator.nextDouble();

		if (failure > TRANSMISSION_FAILURE_RATE) {
			// TODO:
			// * marshal message if necessary
			// * send a chat message to the server
			
			DatagramPacket p = marshal(message);

			try {
				m_socket.send(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// Message got lost
		}
	}

	private DatagramPacket marshal(String msg) {
		byte[] buffer = new byte[256];
		buffer = msg.getBytes();
		return new DatagramPacket(buffer, buffer.length, m_serverAddress, m_serverPort);

	}

	private String[] unmarshal(DatagramPacket p) {
		String received = new String(p.getData(), 0, p.getLength());
		return received.split("-");
	}
}
