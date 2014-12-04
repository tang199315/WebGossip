import java.net.*;
import java.io.*;

public class Listener implements Runnable {
	private ServerSocket incomeRequest;
	
	public Listener(ServerSocket incomeRequest){
		this.incomeRequest = incomeRequest;
	}
	
	public void run(){
	try{
		while(true){
			System.out.println("getOne");
			Socket connection = null;
			try{
				while(true){
					connection = incomeRequest.accept();	
					//create a new thread for a new income connection2Friend
					Session new_session = new Session(connection);
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
