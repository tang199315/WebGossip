import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Application {
	
	private static Socket connection2Server;
	private static BufferedReader inFormServer;
	private static BufferedWriter outToServer;
	
	private static CentralServerSocket connection2CentralServer;
	private static ServerSocket incomeRequest;
	private static User user;
	private static HashMap<String, Session> friend_status = new HashMap<String,Session>();
	

	public static void main(String[] args){
		String login_err = "Incorrect login No.";
		
		friend_status.put("2012011454",null);
		String pwd = "net2014";
		
		try{
			//Connect Central Server
			connection2CentralServer = new CentralServerSocket();
			
			//Login in
			user = new User("201101144","net2014");
			Login ln = new Login();
			
			while (!connection2CentralServer.login(user)){
				ln.reset();
				ln.showError();
				while(!ln.getJudge()); //waiting for login
				System.out.println(ln.getUsername());
				System.out.println(ln.getPWD());
				user = new User(ln.getUsername(),ln.getPWD());
				//user = new User("2011011437","net2014");  //OK
			}
			
			System.out.println("login OK");
			ln.setVisible(false);
			
			//===========
			incomeRequest = new ServerSocket(12345);
			Listener portListener = new Listener(incomeRequest,connection2CentralServer,
										user,friend_status);
			Thread t1 = new Thread(portListener);
			t1.start();
			
			String server_ip = "127.0.0.1";
			connection2Server = new Socket(InetAddress.getByName(server_ip),12345);
			
			Session myChat = new Session(connection2Server,connection2CentralServer,
					user, friend_status, true);
			myChat.sayHello();
			Thread t2 = new Thread(myChat);
			t2.start();
			
			
			//TODO use while to wait??
			while(true);
			
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			try{
				if (incomeRequest!=null ){
					incomeRequest.close();
				}
				if (connection2CentralServer!=null){
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
	
	public static void close(){
		//TODO
	}
}