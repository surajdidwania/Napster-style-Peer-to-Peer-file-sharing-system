/*=========================================================*/
/*       					         					   */ 
/*	          CENTRAL INDEX SERVER				           */
/*						       							   */
/*=========================================================*/

//CentralIndxServer Implementation

package CentralServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
//import java.util.HashMap;

import Peer.PeerClientIF;
/**
 * @author Lawrence
 * CentralServer Implementation
 * task: index peers and its file contents by adding to ArrayList,
 * 		synchronize file contents of ArrayList with updated contents in peer,
 * 		search ArrayList for peers that contain a certain file and,
 * 		display all peers registered to server and their contents
 */
public class PeerServer extends UnicastRemoteObject implements PeerServerIF {
	private static final long serialVersionUID = 1L;
	private ArrayList<PeerClientIF> peerClients = new ArrayList<PeerClientIF>();
	//private HashMap<String, PeerClientIF> peerClients = new HashMap<String, PeerClientIF>();
	protected PeerServer() throws RemoteException {
		super();
	}	
	/**
	 * task: method indexes a peer to the server and logs the files contained in peer directory
	 * 		it then calls displayRegisteredPeers() to show all peers indexed to the server and
	 * 		their files
	 * @param peerClient: peer client interface object
	 * @return response to peer confirming that it is registered with server and logged files
	 * @throws RemoteException
	 */
	public synchronized String registerPeerClient(PeerClientIF peerClient) throws RemoteException {
		//register peer and log files
		peerClients.add(peerClient);	  	
		//list files
		String peerfiles = "";
		String[] files = peerClient.getFiles();	
		for (int i=0; i<files.length; i++){
			peerfiles += "\n\t- "+files[i];
		}
		System.out.println("\n\nNew Peer has registered to Server. See list of registered Peers Below:");
		displayRegisteredPeers();
		return "Peer '"+peerClient.getName()+"' has registered with central server and logged the following files"+peerfiles;
	}
	/**
	 * task: synchronizes the server with changes that has been made on a peer then 
	 * 		calls displayRegisteredPeers() to show all peers indexed to the server and
	 * 		their files
	 * @param peerClient: peer client interface object
	 * @return returns true to peer signifying that server has been updated
	 * @throws RemoteException
	 */
	public boolean updatePeerClient(PeerClientIF peerClient) throws RemoteException {
		//Update the server with changes has been made 
		for (int l=0; l<peerClients.size(); l++ ) {
			if(peerClient.getName().equals(peerClients.get(l).getName())){
				peerClients.remove(l);
				peerClients.add(peerClient);
			}
		}
		System.out.println("\n\nServer has been updated. See list of registered Peers Below:");
		displayRegisteredPeers();
		return true;
	}
	
	private void displayRegisteredPeers() throws RemoteException {
		for (int l=0; l<peerClients.size(); l++ ) {
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("Name: "+peerClients.get(l).getName());
			System.out.println("Root Directory: "+peerClients.get(l).getPeerDir());
			System.out.println("Files: ");
			String[] filelist = peerClients.get(l).getFiles();
			for (int a=0; a<filelist.length; a++) {
				System.out.println("\t"+filelist[a]);
			}
		}
	}
	/**
	 * task: for each peer indexed in the server it scans the array of files
	 * 		searching for a file that matches the filename passed. it then 
	 * 		compiles a list(array) of all peers that contain the file and sends
	 * 		to the requesting peer.
	 * @param file: filename of the file to be found
	 * @param requestingPeer: name of the peer seeking to find the file
	 * @param flag: controls number of time message is printed
	 * @return returns the compiled lists (array) of peers that contains file if
	 * 		file is found. Else it returns 'null' signifying that the file was not 
	 * 		found and hence is not present in any peer
	 * @throws RemoteException
	 */
	public synchronized PeerClientIF[] searchFile(String file, String requestingPeer) throws RemoteException {
		System.out.println("\nPeer '"+requestingPeer+"' has requested a file.");
		Boolean filefound = false;
		PeerClientIF[] peer = new PeerClientIF[peerClients.size()];
		int count = 0;
		for (int l=0; l<peerClients.size(); l++ ) {
			String[] filelist = peerClients.get(l).getFiles();
			for (int a=0; a<filelist.length; a++) {
				if (file.equals(filelist[a])) {
					filefound = true;
					peer[count] = (PeerClientIF) peerClients.get(l);
					count++;
				}
			}
		}
		if(filefound) {
			System.out.println("A list of Peers containing the file '"+file+"' has been sent to "+requestingPeer);
			return peer;
		} else
			System.out.println("The file: '"+file+"' was not found in any Peer");
			return null;
	}
}
