# OS2 Assignment - THE MASTER SCHEDULE: ANDRE STRIKES AGAIN 

The program uses concurrent threads to simulate bar. The single barman serves each Patron at the bar, representing
a typical scheduler - processes scenario.

## Usage

- To compile the program:
    1) make

    OR
    
    1) make clean
    2) make

- To run the program:
    1) Ensure you are in the directory containing the makefile, src and bin folders.
    2) terminal command: make run ARGS="$no.Patrons $SchedulerAlgorithm"
        - no.Patrons -> the number of patrons in the program
        - SchedulerAlgorithm -> 0 = First Come First Serve, 1 = Shortest Job First
    3) Data will be written to the same directory as makefile with Patron ID, arrivalTime, turnaroundTime,  responseTime, waitingTime and Throughput
    4) Throughtput will be measured in Patrons Served per second

    
