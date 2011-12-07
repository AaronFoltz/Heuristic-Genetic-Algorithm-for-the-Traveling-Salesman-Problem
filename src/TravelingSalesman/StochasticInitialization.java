
package TravelingSalesman;

import java.util.LinkedList;
import java.util.Random;

import org.jgap.Gene;
import org.jgap.impl.IntegerGene;

/**
 * Stochastic Initialization of the Genetic Algorithm's population. Choose a
 * random city in the "not picked" city pile. Find the distance between the last
 * city added to the chromosome and the random city just picked. Now take the
 * average of the edges left in the "not picked" city pile (in order). If the
 * distance added from the inclusion of the random city is more than the
 * "not picked" average, then take it automatically
 * 
 * @author Aaron Foltz
 * 
 */
public class StochasticInitialization {

	/**
	 * We cannot use the FitnessFunction's evaluate here because the list is
	 * just a list of numbers, not a list of Genes
	 * 
	 * @param cityList
	 *            a list containing the cities that have not been picked for
	 *            inclusion in the chromosome
	 * @return the average of the edges left in the "not picked" city list
	 */
	public static int evaluate(LinkedList<Integer> cityList,
			TravelingSalesman salesman) {

		double s = 0;

		// If only one city, just return max value so it will be chosen
		if (cityList.size() == 1) {
			return Integer.MAX_VALUE;
		}

		// Iterate through all of the cities in the list, gathering the distance
		// of the overall tour
		for (int i = 0; i < cityList.size() - 1; i++) {

			// Gather the distance from the edge in the cityList
			s += salesman.distance(cityList.get(i), cityList.get(i + 1));
		}

		// add cost of coming back:
		s += salesman.distance(cityList.get(cityList.size() - 1), 0);

		// Take total edge average through the cityList
		return ((int) (s / cityList.size()));

	}


	/**
	 * Operates on the chromosome to initialize it
	 * 
	 * @param genes
	 *            the genes representing the chromosome
	 * @param sampleGenes
	 *            the sample chromosome - not used here
	 * @param salesman
	 *            the TSP instance at hand
	 * @param cityList
	 *            a linked list of the "not picked" cities
	 * @return a list of genes, representing a chromosome
	 */
	public static Gene[] operate(Gene[] genes, Gene[] sampleGenes,
			TravelingSalesman salesman, LinkedList<Integer> cityList) {

		// We know that the first gene has to be the starting city, city 0, so
		// there is nothing to calculate to find its position
		genes[0] = sampleGenes[0].newGene();
		genes[0].setAllele(sampleGenes[0].getAllele());

		Random generator = new Random();

		// Iterate through each of the genes in the chromosome
		for (int i = 1; i < genes.length; i++) {

			int distance, average, location;
			int counter = 0;
			Random random = new Random();

			// Grab a new location as long as the distance from its inclusion is
			// greater than the average edge length in the "not picked" city
			// list
			do {

				// Grab a random location in the unused list
				location = generator.nextInt(cityList.size());

				// Calculate the average of the cities in the "not picked" list
				average = evaluate(cityList, salesman);

				// Get the distance from the last city and the chosen random
				// city.
				distance = (int) salesman.distance(((IntegerGene) genes[i - 1])
						.intValue(), cityList.get(location));

				// If the distance from the inclusion of the edge is greater
				// than the average, then do 20% of the total length in
				// iterations, then take the edge currently on. Alternatively, a
				// probability dependent on the counter
				// can take the current edge, even if its bigger
				counter++;
				if (((distance > average) && (counter == (int) ((genes.length * .2))))
						|| (random.nextInt((int) (genes.length * .3) / counter) == 0)) {
					counter = 0;
					break;
				}
			} while (distance > average);

			// We found a city for that gene, so add it to the chromosome
			genes[i] = sampleGenes[cityList.get(location)];
			genes[i].setAllele(sampleGenes[cityList.get(location)]
					.getAllele());

			// Remove this city from the "not picked" list
			cityList.remove(location);

		}

		return genes;
	}
}
