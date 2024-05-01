//M. M. Kuttel 2024 mkuttel@gmail.com
package barScheduling;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/*
 This is the basicclass, representing the patrons at the bar
 */

public class Patron extends Thread {
	
	private Random random = new Random();// for variation in Patron behaviour

	private CountDownLatch startSignal; //all start at once, actually shared
	private Barman theBarman; //the Barman is actually shared though

	private int ID; //thread ID 
	private int lengthOfOrder;
	private long startTime; //for all the metrics
	public static FileWriter fileW;

	private DrinkOrder [] drinksOrder;
	private long orderEnd; //Store time when all orders have been made
	private long responseTime; //Store the response time (time first drink recieved)
	private long waitingTime; //Store the time taken that order was not worked on
	
	Patron( int ID,  CountDownLatch startSignal, Barman aBarman) {
		this.ID=ID;
		this.startSignal=startSignal;
		this.theBarman=aBarman;
		this.lengthOfOrder=random.nextInt(5)+1;//between 1 and 5 drinks
		drinksOrder=new DrinkOrder[lengthOfOrder];
		responseTime= Long.MAX_VALUE;
		waitingTime = 0;
	}
	
	public void writeToFile(String data) throws IOException {
	    synchronized (fileW) {
	    	fileW.write(data);
	    }
	}
	
	public void run() {
		try {
			//Do NOT change the block of code below - this is the arrival times
			startSignal.countDown(); //this patron is ready
			startSignal.await(); //wait till everyone is ready
	        int arrivalTime = random.nextInt(300)+ID*100;  // patrons arrive gradually later
	        sleep(arrivalTime);// Patrons arrive at staggered  times depending on ID 
			System.out.println("thirsty Patron "+ this.ID +" arrived");
			//END do not change
			
				
	        //create drinks order
	        for(int i=0;i<lengthOfOrder;i++) {
	        	drinksOrder[i]=new DrinkOrder(this.ID);
	        }

			System.out.println("Patron "+ this.ID + " submitting order of " + lengthOfOrder +" drinks"); //output in standard format  - do not change this
	        startTime = System.currentTimeMillis(); //started placing orders
			for(int i=0;i<lengthOfOrder;i++) {
				System.out.println("Order placed by " + drinksOrder[i].toString());
				theBarman.placeDrinkOrder(drinksOrder[i]);
			}
			orderEnd = System.currentTimeMillis(); //Start timer for each drink at start of order
			

			for(int i=0;i<lengthOfOrder;i++) {
				// Find order with lowest time taken to get response
				long orderTime = drinksOrder[i].waitForOrder(orderEnd); // How long did this order take to complete
				waitingTime += orderTime - drinksOrder[i].getExecutionTime(); // Waiting time is time bartender spends not working on drink 
				if (orderTime < responseTime) { 
					responseTime = drinksOrder[i].waitForOrder(orderEnd); // Update response time
				}
			}

			long turnaroundTime = System.currentTimeMillis() - startTime; //Changed from totalTime -> turnaround time
			writeToFile( String.format("%d,%d,%d,%d,%d\n",ID,arrivalTime,turnaroundTime, responseTime, waitingTime)); //Write instrumentation
			System.out.println("Patron "+ this.ID + " got order in " + turnaroundTime);
			
			
		} catch (InterruptedException e1) {  //do nothing
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
}
}
	

