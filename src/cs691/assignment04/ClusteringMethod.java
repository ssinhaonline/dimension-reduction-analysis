package cs691.assignment04;

import java.util.List;
import java.util.Set;

import weka.core.Instances;

/**
 * This interface abstracts out all the general behavior of each of the three clustering procedures that you have
 * been asked to work on in this assignment.
 * 
 * @author sloscal1
 *
 */
public interface ClusteringMethod {
	/**
	 * The overall method to call from Experiment that follows the specific procedure in each of the
	 * implementing classes to get the clustering result given specific data and which run is being executed.
	 * 
	 * @param data must not be null
	 * @param run (so far used to seed a random value)
	 * @return clustering result as used throughout this work
	 * @throws Exception don't worry about properly handling these - if everything works and the input data is valid
	 * no exceptions should be thrown.
	 */
	List<Set<Integer>> getClusters(Instances data, int run) throws Exception;
}
