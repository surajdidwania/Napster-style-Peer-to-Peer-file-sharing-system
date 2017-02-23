/*=========================================================*/
/*       					         					   */ 
/*	          Peer As a Client Driver					           */
/*						       							   */
/*=========================================================*/

//PeerClientDriver Implementation

package Peer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import CentralServer.PeerServerIF;
/**
 * @author Lawrence & Suraj
 * PeerClientDriver Implementation
 * task: create RMI connections for central server and peer server
 * 		and starts two threads to run peer_client and peer_client_server
 */
public class PeerClientDriver {
	private static final String INDEX_SERVER = "localhost";
	static String arg=null;
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		if (args.length == 3) {
			arg=args[1];
			//link server and client
			String peerServerURL = "rmi://"+INDEX_SERVER+":"+args[0]+"/peerserver";
			PeerServerIF peerServer = (PeerServerIF) Naming.lookup(peerServerURL);
			PeerClient clientserver= new PeerClient(args[2],args[1],peerServer);
			new Thread(new peerclientserver()).start();
			new Thread(clientserver).start();		
		} else {
			System.err.println("Usage: PeerClientDriver <port_no> <Peer_name>");
		}
	}
	static class peerclientserver implements Runnable
	{
		public void run()
		{			
			try {
				String peerClientURL = "rmi://"+INDEX_SERVER+":"+arg+"/clientserver";
				Peer.PeerClientServerIF pcs = new PeerClientServer();
				Naming.rebind(peerClientURL,pcs);
			} catch (RemoteException | MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
