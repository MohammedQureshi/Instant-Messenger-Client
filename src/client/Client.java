package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame{
	
	/**
	 * Mohammed Faisal Qureshi
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	//Constructor
	public Client(String host) {
		super("Instant Messenger - Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(400, 700);
		setVisible(true);	
	}
	
	//Starting Program
	public void startProgram() {
		try {
			connectToServer();
			setupStreams();
			whileMessaging();
		}catch(EOFException eofException){
			showMessage("\n Client ended connection!");
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}finally {
			cleanUp();
		}
	}
	
	//Conneting to Server
	private void connectToServer() throws IOException{
		showMessage(" Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("\n Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//Setting up streams
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Connection Established \n");
	}
	
	private void whileMessaging() throws IOException{
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n"+ message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Unknown Object Type");
			}
		}while(!message.equals(" Server - END"));
	}
	
	//Cleans streams and sockets 
	private void cleanUp() {
		showMessage("\n Closing Connection");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();			
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//Sends message to server
	private void sendMessage(String message){
		try {
			output.writeObject("Client - " + message);
			output.flush();
			showMessage("\n Client - "+ message);
		}catch(IOException ioException) {
			chatWindow.append("\n Coulden't Send Message!");
		}
	}
	
	//Update chat window
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(message);
					}
				}
		);
	}
	
	//Enables and Disables Typing
	private void ableToType(final boolean typing) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(typing);
					}
				}
		);
	}
}
