/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author brom
 */
public class ChatGUI extends JFrame implements ActionListener{
    // This class should be sufficiently complete, although
    // you are allowed to change it if you wish.

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final JTextField m_chatInput;
    private final JTextArea m_chatOutput;
    
    //Added for this assignment:
    private final JTextArea m_participants;

    public ChatGUI(ActionListener listener, String userName) {
	setSize(700,500);
	setTitle("Chat client for "+userName);

	m_chatOutput = new JTextArea(10,15);
	m_chatInput = new JTextField(20);
	
	m_participants = new JTextArea(10,1);
	m_participants.setBounds(0, 0, 70, 500);;

	
	m_participants.setWrapStyleWord(true);
	m_participants.setLineWrap(true);
	m_participants.setEditable(false);
	m_participants.setBackground(Color.DARK_GRAY);
	m_participants.setForeground(Color.PINK);
	
	m_chatOutput.setWrapStyleWord(true);
	m_chatOutput.setLineWrap(true);
	m_chatOutput.setEditable(false);
	m_chatOutput.setBackground(Color.BLACK);
	m_chatOutput.setForeground(Color.GREEN);

	Container pane = getContentPane();
	pane.add(m_chatOutput, BorderLayout.NORTH);
	pane.add(m_chatInput, BorderLayout.SOUTH);
	pane.add(new JScrollPane(m_chatOutput), BorderLayout.CENTER);
	pane.add(m_participants, BorderLayout.EAST);

	m_chatInput.addActionListener(listener);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setVisible(true);
    }

    public void displayMessage(String message) {
	m_chatOutput.append(message+"\n");
    }
    
    public void displayParticipants(String message) {
    	m_participants.setText(message);
    }

    public String getInput() {
	return m_chatInput.getText();
    }

    public void clearInput() {
	m_chatInput.setText("");
    }
    
    public void clearClientList() {
    	m_participants.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	System.out.println(e.getActionCommand());
    }
}
