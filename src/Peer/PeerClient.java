/*=========================================================*/
/*       					         					   */ 
/*	          Peer As a Client					           */
/*						       							   */
/*=========================================================*/

//PeerClient Implementation

package Peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//import org.apache.commons.io.FileUtils;

import CentralServer.PeerServerIF;
/**
 * @author Lawrence & Suraj
 * PeerClient Implementation
 * task: runs all peer client end functions including searching for
 * 		file in central server and downloading file directly 
 * 		from another peer's server
 */
//to implement instances of this class as threads use 'Runnable' which will add the 'run' method

public class PeerClient extends UnicastRemoteObject implements PeerClientIF, Runnable { 
	private static final long serialVersionUID = 1L;
	private PeerServerIF peerServer;
	private String name = null;
	private String port_no;
	String filename1;
	private String peerRootDirectoryPath = null;
	private String[] files;
	
	protected PeerClient() throws RemoteException {
		super();
	}
	/**
	 * task: PeerClient constructor initializes all global fields of PeerClient instance 
	 * 		then calls 'centralServer.registerPeerClient(this)' to register peer with server
	 * 		and finally starts a new thread for event listener 'PeerDirListener(this)'
	 * 		to detect changes to the peer directory contents
	 * @param name: name of peer
	 * @param port_no: port number of peer
	 * @param centralServer: RMI object of central server
	 * @throws RemoteException
	 */
	protected PeerClient(String name,String  port_no, PeerServerIF peerServer) throws RemoteException {
		this.name = name;
		this.peerServer = peerServer;
		this.port_no=port_no;
		//create list of files in peer root directory
		try{
			//get peer root directory pathname
			this.peerRootDirectoryPath = System.getProperty("user.dir");
		    System.out.print("Peer Directory is: "+peerRootDirectoryPath.replace("\\", "/")+"\n");
		    //get all files in peer root directory
			File f = new File(peerRootDirectoryPath);
			this.files = f.list();	//returns directory and files (with extension) in directory			
		}catch (Exception e){
		    System.out.println("Peer path Exception caught ="+e.getMessage());
		}		
		//register peer data structure, including files array
		System.out.println(peerServer.registerPeerClient(this));
		new Thread(new PeerDirListener(this)).start();
	}
	
	public String getName() {
		return name;
	}
	public String[] getFiles() {
		return files;
	}
	public String getPeerDir() {
		return peerRootDirectoryPath;
	}
	public PeerServerIF getPeerServer() {
		return peerServer;
	}
	public String getport_no()
	{
		return port_no;
	}
	/**
	 * task: update the central server with updated information about the peer
	 * 		and the files it contains by calling 'centralServer.updatePeerClient(this)'
	 */
	public synchronized void updateServer() throws RemoteException {
		//get all files in peer root directory
		File f = new File(peerRootDirectoryPath);
		this.files = f.list();	//returns directory and files (with extension) in directory
		if(peerServer.updatePeerClient(this)){
			System.out.println("Server has been updated with new information");
		} else {
			System.out.println("Fault: Server was not updated with new information");
		}		
	}
	/**
	 * search central server for peers that contain a particular file
	 * connect peer_client with user chosen peer_client_server to download file from
	 * download file and
	 * calculate the average runtime of the search-central-server functionality
	 */
	public void run() {
		//read messages from command line
		Scanner cmdline = new Scanner(System.in);
		String command, task, filename;
		System.out.println("||========================================================================================||");
		System.out.println("||                           PEER-TO-PEER FILE SHARING SYSTEM                             ||");
		System.out.println("||                       ========================================                         ||");
		System.out.println("||                                       MENU:                                            ||");
		System.out.println("||========================================================================================||");
		System.out.println("Enter The Option and filename:\n==================\n1. Downloading From Peer Server \n \n2. Exit\n");	
		while (true) {	//continue reading commands
			command = cmdline.nextLine();
			CharSequence symbol = " ";
			//wait till command is received
			if (command.contains(symbol)) {
				task = command.substring(0, command.indexOf(' '));
				filename = command.substring(command.indexOf(' ')+1);
				filename1=filename;
				if(Arrays.asList(this.getFiles()).contains(filename)) 
					{
					System.out.println("Please enter the filename which you don't possess");
					continue;
					}
				if (task.equals("1") || task.equals('d')) {
					PeerClientIF[] peer = findFile(filename);
					int choice = cmdline.nextInt();
					String peerServerURL;
					PeerClientIF peerClientServer = null;
					PeerClientServerIF peerclientserverif = null;
					try {
						String peerclientURL = "rmi://localhost:"+peer[choice-1].getport_no()+"/clientserver";
						peerclientserverif= (PeerClientServerIF) Naming.lookup(peerclientURL);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					downloadFile(peerclientserverif, filename);
				} else {
					System.out.println("Usage: <task> <item>");
				}
				try {
					responsetime();
				} catch (RemoteException | MalformedURLException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void downloadFile(PeerClientServerIF peerWithFile, String filename) {
		//request file directly from Peer
		try {
			if(peerWithFile.sendFile(this,filename)){
				System.out.println("File has been downloaded");
				//updateServer();
			} else {
				System.out.println("Fault: File was NOT downloaded");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/**
	 * task: searches the central server for the peers that contains the file intended
	 * 		for download by calling 'centralServer.searchFile(filename, peerName)'
	 * 		then lists the peers that was returned by the central server and prompts
	 * 		user to choose which of the peers to download from
	 * @param filename: name of the file to be searched in central server
	 * @return returns an array(list) of all peers that contains the file
	 * 		or returns 'null' if file is not found in any peer
	 */
	public synchronized PeerClientIF[] findFile(String filename) {
		System.out.println("You want to download the file: "+filename);
		PeerClientIF[] peer;	//peer client that contains file
		try {
			//returns a peer with file
			peer = peerServer.searchFile(filename, name);
			if (peer != null) {
				//list peers with file
				System.out.println("The following Peers has the file you want:");
				for (int i=0; i<peer.length; i++) {
					if (peer[i] != null)
						System.out.println((i+1)+". "+peer[i].getName());
				}
				//prompt user to choose Peer to download from
				System.out.println("Enter number matching the Peer you will like to download from");
				return peer;
			} else {
				System.out.println("File not found. No Peer registered to server has the file.");
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		return null;		
	}
	/**
	 * task: accepts the file_input_stream coming from another peer's server
	 * 		and saves the file in the peers directory. Hence accepts download
	 * @param filename: name of the file that is being sent from another peer's server
	 * @param data: byte array of file_input_stream sent from another peer's server
	 * @param len: length of file_input_stream sent from another peer's server
	 * @return returns true if file is successfully written to peer's directory
	 * @throws RemoteException
	 */
	public synchronized boolean acceptFile(String filename, byte[] data, int len) throws RemoteException{
		System.out.println("File downloading!!");
        try{
        	File f=new File(filename);
        	f.createNewFile();
        	FileOutputStream out=new FileOutputStream(f,true);
        	out.write(data,0,len);
        	out.flush();
        	out.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
		return true;
	}

	/**
	 * task: calculates the average response time of central server search function
	 * @param filename: name of file to be searched in central server
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public void responsetime() throws RemoteException, MalformedURLException, NotBoundException{
		long resptime=0;
		long endtime=0;
		String filename=filename1;
		PeerClientIF[] peer;
				for(int i=0;i<1000;i++)
				{
					long start = System.currentTimeMillis();
					try {
						peer = peerServer.searchFile(filename, name);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 endtime = System.currentTimeMillis()-start;
					resptime = resptime+endtime;
				}
				System.out.println("Average response time of the Peer "+this.getName()+" is " + resptime/1000.000 + "ms");
	}
}
