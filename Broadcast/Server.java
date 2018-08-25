//package broadcast;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;



/*
 * A server that delivers status messages to other users.
 */
public class Server {

	// Create a socket for the server INITIALIZE TO NULL
	private static ServerSocket serverSocket = null;
	// Create a socket for the server INITIALIZE TO NILL
	private static Socket userSocket = null;
	// Maximum number of users 5 IN THIS INSTANCE
	private static int maxUsersCount = 5;
	// An array of threads for users INITIALIZE TO NULL
	private static userThread[] threads = null;


	public static void main(String args[]) {

		// The default port number.
		// IF NO ARGS SPECIFIED
		int portNumber = 8000;
		if (args.length < 2) {
			System.out.println("Usage: java Server <portNumber>\n"
					+ "Now using port number=" + portNumber + "\n" +
					"Maximum user count=" + maxUsersCount);
		} else {
			portNumber = Integer.valueOf(args[0]).intValue();
			maxUsersCount = Integer.valueOf(args[1]).intValue();
		}

		System.out.println("Server now using port number=" + portNumber + "\n" + "Maximum user count=" + maxUsersCount);
		
		// Array of userThreads called threads is initialized
		userThread[] threads = new userThread[maxUsersCount];


		/*
		 * Open a server socket on the portNumber (default 8000). 
		 */
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * Create a user socket for each connection and pass it to a new user
		 * thread.
		 */
		while (true) {
			try {
				// socket assigned to userSocket
				userSocket = serverSocket.accept();
				int i = 0;
				// INITIALIZES EACH USER THREAD IN THREADS ARRAY
				for (i = 0; i < maxUsersCount; i++) {
					if (threads[i] == null) {
						threads[i] = new userThread(userSocket, threads);
						threads[i].start();
						break;
					}
				}
				// BUSY IS RETURNED IF TOO MANY CLIENTS ON ALREADY
				if (i == maxUsersCount) {
					PrintStream output_stream = new PrintStream(userSocket.getOutputStream());
					output_stream.println("#busy");
					output_stream.close();
					userSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

/*
 * Threads
 */
class userThread extends Thread {

	// THREAD'S USERNAME
	private String userName = null;
	// INITIALIZING input_stream
	private BufferedReader input_stream = null;
	// INITIALIZING output_stream
	private PrintStream output_stream = null;
	// USER'S SOCKET INTIALIZED TO NULL & LATER CHANGED
	private Socket userSocket = null;
	// PRIVATE ARRAY OF USER THREADS
	private final userThread[] threads;
	// ALSO CONTAINS AN INSTANCE OF MAX USERS COUNT
	private int maxUsersCount;

	// TO DECLARE/INITIALIZE A USER THREAD:
	public userThread(Socket userSocket, userThread[] threads) {
		this.userSocket = userSocket;
		this.threads = threads;
		maxUsersCount = threads.length;
	}

	// OVERRIDING RUN METHOD INHERITED FROM THREAD
	public void run() {
		// INITIALIZE MAX USERS COUNT
		int maxUsersCount = this.maxUsersCount;
		userThread[] threads = this.threads;

		try {
			/*
			 * Create input and output streams for this client.
			 * Read user name.
			 */
			// GETTING INSTANCES OF INPUT AND OUTPUT STREAMS
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
			output_stream = new PrintStream(userSocket.getOutputStream());

			String username = input_stream.readLine().trim();
			if (username.startsWith("#join")){
				this.userName = username.substring(5);
				output_stream.println("#welcome");
			}

			/* Welcome the new user. */
			// synchronized block to send new user message to all other users
			// no more than one thread can access code

			synchronized (userThread.class) {
				for (int i = 0; i < maxUsersCount; i++) {
					if (threads[i] != null) {
						threads[i].output_stream.println("#newuser " + this.userName);
					}
				}
			}

			/* Start the conversation. */
			while (true) {
				String activity = input_stream.readLine().trim();
				if (activity.startsWith("#status")){
					activity = activity.substring(8);
					String message = "#newStatus " + this.userName + " " + activity;
					output_stream.println("#statusPosted");
					synchronized (userThread.class) {
						for (int i = 0; i < maxUsersCount; i++) {
							if (threads[i] != null) {
								threads[i].output_stream.println(message);
							}
						}
					}
				}
				else if (activity.startsWith("#Bye")){
					output_stream.println("#Bye");
					String message = "#Leave " + this.userName; 
					synchronized (userThread.class) {
						for (int i = 0; i < maxUsersCount; i++) {
							if (threads[i] != null) {
								threads[i].output_stream.println(message);
							}
						}
					}
					break;
				}


			}

			// conversation ended.

			/*
			 * Clean up. Set the current thread variable to null so that a new user
			 * could be accepted by the server.
			 */
			synchronized (userThread.class) {
				for (int i = 0; i < maxUsersCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			input_stream.close();
			output_stream.close();
			userSocket.close();
		} catch (IOException e) {
		}
	}
}




