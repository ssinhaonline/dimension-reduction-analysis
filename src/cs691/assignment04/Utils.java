package cs691.assignment04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.Math;

import weka.clusterers.Clusterer;
import weka.clusterers.EM;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.EuclideanDistance;

/**
 * This class contains utility methods that will be useful for several portions of this assignment. For those of you new to Java,
 * they are static methods, meaning they belong to the class and not any specific object and that you can call them 
 * (from other places) using: Utils.computeProbs(...).
 * 
 * @author sloscal1
 *
 */
public class Utils {	
	/**
	 * Compute the probability p_{i,j} - the probability that an instance in cluster j belongs to class i.
	 * 
	 * @param clustering sets of ids representing cluster membership
	 * @param classLabels contains the class labels of each element in the data (.get(id) returns instance at index id's class label).
	 * classLabels are assumed to be 0 .. number of class labels - 1.
	 * @return a matrix such that matrix[j][i] is the probability that an instance in cluster j belongs to class i.
	 */
	public static double[][] computeProbs(List<Set<Integer>> clustering, List<Integer> classLabels){
		//System.out.println("ComputeProbs printing:");
		List<Integer> classLabelsDup = classLabels;
		Set<Integer> uniqueClassLabels = new HashSet<Integer>(classLabelsDup); //Distinct class labels
		
		int numClassLabels = uniqueClassLabels.size();	//TODO Get the number of distinct class labels
		List<Set<Integer>> classes = new ArrayList<>();
		for(int i = 0; i < uniqueClassLabels.size(); i++){
			classes.add(new HashSet<Integer>());
		}
		for(int i = 0; i < classLabels.size(); i++){
			int classID = classLabels.get(i);
			classes.get(classID).add(i);
		}
		/*for(int i = 0; i < classes.size(); i++){
			System.out.print(classes.get(i) + " ");
		}
		System.out.println();*/
		double[][] probs = null;
		//System.out.println("NumClusters = " + clustering.size() + "NumClassLabels = " + numClassLabels);
		probs = new double[clustering.size()][numClassLabels];	//TODO Create a num clusters x num class labels matrix
		for(int j = 0; j < clustering.size(); j++){
			Set<Integer> clusterIds = clustering.get(j);
			for(int i = 0; i < classes.size(); i++){
				Set<Integer> classIds = classes.get(i);
				Set<Integer> intersection = new HashSet<Integer>(classIds);
				intersection.retainAll(clusterIds);
				//TODO Fill in the probability value for each cell in the matrix (make sure they are normalized so that all values fall between 0 and 1).
				probs[j][i] = (double)intersection.size() / (double)classLabels.size();
				//System.out.print(probs[j][i] + ",");
			}
			//System.out.println();
		}
		return probs;
	}



	/**
	 * Compute the entropy of an entity given an array of probability distributions with respect to class labels
	 * @param probabilities an array of p_{i,j} values, in this case, each index contains the probability of an element in
	 * this cluster belonging to class i.
	 * @return the entropy value
	 */
	public static double entropy(double[] probabilities){
		double entropy = 0.0;
		for(int i = 0; i < probabilities.length; i++){
			if(probabilities[i]!=0){
				double p = probabilities[i];
				entropy +=  -1 * p * Math.log(p)/Math.log(2.0);
			}
		}
		//TODO Be sure to use log base 2 in your computations! This is as was done with information gain for decision trees.
		//Be careful to avoid taking a log of 0...
		return entropy;
	}

	/**
	 * Compute normalized mutual information according to the description on page 5 of http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf
	 * 
	 * @param clustering sets of ids representing cluster membership
	 * @param classLabels contains the class labels of each element in the data (.get(id) returns instance at index id's class label).
	 * classLabels are assumed to be 0 .. number of class labels - 1.
	 * @return the normalized mutual information for this clustering given the class labels
	 */
	public static double computeNormalizedMutualInformation(List<Set<Integer>> clustering, List<Integer> classLabels){
		//Get the probability of having label i when in cluster j
		double[][] pij = computeProbs(clustering, classLabels);
		List<Integer> classLabelsDup = classLabels;
		Set<Integer> uniqueClassLabels = new HashSet<Integer>(classLabelsDup); //Distinct class labels
		int numClassLabels = uniqueClassLabels.size(); // Number of distinct class labels
		int[] classDistribution = new int[numClassLabels];
		List<Set<Integer>> classes = new ArrayList<>();
		for(int i = 0; i < uniqueClassLabels.size(); i++){
			classes.add(new HashSet<Integer>());
		}
		for(int i = 0; i < classLabels.size(); i++){
			int classID = classLabels.get(i);
			classes.get(classID).add(i);
		}
		for(int i = 0; i < numClassLabels; i++){
			classDistribution[i] = classes.get(i).size(); //TODO Get the class count distribution
		}
		//TODO Use that to get the probability of being in any cluster
		double[] pj = new double[clustering.size()];
		for(int j = 0; j < clustering.size(); j++){
			double pjsum = 0.0;
			for(int i = 0; i < numClassLabels; i++){
				pjsum += pij[j][i];
			}
			pj[j] = pjsum;
		}
		//TODO Compute the probabilities of being in class i
		double[] pi = new double[numClassLabels];
		//Again, these are probabilities so should be between 0 and 1.
		for(int i = 0; i < numClassLabels; i++){
			double pisum = 0.0;
			for(int j = 0; j < clustering.size(); j++){
				pisum += pij[j][i];
			}
			pi[i] = pisum;
		}
		double mutualInfo = 0;
		for(int j = 0; j < clustering.size(); j++){
			for(int i = 0; i < numClassLabels; i++){
				if(pij[j][i] > 0)
					mutualInfo += pij[j][i] * (Math.log((pij[j][i] / (pi[i] * pj[j]))) / Math.log(2.0)); //TODO Compute the mutual information using the above quantities (and log base 2)
				else
					continue;
			}
		}
		double NMI = mutualInfo / (Math.sqrt(Utils.entropy(pi) * Utils.entropy(pj)));
		//TODO return the normalized mutual information
		return NMI;
	}

	/**
	 * Compute conditional entropy according to the description on page 5 of http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf
	 * 
	 * @param clustering sets of ids representing cluster membership
	 * @param classLabels contains the class labels of each element in the data (.get(id) returns instance at index id's class label).
	 * classLabels are assumed to be 0 .. number of class labels - 1.
	 * @return the conditional entropy for this clustering given the class labels
	 */
	public static double computeConditionalEntropy(List<Set<Integer>> clustering, List<Integer> classLabels){
		double ce = 0.0;
		List<Integer> classLabelsDup = classLabels;
		Set<Integer> uniqueClassLabels = new HashSet<Integer>(classLabelsDup); //Distinct class labels
		int numClassLabels = uniqueClassLabels.size(); // Number of distinct class labels
		double[][] pij = computeProbs(clustering, classLabels);
		//TODO Sum the entropy of each cluster (easier than the NMI)
		double Ej;
		for(int j = 0; j < clustering.size(); j++){
			Ej = 0.0;
			for(int i = 0; i < numClassLabels; i++){
				//System.out.rintln(Math.log(pij[j][i]));
				if(pij[j][i] <= 0)
					continue;
				else{
					Ej += -1.0 * pij[j][i] * Math.log(pij[j][i])/Math.log(2.0);
				}
				
			}
			int nj = clustering.get(j).size();//get size of clustering[j]
			ce += (nj * Ej);
		}
		
		return ce/(double)classLabels.size();
	}
	/*public static double computeConditionalEntropy(List<Set<Integer>> clustering, List<Integer> classLabels){
		double ce = 0.0;
		double[][] pij = computeProbs(clustering, classLabels);
		//TODO Sum the entropy of each cluster (easier than the NMI)
		for (int j = 0; j < clustering.size(); j++){
			ce = ce + clustering.size()*entropy(pij[j])/classLabels.size();
		}
		
		return ce;
	}*/

	/**
	 * Given a constructed Clusterer object (like EM) and a set of instances, return a list of sets of indices where
	 * each set represents a cluster, and the indices are with respect to the given data object.
	 * 
	 * @param clusterer a pre-built clustering algorithm (i.e., buildClusterer has already been called)
	 * @param data a non-null set of instances
	 * @return the cluster member ship of all instances in data
	 * 
	 * @throws Exception could be thrown if the clusterer does not handle the instance or is not built.
	 */
	public static List<Set<Integer>> getClustering(Clusterer clusterer, Instances data) throws Exception {
		//Create an appropriate number of empty clusters
		List<Set<Integer>> assignments = new ArrayList<>();
		for(int i = 0; i < clusterer.numberOfClusters(); ++i)
			assignments.add(new HashSet<Integer>());
		//TODO Put the id's of each instance into the appropriate set
		//use the clusterInstance method of clusterer
		for(int i = 0; i < data.numInstances(); i++){
			int clusterID = clusterer.clusterInstance(data.get(i));
			assignments.get(clusterID).add(i);
		}
		return assignments;
	}
	
	/**
	 * Creates a list of integers representing the class labels of the data, the list has the same number of entries
	 * as instances in data. Unlike the class labels in data, the labels in the returned list are integers from 0 .. num unique class
	 * labels. This method allows easier indexing in some of the evaluation methods.
	 * @param data must be non-null
	 * @return a list of integers representing the class label for each instance in data, or null if the data does not have an
	 * attribute named "class".
	 */
	public static List<Integer> extractClassLabels(Instances data){
		List<Integer> classLabels = null;
		//The map from Weka's internal double value to our desired class label value
		Map<Double, Integer> mappedLabels = new HashMap<>();
		int labelOn = 0;
		//Assume that the arff file calls the class attribute "class" - the classIndex may not be set so
		//don't rely on it!
		if(data.attribute("class") != null){
			//TODO Get the index where the class attribute occurs
			int i = data.attribute("class").index();
			//TODO Go through each instance and create a map entry for each previously unseen double - each
			//new entry in the map will increment labelOn
			//You may find containsKey useful for this part.
			classLabels = new ArrayList<>();
			
			for (int pos = 0; pos < data.size(); pos++){
				double key = data.get(pos).value(i);
				if(!mappedLabels.containsKey(key)){
					mappedLabels.put(key, labelOn);
					labelOn++;
				}
				classLabels.add(mappedLabels.get(key));
			}
		}
		return classLabels;
	}
	
	/**
	 * Create a new Instances object that is identical to data except the class attribute (if it exists in data) has
	 * been removed from the returned object.
	 * @param data non-null
	 * @return a new Instances object as described above
	 */
	public static Instances removeClassLabel(Instances data){
		//This is already done for you - if there is a class attribute, remove it.
		Instances ret = new Instances(data);
		if(data.attribute("class") != null)
			ret.deleteAttributeAt(data.attribute("class").index());
		
		return ret;
	}
	
	/**
	 * Compute the Bayesian Information Criterion (BIC) given this data and an EM clusterer (that has already been built).
	 * Use http://www.aladdin.cs.cmu.edu/papers/pdfs/y2000/xmeans.pdf Section 3.2 to complete this implementation (wikipedia
	 * has too high a level discussion on BIC to implement it, but it may help understand what it is doing).
	 * 
	 * @param data non-null
	 * @param clusterer already built
	 * @return the BIC expressing how likely this data is given the model subject to some mild model complexity penalty
	 * @throws Exception don't worry about throwing exceptions... if shouldn't happen if the input is right.
	 */
	/*public static double computeBIC(Instances data, EM clusterer) throws Exception{
		double bic = 0.0;
		int numInstances = data.numInstances();
		List<Set<Integer>> clustering = Utils.getClustering(clusterer, data);

		//Compute the standard deviation of the data {\sigma^2} of the data (assuming spherical Gaussian distributions)
		//TODO First we need the centers of each cluster
		double[][] centers = new double[clustering.size()][data.numAttributes()];
		//TODO Fill in centers correctly!
		for(int i = 0; i < clustering.size(); i++){
			Set<Integer> insIDs = clustering.get(i);
			Instances subData = new Instances(data, insIDs.size());
			for(Integer ins : insIDs){
				subData.add(data.get(ins));
			}
			for(int j = 0; j < subData.numAttributes(); j++){
				double sum = 0;
				for(int k = 0; k < subData.size(); k++){
					sum += subData.get(k).value(j);
				}
				centers[i][j] = sum / subData.size() * 1.0;
			}
		}
		//TODO Next, we compute the variance of each sample as described in that paper
		//Remember: that paper is not the same one as for the rest of this assignment!
		
		//TODO Sum the log-likelihood for each cluster
		
		//TODO Compute the complexity penalty (number of free parameters)
	
		//TODO Sum the log-likelihood and complexity penalty to complete the BIC computation
		return bic;
	}*/
	public static double computeSSE(Instances data, EM clusterer) throws Exception{
		//System.out.println("Entered ComputeSSE...");
		double sse = 0.0;
		//int numInstances = data.numInstances();
		List<Set<Integer>> clustering = Utils.getClustering(clusterer, data);
		double[][] centers = new double[clustering.size()][data.numAttributes()];
		//TODO Fill in centers correctly!
		for(int i = 0; i < clustering.size(); i++){
			Set<Integer> insIDs = clustering.get(i);
			Instances subData = new Instances(data, insIDs.size());
			for(Integer ins : insIDs){
				subData.add(data.get(ins));
			}
			for(int j = 0; j < subData.numAttributes(); j++){
				double sum = 0;
				for(int k = 0; k < subData.size(); k++){
					sum += subData.get(k).value(j);
				}
				centers[i][j] = sum / subData.size() * 1.0;
			}
		}
		for(int i = 0; i < clustering.size(); i++){
			Instance clusterCenter = new DenseInstance(data.get(0));
			for(int j = 0; j < data.numAttributes(); j++){
				clusterCenter.setValue(j, centers[i][j]);
			}
			Set<Integer> insIDs = clustering.get(i);
			Instances subData = new Instances(data, insIDs.size());
			for(Integer ins : insIDs){
				subData.add(data.get(ins));
			}
			EuclideanDistance d = new EuclideanDistance(subData);
			for(int k = 0; k < subData.size(); k++){
				sse += d.distance(clusterCenter, subData.get(k));
			}
		}
		return sse;
	}
}
