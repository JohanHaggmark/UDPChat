package Server;

import java.util.ArrayList;
import java.util.Date;

public class Status {

	private long time;
	private long cooldownTime = 1000;
	private long lastTime = 0;
	private String m_name;
	ArrayList<ClientConnection> m_connectedClients;


	public Status(ArrayList<ClientConnection> m_connectedClients) {
		this.m_connectedClients = m_connectedClients;
		new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (m_connectedClients.size() > 0) {
					for (ClientConnection c : m_connectedClients) {
						// if this method is called again without Client has sent updateClientList.
						// isAlive will still be false
						// and therefore the connection is lost.
						if (c.isAlive) {
							c.isAlive = false;
						} else {
							m_connectedClients.remove(c);
							System.out.println("removed a client!");
							break;
						}
					}
				}
			}

		}, new Date(), 2000);
	}
}
