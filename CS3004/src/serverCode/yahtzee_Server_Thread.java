package serverCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import baseCode.YahtzeeGameLogic;
import serverCode.yahtzee_Server;
import serverCode.yahtzee_Server.Server;
import clientCode.*;

/**
 * Main server thread which acts as the manager between clients
 * Individual Client instances can be accessed via obtaining their output stream
 * 
 * This thread implementation extends parent class yahtzee_Server
 * such that it can access key identification variables such as onlineUsers
 * which in turn allows control over the state
 * @author Shane
 *
 */
public class yahtzee_Server_Thread extends yahtzee_Server implements Runnable{

	private Socket socket = null;
	private Server server;
	private Thread client;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private boolean continueServer;
	private String name;
	
	//private int controlIndex = 0; //because we start off with player ones turn
	//explicitly call player 2's turn from this point forward up to player n
	
	private String activePlayer;
	
	yahtzee_Server_Thread(Socket socket, Server server) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.server = server;
		continueServer = true;
	}

	public void run() {
		try {
			try{
				//where in refers to anything that client prints to its outstream
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
			}
			catch(IOException e){
				System.out.println("I/O Failed");
				e.printStackTrace();
				System.exit(-1);
			}

			while (continueServer) {
				try{
					//whilst the server is running process the input from the client instances to the dedicated function
					processInput(in.readLine());
				}
				catch(IOException e){
					System.out.println("Could Not Read From Server");
					e.printStackTrace();
					System.exit(-1);
				}
				
				//sleep 100 milliseconds and yield to any other threads deemed higher priority
				Thread.sleep(100);
				Thread.yield();
			}

		} 
		catch (InterruptedException e) {
			e.printStackTrace();
			continueServer = false;
			if(client != null){
				client.interrupt();
			}
		}
	}

	/**
	 * A key function in controlling the state of the game
	 * 
	 * If the client wishes to call a function, it must specify the function 
	 * using outstream.println("function")
	 * anything outstreamed after that point can be arguments to that function
	 * @param theInput String literal sent by client instance
	 * @throws IOException Input/Output exception
	 * @throws InterruptedException
	 */
	public void processInput(String theInput) throws IOException, InterruptedException{
		
		if(theInput.equals("addPlayer")) 
			addPlayer();
		else if((theInput.equals("sendActiveNameToClients"))) {
			activePlayer = in.readLine();
			for(int i = 0; i < onlineUsers.size(); i++) {
				//we read the first line when entering the function
				//where theInput is what we sent first(the command)
				//we read the next line which contains the clients name and broadcast to all existing clients
				if(i != controlIndex) {
					onlineUsers.get(i).getClientOutputStream().println("\nIt is Player "+activePlayer+"'s turn\n");
				}
			}
		}
		else if((theInput.equals("displayCurrentClientScore"))) {
			
			if((pastPlayerName = in.readLine()) != null) {
				pastPlayerScore = in.readLine();
			}
			
			for(int i = 0; i < onlineUsers.size(); i++) {
				if(i != controlIndex) { //if not other player
					//we read the first line when entering the function
					//where theInput is what we sent first(the command)
					//we read the next line which contains the clients name and broadcast to all existing clients
					onlineUsers.get(i).getClientOutputStream().println("updateOtherUsersOnScore");
					
					onlineUsers.get(i).getClientOutputStream().println(pastPlayerName); //other player name
					onlineUsers.get(i).getClientOutputStream().println(pastPlayerScore); //other player score
				}
			}
			
			playersAndScore.put(pastPlayerName, Integer.parseInt(pastPlayerScore));
		}
		else if((theInput.equals("updateWhoIsWinning"))) {
			
			int max = (int) Collections.max(playersAndScore.values());
			
			String winningPlayer = null;
			
			for(Entry<String, Integer> entry : playersAndScore.entrySet()) {
				if(entry.getValue() == max) {
					winningPlayer = entry.getKey();
				}
			}
			
			int position = new ArrayList<String>(playersAndScore.keySet()).indexOf(winningPlayer);
			
			for(int j = 0; j < onlineUsers.size(); j++) {
				if(j != position) {
					onlineUsers.get(j).getClientOutputStream().println("Player "+winningPlayer+" is in the lead");
					onlineUsers.get(j).getClientOutputStream().println("You are losing");
				}
				else {
					onlineUsers.get(j).getClientOutputStream().println("You are winning!");
				}
			}
			
		}
		else if((theInput.equals("IHaveFinished"))) {
			numOfPlayersFinished++;
			
			if(numOfPlayersFinished == onlineUsers.size()) {
				broadCastFinalWinner();
			}
		}
		else if((theInput.equals("turnChange"))) {
			
			if(controlIndex + 1 >= onlineUsers.size()){
				controlIndex = 0; //all users have had a turn, reset the control Index so that user 1 starts next round
			}
			else {
				controlIndex++;
			}
			System.out.println("Control Index " +controlIndex);
			onlineUsers.get(controlIndex).getClientOutputStream().println("startClientTurn");
		}
		else if((theInput.equals("clientReady"))) {
			readyCounter++;
			
			if(readyCounter >= onlineUsers.size() && readyCounter >= 2) {
				beginGame();
			}
			else {
				System.out.println("Waiting on one more player");
			}
			System.out.println(readyCounter);
		}
	}
	
	/*
	 * Begins the actual game with the first person who connected starting first 
	 */
	private void beginGame() {
	
		if(readyCounter >= onlineUsers.size()) {
			System.out.println("Game Started");
			//Starts with first user that joined
			onlineUsers.get(0).getClientOutputStream().println("startClientTurn");
		}		
	}
	
	/**
	 * End of game function that broadcasts who won and who lost to everyone.
	 * @throws InterruptedException 
	 */
	private void broadCastFinalWinner() throws InterruptedException {
		
		int max = (int) Collections.max(playersAndScore.values());
		
		String winningPlayer = null;
		
		for(Entry<String, Integer> entry : playersAndScore.entrySet()) {
			if(entry.getValue() == max) {
				winningPlayer = entry.getKey();
			}
		}
		
		int position = new ArrayList<String>(playersAndScore.keySet()).indexOf(winningPlayer);
		
		for(int j = 0; j < onlineUsers.size(); j++) {
			if(j != position) {
				onlineUsers.get(j).getClientOutputStream().println("\n\nFinal Result\n");
				onlineUsers.get(j).getClientOutputStream().println("\t\t\tPlayer\tScore");
				onlineUsers.get(j).getClientOutputStream().println("Highest Player Score :  "+winningPlayer+"\t"+max);
				onlineUsers.get(j).getClientOutputStream().println("Player "+winningPlayer+" has won");
				onlineUsers.get(j).getClientOutputStream().println("You have lost");
				onlineUsers.get(j).getClientOutputStream().println("Better luck next time");
				onlineUsers.get(j).getClientOutputStream().println("Closing the Game");
				onlineUsers.get(j).getClientOutputStream().flush();
			}
			else {
				onlineUsers.get(j).getClientOutputStream().println("\n\nFinal Result\n");

				onlineUsers.get(j).getClientOutputStream().println("Congratulations!");
				onlineUsers.get(j).getClientOutputStream().println("You have won the game");
				onlineUsers.get(j).getClientOutputStream().println("Closing the Game");
			}
		}
		
		for(int i = 0; i < onlineUsers.size();i++) {
			onlineUsers.get(i).getClientOutputStream().println("closeConnections");
		}
		continueServer = false;		
	}
	
	/**
	 * Function to return the clients output stream	
	 * @return the output stream of the indexed client
	 */
	public PrintWriter getClientOutputStream() {
		return out;
	}
	
	/**
	 * Function to return the clients input stream
	 * @return the input stream of the indexed client
	 */
	public BufferedReader getInputStream() {
		return in;
	}
	
	public void addPlayer(){
		try{
			String connectingPlayer = in.readLine();
			System.out.println("Adding "+ connectingPlayer);
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}
}
