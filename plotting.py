# Shaylin Velen
# VLNSHA004
# 08/05/2024
# Program to generate histogram of Barman (scheduler) performance metrics

import numpy as np
import matplotlib.pyplot as plt
import os
import pandas as pd


# Define the data for the two programs
def get_averages(path):

    # Append all trials together -> get average
    df = pd.read_csv(path, header = None, sep = ',')
    df = df.iloc[:, 2:] # Obtain only columns for 'turnaroundTime', 'responseTime', 'waitingTime', 'throughput'

    df.columns = ['turnaroundTime', 'responseTime', 'waitingTime', 'throughput'] # Assign name for each column
    averages = []
    for col in df.columns:
        averages.append(np.mean(df[col])) #Get average metrics over all trials

    return averages

def get_frame_averages(frame):

    # Append all trials together -> get average
    averages = []
    for col in frame.columns:
        averages.append(np.mean(frame[col])) #Get average metrics over all trials
    return averages

def main():
    directories = ['/home/vlnsha004/OS2_skeletonCode/sjf', '/home/vlnsha004/OS2_skeletonCode/fcfs'] # For both sjf and fcfs
    bars = {} # Store the average of the averages for each scheduling algorithm
    for dir in directories:
        directory_in_str = dir
        directory = os.fsencode(directory_in_str)
        ave_of_averages = [] # List to store the average of the averages for each program
        for file in os.listdir(directory):
            filename = os.fsdecode(file)
            if filename.endswith(".txt"):
                temp_df = pd.DataFrame([get_averages(os.path.join(directory_in_str, filename))])
                ave_of_averages = get_frame_averages(temp_df)
        bars[dir.split('/')[-1]] = ave_of_averages
        print(bars[dir.split('/')[-1]])

    # Define the labels for the x-axis
    labels = ['Avg TurnAroundTime FCFS', 'Avg TurnAroundTime SJF', 'Avg ResponseTime FCFS', 'Avg ResponseTime SJF', 'Avg WaitingTime FCFS', 'Avg WaitingTime SJF']

    # Define the data for the bar graph
    data = []
    for i in range(3):
        data.append(bars['fcfs'][i])
        data.append(bars['sjf'][i])

    # Create the bar graph
    plt.bar(labels, data, color=['blue', 'red', 'blue', 'red', 'blue', 'red'])
    plt.xlabel('Metric')
    plt.ylabel('Average Time')
    plt.title('Average Metrics Comparison FCFS vs. SJF')
    
    # Create a separate bar graph for average throughput
    throughput_labels = ['Throughput FCFS', 'Throughput SJF']
    throughput_data = [bars['fcfs'][3], bars['sjf'][3]]
    plt.figure()  # Create a new figure for the separate graph
    plt.bar(throughput_labels, throughput_data, color=['purple', 'yellow'])
    plt.xlabel('Scheduling Algorithm')
    plt.ylabel('Average Throughput (patrons per minute)')
    plt.title('Average Throughput Comparison FCFS vs. SJF')   
    plt.show()
        
if __name__ == '__main__':
    main()