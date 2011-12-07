
package TravelingSalesman;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.jgap.BaseGeneticOperator;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.RandomGenerator;

/**
 * This implementation of Grefenstette's Heuristic Crossover was taken from the
 * base Greedy Crossover designed by Audrius Meskauskas in order to expand its
 * capabilities. Meskauskas' implementation took the starting city from a single
 * parent, but this implementation takes its starting city from a Random parent,
 * hopefully giving the child a bit more variation. Also, when both of the
 * parent edges set for inclusion create a cycle, Meskauskas' implementation
 * just chooses the first edge that hasn't been picked yet, so it isn't exactly
 * random. My implementation takes a random selection from the remaining
 * "not picked" cities.
 * 
 * @author Audrius Meskauskas
 * @author <font size=-1>Neil Rotstan, Klaus Meffert (reused code from
 *         {@link org.jgap.impl.CrossoverOperator CrossoverOperator})</font>
 * @since 2.0
 */
public class TravelingSalesmanHeuristicCrossover
		extends BaseGeneticOperator {

	private int					m_startOffset	= 1;
	private TravelingSalesman	salesman;

	/** Switches assertions on/off. Must be true during tests and debugging. */
	boolean						ASSERTIONS		= true;


	/**
	 * Default constructor for dynamic instantiation.
	 * <p>
	 * Attention: The configuration used is the one set with the static method
	 * Genotype.setConfiguration.
	 * 
	 * @throws InvalidConfigurationException
	 * 
	 * @author Klaus Meffert
	 * @since 2.6
	 * @since 3.0 (since 2.0 without a_configuration)
	 */
	public TravelingSalesmanHeuristicCrossover()
			throws InvalidConfigurationException {

		super(Genotype.getStaticConfiguration());
	}


	/**
	 * Using the given configuration and TravelingSalesman object
	 * 
	 * @param a_configuration
	 *            the configuration to use
	 * @throws InvalidConfigurationException
	 * 
	 * @author Klaus Meffert
	 * @since 3.0 (since 2.6 without a_configuration)
	 */
	public TravelingSalesmanHeuristicCrossover(Configuration a_configuration,
			TravelingSalesman salesman)
			throws InvalidConfigurationException {

		super(a_configuration);
		this.salesman = salesman;
	}


	/**
	 * Compares the given GeneticOperator to this GeneticOperator.
	 * 
	 * @param a_other
	 *            the instance against which to compare this instance
	 * @return a negative number if this instance is "less than" the given
	 *         instance, zero if they are equal to each other, and a positive
	 *         number if this is "greater than" the given instance
	 * 
	 * @author Klaus Meffert
	 * @since 2.6
	 */
	@Override
	public int compareTo(final Object a_other) {

		if (a_other == null) {
			return 1;
		}
		TravelingSalesmanHeuristicCrossover op = (TravelingSalesmanHeuristicCrossover) a_other;
		if (getStartOffset() < op.getStartOffset()) {
			// start offset less, meaning more to do --> return 1 for
			// "is greater than"
			return 1;
		} else if (getStartOffset() > op.getStartOffset()) {
			return -1;
		} else {
			// Everything is equal. Return zero.
			// ---------------------------------
			return 0;
		}
	}


	/**
	 * Gets a number of genes at the start of chromosome, that are excluded from
	 * the swapping. In the Salesman task, the first city in the list should
	 * (where the salesman leaves from) probably should not change as it is part
	 * of the list. The default value is 1.
	 * 
	 * @return the start offset used
	 */
	public int getStartOffset() {

		return m_startOffset;
	}


	/**
	 * Performs a greedy crossover for the two given chromosoms.
	 * 
	 * @param a_firstMate
	 *            the first chromosome to crossover on
	 * @param a_secondMate
	 *            the second chromosome to crossover on
	 * @throws Error
	 *             if the gene set in the chromosomes is not identical
	 * 
	 * @author Audrius Meskauskas
	 * @since 2.1
	 */
	public void operate(final IChromosome a_firstMate,
						final IChromosome a_secondMate) {

		// Pick the first and second chromosome from the population which will
		// mate
		Gene[] g1 = a_firstMate.getGenes();
		Gene[] g2 = a_secondMate.getGenes();

		Gene[] c1, c2;
		try {
			// Crossover both chromosomes two different ways - to get the two
			// different children
			c1 = operate(g1, g2);
			c2 = operate(g2, g1);

			a_firstMate.setGenes(c1);
			a_secondMate.setGenes(c2);
		} catch (InvalidConfigurationException cex) {
			throw new Error("Error occured while operating on:"
						+ a_firstMate + " and "
						+ a_secondMate
						+ ". First " + m_startOffset + " genes were excluded "
						+ "from crossover. Error message: "
						+ cex.getMessage());
		}
	}


	@Override
	public void operate(final Population a_population,
						final List a_candidateChromosomes) {

		// Get the size of the population
		int size = Math.min(getConfiguration().getPopulationSize(),
						a_population.size());

		// Get the number of crossovers required for this population
		int numCrossovers = size / 2;

		RandomGenerator generator = getConfiguration().getRandomGenerator();

		// For each crossover, grab two random chromosomes and do what
		// Grefenstette et al say.
		// --------------------------------------------------------------
		for (int i = 0; i < numCrossovers; i++) {

			int position1 = generator.nextInt(size);
			IChromosome origChrom1 = a_population.getChromosome(position1);
			IChromosome firstMate = (IChromosome) origChrom1.clone();

			int position2 = generator.nextInt(size);
			IChromosome origChrom2 = a_population.getChromosome(position2);
			IChromosome secondMate = (IChromosome) origChrom2.clone();

			// In case monitoring is active, support it.
			// -----------------------------------------
			if (m_monitorActive) {
				firstMate.setUniqueIDTemplate(origChrom1.getUniqueID(), 1);
				firstMate.setUniqueIDTemplate(origChrom2.getUniqueID(), 2);
				secondMate.setUniqueIDTemplate(origChrom1.getUniqueID(), 1);
				secondMate.setUniqueIDTemplate(origChrom2.getUniqueID(), 2);
			}

			operate(firstMate, secondMate);

			// Add the modified chromosomes to the candidate pool so that
			// they'll be considered for natural selection during the next
			// phase of evolution.
			// -----------------------------------------------------------
			a_candidateChromosomes.add(firstMate);
			a_candidateChromosomes.add(secondMate);
		}
	}


	/**
	 * Sets a number of genes at the start of chromosome, that are excluded from
	 * the swapping. In the Salesman task, the first city in the list should
	 * (where the salesman leaves from) probably should not change as it is part
	 * of the list. The default value is 1.
	 * 
	 * @param a_offset
	 *            the start offset to use
	 */
	public void setStartOffset(int a_offset) {

		m_startOffset = a_offset;
	}


	protected Gene findNext(final Gene[] a_g, final Gene a_x) {

		for (int i = m_startOffset; i < a_g.length - 1; i++) {
			if (a_g[i].equals(a_x)) {
				return a_g[i + 1];
			}
		}
		return null;
	}


	protected Gene[] operate(final Gene[] a_g1, final Gene[] a_g2) {

		int n = a_g1.length;
		LinkedList out = new LinkedList();
		TreeSet not_picked = new TreeSet();

		// Get the number of genes per chromosome
		int size = getConfiguration().getChromosomeSize();

		// Gather the RandomGenerator for this configuration
		RandomGenerator generator = getConfiguration().getRandomGenerator();

		// Choose a random integer between 0 and 1
		int parent = generator.nextInt(2);

		// Decide which parent to get the starting city from based on the random
		// number
		if (parent == 0) {
			out.add(a_g1[m_startOffset]);
		} else if (parent == 1) {
			out.add(a_g2[m_startOffset]);
		}

		// Add the rest of the genes to the TreeSet
		for (int j = m_startOffset + 1; j < n; j++) { // g[m_startOffset] picked

			// Should be skipped during normal execution
			if ((parent == 0) && ASSERTIONS && not_picked.contains(a_g1[j])) {
				throw new Error("All genes must be different for "
						+ getClass().getName()
						+ ". The gene " + a_g1[j] + "[" + j
						+ "] occurs more "
						+ "than once in one of the chromosomes. ");
			}

			if ((parent == 1) && ASSERTIONS && not_picked.contains(a_g2[j])) {
				throw new Error("All genes must be different for "
						+ getClass().getName()
						+ ". The gene " + a_g2[j] + "[" + j
						+ "] occurs more "
						+ "than once in one of the chromosomes. ");
			}

			// Add the remaining edges, depending on the random parent that the
			// starting city was taken from
			if (parent == 0) {
				not_picked.add(a_g1[j]);
			} else if (parent == 1) {
				not_picked.add(a_g2[j]);
			}

		}

		// Should be skipped during normal execution
		if (ASSERTIONS) {
			if (a_g1.length != a_g2.length) {
				throw new Error("Chromosome sizes must be equal");
			}
		}

		// Iterate through the "not picked" city list, building up the child
		// chromosome
		while (not_picked.size() > 1) {

			// Keep the last gene, this is needed for distance calculations
			Gene last = (Gene) out.getLast();

			// Finds the gene immediately after in the chromosome
			Gene n1 = findNext(a_g1, last);
			Gene n2 = findNext(a_g2, last);

			// Decide which gene should be included next, based on distance
			Gene picked, other;
			boolean pick1;
			if (n1 == null) {
				pick1 = false;
			} else if (n2 == null) {
				pick1 = true;
			} else {

				pick1 = salesman.distance(last, n1) < salesman
						.distance(last, n2);
			}
			if (pick1) {
				picked = n1;
				other = n2;
			} else {
				picked = n2;
				other = n1;
			}

			// If the gene has already been used, try the other parents next
			// gene
			if (out.contains(picked)) {
				picked = other;
			}

			// If both parental genes create a cycle, then add a random city
			// that has yet to be picked
			if ((picked == null) || out /* still */.contains(picked)) {

				// Convert to array so we can randomly pick a gene
				Object[] randomPick = not_picked.toArray();
				// Get the random gene
				picked = (Gene) randomPick[generator.nextInt(randomPick.length)];

			}

			// Add this element as picked
			out.add(picked);

			// Remove the item from the Treeset
			not_picked.remove(picked);

		}

		// Should be skipped during normal execution
		if (ASSERTIONS && (not_picked.size() != 1)) {
			throw new Error(
					"Given Gene not correctly created (must have length > 1"
							+ ")");
		}
		out.add(not_picked.last());

		Gene[] g = new Gene[n];
		Iterator gi = out.iterator();
		for (int i = 0; i < m_startOffset; i++) {
			if (parent == 0) {
				g[i] = a_g1[i];
			} else if (parent == 1) {
				g[i] = a_g2[i];
			}

		}

		if (ASSERTIONS) {
			if (out.size() != g.length - m_startOffset) {
				throw new Error("Unexpected internal error. "
						+ "These two must be equal: " + out.size()
						+ " and " + (g.length - m_startOffset) + ", g.length "
						+ g.length + ", start offset " + m_startOffset);
			}
		}
		for (int i = m_startOffset; i < g.length; i++) {
			g[i] = (Gene) gi.next();
		}
		return g;
	}
}
