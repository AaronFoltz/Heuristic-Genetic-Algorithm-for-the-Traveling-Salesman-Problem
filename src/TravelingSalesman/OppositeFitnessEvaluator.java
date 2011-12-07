
package TravelingSalesman;

import org.jgap.FitnessEvaluator;
import org.jgap.IChromosome;
import org.jgap.util.ICloneable;

/**
 * An implementation of a fitness evaluator. This implementation is straight
 * forward: a lower fitness value is seen as fitter.
 * 
 * @author Aaron Foltz
 */
public class OppositeFitnessEvaluator
		implements FitnessEvaluator, ICloneable, Comparable {

	/**
	 * @return deep clone of this instance
	 * 
	 * @author Klaus Meffert
	 * @since 3.2
	 */
	@Override
	public Object clone() {

		return new OppositeFitnessEvaluator();
	}


	/**
	 * @param a_other
	 *            sic
	 * @return as always
	 * 
	 * @author Klaus Meffert
	 * @since 3.2
	 */
	@Override
	public int compareTo(Object a_other) {

		if (a_other.getClass().equals(getClass())) {
			return 0;
		} else {
			return getClass().getName().compareTo(a_other.getClass().getName());
		}
	}


	/**
	 * Compares the first given fitness value with the second and returns true
	 * if the first one is lower than the second one. Otherwise returns false
	 * 
	 * @param a_fitness_value1
	 *            first fitness value
	 * @param a_fitness_value2
	 *            second fitness value
	 * @return true: first fitness value greater than second
	 * 
	 * @author Aaron Foltz
	 */
	@Override
	public boolean isFitter(final double a_fitness_value1,
							final double a_fitness_value2) {

		return a_fitness_value1 < a_fitness_value2;
	}


	@Override
	public boolean isFitter(IChromosome a_chrom1, IChromosome a_chrom2) {

		return isFitter(a_chrom1.getFitnessValue(), a_chrom2.getFitnessValue());
	}
}
