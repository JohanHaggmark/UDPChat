package Server;

import java.util.ArrayList;

import Client.Message;

public class ConnectionStatus implements Runnable{

	private long time;
	private long cooldownTime = 1000;
	private long lastTime = 0;
	private String m_name;
	ArrayList<ClientConnection> m_connectedClients;
	
	public ConnectionStatus(ArrayList<ClientConnection> m_connectedClients) {
		this.m_connectedClients = m_connectedClients;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			//System.out.println(m_connectedClients.size());
			//When client asking for list, its isAlive i set to true.
			time = System.currentTimeMillis();
			if ((time > lastTime + cooldownTime) && (m_connectedClients.size() > 0)) {
				System.out.println("tread");
				for(ClientConnection c : m_connectedClients) {
					//if this method is called again without Client has sent updateClientList. isAlive will still be false
					//and therefore the connection is lost.
					if(c.isAlive) {
						c.isAlive = false;
					} else {
						m_connectedClients.remove(c);
						System.out.println("removed a client!");
						break;
					}
				}
				lastTime = System.currentTimeMillis();
			}
		}
	}

}
