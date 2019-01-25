package Client;

public class UpdateParticipantList implements Runnable {

	private ServerConnection c;
	private long time;
	private long cooldownTime = 1000;
	private long lastTime = 0;
	private String m_name;

	public UpdateParticipantList(String m_name, ServerConnection c) {
		this.m_name = m_name;
		this.c = c;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			time = System.currentTimeMillis();
			if (time > lastTime + cooldownTime) {
				Message msg = new Message(m_name);
				c.sendChatMessage(msg.TranslateToGetListOfParticipant(m_name));
				lastTime = System.currentTimeMillis();
			}
		}
	}
}
