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
	private static HashMap<String, Socket> friend_status = new HashMap<String,Socket>();
	

	public static void main(String[] args){
		String username = "2011011437";
		String login_err = "Incorrect login No.";
		String login_success = "lol";
		User user = new User("2011011437");
		friend_status.put("2012011454",connection2Server);
		
		String pwd = "net2014";
		
		try{
			//initSocket();
			if ("HELO_2011011437".startsWith("HELO"))
				System.out.println("OVG");
				
			
			incomeRequest = new ServerSocket(12345);
			Listener portListener = new Listener(incomeRequest,connection2CentralServer,
										user,friend_status);
			Thread t1 = new Thread(portListener);
			t1.start();

			String server_ip = "192.168.1.100";
			connection2Server = new Socket(InetAddress.getByName(server_ip),12345);
//			inFormServer= new BufferedReader(
//					new InputStreamReader(connection2Server.getInputStream()));
			outToServer = new BufferedWriter(
					new OutputStreamWriter(connection2Server.getOutputStream()));

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
//			System.out.println("USER:" + connection2Server.getPort());
//			System.out.println("USER LOCAL:"+connection2Server.getLocalPort());
			
			Session myChat = new Session(connection2Server,connection2CentralServer,
					user, friend_status, true);
			//myChat.sayHello();
			Thread t2 = new Thread(myChat);
			t2.start();
			
			while(true){
				String msg = br.readLine();
				outToServer.write(msg+(char)3);
				outToServer.flush();
			}
		}
		catch(IOException ex){
			System.out.println(ex);
			
		}
		finally{
			try{
				if (incomeRequest!=null | connection2CentralServer!=null){
					incomeRequest.close();
					connection2CentralServer.close();
				}
				//close all active sessions
				Iterator iter = friend_status.entrySet().iterator();
				while (iter.hasNext()){
					Map.Entry<String,Socket> entry = (Map.Entry<String,Socket>) iter.next();
					Socket conn = entry.getValue();
					if (entry.getValue() != null)
						conn.close();	
				}
			}catch(IOException ex){
				System.out.println(ex);
			}

			
		}
/*		
		try {
			initSocket();
			//create a thread for port listener
			Listener portListener = new Listener(incomeRequest);
			Thread t1 = new Thread(portListener);
			
			
			//create a Thread for user to invoke a conversation
			
			connect2Server();
			if (login(username,pwd))
				System.out.println("Login Sucessfully");
			
			if (logout(username))
				System.out.println("Logout Sucessfully");
        } 
		catch (UnknownHostException ex){
			System.out.println(ex);
		}
		catch (IOException ex){ 
			System.out.println(ex);
		} 
		*/
    }
	
	public static void initSocket() throws IOException{
		incomeRequest = new ServerSocket(12345);
		connection2CentralServer = new CentralServerSocket("166.111.180.60");
	}
	
	public static void close(){
		//TODO
	}
}