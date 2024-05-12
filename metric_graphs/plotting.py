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

def get_frame_variances(frame):

    # Append all trials together -> get average
    variances = []
    for col in frame.columns:
        variances.append(np.var(frame[col])) #Get average metrics over all trials
    return variances

def get_stat(scheduling_algorithm):
    directories = ['/home/vlnsha004/OS2_Assignment_VLNSHA004/fcfs2/', '/home/vlnsha004/OS2_Assignment_VLNSHA004/sjf2/'] # For both sjf and fcfs

    ave_of_averages = [] # List to store the average of the averages for each program
    ave_of_variances = [] # List to store the average of the variances for each program 
    trial_frame = pd.DataFrame()

    for i in range(5, 105, 20):
        for trial in range(1, 11):
            temp_df = pd.DataFrame([get_averages(directories[scheduling_algorithm] + f'turnaround_time_{i}_{scheduling_algorithm}_{trial}.txt')])
            trial_frame = pd.concat([trial_frame, temp_df], ignore_index=True)
        ave_of_averages.append(get_frame_averages(trial_frame)) # Get average for 10 trials - plotting value
        ave_of_variances.append(get_frame_variances(trial_frame))

    return (ave_of_averages, ave_of_variances)

def main():
    set_0 = get_stat(0)
    set_1 = get_stat(1)


    # Define the width of the bars
    width = 6.4

    # Define the positions of the bars on the x-axis
    x = np.arange(5, 105, 20)

    # Plot set_0 against set_1 for each metric, varying number of patrons in a bargraph including variance
    metrics = ['Turnaround Time', 'Response Time', 'Waiting Time', 'Throughput']
    for metric in metrics:
        fig, ax = plt.subplots()
        
        # Plot the bars for set_0 and set_1 with a certain width
        rects1 = ax.bar(x - width/2, [x[metrics.index(metric)] for x in set_0[0]], width, label='FCFS')
        rects2 = ax.bar(x + width/2, [x[metrics.index(metric)] for x in set_1[0]], width, label='SJF')

        # Add some text for labels, title and custom x-axis tick labels, etc.
        ax.set_xlabel('Number of Patrons')
        
        # Check if the current metric is 'Throughput' and set the y-axis label accordingly
        if metric == 'Throughput':
            ax.set_ylabel('Patrons Served per minute')
        else:
            ax.set_ylabel('Average Time (milliseconds)')
        
        ax.set_title(f'Average {metric.capitalize()} Comparison FCFS vs. SJF')
        ax.set_xticks(x)
        ax.set_xticklabels(x)
        ax.legend()

        fig.tight_layout()

        plt.show()

        # Plot the variance on a separate plot
        fig, ax = plt.subplots()

        # Plot the bars for the variances of set_0 and set_1 with a certain width
        rects1 = ax.bar(x - width/2, [x[metrics.index(metric)] for x in set_0[1]], width, label='FCFS')
        rects2 = ax.bar(x + width/2, [x[metrics.index(metric)] for x in set_1[1]], width, label='SJF')

        # Add some text for labels, title and custom x-axis tick labels, etc.
        ax.set_xlabel('Number of Patrons')
        ax.set_ylabel('Variance')
        ax.set_title(f'Variance of {metric.capitalize()} Comparison FCFS vs. SJF')
        ax.set_xticks(x)
        ax.set_xticklabels(x)
        ax.legend()

        fig.tight_layout()

        plt.show()
        
if __name__ == '__main__':
    main()