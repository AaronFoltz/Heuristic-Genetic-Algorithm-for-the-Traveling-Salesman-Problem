
package TravelingSalesman;

import java.util.LinkedList;
import java.util.Random;

import org.jgap.Gene;
import org.jgap.impl.IntegerGene;

public class StochasticInitialization {

	/**
	 * We cannot use the FitnessFunction's evaluate here because the list is
	 * just a list of numbers, not a list of Genes
	 * 
	 * @param cityList
	 * @return
	 */
	public static int evaluate(LinkedList<Integer> cityList,
			TravelingSalesman salesman) {

		double s = 0;

		for (int i = 0; i < cityList.size() - 1; i++) {

			// Gather the distance from the edge in the cityList
			s += salesman.distance(cityList.get(i), cityList.get(i + 1));
		}

		// add cost of coming back:
		s += salesman.distance(cityList.get(cityList.size() - 1), 0);

		// Take total edge average through the cityList
		return (int) s / cityList.size();

	}


	public static Gene[] operate(Gene[] genes, Gene[] sampleGenes,
			TravelingSalesman salesman, LinkedList<Integer> cityList) {

		genes[0] = sampleGenes[0].newGene();
		genes[0].setAllele(sampleGenes[0].getAllele());

		Random generator = new Random();
		for (int i = 1; i < genes.length; i++) {

			// Grab a random location in the unused list
			int location = generator.nextInt(cityList.size());

			int average = evaluate(cityList, salesman);

			// Get the distance from the last city and the chosen random city.
			// Also get the edges to its surrounding neighbors and accept the
			// lowest
			// distance in a stochastic heuristic approach to initialization
			// Get the distance between the the last city and the chosen city
			int distance = (int) salesman.distance(((IntegerGene) genes[i - 1])
					.intValue(), cityList.get(location));

			// System.out.println(distance);

			int counter = 0;

			// Get the distance between the last city and the city forward one
			// from the chosen city - if its lower than the previous distance
			if (distance > (int) salesman.distance(((IntegerGene) genes[i - 1])
							.intValue(), cityList.get(((location + 1)
							% cityList.size())))) {
				distance = (int) salesman.distance(((IntegerGene) genes[i - 1])
						.intValue(), cityList.get(((location + 1)
						% cityList.size())));

				counter = 1;
			}
			// System.out.println((int) salesman
			// .distance(((IntegerGene) genes[i - 1])
			// .intValue(), cityList.get(((location + 1)
			// % cityList.size()))));

			// Get the distance between the last city and the city backward one
			// from the chosen city

			// If a negative value, set it to the last element in the list
			if ((location - 1) == -1) {
				location = cityList.size() - 1;
			}

			if (distance > (int) salesman.distance(((IntegerGene) genes[i - 1])
							.intValue(), cityList.get(((location - 1)
							% cityList.size())))) {
				distance = (int) salesman.distance(((IntegerGene) genes[i - 1])
						.intValue(), cityList.get(((location - 1)
								% cityList.size())));
				counter = -1;
			}

			// System.out.println((int) salesman
			// .distance(((IntegerGene) genes[i - 1])
			// .intValue(), cityList.get(((location - 1)
			// % cityList.size()))));

			// If we went past the array - readjust
			if ((location + counter) == cityList.size()) {
				location = 0;
			} else {
				// Set the new gene - this may be temporary
				location += counter;
			}
			genes[i] = sampleGenes[cityList.get(location)];
			genes[i].setAllele(sampleGenes[cityList.get(location)]
					.getAllele());

			// System.out.println("@@@@@@@@@@@@@@\nLOCATION: " + location);
			// System.out.println("DISTANCES: " + distance);
			// System.out.println("COUNTER: " + counter);
			// System.out.println("SIZE" + cityList.size());
			// System.out.println(((IntegerGene) genes[i - 1])
			// .intValue() + " " + cityList.get(location));
			// System.out.println("SIZE: " + (cityList.size() - 1));
			// System.out.println(cityList);
			// System.out.println(i + " " + (genes.length - 1));
			// System.out.println(genes[i] + " " + genes[i].getAllele());
			// Remove the city from the list - it cannot be used again
			cityList.remove(location);

		}
		return genes;
	}
}
