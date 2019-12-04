package baseCode;

import java.util.*;
public class YahtzeeGameLogic {
	
	private boolean notMyTurn;
	
	private boolean clientInstanceRunning;
	
	
	
	private int[][] currentScoreRecord;
	private int[][] canScoreThisRound;
	
	private int[] theDice;
	private int[] rerollDice;
	
	private int noRolls;
	private int temp;
	
	private int currentScore = 0;

	private int rerollDie;
	
	private int iteration = 1;
	
	private boolean reroll = true;
	
	private boolean gameFinished = false;
	
	
	public YahtzeeGameLogic(){
		currentScoreRecord = new int[][] {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}};
		canScoreThisRound = new int[][] {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}};

	    theDice = new int[] {0, 0, 0, 0, 0 };// dice scores
	    noRolls = 0;
	    temp = 0;
	    reroll = true;
	    rerollDice = new int[5];
	    rerollDie = 0;

		clientInstanceRunning = true;

	}
	
	//Read an integer
	  public static int inputInt(String Prompt) {
	    int result = 0;
	    try {
	      result = Integer.parseInt(input(Prompt).trim());
	    } catch (Exception e) {
	      result = 0;
	    }
	    return result;
	  }//inputInt
	 
	  //Set up the input
	  public static String input(String prompt) {
	    String inputLine = "";
	    System.out.print(prompt);
	    try {
	      java.io.InputStreamReader sys = new java.io.InputStreamReader(
	          System.in);
	      java.io.BufferedReader inBuffer = new java.io.BufferedReader(sys);
	      inputLine = inBuffer.readLine();
	    } catch (Exception e) {
	      String err = e.toString();
	      System.out.println(err);
	    }
	    return inputLine;
	}//input
	 
	//Six sided die roller
	private static int die() {
		Random r = new Random();
		return r.nextInt(6)+1;
	}

	private static void showDice(int[] theseDice) {
	      System.out.println("You rolled: " + theseDice[0] + " " + theseDice[1] + " "+ theseDice[2] + " "+ theseDice[3] + " " + theseDice[4]);
	}//showDice

	private static int showCurrentScore(int[][] currentScoreRecord) {
		//Scoring Y FH LS SS 4K 3K On Tw Th Fo Fi Si C

		int score = 0;
	    String[] options = {"Yahtzee", "Full-House", "Long-Straight", "Short-Straight", "Quad", "Triple", "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Chance"};

		//Calculate current score
		for (int i=0; i<currentScoreRecord.length; i++) {
			score = score + currentScoreRecord[i][1];
		}//endfor
		//Show what's been scored
	    for (int i=0; i<13; i++) {
	    	System.out.println(options[i] + " scoring : " + currentScoreRecord[i][1] + " points");
	    	}
	    System.out.println("");
	    return score;
		
	}//showCurrentScore

	
    private static int[][] whatCanBeScored(int[][] currentScoreRecord, int[] theDice) {
		//Scoring Y FH LS SS 4K 3K On Tw Th Fo Fi Si C
    	//Updates canScoreThisRound
    	
	    int[][] canScoreThisRound = new int[13][2];
	    int count = 0;
	    int score = 0;
	    boolean found4K = false;
	    boolean found3K = false;
	    int ones = 0, twos = 0, threes = 0, fours = 0, fives = 0, sixes = 0;
	    Arrays.sort(theDice);
	    
	    //Check the number scores
	    //Check first if the number has been scored
	    //If there is a score possible then note it
	    
	    if (currentScoreRecord[0][0] == 0) {
	    	//CHECK FOR YAHTZEE
	    	count = 0;
	        for (int i = 0; i < theDice.length; i++){
	            if(theDice[0] != theDice[i]){
	                count++;
	            }
	        }
	        if(count == 0){
	            canScoreThisRound[0][0] = 1;
	            canScoreThisRound[0][1] = 50;
//	            System.out.println("Can score a Yahtzee for 50");
	        }
	    }//Y
	    	
	    if (currentScoreRecord[1][0] == 0) {
	    	//CHECK FULL HOUSE
	    	count = 0;
	    	//Check it's not a Y
	        for (int i = 0; i < 4; i++){
	        	if(theDice[0] != theDice[i]){
	                count++;
	            }
	        }
	        if(count >0){
	    		//Two conditions 2 then 3 or 3 then 2

	    		count = 0;

	    		//Check first two the same then check last three
		        for (int i = 0; i < 2; i++){
		        	if(theDice[0] != theDice[i]){
		                count++;
		            }
		        }
	
		        //Check last three the same
		        for (int i = 2; i < 5; i++){
		        	if(theDice[2] != theDice[i]){
		        		count++;
			            }
			    }	
	
	            if (count == 0) {
		            canScoreThisRound[1][0] = 1;
		            canScoreThisRound[1][1] = 25;
	            } else {
	            	count = 0;
	    		    //Check first three the same then check last two
	    	        for (int i = 0; i < 3; i++){
	    	        	if(theDice[0] != theDice[i]){
	    	                count++;
	    	            }
	    	        }
	
	    	        //Check last three the same
	    	        for (int i = 3; i < 5; i++){
	    	        	if(theDice[3] != theDice[i]){
	    	        		count++;
	    		            }
	    		    }	

		            if (count == 0) {
			            canScoreThisRound[1][0] = 1;
			            canScoreThisRound[1][1] = 25;
		            }

	            } //end if
	        } else {
	        	//It's a Y
//	            System.out.println("It's  a Yahtzee - move on");
	        }
	   }//FH
	        
	    if (currentScoreRecord[2][0] == 0) {
	    	//CHECK LONG STRAIGHT

		    //Check for Straight D1-5
	    	if ((theDice[0] == theDice[1] - 1) && (theDice[1] == theDice[2] - 1) && (theDice[2] == theDice[3] - 1) && (theDice[3] == theDice[4] - 1)){
	            canScoreThisRound[2][0] = 1;
	            canScoreThisRound[2][1] = 40;
//	            System.out.println("Can score a LS for 40");
	     	}
	    }//LS

		if (currentScoreRecord[3][0] == 0) {
			//CHECK SHORT STRAIGHT		
			
		    //Check for Short Straight D1-4, D2-5
			if ((theDice[0] == theDice[1] - 1) && (theDice[1] == theDice[2] - 1) && (theDice[2] == theDice[3] - 1)){
	            canScoreThisRound[3][0] = 1;
	            canScoreThisRound[3][1] = 30;
//	            System.out.println("Can score a SS for 30");
	     	}
	    	if ((theDice[1] == theDice[2] - 1) && (theDice[2] == theDice[3] - 1) && (theDice[3] == theDice[4] - 1)){
	            canScoreThisRound[3][0] = 1;
	            canScoreThisRound[3][1] = 30;
//	            System.out.println("Can score a SS for 30");
	     	}
	 	}//SS
    
	 
		if (currentScoreRecord[4][0] == 0) {
		    //CHECK FOR 4 OF A KIND
			count = 0;
			found4K = false;
		    //Check first set are the same
	        for (int i = 0; i < 4; i++){
	            if(theDice[0] != theDice[i]){
	                count++;
	            }
	        }
	        if (count == 0) {
	        	found4K = true;
//	        	System.out.println("4K true here first");
	        }
	        
	        count = 0;
		    //Check last set are the same
	        for (int i = 1; i < 5; i++){
	            if(theDice[1] != theDice[i]){
	                count++;
	            }
	        }
	        if (count == 0) {
	        	found4K = true;
//	        	System.out.println("4K true here second");

	        }

	        
	        if (found4K) {
//	        	System.out.println("It's 4K");
	        	score = 0;
		        for (int i = 0; i < theDice.length; i++){
		            score = score + theDice[i];
		        }
	        	canScoreThisRound[4][0] = 1;
	            canScoreThisRound[4][1] = score;
	        }
	            
		}//4K
		
		if (currentScoreRecord[5][0] == 0) {
		    //CHECK FOR 3 OF A KIND
			count = 0;
			found3K = false;
		    //Check first set are the same
	        for (int i = 0; i < 3; i++){
	            if(theDice[0] != theDice[i]){
	                count++;
	            }
	        }
	        if (count == 0) {
	        	found3K = true;
//	        	System.out.println("3K true here first");
	        }
	        
	        count = 0;
		    //Check last set are the same
	        for (int i = 1; i < 4; i++){
	            if(theDice[1] != theDice[i]){
	                count++;
	            }
	        }
	        if (count == 0) {
	        	found3K = true;
//	        	System.out.println("3K true here second");

	        }

	        count = 0;
		    //Check last set are the same
	        for (int i = 2; i < 5; i++){
	            if(theDice[1] != theDice[i]){
	                count++;
	            }
	        }
	        if (count == 0) {
	        	found3K = true;
//	        	System.out.println("4K true here third");

	        }
        
	        if (found3K) {
//	        	System.out.println("It's 3K");
	        	score = 0;
		        for (int i = 0; i < theDice.length; i++){
		            score = score + theDice[i];
		        }
	            canScoreThisRound[5][0] = 1;
	            canScoreThisRound[5][1] = score;
	        }
	            
		}//3K		
		
		if (currentScoreRecord[6][0] == 0) {
			//Check 1s
			ones = 0;
	        for (int i = 0; i < theDice.length; i++){
	            if(theDice[i] == 1){
	                ones = ones + 1;
	            }
	        }			
            canScoreThisRound[6][0] = 1;
            canScoreThisRound[6][1] = ones;
		}

		if (currentScoreRecord[7][0] == 0) {
			//Check 2s
			twos = 0;
	        for (int i = 0; i < theDice.length; i++){
	            if(theDice[i] == 2){
	                twos = twos + 2;
	            }
	        }			
            canScoreThisRound[7][0] = 1;
            canScoreThisRound[7][1] = twos;
		}

		if (currentScoreRecord[8][0] == 0) {
			//Check 3s
			threes = 0;
	        for (int i = 0; i < theDice.length; i++){
	            if(theDice[i] == 3){
	                threes = threes + 3;
	            }
	        }			
            canScoreThisRound[8][0] = 1;
            canScoreThisRound[8][1] = threes;
		}

		if (currentScoreRecord[9][0] == 0) {
			//Check 4s
			fours = 0;
	        for (int i = 0; i < theDice.length; i++){
	            if(theDice[i] == 4){
	                fours = fours + 4;
	            }
	        }			
            canScoreThisRound[9][0] = 1;
            canScoreThisRound[9][1] = fours;
		}

		if (currentScoreRecord[10][0] == 0) {
			//Check 5s
			fives = 0;
	        for (int i = 0; i < theDice.length; i++){
	            if(theDice[i] == 5){
	                fives = fives + 5;
	            }
	        }			
            canScoreThisRound[10][0] = 1;
            canScoreThisRound[10][1] = fives;
		}
		
		if (currentScoreRecord[11][0] == 0) {
			//Check 6s
			sixes = 0;
	        for (int i = 0; i < theDice.length; i++){
	            if(theDice[i] == 6){
	                sixes = sixes + 6;
	            }
	        }			
	        canScoreThisRound[11][0] = 1;
            canScoreThisRound[11][1] = sixes;
		}
		
		if (currentScoreRecord[12][0] == 0) {
			//Check chance
			canScoreThisRound[12][0] = 1;
			canScoreThisRound[12][1] = 0;
	        for (int i = 0; i < theDice.length; i++){
	        	canScoreThisRound[12][1] = canScoreThisRound[12][1] + theDice[i];
	        }			
		}
		
		return canScoreThisRound;
    }//whatCanBeScored
	 

	private static int[][] chooseWhatToScore(int[][] currentScoreRecord, int [][]canScoreThisRound){
		//Scoring Y FH LS SS 4K 3K On Tw Th Fo Fi Si C

	    int[][] newScoreRecord = new int[13][2];
	    int[][] potentialChoice = {{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}};
	    String[] options = {"Yahtzee", "Full-House", "Long-Straight", "Short-Straight", "Quad", "Triple", "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Chance"};
	    int choice = 0;
	    
	    //Check to see if something has been scored and if it can be scored
	    //Is current score = 0 (it's not been scored) and can score = 1 (it can be scored)
	    //PotentialChoice is 0 (it's not been scored), 1 (it's been scored)

	    newScoreRecord = currentScoreRecord;
	    System.out.println("With your roll you can select...");
	    //Present choices - check if it has been scored and if it can be scored
	    for (int i=0; i<13; i++) {
	    	if ((currentScoreRecord[i][0] == 0) && (canScoreThisRound[i][0] == 1)){
	    		potentialChoice[i][0] = 1;
	    		potentialChoice[i][1] = canScoreThisRound[i][1];
	    		System.out.println("Select " + i + " for " + options[i] + " scoring " + canScoreThisRound[i][1] + " points");
	    	}
	    }

	    //Choose and update score
	    choice = inputInt("Choose one choice! ");
		System.out.println("You have chosen " + options[choice]);
		newScoreRecord[choice][0] = 1;
		newScoreRecord[choice][1] = canScoreThisRound[choice][1];
		
		return newScoreRecord;
	}//chooseWhatToScore
	
	public void startTurn() {
				
		/*
	    Welcome to Yahtzee - Simon Taylor Sept 2019 
		Scoring - Y FH LS SS 4K 3K On Tw Th Fo Fi Si C
		
		currentScoreRecord - For each of the above {status, score}
		canScoreThisRound - For each of the above  {can it be scored? i.e. can be scored and not previously scored, score}
		theDice - {what's been rolled this turn}
		currentScore - current score
		
		showCurrentScore - calculate and show the current score from currentScoreRecord
		whatCanBeScored - update canScoreThisRound from theDice and currentScoreRecord
		chooseWhatToScore - user chooses from canScoreThisRound and update currentScoreRecord

		Game - 	13 rounds
				Show what's been scored
				Roll/reroll dice max 3 times
				Check what can be scored
				Choose what can be scored
				End of 13 rounds show final score and status
	    */

		

	    //System.out.println("Welcome to Yahtzee!");
	    
	    //Add loop for 13 rounds	    
	    while (iteration < 14) {
	      
	    	//Print current status and score
	    	System.out.println("\n\nRound " + iteration + " of 13" );
	    	System.out.println("Current score is " + currentScore);
	    	System.out.println("Your current scoring status is:\n");
	    	currentScore = showCurrentScore(currentScoreRecord);
	    	
	    	//Roll the dice  
	    	for (int i = 0; i < 5; i++) {
	    		theDice[i] = die();// sets the dice values
	    	}

	    	//See what we have rolled
	    	showDice(theDice);
	      
	    	//Check rerolls - three dice to reroll
	    	System.out.println("Three chances to reroll");
	    	noRolls = 0;
	    	reroll = true;
	    	rerollDie = 1;
	    	while (reroll){
	    	  noRolls++;
	    	  if (rerollDie > 0) {
	    		  rerollDie = inputInt("How many dice do you want to reroll? (1-5 - 0 for no dice)");
	    		  if (rerollDie > 0) {
	    			  for (int i=0; i<rerollDie;i++) {
	    				  temp = inputInt("Select a die (1-5) ");
	    				  rerollDice[i] = temp - 1; //adjust for array index
	    			  }
	    			  for (int i=0; i<rerollDie;i++) {
	    				  theDice[rerollDice[i]] = die();
	    			  }
	    			  showDice(theDice);
	    		  }
	    	  }else {
	    		  reroll = false;
	    	  }
	    	  if (noRolls == 3) {
	    		  reroll = false;
	    	  }
	    	}//while
	      
	    	//What can be scored?
	    	
	    	canScoreThisRound = whatCanBeScored(currentScoreRecord, theDice);

	    	//User chooses

	    	currentScoreRecord = chooseWhatToScore(currentScoreRecord, canScoreThisRound);
	      
	    	//Now print total score so far
	      
	    	this.currentScore = showCurrentScore(currentScoreRecord);
	    	
	    	System.out.println("\n\n\n\nYour Current Score is "+this.currentScore+"\n\n");
	    	
	    	break;
	    	
	    	/*
	    	while(notMyTurn) {
	    		try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	*/
	      
	    }//end for
	    iteration++;
	    
	    if(iteration >= 14) {
	    	currentScore = showCurrentScore(currentScoreRecord);
    		System.out.println("Your final score is " + currentScore);
    		System.out.println("You scored:");
    		showCurrentScore(currentScoreRecord);
    		gameFinished = true;
	    }
	}
	
	public boolean getGameFinished() {
		return gameFinished;
	}
	
	
	public void setNotMyTurn(boolean clientState) {
		notMyTurn = clientState;
	}
	
	public int getCurrentScore() {
		return currentScore;
	}
	
	public boolean getIsClientGameLoopRunning() {
		return clientInstanceRunning;
	}
	
	
	
	 
}//end class
	