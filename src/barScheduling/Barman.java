package barScheduling;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.lang.model.element.ModuleElement.DirectiveKind;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/*
 Barman Thread class.
 */

public class Barman extends Thread {
	

	private CountDownLatch startSignal;
	private BlockingQueue<DrinkOrder> orderQueue;

	// Comparator to compare Strings
	class COMPARING implements Comparator<DrinkOrder> {
    	public int compare(DrinkOrder order1, DrinkOrder order2)
    {
		// uses compareTo method on execution time of drinkorder
		if (order1.getExecutionTime() > order2.getExecutionTime()) {
			return 1;
		}
		else if (order1.getExecutionTime() < order2.getExecutionTime()) {
			return -1;
		}
        return 1; //if equal any order would do, take order1
    }
	}


	
	Barman(  CountDownLatch startSignal,int schedAlg) {
		if (schedAlg==0)
			this.orderQueue = new LinkedBlockingQueue<>();
		//FIX below
		//Changed to use Priority blocking queue to compare the execution time of the orders
		// 
		else this.orderQueue = new PriorityBlockingQueue <DrinkOrder> (11, new COMPARING()); //this just does the same thing
		
	    this.startSignal=startSignal;
	}
	
	
	public void placeDrinkOrder(DrinkOrder order) throws InterruptedException {
        orderQueue.put(order);

    }
	
	
	public void run() {
		try {
			DrinkOrder nextOrder;
			
			startSignal.countDown(); //barman ready
			startSignal.await(); //check latch - don't start until told to do so

			while(true) {
				System.out.println(orderQueue);
				nextOrder=orderQueue.take();
				System.out.println(orderQueue);
				System.out.println("---Barman preparing order for patron "+ nextOrder.toString());
				sleep(nextOrder.getExecutionTime()); //processing order
				System.out.println("---Barman has made order for patron "+ nextOrder.toString());
				nextOrder.orderDone();
			}
				
		} catch (InterruptedException e1) {
			System.out.println("---Barman is packing up ");
		}
	}
}


