#!/bin/bash

# Compile the program & remove all textfiles (previous data)
make clean
make

# Run the program with varying parameters
for n in {5..105..20}; 
do
  # Run the program 10 times for each parameter
  for i in {1..10};
  do
    # Run for FCFS/SJF
    make run ARGS="$n 0 $i"
  done
done

