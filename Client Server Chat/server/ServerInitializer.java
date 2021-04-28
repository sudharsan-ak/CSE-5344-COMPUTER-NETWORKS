/**
 * @author Sudharsan Srinivasan
 * Spring 2021
 */

//Server side is started from command line by running this class

//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html

public class ServerInitializer {

	public static void main(String[] args) {

		int portnumber = 8080; //default value for the port if nothing is explicitly mentioned in the command line arguments
		

		if(args.length == 1)
		{

			try {
				portnumber = Integer.parseInt(args[0]); //check if port number provided is an integer
			}
			catch (NumberFormatException e)
			{
				System.err.println("[SERVER]>> Port number not provided. Using Default Server port number.");
			}
		}

		System.out.println("[SERVER]>> Server Port Number in Use: " + portnumber);
	
		WebServer ws = new WebServer(portnumber);
		new Thread(ws).start();
	}
}
