import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
	private HashMap<String, Session> friend_status;
	private User user;
	private File sendfileInfo;
	
	private TextArea textField_1;
	private TextArea textField;
	private String friend_name;
	
	
	private final String UNIT_DELIMITER = String.valueOf((char)31);
	private final char END_DELIMITER = (char)3;
	private final char CMD_DELIMITER = (char)2; 
	
	public Session(Socket connection, CentralServerSocket connection2CentralServer,
					User user, HashMap<String,Session> friend_status, boolean isActive) 
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
		//create thread for reading text from message box
		try{
			//Inform the other side my identity
			sayHello();
			
			StringBuffer reply = new StringBuffer();
			int c = 0;
			while(true){
				//Get Message
				if ( msgIn.ready() && (c = msgIn.read()) != -1)
					reply.append((char) c);
				
				//An integral message received
				if (reply.length()>1 && 
					reply.charAt(reply.length()-1) == END_DELIMITER){
					String reply_str = null;
					//Parse a command
					if (reply.charAt(0) == CMD_DELIMITER){
						reply_str = reply.toString().substring(1, reply.length()-1);
						//HELO command
						if (reply_str.startsWith("HELO")){
							//TODO
							friend_name = reply_str.split(UNIT_DELIMITER)[1];
							friend_status.put(friend_name, this); //refresh friend status
							this.setTitle(friend_name);	
						//BYEBYE command
						}else if (reply_str.startsWith("BYEBYE")){
					        JOptionPane.showOptionDialog(null,"對方己經下線", "消息",
					        		JOptionPane.CLOSED_OPTION, 0, null, null, null);
							connection.close();
							friend_status.put(friend_name, null);
							//Quit the current thread
							break; 
							
						//File request command
						}else if(reply_str.startsWith("FILEREQ")){
							//TODO
							this.setVisible(true);
							String fileName = reply_str.split(UNIT_DELIMITER)[1];
							String size = reply_str.split(UNIT_DELIMITER)[2];
							if ((JOptionPane.showOptionDialog(null,
									"您想接受文件  " + fileName + " 吗？", "消息",
									JOptionPane.YES_NO_OPTION, 0, null, null, null)) == 0){
								
								//Choose the path to save
								JFileChooser jfc = new JFileChooser();
								jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								jfc.setDialogTitle("选择保存位置");
								String save_dirpath = null;
							    if(jfc.showOpenDialog(Session.this)==JFileChooser.APPROVE_OPTION ){
							    	save_dirpath = jfc.getSelectedFile().getAbsolutePath();
									//TODO
									ServerSocket listener = new ServerSocket(0);
									acceptFileTransfer(listener.getLocalPort());
									Socket fileConnection = listener.accept(); //Wait for connection
									listener.close();

								    FileReceiver fr;
								    if (save_dirpath != null){ 
								    	fr = new FileReceiver(fileConnection,save_dirpath + File.separator + fileName );
								    }else{
								    	fr = new FileReceiver(fileConnection,fileName);
								    }
									Thread t= new Thread(fr);
									t.start(); 
									echo("System","正在接收文件 ");
							    }else{
									//Send denial message
									denyFileTransfer();
							        JOptionPane.showOptionDialog(null,"拒絕接收文件 ", "消息",
							        		JOptionPane.CLOSED_OPTION, 0, null, null, null);
							        echo("Me","拒絕接收文件 ");
							    }

							}else{
								//Send denial message
								denyFileTransfer();
						        JOptionPane.showOptionDialog(null,"拒絕接收文件 ", "消息",
						        		JOptionPane.CLOSED_OPTION, 0, null, null, null);
						        echo("Me","拒絕接收文件 ");
							}
						//File transfer acceptance command	
						}else if(reply_str.startsWith("FILEACPT")){
							//TODO: Starting Send
							this.setVisible(true);
							int port = Integer.parseInt(reply_str.split(UNIT_DELIMITER)[1]);
							FileSender fs = new FileSender(connection.getInetAddress(),port,sendfileInfo.getAbsolutePath());
							Thread t = new Thread(fs);
							t.start();
							echo("System","對方接收文件，文件正在傳輸...");
						//File transfer denial command			
						}else if(reply_str.startsWith("FILEDENY")){
							//TODO:Display Denial message
							this.setVisible(true);
					        JOptionPane.showOptionDialog(null,"對方拒絕接受文件，傳輸中斷", "消息",
					        		JOptionPane.CLOSED_OPTION, 0, null, null, null);
					        echo("System","文件傳輸中斷");
						}
					}else{
						
					//Show the Chat message to User
						reply_str = reply.toString().substring(0, reply.length()-1);
							this.setVisible(true);
							display(reply_str);
							System.out.println(reply_str);
						
					}
					//clean buffer
					reply.delete(0,reply.length()); 
				}
			}
			System.out.println("Session:Quit thread");
		}
	
		catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void closeConnection() throws IOException{
		sayBye();
		this.connection.close();
	}
	
	public boolean isActive(){ return !connection.isClosed();}
	
	public void sendData(String s) throws IOException{
		msgOut.write(s);
		msgOut.flush();
	}
	
	public void sayHello() throws IOException{ sendData (CMD_DELIMITER + "HELO" + 
			UNIT_DELIMITER + user.getName() + END_DELIMITER);}
	
	public void sayBye() throws IOException{ sendData (CMD_DELIMITER + "BYEBYE" + 
			 END_DELIMITER);}
	
	public void acceptFileTransfer(int port) throws IOException{ sendData( CMD_DELIMITER +
			"FILEACPT" +  UNIT_DELIMITER  + Integer.toString(port) +END_DELIMITER); }
	
	public void denyFileTransfer() throws IOException{ sendData(CMD_DELIMITER + 
			"FILEDENY" + END_DELIMITER); }
	
	
	public void sendMessage() throws IOException{
		//TO DO
		//Check if the other side is online
		String dataStr = getDateInString();
		
		//GUI
		String s = textField.getText();            // s is the string that tang will send
		textField.setText("");               // clear the Text Box
		if (s.length() > 0) {
			String temp = user.getName() + " " + dataStr  + "\n" + 
					s  + END_DELIMITER;
			sendData(temp);
			//Show as History
			echo("ME",s);
		}

	}
	
	public void echo(String ID, String data){
		String dataStr = getDateInString();
		String temp = ID + " " + dataStr  + "\n" + data ;
		display(temp);
	}
	
	public void sendFileTransferRequest(String filename, long size)throws IOException{
		//TO DO
		//Check if the other side is online
		if (filename.length() > 0) {
			String s = CMD_DELIMITER + "FILEREQ" + UNIT_DELIMITER + 
			filename + UNIT_DELIMITER + Long.toString(size) + END_DELIMITER;
			//TCP/IP
			sendData(s);
		}
	}

	public String getDateInString(){
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format( now );  
	}
	
//GUI==================================
	
	public void display(String sString) {
		textField_1.setText(textField_1.getText() + sString + "\n\n");		
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
					ex.printStackTrace();
				}			
			}
		});
		
		JButton button_1 = new JButton("\u6587\u4EF6");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
			    if(jfc.showOpenDialog(Session.this)==JFileChooser.APPROVE_OPTION ){
			    	String filepath = jfc.getSelectedFile().getAbsolutePath();
			    	sendfileInfo = new File(filepath);
			    	//Send file transfer request
			    	try{
						echo("System","等待對方接收文件 ");
				    	sendFileTransferRequest(sendfileInfo.getName(),sendfileInfo.length());
			    	}
			    	catch(IOException ex){
						ex.printStackTrace();
			    	}

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
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(button_1)
									.addComponent(button))))
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
							.addComponent(button)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(button_1)
							)))
		);
		getContentPane().setLayout(groupLayout);
	}
	
}
