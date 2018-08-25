//package broadcast;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;

// EXTENDS/INHERITS THREAD
public class User extends Thread {

	// The user socket
	private static Socket userSocket = null;
	// The output stream
	private static PrintStream output_stream = null;
	// The input stream
	private static BufferedReader input_stream = null;

	private static BufferedReader inputLine = null;

	// IS THE CONNECTION OPENED OR CLOSED
	private static boolean closed = false;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 8000;
		// The default host.
		String host = "localhost";

		if (args.length < 2) {
			System.out
			.println("Usage: java User <host> <portNumber>\n"
					+ "Now using host=" + host + ", portNumber=" + portNumber);
		} else {
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
		}

		/*
		 * Open a socket on a given host and port. Open input and output streams.
		 */
		try {
			userSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			output_stream = new PrintStream(userSocket.getOutputStream());
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host "
					+ host);
		}

		/*
		 * If everything has been initialized then we want to write some data to the
		 * socket we have opened a connection to on port portNumber.
		 */
		// IF neither output/input or usersocket is null, then we can begin
		if (userSocket != null && output_stream != null && input_stream != null) {
			try {                
				/* Create a thread to read from the server. */
				new Thread(new User()).start();

				// Get user name and join the social net
				// while connection is not closed:

				String userMessage = new String(); // new empty string

				System.out.println("Enter your username: ");
					// basically scanner
				String username = inputLine.readLine().trim();
					
					// Read user input and send protocol message to server
				output_stream.println("#join " + username);

				while (!closed) {
					String userInput = inputLine.readLine().trim();
					if (userInput.equals("exit")) {
						output_stream.println("#Bye");
						
					}
					else {
						output_stream.println("#status " + userInput);
					}
				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	/*
	 * Create a thread to read from the server.
	 */
	public void run() {
		/*
		 * Keep on reading from the socket till we receive a Bye from the
		 * server. Once we received that then we want to break.
		 */
		String responseLine;
		
		try {
			while ((responseLine = input_stream.readLine()) != null) {
				if (responseLine.startsWith("#welcome")) {
					System.out.println("Connection has been established");
				}
				else if (responseLine.startsWith("#busy")) {
					System.out.println("The server is busy, try again later");
					break;
				}
				else if (responseLine.startsWith("#statusPosted")) {
					System.out.println("Your status has been successfully posted");
				}
				else if (responseLine.startsWith("#newuser")) {
					System.out.println(responseLine.substring(10) + " has joined");
				}
				else if (responseLine.startsWith("#newStatus")) {
					// Splitting responseLine into parts of the protocol
					String[] newstatus = responseLine.split("\\s+");
					String str = new String();
					for (int i = 2; i < newstatus.length; i++){
						str = str + " " + newstatus[i];
					}
					System.out.println(newstatus[1] + ":" + str);
				}
				else if (responseLine.startsWith("#Leave")) {
					System.out.println(responseLine.substring(8) + " has left");
				}
				else if (responseLine.startsWith("#Bye")) {
					System.out.println("Connection closed");
					break;
				}
				else {
					System.out.println("Could not understand message from server");
				}
			}
			closed = true;
			output_stream.close();
			input_stream.close();
			userSocket.close();
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}
}


// Part 3
// Second arraylist of user's friends
// parse different messages starting with different characters
// reading/writing vs printing/recieving
