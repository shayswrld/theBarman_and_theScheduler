#!/bin/bash

# Compile the program & remove all textfiles (previous data)
make clean
make

# Run the program with varying parameters
for n in {5..100..5}; 
do
  # Run only for FCFS
  for j in 1
  do
    make run ARGS="$n $j"
  done
done
