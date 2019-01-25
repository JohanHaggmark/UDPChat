package Client;

public class Message {

	private String m_name;

	public Message(String m_name) {
		this.m_name = m_name;
	}

	public String getTranslattionFromInputToMessage(String input) {
		if (input.startsWith("/tell ")) {
			input = input.substring(6);
			// private message
			String[] parts = input.split(", ");
			if (parts.length != 1) {
				String dest_name = input.substring(0, parts[0].length());
				input = input.substring(parts[0].length() + 1);
				return ("1-" + m_name + "-" + dest_name + "-" + input);
			}
		} else if (input.startsWith("/quit")) {
				return ("4-" + m_name);
		} else if (input.startsWith("/list")) {
				return ("5-"+ m_name);
		} else {
			// broadcast
			return ("2-" + m_name + "-" + input);
		}
		return "";
	}

	public String TranslateToGetListOfParticipant(String m_name) {
		return ("3-" + m_name);
	}
}
