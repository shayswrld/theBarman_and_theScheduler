package barScheduling;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.lang.model.element.ModuleElement.DirectiveKind;

import java.util.Comparator;
import barScheduling.DrinkOrder.Drink;
import barScheduling.DrinkOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/*
 Barman Thread class.
 */

public class Barman extends Thread {
	

	private CountDownLatch startSignal;
	private BlockingQueue<DrinkOrder> orderQueue;
	public int drinksServed;
	private static long totalLengthStart, totalLengthEnd, totalLength; //for all the metrics
	private int schedAlg;
	private int q;

	// Comparator to compare Drink Orders
	class COMPARING implements Comparator<DrinkOrder> {
		@Override
    	public int compare(DrinkOrder order1, DrinkOrder order2) {
		// uses compareTo method on execution time of drinkorder
		if (order1.getExecutionTime() <= order2.getExecutionTime()) //order1 has shorter 'cpu burst' than order 2
		{
			return -1; //order1 prioritized if lower execution time
		}
		else return 1;
    }
	}

	Barman(  CountDownLatch startSignal,int schedAlg) {
		drinksServed = 0;
		if (schedAlg==0 || schedAlg==2 || schedAlg==3 || schedAlg==4)
			{this.orderQueue = new LinkedBlockingQueue<>();}
		//FIX below
		//Changed to use Priority blocking queue to compare the execution time of the orders
		// Implementation of non-preemptive Shortest Job First
		else this.orderQueue = new PriorityBlockingQueue <DrinkOrder> (11, new COMPARING()); //this just does the same thing**
		
	    this.startSignal=startSignal;
		this.schedAlg = schedAlg; //Changed to use RR if arg = 2
		this.drinksServed = 0; // Added to count drinks for throughput calculation
		this.q = 90; // Set time quantum to 80ms
	}

	public void writeToFile(String data) {
		try {
	    synchronized (Patron.fileW) {
	    	Patron.fileW.write(data);
	    }
		} catch (IOException e) {
			System.out.println("Error writing to file");
			e.printStackTrace();
		}
	}
	
	public void placeDrinkOrder(DrinkOrder order) throws InterruptedException {
        orderQueue.put(order);

    }
	
	public void run() {
		try {
			DrinkOrder nextOrder;
			startSignal.countDown(); //barman ready
			startSignal.await(); //check latch - don't start until told to do so
			totalLengthStart = System.currentTimeMillis();
			int processTime = 0; //Added for round Robin

			while(true) {
				nextOrder=orderQueue.take();

				// Added for Round Robin, check if we are serving the next Patron
				if (schedAlg == 2 || schedAlg == 3 || schedAlg == 4) {
					processTime = nextOrder.getRemainingExecutionTime();
					if (processTime > q) { //Drink takes longer than time quantum
						System.out.println("---Barman preparing **PART** order for patron "+ nextOrder.toString());
						sleep(q); //processing order - will always sleep q if drink takes longer than q
						nextOrder.setExecutionTime(processTime - q); // Set remaining time to process
						orderQueue.put(nextOrder); // Put the drink back in the queue
						System.out.println("---Barman has sent order for "+ nextOrder.toString() + " to the back of the queue");
						continue; // Continue to next iteration
					}
				}

				//BELOW THE SAME AS BEFORE
				System.out.println("---Barman preparing order for patron "+ nextOrder.toString());
				sleep(nextOrder.getExecutionTime()); //processing order
				System.out.println("---Barman has made order for patron "+ nextOrder.toString());
				nextOrder.orderDone();
				drinksServed += 1;
			}
			

			
				
		} catch (InterruptedException e1) {
			System.out.println("---Barman is packing up ");
			totalLengthEnd = System.currentTimeMillis();
			float timeTaken = (float) (totalLengthEnd - totalLengthStart);
			
			System.out.println("---Barman has served "+ drinksServed + " drinks in " + (timeTaken) + " milliseconds");
			// patrons served per millisecond * 1000 => throughput per second
			float throughput = (float) SchedulingSimulation.noPatrons / ((timeTaken) / 1000); // Perform division using floating-point arithmetic
			System.out.println("---Barman serves "+ Float.toString(throughput) + " patrons per minute");
			writeToFile(",,,,," + throughput); // Write drinks served per minute in 6th column
		}
	}
}


