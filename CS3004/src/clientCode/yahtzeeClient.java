package clientCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Miscellaneous.inputFunctions;
import baseCode.YahtzeeGameLogic;
import clientCode.*;
import serverCode.yahtzee_Server;

/**
 * 
 * @author Shane Gill Student ID : 1612862 Facilitates the introduction of a new
 *         client
 * 
 */
public class yahtzeeClient{

	// Reference Types
	private Socket yahtzeeClient = null;
	private PrintWriter outStream = null;
	private BufferedReader readStream = null;
	
	private boolean waitingForPlayer = false;
	private boolean activePlayer = false;
	
	private boolean clientWaitState = false;

	private boolean playersReady = false;
	
	private String playerName;	
	
	private static String pastPlayerName;
	private static int pastPlayerScore;

	private String serverHostName;
	private int portNum;
	
	YahtzeeGameLogic gameLogic = new YahtzeeGameLogic();
	

	/**
	 * Function executes upon running jar file on client machine
	 */
	public void execute() {

		// put simply notify server that we are trying to connect to it
		try {

			//System.out.println(serverHostName);
			//System.out.println(portNum);

			yahtzeeClient = new Socket(serverHostName, portNum);
			outStream = new PrintWriter(yahtzeeClient.getOutputStream());
			readStream = new BufferedReader(new InputStreamReader(yahtzeeClient.getInputStream()));
			
		} catch (UnknownHostException hostException) {
			hostException.printStackTrace();
			System.err.println("Host error occurred");
			System.err.println(hostException.getStackTrace());
			
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: " + portNum);
		}
		
		
	}
///////////////////////////////////////////////////
	//Listener function
	/**
	 * Function to listen for incomign requests from server
	 * If it is a command then it is filterred and an action is caused
	 * If it doesnt match a command then it is printed to the console.
	 * @param serverRequest
	 * @throws IOException
	 */
	private void clientProcessInput(String serverRequest) throws IOException {
		if (serverRequest.equals("enterClientRecieveState")) {
			assignClientWaitState(true);
			triggerWaitState();
		}
		else if (serverRequest.equals("setActivePlayer")) {
			assignClientWaitState(false);//should exit out of existing for loop
		}
		else if(serverRequest.equals("startClientTurn"))
			startMyTurn();
		
		else if(serverRequest.equals("updateOtherUsersOnScore")) {
			System.out.println("-----------------------------Update--------------------------------");
			pastPlayerName = readStream.readLine();
			pastPlayerScore = Integer.parseInt(readStream.readLine());
			//System.out.println("\n\n"+readStream.readLine()+" scored "+readStream.readLine());

			System.out.println("\n"+pastPlayerName+"'s score is now "+pastPlayerScore);
		}
		else if(serverRequest.equals("checkReady")) {
			inputFunctions inputObject = new inputFunctions();
						
				System.out.println("Sorry but I need you to ready up");
				
				String response;
				
				String[] readyInputs = {"yes", "Yes", "y", "Y"};
				List<String> list = Arrays.asList(readyInputs);
				do {
					response = inputObject.cmdInput("Are you Ready ? (Y/N)");
				}
				while(!list.contains(response));
				
			System.out.println("Ready");
			outStream.println("clientReady");
			outStream.flush();
		}
		else if(serverRequest.equals("closeConnections")) {
			System.exit(1);
			this.closeConnections();
		}
		else 
			System.out.println(serverRequest);
	}

	/**
	 * Function to send the players name to the server such that it can be added to list of known players
	 * @param playerName
	 */
	public void addPlayer(String playerName) {
		this.playerName = playerName;
		outStream.println("addPlayer");
		outStream.println(playerName);
		outStream.flush();
	}
	
	/**
	 * Return the name of this client instance
	 * @return
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	public void setServerHostName(String hostname) {
		this.serverHostName = hostname;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}
	
	public void gameHasStarted(String currentPlayer) {
		System.out.println("It is "+currentPlayer+"'s turn");
	}
	
	public void assignClientWaitState(boolean state) {
		clientWaitState = state;
	}
	
	public void triggerWaitState() {
		while(clientWaitState) {
			try {
				//
				Thread.sleep(200);
				clientProcessInput(readStream.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	public void closeConnections() {
		if (outStream != null)
			outStream.close();
		if (readStream != null)
			try {
				readStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (yahtzeeClient != null)
			try {
				yahtzeeClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void startMyTurn() {
		
		//flush the active player to clients
		outStream.println("sendActiveNameToClients");
		outStream.println(getPlayerName());
		outStream.flush();
		
		
		//start the current clients turn
		gameLogic.startTurn();
	
		//send the most recent score to existing clients
		outStream.println("displayCurrentClientScore");
		outStream.println(getPlayerName());
		outStream.println(gameLogic.getCurrentScore());
		
		outStream.println("updateWhoIsWinning");		
		
		//trigger the turn change
		outStream.println("turnChange"); //relay to server that i have finished my turn and next user can start theirs
		
		outStream.flush();
		
		//this will equate to true after 13 iterations have concluded
		if(gameLogic.getGameFinished()) {
			outStream.println("IHaveFinished");
			outStream.flush();
		}
	}
	
	

}
