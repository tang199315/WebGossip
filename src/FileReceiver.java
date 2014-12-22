import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class FileReceiver implements Runnable{
	private DataInputStream fis;
	private DataOutputStream fos;
	private Socket fileSocket;
	private String filepath;
	
	public FileReceiver(Socket connection,String filepath) throws IOException{
		this.filepath = filepath;
		fileSocket = connection;
		fis = new DataInputStream(
				fileSocket.getInputStream());
		fos = new DataOutputStream(
				new FileOutputStream(filepath));
	}
	
	public void run(){
		int len=0;
		byte[] data = new byte[1024];
		try{
			while( (len = fis.read(data)) != -1){
				fos.write(data, 0, len);
				fos.flush();
			}
	          JOptionPane.showOptionDialog(null,
	        		  "FileRecieve OK \n" + "Saved to: "+ filepath, "消息",
	              JOptionPane.CLOSED_OPTION, 0, null, null, null);
			fileSocket.shutdownInput();
	          
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{

				try{			
					if (fileSocket != null)
						fileSocket.close();
					if (fis != null)
						fis.close();
					if (fos != null)
						fos.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
		}
	}

}
