import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;;

public class Application {
	
	private static Socket connection2Server;
	private static BufferedReader inFormServer;
	private static BufferedWriter outToServer;
	private static ServerSocket incomeRequest;
	private static User user;
	
	
	private static Hashtable friend_status = new Hashtable<String,Integer>();

	public static void main(String[] args){
		String username = "2011011437";
		String login_err = "Incorrect login No.";
		String login_success = "lol";
		friend_status.put("2012011454",-1);
		String pwd = "net2014";
		
		
		try{
			incomeRequest = new ServerSocket(12345);
			Listener portListener = new Listener(incomeRequest);
			Thread t1 = new Thread(portListener);
			t1.start();
			
			t1.

			String server_ip = "192.168.1.102";
			connection2Server = new Socket(InetAddress.getByName(server_ip),12345);
//			inFormServer= new BufferedReader(
//					new InputStreamReader(connection2Server.getInputStream()));
			outToServer = new BufferedWriter(
					new OutputStreamWriter(connection2Server.getOutputStream()));

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("USER:" + connection2Server.getPort());
			System.out.println("USER LOCAL:"+connection2Server.getLocalPort());
			
			while(true){
				String msg = br.readLine();
				outToServer.write(msg+(char)3);
				outToServer.flush();
			}
			

			
		}
		catch(IOException ex){
			System.out.println(ex);
			
		}

		
		
		
/*		
		try {
			initSocket();
			//create a thread for port listener
			Listener portListener = new Listener(incomeRequest);
			Thread t1 = new Thread(portListener);
			
			
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
		connect2Server();
	} 
	
	public static void connect2Server() throws IOException,UnknownHostException{
		String server_ip = "166.111.180.60";
		
		connection2Server = new Socket(InetAddress.getByName(server_ip),8000);
		inFormServer= new BufferedReader(
				new InputStreamReader(connection2Server.getInputStream()));
		
		outToServer = new BufferedWriter(
				new OutputStreamWriter(connection2Server.getOutputStream()));
	}
	public static String queryServer(String query) throws IOException{
		outToServer.write(query);
		outToServer.flush();
		
		StringBuffer reply = new StringBuffer();
		int c;
		while ( !inFormServer.ready()){} //wait
		while (inFormServer.ready() && ( c = inFormServer.read()) != -1 )
			reply.append((char) c);
		return reply.toString();
		
	}

	public static boolean login(String username, String pwd) throws IOException{
		String reply = queryServer(username + "_" + pwd);
		if ("lol".contentEquals(reply))
			return true;
		else
			return false;
		
	}
	public static boolean logout(String username)throws IOException{
		String reply = queryServer("logout" + username);
		//to deal with the ongoing thread
		connection2Server.close();
		
		if ("loo".contentEquals(reply))
			return true;
		else
			return false;
	}
}