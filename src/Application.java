import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.List;

import javax.swing.JList;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.SystemColor;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Application extends JFrame {
	
	private Socket connection2Server;
	private BufferedReader inFormServer;
	private BufferedWriter outToServer;
	
	private CentralServerSocket connection2CentralServer;
	private ServerSocket incomeRequest;
	private User user;
	private HashMap<String, Session> friend_status = new HashMap<String,Session>();
	
	private JPanel contentPane;
	private List list = new List();    //列表
	private JButton button = new JButton("\u767B\u51FA");   //登出按钮
	
	
	public Application(){
		//UI
		GUIinit();
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){               //设置双击事件
					String selectedFriend = list.getSelectedItem();   //可以得到双击后选中的好友的学号
					
					//TODO
					System.out.println(selectedFriend);
					
					Session currentSession = null;
					//If there is a ongoing session run in Deamon mode, set visible
					
					if( (currentSession = friend_status.get(selectedFriend) )!=null){
						currentSession.setVisible(true);
					}else{
						//Try to init a new session
						String IP=null;
						try{
							if( (IP = connection2CentralServer.isOnline(selectedFriend)) != null){
								System.out.println("friend ip" + IP);
								//Set up new session
								Session newSession = new Session(new Socket(InetAddress.getByName(IP),0),
										connection2CentralServer,user, friend_status, true);
								//Refresh friend list
								friend_status.put(selectedFriend,newSession);
								Thread t = new Thread(newSession);
								t.start();
							}
							else{
								//friend Offline warning
						         JOptionPane.showOptionDialog(null,
						        		  "朋友不在線", "消息",
						              JOptionPane.CLOSED_OPTION, 0, null, null, null);
							}
						}
						catch (IOException ex){
							ex.printStackTrace();
						}

						
					}
				}
			}
		});
		
		//init event Listener
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				/**
				//添加点击小红叉响应事件在这里
				 * 
				 */	
			}
		});
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {     //登出响应事件
				/**
				//添加按登出响应事件在这里
				 * 
				 */				
			}
		});
	
		try{
			initFriendList();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		
		this.setVisible(false);
		
	}

	public static void main(String[] args) {
			//Launch Application
			Application app = new Application();
			app.appStart();

    }
	
	public void appStart(){
		try{
			//Connection to CentralCenter
			connection2CentralServer = new CentralServerSocket();
			
			//Login in
			User user_temp = new User("xxx","xxx");
			Login ln = new Login();
			while (!connection2CentralServer.login(user_temp)){
				ln.reset();
				ln.showError();
				while(!ln.getJudge()){Thread.sleep(20);} //waiting for login
				user_temp = new User(ln.getUsername(),ln.getPWD());
			}
			this.user = user_temp;
			System.out.println("login OK");
			ln.setVisible(false);
			
			//Init network socket
			incomeRequest = new ServerSocket(12345);
			Listener portListener = new Listener(incomeRequest,connection2CentralServer,
										user,friend_status);
			Thread t1 = new Thread(portListener);
			t1.start();
			
			this.setVisible(true);
			
			//TODO testing
			System.out.println(connection2CentralServer.isOnline("2011011437"));	
			//TODO use while to wait??
			while(true);

		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}
		finally{
			try{
				if (incomeRequest!=null ){
					incomeRequest.close();
				}
				if (connection2CentralServer!=null){
					connection2CentralServer.logout(user);
					connection2CentralServer.close();
				}
				//close all active sessions
				Iterator iter = friend_status.entrySet().iterator();
				while (iter.hasNext()){
					Map.Entry<String,Session> entry = (Map.Entry<String,Session>) iter.next();
					Session session = entry.getValue();
					if (entry.getValue() != null)
						session.closeConnection();
	
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		

		
	} 

	
	public void close(){
		//TODO
	}

/**GUI Logic=================================*/

	
	/**得到列表的内容*/
	public void initFriendList() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("S.txt"));  //读文本，文本的名字是S.txt,"\"比较特殊，
                                                                               //所以不是123.txt.
        String data = br.readLine();  //从S。txt里面读出来的一行
		while( data!=null){    //while用于把列表内容从文本里面全读出来
		      list.add(data);      //加到列表里面
		      friend_status.put(data, null);
		      data = br.readLine(); //接着读下一行  
		}	
	}
	
	
	//GUI init
	public void GUIinit(){ 

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 321, 318);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setText("\u597D\u53CB\u5217\u8868");
		editorPane.setBackground(SystemColor.menu);
		editorPane.setEditable(false);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(list, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
					.addComponent(button)
					.addGap(41))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(33)
					.addComponent(editorPane, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(203, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(button)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(editorPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(list, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}