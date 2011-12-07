
package TravelingSalesman;

import java.util.List;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IUniversalRateCalculator;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.RandomGenerator;
import org.jgap.impl.MutationOperator;

/**
 * An implementation of the heuristic 2-Opt Mutation Operator, which swaps edges
 * that better the overall fitness of the chromosome
 * 
 * 
 * @author Aaron Foltz
 */
public class SegmentSwappingMutation
		extends MutationOperator {

	private int					m_startOffset	= 1;

	private TravelingSalesman	salesman;


	/**
	 * Constructs a new instance of this operator.
	 * <p>
	 * Attention: The configuration used is the one set with the static method
	 * Genotype.setConfiguration.
	 * 
	 * @throws InvalidConfigurationException
	 * 
	 * @author Klaus Meffert
	 */
	public SegmentSwappingMutation()
			throws InvalidConfigurationException {

		super();
	}


	/**
	 * Constructs a new instance of this MutationOperator with the given
	 * mutation rate.
	 * 
	 * @param a_config
	 *            the configuration to use
	 * @param a_desiredMutationRate
	 *            desired rate of mutation, expressed as the denominator of the
	 *            1 / X fraction. For example, 1000 would result in 1/1000 genes
	 *            being mutated on average. A mutation rate of zero disables
	 *            mutation entirely
	 * @param salesman
	 *            An instance of the TSP problem at hand
	 * @throws InvalidConfigurationException
	 * 
	 * @author Aaron Foltz
	 * @since 3.0 (previously: without a_config)
	 */
	public SegmentSwappingMutation(final Configuration a_config,
									final int a_desiredMutationRate,
			TravelingSalesman salesman)
			throws InvalidConfigurationException {

		super(a_config, a_desiredMutationRate);
		this.salesman = salesman;
	}


	/**
	 * Constructs a new instance of this operator with a specified mutation rate
	 * calculator, which results in dynamic mutation being turned on.
	 * 
	 * @param a_config
	 *            the configuration to use
	 * @param a_mutationRateCalculator
	 *            calculator for dynamic mutation rate computation
	 * @throws InvalidConfigurationException
	 * 
	 * @author Klaus Meffert
	 * @since 3.0 (previously: without a_config)
	 */
	public SegmentSwappingMutation(final Configuration a_config,
									final IUniversalRateCalculator
									a_mutationRateCalculator)
			throws InvalidConfigurationException {

		super(a_config, a_mutationRateCalculator);
	}


	/**
	 * @param a_config
	 *            the configuration to use
	 * @param salesman
	 *            an instance of the TSP at hand
	 * @throws InvalidConfigurationException
	 * 
	 * @author Klaus Meffert
	 * @since 3.0
	 */
	public SegmentSwappingMutation(final Configuration a_config,
			TravelingSalesman salesman)
			throws InvalidConfigurationException {

		super(a_config);
		this.salesman = salesman;
	}


	/**
	 * Gets a number of genes at the start of chromosome, that are excluded from
	 * the swapping. In the Salesman task, the first city in the list should
	 * (where the salesman leaves from) probably should not change as it is part
	 * of the list. The default value is 1.
	 * 
	 * @return the start offset
	 * 
	 * @author Audrius Meskauskas
	 * @since 2.0
	 */
	public int getStartOffset() {

		return m_startOffset;
	}


	/**
	 * @param a_population
	 *            the population of chromosomes from the current evolution prior
	 *            to exposure to any genetic operators. Chromosomes in this
	 *            array should not be modified. Please, notice, that the call in
	 *            Genotype.evolve() to the implementations of GeneticOperator
	 *            overgoes this due to performance issues
	 * @param a_candidateChromosomes
	 *            the pool of chromosomes that have been selected for the next
	 *            evolved population
	 * 
	 * @author Audrius Meskauskas
	 * @author Klaus Meffert
	 * @since 2.0
	 */
	@Override
	public void operate(final Population a_population,
						List a_candidateChromosomes) {

		// this was a private variable, now it is local reference.
		final IUniversalRateCalculator m_mutationRateCalc = getMutationRateCalc();
		// If the mutation rate is set to zero and dynamic mutation rate is
		// disabled, then we don't perform any mutation.
		// ----------------------------------------------------------------
		if ((getMutationRate() == 0) && (m_mutationRateCalc == null)) {
			return;
		}
		// Determine the mutation rate. If dynamic rate is enabled, then
		// calculate it based upon the number of genes in the chromosome.
		// Otherwise, go with the mutation rate set upon construction.
		// --------------------------------------------------------------
		int currentRate;
		if (m_mutationRateCalc != null) {
			currentRate = m_mutationRateCalc.calculateCurrentRate();
		} else {
			currentRate = getMutationRate();
		}

		RandomGenerator generator = getConfiguration().getRandomGenerator();
		// It would be inefficient to create copies of each Chromosome just
		// to decide whether to mutate them. Instead, we only make a copy
		// once we've positively decided to perform a mutation.
		// ----------------------------------------------------------------
		int size = a_population.size();

		for (int i = 0; i < size; i++) {
			IChromosome x = a_population.getChromosome(i);
			// This returns null if not mutated:
			IChromosome xm = operate(x, currentRate, generator);
			if (xm != null) {
				a_candidateChromosomes.add(xm);
			}
		}
	}


	/**
	 * Sets a number of genes at the start of chromosome, that are excluded from
	 * the swapping. In the Salesman task, the first city in the list should
	 * (where the salesman leaves from) probably should not change as it is part
	 * of the list. The default value is 1.
	 * 
	 * @param a_offset
	 *            the offset to set
	 * 
	 * @author Audrius Meskauskas
	 * @since 2.0
	 */
	public void setStartOffset(final int a_offset) {

		m_startOffset = a_offset;
	}


	/**
	 * Operate on the given chromosome with the given mutation rate.
	 * 
	 * @param a_chrom
	 *            chromosome to operate
	 * @param a_rate
	 *            mutation rate
	 * @param a_generator
	 *            random generator to use (must not be null)
	 * @return mutated chromosome of null if no mutation has occured.
	 * 
	 * @author Audrius Meskauskas
	 * @since 2.0
	 */
	protected IChromosome operate(final IChromosome a_chrom, final int a_rate,
								final RandomGenerator a_generator) {

		IChromosome chromosome = null;

		// If this branch is taken, then this chromosome has been chosen for
		// mutation
		if ((a_generator.nextInt(a_rate) == 0)) {
			if (chromosome == null) {
				chromosome = (IChromosome) a_chrom.clone();
				// In case monitoring is active, support it.
				// -----------------------------------------
				if (m_monitorActive) {
					chromosome
							.setUniqueIDTemplate(a_chrom.getUniqueID(), 1);
				}
			}

			// Mutate 20% of the genes in the chromosome
			for (int i = 0; i < (int) (.2 * (a_chrom.size())); i++) {
				Gene[] genes = chromosome.getGenes();
				Gene[] mutated = operate(a_generator, genes);

				// setGenes is not required for this operator, but it may
				// be needed for the derived operators.
				// ------------------------------------------------------
				try {
					chromosome.setGenes(mutated);
				} catch (InvalidConfigurationException cex) {
					throw new Error(
							"Gene type not allowed by constraint checker", cex);
				}
			}
		}
		return chromosome;
	}


	/**
	 * Operate on the given array of genes. This method is only called when it
	 * is already clear that the mutation must occur under the given mutation
	 * rate. It provides a simple 2-Opt Mutation Operator, swapping segments in
	 * the array if it is advantageous (in terms of distance)
	 * 
	 * @param a_generator
	 *            a random number generator that may be needed to perform a
	 *            mutation
	 * @param a_target_gene
	 *            an index of gene in the chromosome that will mutate
	 * @param a_genes
	 *            the array of all genes in the chromosome
	 * @return the mutated gene array
	 * 
	 * @author Aaron Foltz
	 */
	protected Gene[] operate(final RandomGenerator a_generator,
			final Gene[] a_genes) {

		// We don't want to choose the last gene as the starting point because
		// it doesn't have a leaving edge (in the chromosome at least)

		// Choose two random genes and their following edges
		int gene1 = (m_startOffset)
				+ (int) (Math.random() * (((getConfiguration()
						.getChromosomeSize() - 2) - (m_startOffset)) + 1));
		int gene2 = gene1 + 1;
		int gene3 = (m_startOffset)
				+ (int) (Math.random() * (((getConfiguration()
						.getChromosomeSize() - 2) - (m_startOffset)) + 1));
		int gene4 = gene3 + 1;

		// Check the distances between the two original edges
		int edge1 = (int) salesman.distance(a_genes[gene1], a_genes[gene2]);
		int edge2 = (int) salesman.distance(a_genes[gene3], a_genes[gene4]);

		// Check the distance between the swapped edges
		int edge3 = (int) salesman.distance(a_genes[gene1], a_genes[gene4]);
		int edge4 = (int) salesman.distance(a_genes[gene2], a_genes[gene3]);

		// If original edges cost more, then switch them. make sure that the
		// edges are NOT the same
		if ((gene1 != gene3) && ((edge1 + edge2) > (edge3 + edge4))) {
			Gene savedGene2 = a_genes[gene2];
			Gene savedGene3 = a_genes[gene3];
			Gene savedGene4 = a_genes[gene4];
			a_genes[gene4] = savedGene3; // Swap gene 4 for gene 3
			a_genes[gene3] = savedGene2; // Swap gene 3 for gene 2
			a_genes[gene2] = savedGene4; // Swap gene 2 for gene 4

		}

		return a_genes;
	}
}
