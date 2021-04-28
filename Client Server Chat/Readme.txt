*Project 1*
*Sudharsan Srinivasan (1001755919)*
*CSE 5344 Spring 2021*
---------------------------------------------------------------------------------------------------------------------
A Java implementation of Simple HTTP Web Client and a Multi-threaded Web Server.

#Tools:    
1. *Programming Language* : Java (jdk 1.8)
2. *IDE*: Eclipse
3. *External Packages* : No external packages are required other than default Java packages.
4. *OS* : Windows 10
5. *Command Line Interface* : Windows command prompt used to run/test the program
---------------------------------------------------------------------------------------------------------------------
#*Project Structure:*
1. *Server* This folder contains source files for the server implementation along with a default 'home.html' file.   
    - *ServerInitializer.java* : This Java class initializes the WebServer using 8080 or client defined port number.
    - *WebServer.java* : This Java class runs a multi-threaded server and initializes a connection to the client requests. After client is connected, it trasnfer  the execution to RequestHandler.java which handles the incoming request.
    - *RequestHandler.java* : This Java class acts on the incoming request from the client and provides a response to the client.
    - *home.html* : A default html file which the client can request from the server, using the filepath('/').
    - *test.html* : A default html file which the client can request from the server, using the filepath('/').
    - *test1.html* : A default html file without title tag which the client can request from the server, using the filepath('/').

2. *Client:* This folder contains source files for the client implemenetation.
    *WebClient.java:* This Java class implements a single threaded web client to establish connection server on a port to request a file from the server.
---------------------------------------------------------------------------------------------------------------------
#Compile & Run Instructions (Run on Windows cmd prompt):
1. Have 2 separate folder for Server and Client.

2. Open a new terminal, Navigate to Server folder by using cd command and run the java file using the below commands:

	cd Server
	javac *.java

3. Open a new terminal, Navigate to Client folder by using cd command and run the java file using the below commands:

	cd Server
	javac *.java

4. Go to the Server terminal and initiate the connection for the client request using the below command:

	java ServerInitializer

   Now, the Server initiates the connection and awaits client requests.

5. Go to the Client terminal and request for connecting to the server and also to receive a file using one of the below commands:

	java WebClient localhost 8080 home.html  (This runs in user defined port 8080)
	java WebClient 127.0.0.1 8080 test.html	 (Explicitly mentioned IP Address)
	java WebClient localhost test.html	 (Runs in default port 8080)

Note: Execute any one of the above commands (or) one after another to establish connection to server in different ways.
 
6. If the requested ".html" file exists on the server, the server will return a `HTTP/1.0 200 OK` response with appropriate content-type and file content shown on the command prompt on client side. The content of the file will be extracted and written to a local html file on Client folder using a default filename 'Index.html'

7. If the requested ".html" file does NOT exist on the server, the server will return a `HTTP/1.0 404 Not Found` response with a general error html file. The content of the file will be extracted and written to a local html file with the name "Error 404.html"

8. All requested file paths must be relative to the `RequestHandler.java` class. If not, 404 error will be returned.
---------------------------------------------------------------------------------------------------------------------
#References
1. Project 1 document available on Course Materials section of 2212-CSE-5344-001 section class on the UTA.
2. Book: James F. Kurose, Keith W. Ross - Computer Networking_ A Top-Down Approach-Pearson (2012)
3. Thread Tutorial(http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html)
4. Socket Communications(http://www.oracle.com/technetwork/java/socket-140484.html)
5. MultiThreading (https://www.geeksforgeeks.org/multithreaded-servers-in-java/)
