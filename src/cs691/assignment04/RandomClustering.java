package cs691.assignment04;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import weka.core.Instances;

/**
 * This class performs a "random" partition clustering of the data to provide some basic sanity-checking values for the other methods
 * you will need to implement as part of this assignment. If the Conditional Entropy and Normalized Mutual Information values
 * are not better than what is done by random, something is not quite right in your implementations!
 * @author sloscal1
 *
 */
public class RandomClustering implements ClusteringMethod {
	/** The number of "clusters" to find */
	private int numClusters;
	
	/**
	 * Create a new random clustering procedure which will "find" numClusters number of clusters (or fewer if there
	 * are not enough data points given to the algorithm).
	 * 
	 * @param numClusters must be &gt; 0
	 */
	public RandomClustering(int numClusters){
		this.numClusters = numClusters;
	}
	
	@Override
	public List<Set<Integer>> getClusters(Instances data, int run)
			throws Exception {
		//Make a few empty clusters...
		List<Set<Integer>> clustering = new ArrayList<>(numClusters);
		for(int i = 0; i < numClusters; ++i)
			clustering.add(new HashSet<Integer>());
		
		//Randomly assign each index of data to one of the clusters
		Random rand = new Random(run);
		for(int i = 0; i < data.numInstances(); ++i)
			clustering.get(rand.nextInt(numClusters)).add(i);
		
		//Ensure we return no empty clusters
		List<Set<Integer>> nonEmptyClustering = new ArrayList<>(numClusters);
		for(int i = 0; i < numClusters; ++i)
			if(clustering.get(i).size() != 0)
				nonEmptyClustering.add(clustering.get(i));
		
		//Return the clustering!
		return nonEmptyClustering;
	}

}
