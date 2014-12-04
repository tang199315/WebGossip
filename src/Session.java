import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;


public class Session implements Runnable{
	private Socket connection;
	private BufferedReader msgIn;
	private BufferedWriter msgOut;
	
	public Session(Socket connection) throws IOException{ 
		this.connection = connection;
		System.out.println("Sesson:" + connection.getPort());
		System.out.println("Sesson Local:" + connection.getLocalPort());
		msgIn= new BufferedReader(
				new InputStreamReader(this.connection.getInputStream()));
		
		msgOut = new BufferedWriter(
				new OutputStreamWriter(this.connection.getOutputStream()));
	}
	
	public void run(){
		System.out.println("InSession");
		char escape_char = (char)3;
		//create thread for reading text from message box
		try{
			
			StringBuffer reply = new StringBuffer();
			int c;
			while(true){
				if ( msgIn.ready() && (c = msgIn.read()) != -1 ){
					reply.append((char) c);
				}
				if (reply.length()>1 && 
					reply.charAt(reply.length()-1) == escape_char){
						System.out.println( reply.substring(0, reply.length()-1));
						reply.delete(0,reply.length());
				}
				
			}
//			while (msgIn.ready() && ( c = msgIn.read()) != -1 )
//				reply.append((char) c);
//			System.out.println( reply.toString());
		}
		catch(IOException ex){
			System.out.println(ex);
		}

		
		//create thread to listen for the income message
	}
	
	public void sendMessage(String msg) throws IOException{
		//TO DO
		//Check if the other side is online
		char escape_char = (char)3;
		String username = "Leon Chan";
		String dataStr = getDateInString();
		msgOut.write( username + " " + dataStr  + "\n" +
						msg + escape_char);
		msgOut.flush();
	}

	public String getDateInString(){
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format( now );  
	}
	
}
