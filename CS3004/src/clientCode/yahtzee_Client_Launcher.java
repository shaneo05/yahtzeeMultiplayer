package clientCode;

import Miscellaneous.inputFunctions;
import clientCode.*;
import serverCode.yahtzee_Server;


/**
 * Client launcher class started upon running Client jar
 * @author shane
 *
 */
public class yahtzee_Client_Launcher{

	private static inputFunctions input = null;
	

	private String serverHostName = "DESKTOP-FTE4ROI";
	private String userName = "default";
	//Primitive Types
	private int portNum;
	public static void main(String[]args) {
		
		System.out.println("\n--------------------------------------------------------------\n");
		System.out.println("Welcome to Yahtzee!\n");
		System.out.println("--------------------------------------------------------------\n");

		yahtzee_Client_Launcher yahtzee = new yahtzee_Client_Launcher();
		yahtzee.initiate();
		
	}
	/**
	 * Kick starts the application off asking for the host name and port num
	 * The asks for username for which other clients can identify you
	 */
	private void initiate() {
		
		input = new inputFunctions();
		serverHostName = input.stringInput("Please Enter Server Host Name: ");
		portNum = input.inputInt("Please enter the server Port Number: ");
		
		userName = input.stringInput("Nice! now enter your username: ");
		
		addPlayerToGame();
	}
	/**
	 * triggers functions in yahtzeeClient,
	 * passing along the aforementioned and entering the client into a waiting status,
	 * inside this waiting status inputs from the server can be processed.
	 * Such inputs may include start your turn, give me your score, stop your turn.
	 */
	private void addPlayerToGame() {
		
		yahtzeeClient client = new yahtzeeClient();
		client.setServerHostName(serverHostName);
		client.setPortNum(portNum);
		client.execute();

		client.addPlayer(userName);
		
		client.assignClientWaitState(true);
		client.triggerWaitState();
				
	}
}
