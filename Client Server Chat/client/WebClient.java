/**
 * @author Sudharsan Srinivasan
 * Spring 2021
 */
//importing all necessary header files for socket programming.
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.GregorianCalendar;

//WebClient is the side that request to establish connection with Server and later to request files.
//Reference: http://www.oracle.com/technetwork/java/socket-140484.html

public class WebClient {

	public static void main(String[] args) {
		final String CRLF = "\r\n"; //carriage return line feed
		final String SP = " "; //status line parts separator
		
		String hostAddress = null;
		
		int serverPortNumber = 8080; //initialize port with a value, if not provided, default value is set at run time.
		
		String filePath = "/"; //to request files from server using '/' in the path name of the file.
		
		//Command line arguments are given from client side to request connection to transfer files
		//General Format is hostAddress PORT FILEPATH (minimum one argument is needed)
		if(args.length == 1)
		{

			hostAddress = args[0];
		}
		else if (args.length == 2){
			//first argument is hostAddress
			hostAddress = args[0];
			
			//check if second argument is either serverPortNumber or filePath
			try {
				serverPortNumber = Integer.parseInt(args[1]); //check if port number is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]>> Port number not provided. Using Default Server port number.");
				
				//if check for serverPortNumber results in an exception, then assume its filePath
				filePath = args[1];
			}
		}
		else if (args.length == 3){
			//first argument is hostAddress
			hostAddress = args[0];
			
			//second argument is serverPortNumber
			try {
				serverPortNumber = Integer.parseInt(args[1]); //check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]>> Port number not provided. Using Default Server port number.");
			}
			
			//third argument is fileName since we already have hostname and port number for server
			filePath = args[2];
		}
		else //arguments cannot be empty, hence the below error message.
		{
			System.err.println("[CLIENT]>> Cannot  At least hostAddress is required.");
			System.exit(-1);
		}
		//printout the port and file path provided by the client.
		System.out.println("[CLIENT]>> Server Port in use: " + serverPortNumber);
		System.out.println("[CLIENT]>> FilePath in use: " + filePath);
		
		//define a socket
		Socket socket = null;
		
		//define input and output streams
		BufferedReader socketInStream = null; // to read data received over the inputStream
		DataOutputStream socketOutStream = null; //to write data over the outputStream
		
		FileOutputStream fos = null; //writes content of the responded file in a file
		
		try {
			
			//fetch inet address of the hostAddress and store in a variable
			InetAddress serverInet = InetAddress.getByName(hostAddress);
			
			//initiate a connection to the server
			socket = new Socket(serverInet, serverPortNumber);
			System.out.println("[CLIENT]>> Server Connected at " + hostAddress + ":" + serverPortNumber);
			
			//reference to socket's input and output stream
			socketInStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketOutStream = new DataOutputStream(socket.getOutputStream());

			//initiate a GET request from client side to the server
			String getReq = "GET" + SP + filePath + SP +"HTTP/1.0" + CRLF;
			System.out.println("[CLIENT]>> HTTP GET request to the Server: " + getReq);
			
			/*
				To calculate Round Trip Time, we start a timer to check when the request is sent from the server to the time the server responds back with
				a response
			*/
			long start = new GregorianCalendar().getTimeInMillis();  //start timer
			
			//send the getReq
			socketOutStream.writeBytes(getReq);
			socketOutStream.writeBytes(CRLF);
			socketOutStream.flush(); //flush out output stream
			
			System.out.println("[CLIENT]>> Awaiting Server Response.....");
			String getResponse = socketInStream.readLine();
			System.out.println("[CLIENT]>> Received HTTP Response with status line: " + getResponse);
			//print the content type of the response received from the server
			String contentType = socketInStream.readLine();
			System.out.println("[CLIENT]>> Received Content Type:" + contentType);
			socketInStream.readLine();

			System.out.println("[CLIENT]>> Received Response Body:");
			//read and save the content to a buffer
			StringBuilder content = new StringBuilder();
			String res;
			while((res = socketInStream.readLine()) != null)
			{
				content.append(res + "\n");
				System.out.println(res);
			}
			
			//get filename
			String fileName = getFileName(content.toString());
			//set the end timer here to know when the response has been received by the client
			long end = new GregorianCalendar().getTimeInMillis();
			//calculate the RTT using the difference between the start and end timer
			System.out.println("Round Trip Time: " + (end-start+" ms"));
			
			fos = new FileOutputStream(fileName);
			
			fos.write(content.toString().getBytes());
			fos.flush(); //flush outputstream
			
			System.out.println("[CLIENT]>> HTTP Response received. File Created: " + fileName);

		} catch (IllegalArgumentException iae) {
			System.err.println("[CLIENT]>> EXCEPTION in connecting to the Server: " + iae.getMessage());
		} catch (IOException e) {//any other exception
			System.err.println("[CLIENT]>> ERROR " + e);
		}
		finally {
			try {
				//close all input/output streams
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (socket != null) {
					socket.close();
					System.out.println("[CLIENT]>> Closing the Connection on the Client side.");
				}
			} catch (IOException e) {
				System.err.println("[CLIENT]>> EXCEPTION in closing resource on the Client side." + e);
			}
		}
	}
 
 	//The below function gets the file name entered in the command line arguments and return it.
	
	private static String getFileName(String content)
	{
		
		String filename = "";
		filename = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));//gets the file name from title tag
		if(filename.equals("")) //this assume the default name for the filename if nothing is explicitly provided.
		{
			filename = "index";
		}
		filename = filename+".html";
		return filename;
	}
}
