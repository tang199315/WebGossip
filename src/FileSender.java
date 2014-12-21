import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class FileSender implements Runnable{
	private Socket fileSocket;
	private DataInputStream fis;
	private DataOutputStream fos;
	
	public FileSender(InetAddress destIP, int destPort,String filepath) throws IOException{
		fileSocket = new Socket(destIP,destPort);
		fis = new DataInputStream (
					new FileInputStream(filepath));
		fos = new DataOutputStream(
				fileSocket.getOutputStream());
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
	        		  "FileTransfer OK : )", "消息",
	              JOptionPane.CLOSED_OPTION, 0, null, null, null);
	          
			fileSocket.shutdownOutput();
	          
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
				System.out.println("Sender end");
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
