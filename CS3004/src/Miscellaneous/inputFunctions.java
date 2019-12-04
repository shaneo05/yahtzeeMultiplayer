package Miscellaneous;

import java.util.Scanner;

/**
 * Class dedicated to the handling of user input
 * @author Shane Gill
 *
 */
public class inputFunctions {

	  //An integer is expected, String is then parsed to int
	  public int inputInt(String Prompt) {
		    int result = 0;
		    try {
		      result = Integer.parseInt(stringInput(Prompt).trim());
		    } catch (Exception e) {
		      result = 0;
		    }
		    return result;
	  }//inputInt
		 
		  //Set up the input
	  public String stringInput(String prompt) {
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
	  public String cmdInput(String prompt) {
		    Scanner input = new Scanner(System.in);
		    System.out.println(prompt);
		    String entry = input.next();
		    return entry;
	  }
}
