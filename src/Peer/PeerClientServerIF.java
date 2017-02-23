/*=========================================================*/
/*       					         					   */ 
/*	          Peer As a Server Interface		           */
/*						       							   */
/*=========================================================*/

//PeerClientServerIF Implementation

package Peer;
/**
 * @author Suraj
 * PeerClientServerIF Implementation
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerClientServerIF  extends Remote{
	public String getName() throws RemoteException;
	boolean sendFile(PeerClientIF peerClient, String filename) throws RemoteException;
}
