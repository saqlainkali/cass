import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public  class Client extends JFrame
{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message="";
	private String serverIp;
	private Socket connection;


//Constructor

	public Client(String host){
		super(" Saqlain's  Chat App CLIENT");


			serverIp=host;
			userText=new JTextField("Type Here");
			userText.setEditable(false);
			userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
			);
			add(userText,BorderLayout.NORTH);
			chatWindow = new JTextArea();
			add(new JScrollPane(chatWindow),BorderLayout.CENTER);
			setSize(300,150);
			setVisible(true);
	}

//Connect to server

	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException	eofException){
			showMessage("\nClient Treminated Connection");
		}catch(IOException ioexception){
			ioexception.printStackTrace();
		}finally{
			closeCrap();
		}
	}
//Connecting to Server
	private void connectToServer() throws IOException{
		showMessage("\n Attempting connection!  ");
		connection=new Socket(InetAddress.getByName(serverIp),6789);
		showMessage(" Connected to: "+ connection.getInetAddress().getHostName());
	}

//Setup stream and receive message
	private void setupStreams() throws IOException{
		output=new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input=new ObjectInputStream(connection.getInputStream());
		showMessage("\n Stream are now good to go");
	}

//While Chatting with Server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message=(String) input.readObject();
			showMessage("\n"+ message);
			}catch(ClassNotFoundException classnotfoundexception){
			showMessage("\n I dont know that Object type");
			}
		}while(!message.equals("SERVER - END"));
	}
//Closing the streams and sockets

	private void closeCrap(){
		showMessage("\n Closing Crap Down...\n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioexception){
			ioexception.printStackTrace();
		}
	}
//send message to server

	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - "+message);
			output.flush();
			showMessage("\nCLIENT - "+message);
		}catch(IOException ioexception){
			chatWindow.append("\n Somthing messed up sending message:");
		}
	}
//change update chat window

	private void showMessage(final String m){
		SwingUtilities.invokeLater(
			new  Runnable(){
				public void run(){
					chatWindow.append(m);
				}
			}
		);
	}

//Giving user permission to type in the box

	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(true);
				}
			}
		);
	}
}