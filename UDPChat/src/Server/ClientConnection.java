/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/**
 * 
 * @author brom
 */
public class ClientConnection {
	
	static double TRANSMISSION_FAILURE_RATE = 0.0;
	
	private final String m_name;
	private final InetAddress m_address;
	private final int m_port;
	private DatagramPacket p;
	private byte[] buffer;
	private String received;
	public boolean isAlive = true;
	

	public ClientConnection(String name, InetAddress address, int port) {
		m_name = name;
		m_address = address;
		m_port = port;	
		buffer = new byte[256];
		p = new DatagramPacket(buffer, buffer.length);
		
	}

	public void sendMessage(String message, DatagramSocket socket) {
		
		Random generator = new Random();
    	double failure = generator.nextDouble();
    	
    	if (failure > TRANSMISSION_FAILURE_RATE){
    		// TODO: send a message to this client using socket.
    		buffer = message.getBytes();
    		p = new DatagramPacket(buffer, buffer.length, m_address, m_port);
    		try {
				socket.send(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		// Message got lost
    	}
		
	}

	public boolean hasName(String testName) {
		return testName.equals(m_name);
	}
	
	public String getName() {
		return m_name;
	}

}
