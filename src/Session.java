import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Button;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.TextField;

import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Session extends JDialog implements Runnable{
	private Socket connection;
	private CentralServerSocket connection2CentralServer;
	private BufferedReader msgIn;
	private BufferedWriter msgOut;
	private HashMap<String, Socket> friend_status;
	private User user;
	
	private TextArea textField_1;
	private TextArea textField;
	private String friend_name;
	
	public Session(Socket connection, CentralServerSocket connection2CentralServer,
					User user, HashMap<String,Socket> friend_status, boolean isActive) 
					throws IOException{
		// TCP/IP Connection init 
		this.connection = connection;
		this.connection2CentralServer = connection2CentralServer;
		this.user = user;
		this.friend_status = friend_status;
		//TODO TEST
		System.out.println("Sesson:" + connection.getPort());
		System.out.println("Sesson Local:" + connection.getLocalPort());
		
		msgIn= new BufferedReader(
				new InputStreamReader(this.connection.getInputStream()));
		
		msgOut = new BufferedWriter(
				new OutputStreamWriter(this.connection.getOutputStream()));
		
		//GUI init
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
					setVisible(false);
			}
		});
		initGUI();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(isActive);
	}
	
	
	public void run(){
		System.out.println("InSession");
		char escape_char = (char)3;
		//create thread for reading text from message box
		try{
			sayHello();
			
			StringBuffer reply = new StringBuffer();
			int c;
			while(true){
				//Get Message
				if ( msgIn.ready() && (c = msgIn.read()) != -1 )
					reply.append((char) c);
				
				//Parse Message
				if (reply.length()>1 && 
					reply.charAt(reply.length()-1) == escape_char){
					String reply_str = reply.toString().substring(0, reply.length()-1);
						if (reply_str.startsWith("HELO")){
							//TODO
							friend_name = reply_str.split("_")[1];
							friend_status.put(friend_name, connection); //refresh friend status
							this.setTitle(friend_name);
							
						}else if (reply.toString().startsWith("BYEBYE")){
							//TODO
							
						}else{
							//Display msg
							if (this.isVisible()==false)
								this.setVisible(true);
							display(reply_str);
							System.out.println(reply_str);
						}
						reply.delete(0,reply.length()); //clean buffer
				}
			}
		}
		
		catch(IOException ex){
			System.out.println(ex);
		}
	}
	
	public void closeConnection() throws IOException{
		this.connection.close();
		
	}
	
	public void sayHello() throws IOException{
		char escape_char = (char)3;
		String s = "HELO_" + user.getName() + escape_char;
		msgOut.write(s);
		msgOut.flush();
		
	}
	public void sendMessage() throws IOException{
		//TO DO
		//Check if the other side is online
		char escape_char = (char)3;
		String dataStr = getDateInString();
		
		//GUI
		String s = textField.getText();            // s is the string that tang will send
		textField.setText("");               // clear the Text Box
		if (s.length() > 0) {
			s = user.getName() + " " + dataStr  + "\n" + 
					s  + escape_char;
		}
		//TCP/IP
		msgOut.write(s);
		msgOut.flush();
		
	}

	public String getDateInString(){
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format( now );  
	}
	
//GUI==================================
	
	public void display(String sString) {
		textField_1.setText(textField_1.getText() + sString + "\n");		
	}
	
	public void initGUI(){                        
		setBounds(100, 100, 583, 432);
		{
			textField = new TextArea();
		}
		{
			textField_1 = new TextArea();
			textField_1.setBackground(Color.WHITE);
			textField_1.setEditable(false);
		}
		
		JButton button = new JButton("\u53D1\u9001");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					sendMessage();
				}catch(IOException ex){
					//TODO
				}			
			}
		});
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 557, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 445, GroupLayout.PREFERRED_SIZE)
							.addGap(22)
							.addComponent(button)))
					.addGap(10))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(24)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(50)
							.addComponent(button))))
		);
		getContentPane().setLayout(groupLayout);
	}
	
}
