import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Listener implements Runnable {
	private ServerSocket incomeRequest;
	private CentralServerSocket connection2CentralServer;
	private HashMap<String, Session> friend_status;
	private User user;
	private boolean isActive;
	
	public Listener(ServerSocket incomeRequest, CentralServerSocket connection2CentralServer,
					User user, HashMap<String, Session> friend_status){
		this.isActive = true;
		this.incomeRequest = incomeRequest;
		this.connection2CentralServer = connection2CentralServer;
		this.user = user;
		this.friend_status = friend_status;
	}
	
	public void run(){
			System.out.println("getOne");
			Socket connection = null;
			try{
				while(isActive){
					//System.out.println("happy");
					connection = incomeRequest.accept();	
					//create a new thread for a new income connection2Friend
					Session new_session = new Session(connection,connection2CentralServer,
														user, friend_status,false);
					Thread t = new Thread(new_session);
					t.start();
				}
				//release resource
				incomeRequest.close();
				System.out.println("Listener: port release");
			}
			catch(IOException ex){
				ex.printStackTrace();
			}// end catch
			
			finally{
				try{if (connection!=null) connection.close();}
				catch(IOException ex){ex.printStackTrace();}
			}
		}
	
	public void shutdown(){
		isActive = false;
		System.out.println("shutdown Call");
	}

	
	
	

}
