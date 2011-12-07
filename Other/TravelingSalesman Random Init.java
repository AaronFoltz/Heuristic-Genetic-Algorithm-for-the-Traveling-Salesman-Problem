
package TravelingSalesman;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.event.EventManager;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.StockRandomGenerator;

// ---------------------------------------------
// OPTIMAL TOURS:
// Length 29: 27603
// Length 48: 10628
// Length 101: 629
// Length 280: 2579
// ---------------------------------------------

/**
 * @author Aaron Foltz
 */
public class TravelingSalesman
		implements Serializable {

	/** The number of cities to visit */
	public static int				CITIES;
	public static double[][]		CITYARRAY;

	public static String			EdgeWeightType		= null;

	private static IChromosome		bestChromosome;

	// Set to true if you want to see more textual output as well as writing
	// data to a file
	private static boolean			debugOutput			= true;

	private static Configuration	m_config;

	// Set up writing data to a file
	private static BufferedWriter	writer				= null;

	// The population for the GA
	static Genotype					population			= null;

	private final double			cullingPercentage	= .75;

	private int						m_maxEvolution		= 1 * (int) ((Math
																.log(1 - Math
																		.pow(.99,
																				(1.0 / CITIES)))) / (Math
																.log(((float) (CITIES - 3)
																/ (float) (CITIES - 1)))));
	// private int m_maxEvolution = 50;
	// Population size estimation from Tommi Rintala
	// located at:
	// http://lipas.uwasa.fi/cs/publications/2NWGA/node11.html#SECTION04120000000000000000
	private int						m_populationSize	= 1 * (int) ((Math
																.log(1 - Math
																		.pow(.99,
																				(1.0 / CITIES)))) / (Math
																.log(((float) (CITIES - 3)
																/ (float) (CITIES - 1)))));

	private int						m_startOffset		= 0;

	// Mutation rate = 1/X
	// The entire GA seems to work better with a high mutationRate (about 1 in
	// every 3 are mutated)
	// private final int mutationRate = (int) (m_populationSize * .03);
	private final int				mutationRate		= 3;


	public static Configuration getConfiguration() {

		return m_config;
	}


	/**
	 * Solve a sample task with the number of cities, defined in a CITIES
	 * constant. Print the known optimal way, sample chromosome and found
	 * solution.
	 * 
	 * @param args
	 *            not relevant here
	 * 
	 * @author Aaron Foltz
	 * @throws IOException
	 * @since 2.0
	 */
	public static void main(String[] args) throws IOException {

		// ---------------------------------------------
		// Gather input from the user
		System.out.print("Enter the file: ");
		Scanner scan = new Scanner(System.in);
		String file = scan.nextLine().concat(".tsp");

		System.out.print("Enter iterations: "); 
		int iterations = scan.nextInt();

		System.out.print("Enter optimal for this problem: ");
		int optimalTour = scan.nextInt();
		// ---------------------------------------------

		// ---------------------------------------------
		if (debugOutput) {
			// Write data to a file
			writer = new BufferedWriter(new FileWriter("data/"
					+ file.concat(".data")));
		}
		// ---------------------------------------------

		// Get the coordinates from the file. Store in an Arraylist
		CITYARRAY = Reader.getCoordinates(file);

		// Set the length of the CITIES array to the length of cities
		CITIES = CITYARRAY.length;

		/*
		 * Get the EdgeWeightType from the problem file. This will determine the
		 * type of approach to take when calculating distance
		 */
		EdgeWeightType = Reader.getEdgeWeightType(file);

		int average = 0;
		double averagePercent = 0, averageRunningTime = 0;
		int bestOverall = Integer.MAX_VALUE;

		for (int i = 0; i < iterations; i++) {
			// Gather the starting time for the program
			long startTime = System.currentTimeMillis();

			try {
				TravelingSalesman t = new TravelingSalesman();
				IChromosome optimal = t.findOptimalPath(null);

				// Gather the ending time of the program
				long endTime = System.currentTimeMillis();

				System.out.println("Solution: ");
				System.out.println(bestChromosome);
				System.out
							.println("Score " +
									(bestChromosome.getFitnessValue()));

				// Print out the total running time at the end
				System.out.println("RUNNING TIME: "
							+ (endTime - startTime) / 1000F + " seconds");

				// Save the best overall chromosome
				if (bestOverall > bestChromosome.getFitnessValue()) {
					bestOverall = (int) bestChromosome.getFitnessValue();
				}

				// Add fitness value, percentage, and running time to the
				// average
				average += bestChromosome.getFitnessValue();
				averagePercent += (100 * ((optimal.getFitnessValue() - optimalTour) / optimal
						.getFitnessValue()));
				averageRunningTime += (endTime - startTime) / 1000F;

				if (debugOutput) {
					// ---------------------------------------------
					// Add the fitness value and running time for each iteration
					writer.write((int) optimal.getFitnessValue() + "\t"
							+ (endTime - startTime) / 1000F + " seconds" + "\t");
					writer.write((100 * ((optimal.getFitnessValue() - optimalTour) / optimal
									.getFitnessValue()))
							+ "\n\n");

					// for (Object chromosomes2 :
					// population.getFittestChromosomes(100)) {
					// System.out.println("VALUE: "
					// + ((IChromosome) chromosomes2).getFitnessValue());
					// for (Gene gene : ((IChromosome) chromosomes2).getGenes())
					// {
					// System.out.print(gene.getAllele() + " ");
					// }
					// System.out.println();
					// }
				}

				getConfiguration().reset();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (debugOutput) {
			// Write out the average and best run
			writer.write("\n--------------------------------------------------\n");
			writer.write("AVERAGE: " + (average / iterations) + "\n");
			writer.write("AVERAGE PERCENT: " + (averagePercent / iterations)
					+ "\n");
			writer.write("AVERAGE RUNNINGTIME: "
					+ (averageRunningTime / iterations)
					+ "\n");
			writer.write("BEST: " + bestOverall + "\n\n");
			writer.write("PERCENTAGE: "
					+ ((float) (100 * (bestOverall - optimalTour) / bestOverall)));
			writer.close();
		}

		System.out
				.println("\n\n----------------------------------------------\nAVERAGE: "
						+ (average / iterations));
		System.out.println("AVERAGE PERCENT: " + (averagePercent / iterations));
		System.out.println("AVERAGE RUNNINGTIME: "
				+ (averageRunningTime / iterations));
		System.out.println("BEST: " + bestOverall);
		System.out.println("PERCENTAGE: "
				+ ((float) (100 * (bestOverall - optimalTour) / bestOverall)));

	}


	/**
	 * Create a configuration. The configuration should not contain operators
	 * for odrinary crossover and mutations, as they make chromosomes invalid in
	 * this task. The special operators SwappingMutationOperator and
	 * GreedyCrossover should be used instead.
	 * 
	 * @param a_initial_data
	 *            the same object as was passed to findOptimalPath. It can be
	 *            used to specify the task more precisely if the class is used
	 *            for solving multiple tasks
	 * 
	 * @return created configuration
	 * 
	 * @throws InvalidConfigurationException
	 * 
	 * @author Aaron Foltz
	 */
	public Configuration createConfiguration(final Object a_initial_data)
			throws InvalidConfigurationException {

		// TODO Look at setMonitor

		// This is copied from DefaultConfiguration.
		// -----------------------------------------
		Configuration config = new Configuration();
		BestChromosomesSelector bestChromsSelector =
				new BestChromosomesSelector(config, cullingPercentage);

		// The remaining 75% will be selected as duplicates of the most fit 25%
		// This allows us to keep the population size constant
		bestChromsSelector.setDoubletteChromosomesAllowed(true);
		config.addNaturalSelector(bestChromsSelector, true);

		// Creates random numbers used throughout the process
		config.setRandomGenerator(new StockRandomGenerator());

		config.setMinimumPopSizePercent(100);

		config.setEventManager(new EventManager());

		// Decides if a given fitness value is better if it is higher or lower
		config.setFitnessEvaluator(new OppositeFitnessEvaluator());

		// Used to preserve memory with the chromosome allocations
		config.setChromosomePool(new ChromosomePool());

		// Genetic operator for crossover - Grefenstettes Heuristic (greedy)
		// Crossover
		config.addGeneticOperator(new TravelingSalesmanHeuristicCrossover(
				config, this));

		// Genetic operator for mutation - simple swapping mutation
		// config.addGeneticOperator(new SwappingMutationOperator(config));
		config.addGeneticOperator(new SegmentSwappingMutation(config,
				mutationRate,
				this));
		return config;
	}


	/**
	 * Return the fitness function to use.
	 * 
	 * @param a_initial_data
	 *            the same object as was passed to findOptimalPath. It can be
	 *            used to specify the task more precisely if the class is used
	 *            for solving multiple tasks
	 * @return an applicable fitness function
	 * 
	 * @author Aaron Foltz
	 */
	public FitnessFunction createFitnessFunction(final Object a_initial_data) {

		return new TravelingSalesmanFitnessFunction(this);
	}


	/**
	 * Create an array of the given number of integer genes. The first gene is
	 * always 0, this is the city where the salesman starts the journey.
	 * 
	 * @param a_initial_data
	 *            ignored
	 * @return Chromosome
	 * 
	 * @author Aaron Foltz
	 */
	public IChromosome createSampleChromosome(Object a_initial_data) {

		try {
			Gene[] genes = new Gene[CITIES];
			for (int i = 0; i < genes.length; i++) {
				genes[i] = new IntegerGene(getConfiguration(), 0, CITIES - 1);
				genes[i].setAllele(new Integer(i));
				// System.out.println(genes[i]);
			}
			IChromosome sample = new Chromosome(getConfiguration(), genes);
			return sample;

		} catch (InvalidConfigurationException iex) {
			throw new IllegalStateException(iex.getMessage());
		}
	}


	/**
	 * Distance is equal to the difference between city numbers, except the
	 * distance between the last and first cities that is equal to 1. In this
	 * way, we ensure that the optimal solution is 0 1 2 3 .. n - easy to check.
	 * 
	 * @param a_from
	 *            first gene, representing a city
	 * @param a_to
	 *            second gene, representing a city
	 * @return the distance between two cities represented as genes
	 * 
	 * @author Aaron Foltz
	 */
	public double distance(Gene a_from, Gene a_to) {

		if (EdgeWeightType.equals("ATT")) {
			IntegerGene geneA = (IntegerGene) a_from;
			IntegerGene geneB = (IntegerGene) a_to;
			int a = geneA.intValue();
			int b = geneB.intValue();

			// If the same city, the distance is 0
			if (a == b) {
				return 0;
			}

			double x1 = CITYARRAY[a][0];
			double y1 = CITYARRAY[a][1];
			double x2 = CITYARRAY[b][0];
			double y2 = CITYARRAY[b][1];

			double xd = x1 - x2;
			double yd = y2 - y1;

			double rij = Math.sqrt(((xd * xd) + (yd * yd)) / 10.0);
			double tij = Math.round(rij);

			if (tij < rij) {
				return tij + 1;
			} else {
				return tij;
			}
		} else if (EdgeWeightType.equals("EUC_2D")) {
			IntegerGene geneA = (IntegerGene) a_from;
			IntegerGene geneB = (IntegerGene) a_to;
			int a = geneA.intValue();
			int b = geneB.intValue();

			// If the same city, the distance is 0
			if (a == b) {
				return 0;
			} else {
				double x1 = CITYARRAY[a][0];
				double y1 = CITYARRAY[a][1];
				double x2 = CITYARRAY[b][0];
				double y2 = CITYARRAY[b][1];

				double xd = x1 - x2;
				double yd = y1 - y2;

				return Math.round(Math.sqrt((xd * xd)
						+ (yd * yd)));

			}
		}

		return -1;

	}


	/**
	 * Distance is equal to the difference between city numbers, except the
	 * distance between the last and first cities that is equal to 1. In this
	 * way, we ensure that the optimal solution is 0 1 2 3 .. n - easy to check.
	 * 
	 * @param a_from
	 *            first gene, representing a city
	 * @param a_to
	 *            second gene, representing a city
	 * @return the distance between two cities represented as genes
	 * 
	 * @author Aaron Foltz
	 */
	public double distance(int a_from, int a_to) {

		if (EdgeWeightType.equals("ATT")) {

			int a = a_from;
			int b = a_to;

			// If the same city, the distance is 0
			if (a == b) {
				return 0;
			}

			double x1 = CITYARRAY[a][0];
			double y1 = CITYARRAY[a][1];
			double x2 = CITYARRAY[b][0];
			double y2 = CITYARRAY[b][1];

			double xd = x1 - x2;
			double yd = y2 - y1;

			double rij = Math.sqrt(((xd * xd) + (yd * yd)) / 10.0);
			double tij = Math.round(rij);

			if (tij < rij) {
				return tij + 1;
			} else {
				return tij;
			}
		} else if (EdgeWeightType.equals("EUC_2D")) {

			int a = a_from;
			int b = a_to;

			// If the same city, the distance is 0
			if (a == b) {
				return 0;
			} else {
				double x1 = CITYARRAY[a][0];
				double y1 = CITYARRAY[a][1];
				double x2 = CITYARRAY[b][0];
				double y2 = CITYARRAY[b][1];

				double xd = x1 - x2;
				double yd = y1 - y2;

				return Math.round(Math.sqrt((xd * xd)
						+ (yd * yd)));

			}
		}

		return -1;

	}


	/**
	 * Executes the genetic algorithm to determine the optimal path between the
	 * cities.
	 * 
	 * @param a_initial_data
	 *            can be a record with fields, specifying the task more
	 *            precisely if the class is used to solve multiple tasks. It is
	 *            passed to createFitnessFunction, createSampleChromosome and
	 *            createConfiguration
	 * 
	 * @throws Exception
	 * @return chromosome representing the optimal path between cities
	 * 
	 * @author Aaron Foltz
	 */
	public IChromosome findOptimalPath(final Object a_initial_data)
			throws Exception {

		m_config = createConfiguration(a_initial_data);
		FitnessFunction myFunc = createFitnessFunction(a_initial_data);
		m_config.setFitnessFunction(myFunc);
		// Now we need to tell the Configuration object how we want our
		// Chromosomes to be setup. We do that by actually creating a
		// sample Chromosome and then setting it on the Configuration
		// object.
		// --------------------------------------------------------------
		IChromosome sampleChromosome = createSampleChromosome(a_initial_data);
		m_config.setSampleChromosome(sampleChromosome);

		// Finally, we need to tell the Configuration object how many
		// Chromosomes we want in our population. The more Chromosomes,
		// the larger number of potential solutions (which is good for
		// finding the answer), but the longer it will take to evolve
		// the population (which could be seen as bad). We'll just set
		// the population size to 500 here.
		// ------------------------------------------------------------
		if (debugOutput) {
			System.out.println("\n\nPOPULATION SIZE: " + getPopulationSize());
			System.out.println("MAX EVOLUTIONS: " + m_maxEvolution);
			System.out.println("MUTATION RATE: " + mutationRate);
			System.out.println("CULLING PERCENTAGE: " + cullingPercentage);
		}

		m_config.setPopulationSize(getPopulationSize());
		// Create random initial population of Chromosomes.
		// ------------------------------------------------

		// As we cannot allow the normal mutations if this task,
		// we need multiple calls to createSampleChromosome.
		// -----------------------------------------------------
		IChromosome[] chromosomes =
				new IChromosome[m_config.getPopulationSize()];
		Gene[] samplegenes = sampleChromosome.getGenes();

		System.out.println();
		LinkedList<Integer> ll = new LinkedList<Integer>();
		for (int j = 1; j < samplegenes.length; j++) {
			ll.add(j);
		}

		for (int i = 0; i < chromosomes.length; i++) {
			// Shuffle the collection in place
			Collections.shuffle(ll);
			Gene[] genes = new Gene[samplegenes.length];

			// Manually set the starting city. We never want this to change
			genes[0] = samplegenes[0].newGene();
			genes[0].setAllele(samplegenes[0].getAllele());

			for (int k = 1; k < genes.length; k++) {
				genes[k] = samplegenes[ll.get(k - 1)].newGene();
				genes[k].setAllele(samplegenes[ll.get(k - 1)].getAllele());
			}
			// for (Gene gene : genes) {
			// System.out.print(gene.getAllele() + " ");
			// }
			// System.out.println();
			chromosomes[i] = new Chromosome(m_config, genes);
		}

		// Create the genotype. We cannot use Genotype.randomInitialGenotype,
		// Because we need unique gene values (representing the indices of the
		// cities of our problem).
		// -------------------------------------------------------------------
		population = new Genotype(m_config,
											new Population(m_config,
													chromosomes));
		IChromosome best = null;
		int counter = 0; // Exit after it hasn't changed for a number of times

		int previousBest = Integer.MAX_VALUE; // Track the last best chromosome
		// Evolve the population. Since we don't know what the best answer
		// is going to be, we just evolve the max number of times.
		// ---------------------------------------------------------------
		Evolution: for (int i = 0; i < getMaxEvolution(); i++) {
			best = population.getFittestChromosome();

			// if (debugOutput) {
			// System.out.println("\n----------------------------");
			// System.out.println("STARTING: " + best.getFitnessValue());
			//
			// for (Gene gene : best.getGenes()) {
			// System.out.print(gene.getAllele() + " ");
			// }
			// System.out.println();
			// }

			population.evolve();
			best = population.getFittestChromosome();

			// Write evolutionary progress to the file
			// if (debugOutput) {
			// writer.write(i + "\t" + best.getFitnessValue() + "\n");
			// }
			// if (debugOutput) {
			// System.out.println("\nAFTER EVOLUTION: " +
			// best.getFitnessValue());
			// for (Gene gene : best.getGenes()) {
			// System.out.print(gene.getAllele() + " ");
			// }
			// System.out.println("\n----------------------------");
			// }
			// Save the best fitness value for comparison in the next iteration
			// previousBest = (int) best.getFitnessValue();
			if (best.getFitnessValue() == previousBest) {
				// If it has been stuck here 30% of the total iterations in a
				// row, just exit
				if (counter++ == (m_maxEvolution * .3)) {
					System.out.println("Exiting Early");
					return best;
				}
			}
			// If it is the best so far, then keep it
			else if (best.getFitnessValue() < previousBest) {
				previousBest = (int) best.getFitnessValue();
				bestChromosome = best;
				counter = 0;

				// If worse, reset counter only
			} else if (best.getFitnessValue() > previousBest) {

				counter = 0;

			}

			previousBest = (int) best.getFitnessValue();

		}
		// Return the best solution we found.
		// ----------------------------------
		return best;
	}


	/**
	 * @return maximal number of iterations for population to evolve
	 * 
	 * @author Aaron Foltz
	 */
	public int getMaxEvolution() {

		return m_maxEvolution;
	}


	/**
	 * @return population size for this solution
	 */
	public int getPopulationSize() {

		return m_populationSize;
	}


	/**
	 * Gets a number of genes at the start of chromosome, that are excluded from
	 * the swapping. In the Salesman task, the first city in the list should
	 * (where the salesman leaves from) probably should not change as it is part
	 * of the list. The default value is 1.
	 * 
	 * @return start offset for chromosome
	 * 
	 */
	public int getStartOffset() {

		return m_startOffset;
	}


	/**
	 * Set the maximal number of iterations for population to evolve (default
	 * 512).
	 * 
	 * @param a_maxEvolution
	 *            sic
	 * 
	 * @author Aaron Foltz
	 */
	public void setMaxEvolution(final int a_maxEvolution) {

		m_maxEvolution = a_maxEvolution;
	}


	/**
	 * Set an population size for this solution (default 512)
	 * 
	 * @param a_populationSize
	 *            sic
	 */
	public void setPopulationSize(final int a_populationSize) {

		m_populationSize = a_populationSize;
	}


	/**
	 * Sets a number of genes at the start of chromosome, that are excluded from
	 * the swapping. In the Salesman task, the first city in the list should
	 * (where the salesman leaves from) probably should not change as it is part
	 * of the list. The default value is 1.
	 * 
	 * @param a_offset
	 *            start offset for chromosome
	 */
	public void setStartOffset(final int a_offset) {

		m_startOffset = a_offset;
	}

}
