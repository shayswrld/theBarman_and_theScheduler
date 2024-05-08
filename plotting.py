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
    fcfs_df = pd.read_csv(path, header = None, sep = ',')
    fcfs_df = fcfs_df.iloc[:, 2:] # Obtain only columns for 'turnaroundTime', 'responseTime', 'waitingTime', 'throughput'

    fcfs_df.columns = ['turnaroundTime', 'responseTime', 'waitingTime', 'throughput'] # Assign name for each column
    averages = []
    for col in fcfs_df.columns:
        averages.append(np.mean(fcfs_df[col])) #Get average metrics over all trials

    return averages

def main():
    directories = ['/home/vlnsha004/OS2_skeletonCode/sjf', '/home/vlnsha004/OS2_skeletonCode/fcfs'] # For both sjf and fcfs
    bars = {} # Store the average of the averages for each scheduling algorithm
    for dir in directories:
        directory_in_str = dir
        directory = os.fsencode(directory_in_str)
        ave_of_averages = pd.DataFrame() # List to store the average of the averages for each program
        for file in os.listdir(directory):
            filename = os.fsdecode(file)
            if filename.endswith(".txt"):
                temp_df = pd.DataFrame([get_averages(os.path.join(directory_in_str, filename))])
                ave_of_averages = pd.concat([ave_of_averages, temp_df], ignore_index=True)
        bars[dir.split('/')[-1]] = ave_of_averages
        

if __name__ == '__main__':
    main()



# # Calculate the average metrics for each program
# avg_turnaround_time_p1 = np.mean(program1[::4])
# avg_turnaround_time_p2 = np.mean(program2[::4])
# avg_responsetime_p1 = np.mean(program1[1::4])
# avg_responsetime_p2 = np.mean(program2[1::4])
# avg_waitingtime_p1 = np.mean(program1[2::4])
# avg_waitingtime_p2 = np.mean(program2[2::4])
# throughput_p1 = np.mean(program1[3::4])
# throughput_p2 = np.mean(program2[3::4])

# # Define the labels for the x-axis
# labels = ['Throughput p1', 'Throughput p2', 'Avg TurnAroundTime p1', 'Avg TurnAroundTime p2', 'Avg ResponseTime p1', 'Avg ResponseTime p2', 'Avg WaitingTime p1', 'Avg WaitingTime p2']

# # Define the data for the bar graph
# data = [throughput_p2, throughput_p1, avg_turnaround_time_p2, avg_turnaround_time_p1, avg_responsetime_p2, avg_responsetime_p1, avg_waitingtime_p2, avg_waitingtime_p1]

# # Create the bar graph
# plt.bar(labels, data)
# plt.xlabel('Metric')
# plt.ylabel('Average Value')
# plt.title('Average Metrics Comparison')
# plt.show()