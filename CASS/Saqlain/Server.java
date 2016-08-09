import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;

public  class Server extends JFrame
{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;


//Constructor

	public  Server()
	{
		super("Saqlain's Chat App");
 		
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

		add(userText, BorderLayout.NORTH);
		chatWindow=new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}

//setup and run the Server

	public void startRunning(){
	try{
		server=new ServerSocket(6789,100);
		while(true)
		{
			try{

//Connect and have Conversation
				waitForConnection();
				setupStreams();
				whileChatting();
			}
			catch(EOFException eofexception){
				showMessage("\n Server ended the connection!  ");
			}
			finally{
				closeCrap();
			}
		}
	}catch(IOException ioexception){
		ioexception.printStackTrace();
	}
	}

//Wait for Connection the Display connection Information

	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect......\n");
		connection=server.accept();
		showMessage("Not connected to  "+connection.getInetAddress().getHostName());
	}

//get stream to send and recieve data
	
	private void setupStreams() throws IOException{
		output=new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input=new ObjectInputStream(connection.getInputStream());
		showMessage("\n Stream are now setup!  \n");
	}

//During the chat conversation

	private void whileChatting() throws IOException{
		String message= "You are now Connected! ";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message=(String) input.readObject();
				showMessage("\n"+ message);
			}catch(ClassNotFoundException classnotfoundexception){
				showMessage("\n idk wtf that user send!");
			}
		}while(!message.equals("CLIENT - END"));
	}

//close stream and socket after you are done chatting

	private void closeCrap(){
		showMessage("\n Closing connection \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioexception){
			ioexception.printStackTrace();
		}
	}

//send message to  a cient

	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - "+ message);
			output.flush();
			showMessage("\nSERVER - "+ message);
		}catch(IOException ioexception){
			chatWindow.append("\n ERROR:DUDE I CANT SEND MESSAGE");
		}
	}

//Update chat Window

	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);
	}

//Let the user type stuff into there  box
 
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
}