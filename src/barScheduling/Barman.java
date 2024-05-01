package barScheduling;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.lang.model.element.ModuleElement.DirectiveKind;

import java.util.Comparator;
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
		if (schedAlg==0)
			this.orderQueue = new LinkedBlockingQueue<>();
		//FIX below
		//Changed to use Priority blocking queue to compare the execution time of the orders
		// Implementation of non-preemptive Shortest Job First
		else this.orderQueue = new PriorityBlockingQueue <DrinkOrder> (11, new COMPARING()); //this just does the same thing**
		
	    this.startSignal=startSignal;
		this.drinksServed = 0;
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

			while(true) {
				System.out.println(orderQueue);
				nextOrder=orderQueue.take();

				System.out.println("---Barman preparing order for patron "+ nextOrder.toString());
				sleep(nextOrder.getExecutionTime()); //processing order
				System.out.println("---Barman has made order for patron "+ nextOrder.toString());
				nextOrder.orderDone();
				drinksServed += 1;
				System.out.println("---Barman has served "+ drinksServed + " drinks");
			}
			

			
				
		} catch (InterruptedException e1) {
			System.out.println("---Barman is packing up ");
			totalLengthEnd = System.currentTimeMillis();
			System.out.println("---Barman has served "+ drinksServed + " drinks in " + (totalLengthEnd - totalLengthStart) + " milliseconds");
			long throughput = (drinksServed/((totalLengthEnd - totalLengthStart)/(1000))); //throughput - milliseconds / 1000 = seconds 
			System.out.println("---Barman has served "+ throughput + " drinks in 1 second");
			writeToFile(String.format(",,,,,%d", throughput)); //Write drinks served per minute in 6th column
		}
	}
}


