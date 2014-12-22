import java.net.*;
import java.io.*;

public class CentralServerSocket {
	private Socket connection2Server;
	private BufferedReader inFormServer;
	private BufferedWriter outToServer;
	private String server_ip;
	private int port = 8000;
	
	public CentralServerSocket() throws IOException,UnknownHostException {
		this.server_ip = "166.111.180.60";
		this.port = 8000;
		
		connection2Server = new Socket(InetAddress.getByName(server_ip),port);
		inFormServer= new BufferedReader(
				new InputStreamReader(connection2Server.getInputStream()));
		
		outToServer = new BufferedWriter(
				new OutputStreamWriter(connection2Server.getOutputStream()));

	}
	
	public CentralServerSocket(String ip, int port) throws IOException,UnknownHostException {
		this.server_ip = ip;
		this.port = port;
		
		connection2Server = new Socket(InetAddress.getByName(server_ip),port);
		inFormServer= new BufferedReader(
				new InputStreamReader(connection2Server.getInputStream()));
		
		outToServer = new BufferedWriter(
				new OutputStreamWriter(connection2Server.getOutputStream()));
		
	}
	

	public String query(String query) throws IOException{
		outToServer.write(query);
		outToServer.flush();
		StringBuffer reply = new StringBuffer();
		int c;
		while ( !inFormServer.ready()){} //wait
		while (inFormServer.ready() && ( c = inFormServer.read()) != -1 )
			reply.append((char) c);
		return reply.toString();
	}
	
	public boolean login(User user) throws IOException{
		String reply = query(user.getName() + "_" + user.getPWD());
		if ("lol".contentEquals(reply))
			return true;
		else
			return false;
	}
	
	public boolean logout(User user)throws IOException{
		String reply = query("logout" + user.getName());
		//to deal with the ongoing thread
		close();
		if ("loo".contentEquals(reply)){
			//TODO:remove
			System.out.println("logout OK");
			return true;
		}
		else
			return false;
	}
	
	public String isOnline(String friend_name) throws IOException{
		String reply = query("q" + friend_name);
		if ("n".contentEquals(reply) || "Incorrect No.".contentEquals(reply))
			//TODO: Remove
			return "192.168.1.12";
		else
			return reply;
		

	}
	
	public void close()throws IOException{
		if (connection2Server != null){
			outToServer.flush();
			connection2Server.close();
		}
	}

}
