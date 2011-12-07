Please see Example Output/ for example output from each of the three running examples

TSP Data (Located in /data):
	⁃	wi29 
	⁃	29 Cities
	⁃	Optimal - 27603
	⁃	att48
	⁃	48 cities
	⁃	Optimal - 10628
	⁃	eil101
	⁃	101 cities
	⁃	Optimal - 629
	⁃	a280
	⁃	280 cities
	⁃	Optimal - 2579

	⁃	The data for each of these problems is located in /data, but you shouldn’t have to worry about any of the paths, each of these classes will take care of that for you.  The Genetic Algorithm will collect and write data to a file in /data, so you may wish to access that at some point.  It keeps track of the best/average individual over each iteration, and then prints out statistics at the end of its run.

	⁃	Problem is located in “name.tsp”
	⁃	Optimal Tour is located in “name.opt.tour”
	⁃	wi29 has no optimal tour path, this was retrieved from Georgia Tech with only an Optimal Cost
	⁃	Written data to file is located in “name.tsp.data”


Running: 

Traveling Salesman Problem using the Java Genetic Algorithms Package - Starts the basic Genetic Algorithm.  You will be asked to enter a bit of information at the start:
	1)	The name of the file which houses the TSP problem.  You only need to enter the filename here, not the extension.  You can choose from wi29 (29 cities), att48 (48 cities), eil101 (101 cities), or a280 (280 cities).
	⁃	For example: Enter the file: wi29
	2)	The number of iterations of the TSP problem.  The same problem will be computed this number of times
	⁃	For example: Enter iterations: 10
	3)	The optimal path length for this problem.  The optimal lengths are:
	⁃	Length 29: 27603
	⁃	Length 48: 10628
	⁃	Length 101: 629
	⁃	Length 280: 2579
	⁃	For Example: Enter optimal for this problem: 27603

	⁃	Start the program using:
	⁃	./StartTravelingSalesman.sh - a bash script which will compile and then run the Traveling Salesman Problem
	⁃	javac -d . -classpath "jgap.jar:." *.java
	⁃	java -cp "jgap.jar:." TravelingSalesman.TravelingSalesman
	⁃	Output
	⁃	TSP coordinate matrix
	⁃	GA Parameters (for each iteration of the TSP problem)
	⁃	GA Suboptimal solution (for each iteration of the TSP problem) and Running Time
	⁃	Average Fitness, Average Percentage from Optimal, Average Running Time, Best Fitness, Best Fitness Percentage from Optimal
		
Reader (Reads in TSP coordinates and returns the coordinate and distance matrix.  This class is also used by the Genetic Algorithm in order to retrieve a matrix representation of the coordinates from the Traveling Salesman Problem data file)
	⁃	You will be asked to enter the filename, enter the filename+extension here.
	⁃	For example: Enter the file as your argument: wi29.tsp

	⁃	Start the program using:
	⁃	./StartReader.sh - a bash script which will compile and then run the coordinate reader
	⁃	javac -d . -classpath "jgap.jar:." Reader.java
	⁃	java -cp "jgap.jar:." TravelingSalesman.Reader
	⁃	Output
	⁃	TSP Coordinates matrix
	⁃	TSP distance matrix (between the cities - derived from each of the coordinates)


OptimalPath (Reads in a TSP Optimal Solution Tour File and returns the total cost of that path.  This is used so that we can compare our Genetic Algorithm solutions (by cost/distance) to the optimal solution for the given problem.  It will print out the coordinates given by the problem, its distance matrix, the optimal solution, and the optimal solution’s cost)
	⁃	You will be asked to enter the filename, enter the filename only, without the extension.
	⁃	For example: Enter the file as your argument: att48

	⁃	Start the program using:
	⁃	./StartOptimalpath.sh - a bash script which will compile and then run the optimal solution calculations
	⁃	javac -d . -classpath "jgap.jar:." OptimalPath.java
	⁃	java -cp "jgap.jar:." TravelingSalesman.OptimalPath
	⁃	Ouput
	⁃	TSP coordinate matrix
	⁃	TSP distance matrix
	⁃	TSP optimal path solution
	⁃	Total optimal running cost
