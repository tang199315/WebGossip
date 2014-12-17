import java.net.*;
import java.io.*;
import java.util.*;

public class Listener implements Runnable {
	private ServerSocket incomeRequest;
	private CentralServerSocket connection2CentralServer;
	private HashMap<String, Session> friend_status;
	private User user;
	
	public Listener(ServerSocket incomeRequest, CentralServerSocket connection2CentralServer,
					User user, HashMap<String, Session> friend_status){
		this.incomeRequest = incomeRequest;
		this.connection2CentralServer = connection2CentralServer;
		this.user = user;
		this.friend_status = friend_status;
	}
	
	public void run(){
	try{
		while(true){
			System.out.println("getOne");
			Socket connection = null;
			try{
				while(true){
					//System.out.println("happy");
					connection = incomeRequest.accept();	

					//create a new thread for a new income connection2Friend
					Session new_session = new Session(connection,connection2CentralServer,
														user, friend_status,false);
					Thread t = new Thread(new_session);
					t.start();
				}

			}
			catch(IOException ex){
				System.out.println(ex);
			}// end catch
			finally{
				if (connection!=null) connection.close();
			}
		}
	}
	catch(IOException ex){
		//Desired Port may be occupied 
		System.out.println(ex);
	}
	
}
	
	
	

}
