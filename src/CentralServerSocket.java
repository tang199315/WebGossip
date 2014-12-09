import java.net.*;
import java.io.*;

public class CentralServerSocket {
	private Socket connection2Server;
	private BufferedReader inFormServer;
	private BufferedWriter outToServer;
	private String server_ip;
	
	public CentralServerSocket(String ip){
		this.server_ip = ip;
	}
	
	public void connect() throws IOException,UnknownHostException{
		connection2Server = new Socket(InetAddress.getByName(server_ip),8000);
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
	
	public boolean login(String username, String pwd) throws IOException{
		String reply = query(username + "_" + pwd);
		if ("lol".contentEquals(reply))
			return true;
		else
			return false;
	}
	
	public boolean logout(String username)throws IOException{
		String reply = query("logout" + username);
		//to deal with the ongoing thread
		close();
		if ("loo".contentEquals(reply))
			return true;
		else
			return false;
	}
	
	public void close()throws IOException{
		outToServer.flush();
		connection2Server.close();
	}

}
