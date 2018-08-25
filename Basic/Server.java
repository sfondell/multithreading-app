//package basic;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

/*
 * A server that simply prints out and acknowledges messages back.
 */
public class Server {

	// Create a socket for the server 
	private static ServerSocket serverSocket = null;
	// Create a socket for the user 
	private static Socket userSocket = null;
	private static BufferedReader input_stream = null;
	private static PrintStream output_stream = null;



	public static void main(String args[]) {

		// The default port number.
		int portNumber = 8000;
		if (args.length < 1) {
			System.out.println("Usage: java Server <portNumber>\n"
					+ "Now using port number=" + portNumber + "\n");
		} else {
			portNumber = Integer.valueOf(args[0]).intValue();
		}


		System.out.println("Server using port number=" + portNumber + "\n");

		/*
		 * Open a server socket on the portNumber (default 8000). 
		 */
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * Create a user socket for accepted connection 
		 */
		while (true) {
			try {
				
				userSocket = serverSocket.accept();

				input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
				output_stream = new PrintStream(userSocket.getOutputStream());
				String status = input_stream.readLine().trim();

				if (status.startsWith("#status")){
					System.out.println(status.substring(8));
					output_stream.println("#statusPosted");	
				}
				
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
				input_stream.close();
				output_stream.close();
				userSocket.close(); 
			}
			catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}





