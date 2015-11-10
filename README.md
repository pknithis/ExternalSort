This is a simple and elegant exxternal sort implemented using Java

Usage :
The program requires the input from the user for few parameters.
1)The amount of the main memory of the system in KB(Threshold_MemSize)
2)The number of input files to be sorted
3)The output filename

Logic:
 1)The program  reads the input files for Threshold_MemSize of bytes and sorts the data and writes them to the temporary file.
 2)Next a priority queue(Min Heap) is constructed with the temporary file data. 
 3)Elements are taken from the priority queue and written to output file. Meanwhile after removing the elements from the priority queue the priority 
   queue is populated again from the temp file from which the element was added before.
 4)Steps are repeated till the queue becomes empty.

Below is the sample run of the program:

Enter the maximum amount of main memory of the system(in KB) : 
10
Enter the number of Input Files : 
3
Enter the input file names
C:\Users\nithish\Documents\NetBeansProjects\ExternalSort_MultipleFiles\testfiles\file1.txt
C:\Users\nithish\Documents\NetBeansProjects\ExternalSort_MultipleFiles\testfiles\file2.txt
C:\Users\nithish\Documents\NetBeansProjects\ExternalSort_MultipleFiles\testfiles\file3.txt
Enter the output file name:
C:\Users\nithish\Documents\NetBeansProjects\ExternalSort_MultipleFiles\testfiles\output.txt
Sorting Complete. Please check output.txt
