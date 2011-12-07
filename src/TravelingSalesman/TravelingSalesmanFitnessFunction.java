
package TravelingSalesman;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

/**
 * The fitness function used to evaluate how good a chromosome is in the
 * Traveling Salesman Problem.
 * 
 * @author Aaron Foltz
 */
public class TravelingSalesmanFitnessFunction
		extends FitnessFunction {

	private final TravelingSalesman	m_salesman;


	public TravelingSalesmanFitnessFunction(
			final TravelingSalesman travelingSalesman) {

		m_salesman = travelingSalesman;
	}


	/**
	 * Computes the distance of the tour represented by the chromosome
	 * 
	 * @param a_subject
	 *            chromosome representing cities
	 * @return distance of the journey thru the cities represented in the given
	 *         chromosome
	 * 
	 * @author Aaron Foltz
	 */
	@Override
	protected double evaluate(final IChromosome a_subject) {

		double s = 0;

		// Get the genes represented by the chromosome
		Gene[] genes = a_subject.getGenes();

		// Iterate through each of those genes
		for (int i = 0; i < genes.length - 1; i++) {

			// Add all of the edge distances in the chromosome
			s += m_salesman.distance(genes[i], genes[i + 1]);
		}

		// add cost of coming back:
		s += m_salesman.distance(genes[genes.length - 1], genes[0]);
		return s;
	}
}
