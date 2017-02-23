/*=========================================================*/
/*       					         					   */ 
/*	          Peer As a Server 					           */
/*						       							   */
/*=========================================================*/

//PeerClientServer Implementation

package Peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * @author Suraj & Lawrence
 * PeerClientServer Implementation
 * task: sends file in peer_client_server to requesting peer
 */
public class PeerClientServer extends UnicastRemoteObject implements PeerClientServerIF {

	protected PeerClientServer() throws RemoteException {
		super();
	}
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Suraj";
	}
	/**
	 * task: sends a requested file to the requesting peer by converting
	 * 		the file to message stream using 'FileInputStream' and calling
	 * 		'c.acceptFile(f1.getName(), mydata, mylen)'
	 * @param c: requesting peer object
	 * @param file: filename of the file to be sent from peer server to requesting peer 
	 * @return returns true if file is sent successfully
	 * @throws RemoteException
	 */
	public synchronized boolean sendFile(PeerClientIF c, String file) throws RemoteException{
		/*
		 * Sending The File...
		 * file: receives the filename that was sent to requesting peer from central server
		 * c: requesting Peer client
		 */
		 try{
			 File f1 = new File(file);			 
			 FileInputStream in = new FileInputStream(f1);			 				 
			 byte[] mydata = new byte[1024*1024];						
			 int mylen = in.read(mydata);
			 while(mylen>0){
				 if(c.acceptFile(f1.getName(), mydata, mylen)){
					 System.out.println("File '"+file+"' has been sent to Requesting Peer: "+c.getName());
					 mylen = in.read(mydata);
				 } else {
					 System.out.println("Fault: File was NOT sent");
				 }				 
			 }
		 }catch(Exception e){
			 e.printStackTrace();
			 
		 }
		
		return true;
	}
}