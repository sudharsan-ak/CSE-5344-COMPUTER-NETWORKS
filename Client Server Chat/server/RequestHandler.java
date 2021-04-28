/**
 * @author Sudharsan Srinivasan
 * Spring 2021
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//To handle incoming client request from the WebServer.java class

//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html

public class RequestHandler implements Runnable {

	//socket and ID corresponding to the client's request received from WebServer.java class
	private Socket clientSocket; 
	private int clientID; 

	private final String CRLF = "\r\n"; 
	private final String SP = " "; 
	
	//Using the RequestHandler constructor with the socket and ID passed from the WebServer.java class
	public RequestHandler(Socket cs, int cID) {
		this.clientSocket = cs;
		this.clientID = cID;
	}

	@Override
	public void run() {
		
		//define input and output streams
		BufferedReader socketInStream = null; // to read data received over the inputStream
		DataOutputStream socketOutStream = null; //to write data over the outputStream
		
		FileInputStream fis = null; //reads file from the local file path
		
		try {
			//get a reference to clientSocket's inputStream
			socketInStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			//get a reference to clientSocket's outputStream
			socketOutStream = new DataOutputStream(clientSocket.getOutputStream());

			//get a request from socket inputStream
			String packet = socketInStream.readLine();
			
			//check for empty request
			if(packet != null)
			{
				System.out.println("[SERVER - CLIENTID-"+clientID+"]>> A Request Received: " + packet);

				
				//Splitting the message into packets to be handled
				String[] msgParts = packet.split(SP);
				
				//check if the request received is of method "GET"
				if (msgParts[0].equals("GET") && msgParts.length == 3) {
					
					//check for the filepath requested
					String filePath = msgParts[1];
					
					//check if the filepath requested has a forward slash(/) if not add one
					if(filePath.indexOf("/") != 0)
					{	
						filePath = "/" + filePath;
					}
					
					
					System.out.println("[SERVER - CLIENTID-"+clientID+"]>> FilePath Requested: " + filePath);
					
					//if requested filePath is null or requesting a default index file
					if(filePath.equals("/"))
					{
						System.out.println("[SERVER - CLIENTID-"+clientID+"]>> Default filepath");
						
						//set filePath to the default value if not explicitly requested
						filePath = filePath + "index.html";
					}
					
					filePath = "." + filePath;
					File file = new File(filePath);
					try {
						//check if the file requested with the filePath exists on server side and send status based on it
						if (file.isFile() && file.exists()) {
							//200 OK is sent if the file is present on the server side.
							String getResponse = "HTTP/1.0" + SP + "200" + SP + "OK" + CRLF;
							socketOutStream.writeBytes(getResponse);

							//Printing the content type header line, CRLF - Carriag Return Line Feed
							socketOutStream.writeBytes("Content-type: " + getContentType(filePath) + CRLF);
							socketOutStream.writeBytes(CRLF);
							
							//open the requested file
							fis = new FileInputStream(file);

							// set a default buffer size of 1024
							byte[] buffer = new byte[1024];
							int bytes = 0;
							
							//write into the output buffer stream.
							while((bytes = fis.read(buffer)) != -1 ) {
								socketOutStream.write(buffer, 0, bytes);
							}
							
							System.out.println("[SERVER - CLIENTID-"+clientID+"]>> Response with status line: " + getResponse);
							socketOutStream.flush(); //flush the outputstream
							System.out.println("[SERVER - CLIENTID-"+clientID+"]>> HTTP Response sent from Server to Client");
							
						} else { //If the requested file is not present in the server, then handle that with 404 Error response
							
							System.out.println("[SERVER - CLIENTID-"+clientID+"]>> ERROR: Requested filePath " + filePath + " does not exist in the server side");

							String getResponse = "HTTP/1.0" + SP + "404" + SP + "Not Found" + CRLF;
							socketOutStream.writeBytes(getResponse);
							socketOutStream.writeBytes("Content-type: text/html" + CRLF);
							socketOutStream.writeBytes(CRLF);
							//send the contents of the error file to handle the error
							socketOutStream.writeBytes(geterrorMessage());
							System.out.println("[SERVER - CLIENTID-"+clientID+"]>> Response with status line: " + getResponse);
							socketOutStream.flush(); //flush the outputstream
							System.out.println("[SERVER - CLIENTID-"+clientID+"]>> HTTP Response sent from the Server to Client");
						}
						
					} catch (FileNotFoundException e) {
						System.err.println("[SERVER - CLIENTID-"+clientID+"]>> EXCEPTION: Requested filePath " + filePath + " does not exist in the Server side");
					} catch (IOException e) {
						System.err.println("[SERVER - CLIENTID-"+clientID+"]>> EXCEPTION in processing request." + e.getMessage());
					}
				} else { //Handle invalid HHTP GET Request
					System.err.println("[SERVER - CLIENTID-"+clientID+"]>> Invalid HTTP GET Request. " + msgParts[0]);
				}
			}
			else
			{
				//Handle unknown requests
				System.err.println("[SERVER - CLIENTID-"+clientID+"]>> Ignoring a NULL/unknown HTTP request.");
			}

		} catch (IOException e) 
		{
			System.err.println("[SERVER - CLIENTID-"+clientID+"]>> EXCEPTION in processing request." + e.getMessage());
			
		} finally {
			//close all open input/ouput streams
			try {
				if (fis != null) {
					fis.close();
				}
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
					System.out.println("[SERVER - CLIENTID-"+clientID+"]>> Closing the connection from the Server side.\n");
				}
			} catch (IOException e) {
				System.err.println("[SERVER - CLIENTID-"+clientID+"]> EXCEPTION in closing resource in the Server side." + e);
			}
		}
	}
	
	//check for file type requested
	private String getContentType(String filePath)
	{
		//check if file type is html
		if(filePath.endsWith(".html") || filePath.endsWith(".htm"))
		{
			return "text/html";
		}
		//otherwise, a binary file
		return "application/octet-stream";
	}
	
	//error message to print if requested file is not present in the server path
	private String geterrorMessage ()
	{
		String errorMessage = 	"<!doctype html>" + "\n" +
									"<html lang=\"en\">" + "\n" +
									"<head>" + "\n" +
									"    <meta charset=\"UTF-8\">" + "\n" +
									"    <title>Error 404</title>" + "\n" +
									"</head>" + "\n" +
									"<body>" + "\n" +
									"    <b>ErrorCode:</b> 404" + "\n" +
									"    <br>" + "\n" +
									"    <b>Error Message:</b> The requested file does not exist on this server path." + "\n" +
									"</body>" + "\n" +
									"</html>";
		return errorMessage;
	}
}
