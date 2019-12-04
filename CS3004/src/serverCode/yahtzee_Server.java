package serverCode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import Miscellaneous.inputFunctions;
import Miscellaneous.inputFunctions.*;
import clientCode.yahtzeeClient;

/**
 *
 * Server class file launched upon running of Server.jar on host machine
 * main function is triggered in here and the server turns online
 * It is then able to listen to incoming requests provided the requestee specifies the host name and port num
 * 
 * The Server.jar file must be run before the Client jar files
 * 
 * @author Shane
 */
public class yahtzee_Server {

	private static Thread server;

	private static boolean connectable = false;

	private static int portNum;
	protected static int readyCounter; //if this counter is equal to the size of users that joined, then all users confirmed they are ready
	
	protected static int numOfPlayersFinished;
	
	
	//access to generic input functions class
	private static inputFunctions input;

	//each time a user connects, their thread is stored in this array
	public static ArrayList<yahtzee_Server_Thread> onlineUsers = new ArrayList<>();	
	//used to determine who is winning and losing after each clients turn in a round
	protected static LinkedHashMap<String, Integer> playersAndScore = new LinkedHashMap<String, Integer>();
	
	//control volatile variable used to dictate which output/input stream to address to.
	protected static volatile int controlIndex = 0;

	//contains the recent players past score and name
	//if it is my turn now then these values will contain the name and score of the person playing before me
	protected static String pastPlayerName;
	protected static String pastPlayerScore;
	
	
	public static void main(String[] args) {
		Collections.synchronizedMap(playersAndScore);//make this linkedhashmap somewhat synchronized
		
		input = new inputFunctions();
		portNum = input.inputInt("Enter Port Number ");

		server = new Thread(new Server(portNum));
		server.start();
		
	}

	/**
	 * Nested inner class that implements runnable and is called through the enclosing classes main,
	 * This triggers the threads activation and places it in a continuos loop 
	 * @author shane
	 *
	 */
	public static class Server implements Runnable {

		private int portNum;
		ServerSocket serverSocket = null;

		/*
		 * When the jar file is run, then setup the server and allow clients to connect
		 * if you attempt to run a client jar without the server jar being executed then
		 * no local host will be found and nothing will be routed thus no game starting
		 */

		//parameterised construtor with portnum as its input,
		//The port num and the host name are then displayed 
		Server(int portNum) {
			this.portNum = portNum;
			InetAddress computerAddr = null;

			try {
				computerAddr = InetAddress.getLocalHost();
			} catch (Exception e) {
				System.out.println(e.getCause());
				System.out.println(e.getMessage());
			}

			System.out.println("The address of this computer " + computerAddr.getHostName());
			System.out.println("All Clients must enter this host name upon executing Client JAR");

			// setup the server socket with the given socket value

			try {
				serverSocket = new ServerSocket(portNum);

			} catch (IOException e) {
				System.err.println("Could not setup server socket with given value : " + portNum);
				System.exit(-1); // hard exit
			}

			System.out.println("Server Started");
		}
		
		/**
		 * Function called through the user of .start()
		 * 
		 * For the servers life times, it exists on a while loop with a sleep time of 150milliseconds to not labor the host machine
		 * Whenever a client attempts to connect to the server accept it through the use of serverSocket.accept();
		 * The argument serverSocket.accept means that the thread will connect to
		 * whichever client demanded the connection 
		 * The start command just tells the yahtzee_Server_Thread object to begin execution.
		 */
		@Override
		public void run() {

			//yahtzee_Server_Thread serverThread;

			connectable = true;

			while (server != null) {
				// give it some time between each iteration to reduce inefficiency.				
				
				try {
					Thread.sleep(150);
					final yahtzee_Server_Thread serverThread = new yahtzee_Server_Thread(serverSocket.accept(), this);

					if (connectable == false) {
						// kill it
						server.interrupt();
					}
					//add newly created thread to list of online users array
					onlineUsers.add(serverThread);
					
					//because it implements runnable here
					//what it is doing is calling the run method inside of yahtzee_Server_Thread
					Thread thread = new Thread(serverThread);
					thread.start();	
										
					if(onlineUsers.isEmpty() == false) {
						System.out.println("\nNumber of players connected "+onlineUsers.size());
					}
					
					//prompt the client to instruct the server whether they are ready or not
					//requires a minimum of two clients before the game can actually begin
					serverThread.getClientOutputStream().println("checkReady");
				
					//if any exceptions are throw then capture them here
					//where the first two represent specific known exceptions
					//and the last is just a fail safe for any unknown exceptions that may arise
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Interuppted exception occurred");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("IOException occurred");
				}
				catch (Exception e) {
					
				}
				System.out.println("New server thread started");
				
			}
			try {
				//after while loop termination, attempt to close socket
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
}
