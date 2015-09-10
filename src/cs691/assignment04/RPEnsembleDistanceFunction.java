package cs691.assignment04;

import java.util.HashMap;
import java.util.Map;

import weka.core.Instance;
import weka.core.Instances;

/**
 * This class implements Weka's DistanceFunction interface in order to use the similarity
 * values calculated during the repetitions of the Random Projection (RP)+EM runs directly with
 * Weka's HierarchicalClustering algorithm instead of having to implement your own HierarchicalClustering
 * algorithm. Technically this class extends the AbstractDistanceMeasure class which implements the needed
 * interface plus hides a lot of unused functions to reduce code complexity.
 * 
 * @author sloscal1
 *
 */
public class RPEnsembleDistanceFunction extends AbstractDistanceMeasure {
	/** The similarity values that were calculated during the RP+EM phase */
	private PairwiseStorage simValues;
	/** A mapping from Instances to indices. This is used because Weka expects a distance function to be
	 * calculated on-the-fly but we already have similarity values that we would like to use. We have those
	 * values indexed by their position in the Instances object, so we need a way to convert from
	 * Instance objects to their respective index in the Instances object. This object provides that mapping. */
	private Map<Long, Integer> instanceToIndex = new HashMap<>();
	/** A maximum similarity value is needed to convert from similarity (computed in the simValues) to distance
	 * (needed by the Hierarchical clustering algorithm. */
	private double maxSimilarity = -Double.MAX_VALUE;

	/**
	 * Create the distance function object to be used in the HierarchicalClusterer. It requires both the
	 * data used in the work as well as the similarity values that have been previously computed.
	 * 
	 * @param data must not be null
	 * @param simValues must not be null
	 */
	public RPEnsembleDistanceFunction(Instances data, PairwiseStorage simValues){
		this.simValues = simValues;
		//Create the mapping from Instance objects to their index in the PairwiseStorage object
		//WARNING: This is DANGEROUS! We cannot simply map from Instance to Integer because Weka copies the
		//Instances internally and passes those copies to the distance method and thus they are not equal 
		//(and do not have the same internal hashCode!) to the instances we have here. To get around this,
		//I have implemented a simple hashing function that will report the same hashCode if the instances have
		//the same values. I do not check for collisions, and numeric precision errors could cause trouble as well.
		//In my (limited) tests, it was okay...
		for(int i = 0; i < data.numInstances(); ++i){
			instanceToIndex.put(instanceHash(data.instance(i)), i);
		}
		
		//TODO Compute the maximum similarity value for all elements i != j in the PairwiseStorage object and
		//assign it to maxSimilarity
		for(int i = 0; i < data.numInstances(); i++ ){
			for(int j = 0; j < data.numInstances(); j++){
				if(i == j){
					continue;
				}
				if(simValues.get(i, j) > maxSimilarity){
					maxSimilarity = simValues.get(i, j);
				}
			}
		}
	}

	@Override
	public double distance(Instance arg0, Instance arg1) {
		//TODO Retrieve the similarity value between the two instances and transform it into a distance
		int ins0 = instanceToIndex.get(instanceHash(arg0));
		int ins1 = instanceToIndex.get(instanceHash(arg1));
		double sim = maxSimilarity - simValues.get(ins0, ins1);
		return sim; //FIXME Not exactly sure if this is the correct way, or there is another way to "transform the similarity to distance"
	}
	
	/**
	 * This is a quick hash function implementation that seems to work okay for the data I have tested...
	 * This requires O(n) work (where n is the number of attributes) - I can't cache it either since the Instances continue to be
	 * copied whenever Weka requests a distance lookup. This could be made much more efficient if we implemented our own
	 * HierarchicalClusterer.
	 * @param inst and Instance object to generate a hashcode for must not be null
	 * @return a hashcode.
	 */
	private long instanceHash(Instance inst){
		//This is done for you - you will need it in distance
		long hash = 0;
		for(int i = 0; i < inst.numAttributes(); ++i)
			hash += (int)(31 * inst.value(i));
		return hash;
	}
}