/**
  * @author Sudharsan Srinivasan
 * Spring 2021
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

//This file runs after Server is initialized in ServerInitialier class

//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html

public class WebServer implements Runnable {

	private ServerSocket serverSocket; 
	private String hostAddress; 
	private int serverPortNumber; 
	
	//Default hostaddress and port number values for the server
	private final String DEFAULT_HOST = "localhost";
	private final int DEFAULT_PORT = 8080;
	
	//Using the WebServer constructor function to start connection id no values are passed in command line arguments
	public WebServer ()
	{
		//set address and port number to default values set above
		this.hostAddress = DEFAULT_HOST; 
		this.serverPortNumber = DEFAULT_PORT; 
	}

	//Using the WebServer constructor if host address and port number are passed in command line arguments
	public WebServer (String sHost, int port)
	{
		//set address and port number using values passed
		this.hostAddress = sHost; 
		this.serverPortNumber = port; 
	}
		
	//Using the WebServer constructor if only the port number is passed in command line arguments
 	public WebServer (int port)
	{
		//set address to the default value set above and port number with the value passed
		this.hostAddress = DEFAULT_HOST; 
		this.serverPortNumber = port; 
	}

	
	@Override
	public void run() {
		
		try {

			//fetch inet address of the hostAddress and store in a variable
			InetAddress serverInet = InetAddress.getByName(hostAddress);
			
			//start connection on Server side
			serverSocket = new ServerSocket(serverPortNumber, 0, serverInet);

			System.out.println("[SERVER]>> Server started at host: " + serverSocket.getInetAddress() + " port: " + serverSocket.getLocalPort() + "\n");
			
			//assign unique client ID for each client requesting a connection
			int clientID=0;
			
			//multithreaded server
			while(true){
				
				Socket clientSocket = serverSocket.accept(); //check for client request to connect
				//print this below line if a successful connection is established
				System.out.println("[SERVER - CLIENT"+clientID+"]>> Connection established with the client at " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				
				//To handle incoming client request, we pass this to RequestHandler.java class
				RequestHandler rh = new RequestHandler(clientSocket, clientID);
				new Thread(rh).start();
				clientID++; //increment client ID for next request
			}
			
		} catch (UnknownHostException e) {
			System.err.println("[SERVER]>> Exception for the hostname: " + hostAddress);
		} catch (IllegalArgumentException iae) {
			System.err.println("[SERVER]>> Exception for starting Server: " + iae.getMessage());
		}
		catch (IOException e) {
			System.err.println("[SERVER]>> Exception for starting Server: " + e.getMessage());
		}
		finally {
				try {
					if(serverSocket != null){
						serverSocket.close();
					}
				} catch (IOException e) {
					System.err.println("[SERVER]>> Exception in closing socket of the Server." + e);
				}
		}
	}
}
