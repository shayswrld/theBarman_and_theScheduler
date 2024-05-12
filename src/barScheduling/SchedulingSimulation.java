//M. M. Kuttel 2024 mkuttel@gmail.com

package barScheduling;
// the main class, starts all threads


import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;


public class SchedulingSimulation {
	static int noPatrons=5; //number of customers - default value if not provided on command line
	static int sched=1; //which scheduling algorithm, 0= FCFS
	static int trial=1; //number of trials
	private static long totalLengthStart, totalLengthEnd, totalLength; //for all the metrics
	static CountDownLatch startSignal;

	
	static Patron[] patrons; // array for customer threads
	static Barman Andre;
	static FileWriter writer;

	public static void writeToFile(String data) throws IOException {
	    synchronized (writer) {
	    	writer.write(data);
	    }
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		
		//deal with command line arguments if provided
		if (args.length==1) {
			noPatrons=Integer.parseInt(args[0]);  //total people to enter room
		} else if (args.length==2) {
			noPatrons=Integer.parseInt(args[0]);  //total people to enter room
			sched=Integer.parseInt(args[1]); //scheduling algorithm
		} else {
			noPatrons=Integer.parseInt(args[0]);  //total people to enter room
			sched=Integer.parseInt(args[1]); //scheduling algorithm
			trial = Integer.parseInt(args[2]); //number of trial			
		}
		
		writer = new FileWriter("fcfs2/turnaround_time_"+Integer.toString(noPatrons) + "_" + Integer.toString(sched)+ "_"+ Integer.toString(trial) + ".txt", false);
		Patron.fileW=writer;

		startSignal= new CountDownLatch(noPatrons+2);//Barman and patrons and main method must be ready
		
		//create barman
        Andre= new Barman(startSignal,sched); 
     	Andre.start();
  
	    //create all the patrons, who all need access to Andre
		patrons = new Patron[noPatrons];
		for (int i=0;i<noPatrons;i++) {
			patrons[i] = new Patron(i,startSignal,Andre);
			patrons[i].start();
		}
		
		System.out.println("------Andre the Barman Scheduling Simulation------");
		System.out.println("-------------- with "+ Integer.toString(noPatrons) + " patrons---------------");

      	startSignal.countDown(); //main method ready
      	
      	//wait till all patrons done, otherwise race condition on the file closing!
      	for (int i=0;i<noPatrons;i++) {
			patrons[i].join();
		}


    	System.out.println("------Waiting for Andre------");
    	Andre.interrupt();   //tell Andre to close up
    	Andre.join(); //wait till he has
      	writer.close(); //all done, can close file
      	System.out.println("------Bar closed------");
	}

}
