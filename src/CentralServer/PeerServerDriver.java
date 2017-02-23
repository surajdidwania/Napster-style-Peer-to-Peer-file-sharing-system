/*=========================================================*/
/*       					         					   */ 
/*	          CENTRAL INDEX SERVER DRIVER			       */
/*						       							   */
/*=========================================================*/

//PeerServerDriver Implementation
/**
 * @author Lawrence
 * PeerServerDriver Implementation
 * task: start central server and RMI skeleton
 */
package CentralServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class PeerServerDriver {
	public static void main(String[] args) throws RemoteException, MalformedURLException {
		System.setProperty("java.rmi.server.hostname","localhost");		//192.168.107.1
		PeerServer peerserver = new PeerServer();
		Naming.rebind("rmi://localhost:"+args[0]+"/peerserver",peerserver);
		System.out.println("||========================================================================================||");
		System.out.println("||                           PEER-TO-PEER FILE SHARING SYSTEM                             ||");
		System.out.println("||                       ========================================                         ||");
		System.out.println("||========================================================================================||");
		System.out.println("\n 			<CENTRAL INDEX SERVER IS UP AND RUNNING>"						 );
		System.out.println("  ========================================================================================\n");
	}
	
}
