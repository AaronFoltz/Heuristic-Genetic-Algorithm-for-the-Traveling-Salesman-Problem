
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
 * The Greedy Crossover is a specific type of crossover. It can only be is
 * applied if
 * <ul>
 * <li>
 * 1. All genes in the chromosome are different and</li>
 * <li>
 * 2. The set of genes for both chromosomes is identical and only their order in
 * the chromosome can vary.</li>
 * </ul>
 * 
 * After the GreedyCrossover, these two conditions always remain true, so it can
 * be applied again and again.
 * 
 * The algorithm throws an assertion error if the two initial chromosomes does
 * not satisfy these conditions.
 * 
 * Greedy crossover can be best explained in the terms of the Traveling Salesman
 * Problem:
 * 
 * The algorithm selects the first city of one parent, compares the cities
 * leaving that city in both parents, and chooses the closer one to extend the
 * tour. If one city has already appeared in the tour, we choose the other city.
 * If both cities have already appeared, we randomly select a non-selected city.
 * 
 * See J. Grefenstette, R. Gopal, R. Rosmaita, and D. Gucht. <i>Genetic
 * algorithms for the traveling salesman problem</i>. In Proceedings of the
 * Second International Conference on Genetic Algorithms. Lawrence Eribaum
 * Associates, Mahwah, NJ, 1985. and also <a
 * href="http://ecsl.cs.unr.edu/docs/techreports/gong/node3.html"> Sushil J.
 * Louis & Gong Li</a>}
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

		Gene[] g1 = a_firstMate.getGenes();
		Gene[] g2 = a_secondMate.getGenes();
		// for (Gene gene : g1) {
		// System.out.print(gene.getAllele() + " ");
		// }
		// System.out.println();
		//
		// for (Gene gene : g2) {
		// System.out.print(gene.getAllele() + " ");
		// }
		// System.out.println();

		Gene[] c1, c2;
		try {
			c1 = operate(g1, g2);
			c2 = operate(g2, g1);

			// for (Gene gene : c1) {
			// System.out.print(gene.getAllele() + " ");
			// }
			// System.out.println();
			// for (Gene gene : c2) {
			// System.out.print(gene.getAllele() + " ");
			// }
			// System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

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

		// System.out.println("\n\nOPERATING");

		int size = Math.min(getConfiguration().getPopulationSize(),
						a_population.size());

		int numCrossovers = size / 2;

		// System.out.println("NUMBER OF CROSSOVERS: " + numCrossovers);

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

			// System.out.println("OPERATING SECOND");
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

		/*
		 * Take a random city as the starting point (other than 0, the original
		 * starting point). This will stick with the true implementation of
		 * Grefenstette's Heuristic (Greedy) Crossover and will add in
		 * variability when initialization involves a population of equivalent
		 * chromosomes
		 */
		int startingPoint = (m_startOffset)
				+ (int) (Math.random() * (((size - 1) - (m_startOffset)) + 1));

		// Choose a random integer between 0 and 1
		int parent = generator.nextInt(2);

		// Decide which parent to start from based on the random number. Add
		// the random starting point to the child
		if (parent == 0) {
			out.add(a_g1[startingPoint]);
		} else if (parent == 1) {
			out.add(a_g2[startingPoint]);
		}

		// Add the rest of the genes to the TreeSet
		for (int j = 1; j < n; j++) { // g[m_startOffset] picked

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

			// The random starting point has already been chosen, so skip it
			// here, and add the rest
			if ((parent == 0) && (j != startingPoint)) {
				not_picked.add(a_g1[j]);
			} else if ((parent == 1) && (j != startingPoint)) {
				not_picked.add(a_g2[j]);
			}
		}

		// Should be skipped during normal execution
		if (ASSERTIONS) {
			if (a_g1.length != a_g2.length) {
				throw new Error("Chromosome sizes must be equal");
			}
		}

		while (not_picked.size() > 1) {
			Gene last = (Gene) out.getLast();
			// System.out.println("PICKED: " + out.toString());
			// System.out.println("NOT PICKED: " + not_picked.toString());
			//
			// System.out.println("LAST: " + last);

			// Finds the gene immediately after in the chromosome
			Gene n1 = findNext(a_g1, last);
			Gene n2 = findNext(a_g2, last);

			// System.out.println(n1 + " OR " + n2);

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
			if (out.contains(picked)) {
				picked = other;
			}

			// If we are on the last gene in the chromosome, take the first
			// that hasn't been picked yet
			if ((picked == null) || out /* still */.contains(picked)) {
				// select a non-selected // it is not random
				picked = (Gene) not_picked.first();
			}
			out.add(picked);
			not_picked.remove(picked);
			// System.out.println("END PICKED: " + out.toString());
			// System.out.println("END NOT PICKED: " + not_picked.toString() +
			// "\n");
		}

		// Should be skipped during normal execution
		if (ASSERTIONS && (not_picked.size() != 1)) {
			throw new Error(
					"Given Gene not correctly created (must have length > 1"
							+ ")");
		}
		out.add(not_picked.last());

		// System.out.println("FINAL CHROMOSOME: " + out.toString());

		Gene[] g = new Gene[n];
		Iterator gi = out.iterator();
		for (int i = 0; i < m_startOffset; i++) {
			g[i] = a_g1[i];
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
