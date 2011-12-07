
package TravelingSalesman;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that reads the coordinates from the given TSP problem file and
 * calculates the distances between each of the cities
 * 
 * 
 * This program can be run as a standalone entity: java Reader <problem name>
 * java Reader wi29
 * 
 * @author Aaron Foltz
 * 
 */
public class Reader {

	static double[]		coordinateArray;

	static int			counter	= 0;

	static double[][]	data;
	static double[][]	distances;
	static String		EdgeWeightType;

	static boolean		isData	= false;
	static double		xd, yd, rij, tij;


	/**
	 * Simply retrieves the coordinates from the TSP data file and returns it.
	 * This will be used in my Genetic Algorithm attempt
	 * 
	 * @param file
	 *            the file that the data is in
	 * @return coordinate matrix for each of the cities in the TSP
	 */
	public static double[][] getCoordinates(String file) {

		isData = false;
		counter = 0;

		// Get the coordinates from the file. Store in an array of double
		stripCoordinates(file);

		System.out.println();
		return data;

	}


	/**
	 * Retrieves the distance matrix for the TSP. This was used to verify the
	 * information found in my Genetic Algorithm, to make sure that it was
	 * gathering the right values for the right cities.
	 * 
	 * @param file
	 *            the file that the data is in
	 * @return the distance matrix of the TSP problem
	 */
	public static double[][] getDistances(String file) {

		isData = false;
		counter = 0;

		// Get the coordinates from the file. Store in an Arraylist
		stripCoordinates(file);

		// Calculate distance matrix for all the cities in the problem
		calculateDistances();

		// Print out the distance matrix in prettified format
		print();

		return distances;
	}


	/**
	 * Standalone function in order to strip the Edge Weight out of the file.
	 * Does Sequential access of the file, so it's wasting quite a bit of time
	 * 
	 * @param file
	 * @return the edge weight type of the TSP
	 */
	public static String getEdgeWeightType(String file) {

		file = "data/" + file;

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));

			String input = null;
			try {
				while ((input = in.readLine()) != null) {

					// Gather the distance function of the problem
					if (input.contains("EDGE_WEIGHT_TYPE")) {
						Pattern p = Pattern
								.compile("EDGE_WEIGHT_TYPE : (.+)");
						Matcher m = p.matcher(input);

						// Grab the string that matches the edge weight type
						if (m.matches()) {
							return m.group(1);
						} else {
							return null;
						}

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;

	}


	/**
	 * When used as a standalone program, this program simply prints out the
	 * coordinates in the file, as well as the distance matrix corresponding to
	 * those coordinates
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		isData = false;
		counter = 0;

		// Get the coordinates from a file given on the command line
		if (args.length > 0) {
			// Get the coordinates from the file. Store in an Arraylist
			stripCoordinates(args[0]);

			// Calculate distance matrix for all the cities in the problem
			calculateDistances();

			print();

			// If no arguments are given, just print a message.
		} else {
			System.out.print("Enter the file as your argument: ");
			Scanner scan = new Scanner(System.in);

			// Get the coordinates from the file. Store in an Arraylist
			stripCoordinates(scan.nextLine());

			// Calculate distance matrix for all the cities in the problem
			calculateDistances();

			print();
		}

		System.exit(0);
	}


	/**
	 * Simply print out the distance matrix for the problem
	 */
	public static void print() {

		System.out.println("\nCorresponding distance matrix");
		StringBuilder dataString = new StringBuilder();
		dataString.append("\t");
		// Iterate through the array, creating a prettified matrix
		for (int i = 0; i < distances.length; i++) {
			dataString.append((i) + ":\t");
			for (int j = 0; j < distances.length; j++) {

				dataString.append(distances[i][j] + "\t");

				if (j == distances.length - 1) {
					dataString.append("\n\t");
				}

			}
		}
		System.out.println("\n" + dataString.toString());
	}


	/**
	 * Calculate the distances depending on the EDGE_WEIGHT_TYPE in the given
	 * problem
	 */
	private static void calculateDistances() {

		// Pseudo-Euclidean measurement calculations
		if (EdgeWeightType.equals("ATT")) {

			// Iterate through each element in the array
			for (int i = 0; i < data.length; i++) {

				// Iterate through the other elements in the array
				for (int j = 0; j < data.length; j++) {

					// If we are comparing the same node, just set the distances
					// to 0
					if (i == j) {
						distances[i][j] = 0;

						// If we are on different cities, calculate the
						// differences
						// Derived from the TSPLIB documentation
					} else {
						xd = data[i][0] - data[j][0];
						yd = data[i][1] - data[j][1];

						rij = (float) Math.sqrt(((xd * xd) + (yd * yd)) / 10.0);
						tij = Math.round(rij);

						if (tij < rij) {
							distances[i][j] = tij + 1;
						} else {
							distances[i][j] = tij;
						}
					}
				}

			}

			// Euclidean 2D measurement calculation
		} else if (EdgeWeightType.equals("EUC_2D")) {

			// Iterate through each element in the array
			for (int i = 0; i < data.length; i++) {

				// Iterate through the rest of the elements in the array
				for (int j = 0; j < data.length; j++) {

					// If we are comparing the same node, just set the distances
					// to 0
					if (i == j) {
						distances[i][j] = 0;

						// If we are on different cities, calculate the
						// differences
						// Derived from the TSPLIB documentation
					} else {
						xd = data[i][0] - data[j][0];
						yd = data[i][1] - data[j][1];

						distances[i][j] = Math.round(Math.sqrt((xd * xd)
								+ (yd * yd)));

					}
				}

			}

		}

	}


	/**
	 * Simply strips the coordinates from the file, placing them in an array
	 * representing a coordinate matrix
	 * 
	 * @param file
	 *            the file that the data is in
	 */
	private static void stripCoordinates(String file) {

		file = "data/" + file;

		System.out.println("\nGrabbing the coordinates from " + file);

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));

			String input = null;
			try {

				while ((input = in.readLine()) != null) {

					// Trim whitespace from the beginning of the lines
					input = input.trim();

					// Replace three spaces with one space
					input = input.replace("   ", " ");

					// Replace two spaces with one space
					input = input.replace("  ", " ");

					// Gather the dimension of the TSP problem
					if (input.contains("DIMENSION") && !isData) {

						Pattern p = Pattern.compile("(\\d+)");
						Matcher m = p.matcher(input);

						// Gather the number representing the dimension
						if (m.find()) {
							distances = new double[Integer
									.parseInt(m.group(0))][Integer
									.parseInt(m.group(0))];

							data = new double[Integer
									.parseInt(m.group(0))][2];
						}
					}

					// Gather the distance function of the problem
					if (input.contains("EDGE_WEIGHT_TYPE") && !isData) {
						Pattern p = Pattern
								.compile("EDGE_WEIGHT_TYPE : (.+)");
						Matcher m = p.matcher(input);

						// Gather the string representing the edge weight type
						if (m.matches()) {
							EdgeWeightType = m.group(1);
						}

					}

					// Set the start of the data section
					if (input.contains("NODE_COORD_SECTION") && !isData) {
						isData = true; // We are now in the data section of
										// the file
					}

					// If we are now in the data section, save the
					// coordinates
					else if (isData) {

						// If not end of file, then save coordinates
						if (!input.equals("EOF")) {

							String[] coordinates = input.split(" ");

							coordinateArray = new double[2];

							// Temporary array for the coordinates - primitive
							// array
							// Represents the X coordinate for the city
							coordinateArray[0] = Double
									.parseDouble(coordinates[1]);
							// Represents the Y coordinate for the city
							coordinateArray[1] = Double
									.parseDouble(coordinates[2]);

							System.out.println(coordinateArray[0] + "\t"
									+ coordinateArray[1]);

							// Add the current city to the distance matrix
							data[counter++] = coordinateArray;

						}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			//
			e.printStackTrace();
		}

	}
}
