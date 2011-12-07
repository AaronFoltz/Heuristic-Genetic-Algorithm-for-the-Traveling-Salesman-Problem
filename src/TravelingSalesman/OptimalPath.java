
package TravelingSalesman;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is used to take in a optimal solution to a TSP problem, given by either
 * TSPLIB or the TSP data at Georgia Tech. This class will then compute the
 * optimal path length for that solution, so this class takes a tour list of
 * cities, and generates the tour length from that.
 * 
 * This is needed because most of the TSP problem instances have a optimal path
 * list (of cities), but they have no tangible optimal number cost for that
 * path. The path is needed in my Genetic Algorithm in order to calculate
 * exactly how close my solution is to optimality
 * 
 * @author Aaron Foltz
 * 
 */
public class OptimalPath {

	static ArrayList<Integer>	cities	= new ArrayList<Integer>();

	static double[][]			distances;

	static boolean				isData	= false;
	static double				total	= 0;


	public static void calculate(String file) {

		file = "data/" + file.concat(".opt.tour");

		System.out.println("Getting optimum data from: " + file);

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));

			String input = null;
			try {
				while ((input = in.readLine()) != null) {

					// Set the start of the data section
					if (input.contains("TOUR_SECTION") && !isData) {
						// We are now in the data section of the file
						isData = true;
					}

					// If we are now in the data section, calculate the cost
					else if (isData) {

						// If not end of file, then save coordinates to an
						// ArrayList
						if (!input.equals("EOF") && !input.equals("-1")) {
							cities.add(Integer.parseInt(input));

						}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Iterate through the arraylist, tallying up the total tour cost
		for (int i = 0; i < cities.size(); i++) {

			// If on the last city in the tour, calculate the return trip
			if (i == (cities.size() - 1)) {

				total += distances[cities.get(i) - 1][0];

				// Add the distance between the cities to the total
			} else {
				total += distances[cities.get(i) - 1][cities.get(i + 1) - 1];

				System.out.println((cities.get(i) - 1) + " "
						+ (cities.get(i + 1) - 1)
						+ "\t"
						+ distances[cities.get(i) - 1][cities.get(i + 1) - 1]
						+ "\t" + total);
			}

		}

		System.out.println("Total: " + total);

	}


	public static void main(String[] args) {

		// Get the coordinates from a file given on the command line
		if (args.length > 0) {

			// Get the distance matrix between all of the cities for this
			// problem
			distances = Reader.getDistances(args[0].concat(".tsp"));

			// Calculate the optimal path length given the tour
			calculate(args[0]);

			// If no arguments are given, just print a message.
		} else {
			System.out.print("Enter the file as your argument: ");
			Scanner scan = new Scanner(System.in);

			// Get the file as input
			String file = scan.nextLine();

			// Get the distance matrix between all of the cities for this
			// problem
			distances = Reader.getDistances(file
					.concat(".tsp"));

			// Calculate the optimal path length given the tour
			calculate(file);
		}

		System.exit(0);
	}
}
