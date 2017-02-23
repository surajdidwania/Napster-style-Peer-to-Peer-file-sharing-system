/*=========================================================*/
/*       					         					   */ 
/*	          Peer As a Client Driver					           */
/*						       							   */
/*=========================================================*/

//PeerClientIF Implementation
/**
 * @author Lawrence
 * PeerClientIF Implementation
 */
package Peer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import CentralServer.PeerServerIF;

public interface PeerClientIF extends Remote {
	String[] getFiles() throws RemoteException;
	String getName() throws RemoteException;
	String getPeerDir() throws RemoteException;
	public String getport_no() throws RemoteException;
	PeerServerIF getPeerServer() throws RemoteException;
	void updateServer() throws RemoteException;
	boolean acceptFile(String name, byte[] mydata, int mylen) throws RemoteException;
}
