import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JEditorPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends JFrame {
	//Central host 
	private CentralServerSocket connection2CentralServer;
	private Listener portListener;
	private User user;
	private JPanel contentPane;
	private HashMap<String, Session> friend_status = new HashMap<String,Session>();
	private List list = new List();
	
	public Application(){
		//UI init
		GUIinit();
		//Window Close Handler
		initEventHandler();
	}

	public static void main(String[] args) {
			//Launch Application
			Application app = new Application();
			app.appStart();
    }
	
	public void appStart(){
		try{
			//Establish connection to CentralCenter
			connection2CentralServer = new CentralServerSocket();
			//Login in
			User user_temp = new User("xxx","xxx");
			Login ln = new Login();
			while (!connection2CentralServer.login(user_temp)){
				ln.reset();
				ln.showError();
				while(!ln.getJudge()){
					try{
						Thread.sleep(20);
					}catch(InterruptedException ex){}
				} //waiting for login
				user_temp = new User(ln.getUsername(),ln.getPWD());
			}
			this.user = user_temp;
			System.out.println("login OK");
			ln.setVisible(false);
			//Attach Socket on port 12345 to listen income request 
			try{
				portListener = new Listener(new ServerSocket(12345),
					connection2CentralServer,user,friend_status);
				Thread t1 = new Thread(portListener);
				t1.start();
			}
			catch(IOException ex){
				showErrorMsg("网络端口port 12345被其他程序占用\n请先关闭相关程序!");
				System.exit(1);
			}
			this.setVisible(true);
			while(true);
		}
		catch(IOException ex){
			showErrorMsg("无法连接至中央服务器");
			System.exit(1);
		}
	}
	
	//Application closing handler
	public void initEventHandler(){
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if ((JOptionPane.showOptionDialog(null,
						"要关闭程序吗?", "消息",
						JOptionPane.YES_NO_OPTION, 0, null, null, null)) == 0){
					releaseResources();
					System.exit(1);
				}
			}
		});
		
		//List clicked handler
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){   //设置双击事件
					String selectedFriend = list.getSelectedItem();   //可以得到双击后选中的好友的学号
					Session currentSession = friend_status.get(selectedFriend);
					//If there is a ongoing session run in Deamon mode, set visible
					if( currentSession!=null && currentSession.isActive()){
						currentSession.setVisible(true);
					}else{
						//Try to init a new session
						String IP=null;
						try{
							if( (IP = connection2CentralServer.isOnline(selectedFriend)) != null){
								System.out.println("friend ip" + IP);
								//Set up new session
								try{
									Session newSession = new Session(
											new Socket(InetAddress.getByName(IP),12345),
											connection2CentralServer,user, friend_status, true);
									//Refresh friend list
									friend_status.put(selectedFriend,newSession);
									Thread t = new Thread(newSession);
									t.start();
								}catch(IOException ex){
									showErrorMsg("无法和对方连立连接!");
								}
							}
							else{
								//friend Offline warning
						         JOptionPane.showOptionDialog(null,
						        		  "朋友不在线", "消息",
						              JOptionPane.CLOSED_OPTION, 0, null, null, null);
							}
						}
						catch (IOException ex){
							showErrorMsg("无法连接至中央服务器");
							System.exit(1);
						}
					}
				}
			}
		});
	}
	
	public void releaseResources(){
		try{
			if (portListener!=null ){
				portListener.shutdown();
			}
			if (connection2CentralServer!=null){
				connection2CentralServer.logout(user);
				connection2CentralServer.close();
			}
			//close all active sessions
			Iterator<Entry<String, Session>> iter = friend_status.entrySet().iterator();
			while (iter.hasNext()){
				Map.Entry<String,Session> entry = (Map.Entry<String,Session>) iter.next();
				Session session = entry.getValue();
				if (session != null && session.isActive())
					session.closeConnection();
			}
			System.out.println("clear");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void showErrorMsg(String msg){
        JOptionPane.showOptionDialog(null,
      		  msg, "ERROR",
            JOptionPane.CLOSED_OPTION, 0, null, null, null);
	}
	
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
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(editorPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(list, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		
		//Init Friend List
		try{
			BufferedReader br = new BufferedReader(
					new FileReader("FriendList.txt"));

			String data = br.readLine();  //从S。txt里面读出来的一行
			while( data!=null){    //while用于把列表内容从文本里面全读出来
				list.add(data);      //加到列表里面
				friend_status.put(data, null);
				data = br.readLine(); //接着读下一行  
			}
		}catch (FileNotFoundException ex){
			showErrorMsg("無法讀取朋友列表FriendList.txt\n請確保FriendList.txt和程序在同一目錄下");
			System.exit(1);
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		
		this.setVisible(false);
	}
}