package cs691.assignment04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.unsupervised.attribute.RandomProjection;

/**
 * This class encapsulates the RP+EM algorithm described by the paper found at http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf .
 * 
 * @author sloscal1
 *
 */
public class ClusterEnsemble implements ClusteringMethod {
	/** The number of clustering algorithms to use in the ensemble */
	private int numEnsembleMembers = 30;
	/** The seed value for the random projections (set to ensure experimental repeatability) */
	private int randomProjectionsSeed = 2521641;
	/** The noise threshold used in the HierarchicalClustering phase of the algorithm */
	private double percentNoiseThreshold = 10.0;
	/** The consensus number of clusters to use in the end */
	private int numFinalClusters = 6;
	
	/**
	 * Construct a new ClusterEnsemble method which implements the algorithm described in http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf .
	 * 
	 * 
	 * @param numEnsembleMembers the size of the ensemble (number of Random Projections to use)
	 * @param numFinalClusters the number of clusters to be returned by the Hierarchical Clusterer
	 * @param percentNoiseThreshold the percentage (0 - 100) of points to consider noise and exclude from the Hierarchical Clustering
	 * process.
	 */
	public ClusterEnsemble(int numEnsembleMembers, int numFinalClusters, double percentNoiseThreshold){
		this.numEnsembleMembers = numEnsembleMembers;
		this.numFinalClusters = numFinalClusters;
		this.percentNoiseThreshold = percentNoiseThreshold;
	}


	/**
	 * This method follows the procedure described on the left column of page 3 of 
	 * http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf . It will produce a number of clusterings (each done
	 * on a different random projection of the data) and will use those clustering results to determine pairwise instance 
	 * similarity based on how likely it is that two points appear together in many different clustering results.
	 * 
	 * @param data non-null data with no class label!
	 * @param n the number of random projections to execute
	 * @return an object containing the pairwise similarity values for all pairs i,j of Instances in data according to the procedure
	 * described in the linked paper above.
	 * 
	 * @throws Exception will occur if something is wrong with the data - don't worry about properly handling it
	 */
	public PairwiseStorage doMultipleRandomProjections(Instances data, int n) throws Exception{
		//Create storage for the eventual similarity values
		PairwiseStorage simValues = new PairwiseStorage(data.numInstances());

		//Create a random value to allow the same RandomProjections to be created on subsequent runs of the
		//program.
		Random rand = new Random(randomProjectionsSeed);
		//Loop through the requested number of iterations
		for(int lcv = 0; lcv < n; ++lcv){
			System.out.println("Random Proj "+lcv+": ");
			//Create the weka.filters.unsupervised.attribute.RandomProjection  object...
			RandomProjection proj = new RandomProjection();
			//TODO use the rand variable to set the seed of the proj object
			
			proj.setSeed(rand.nextInt());
			proj.setInputFormat(data);
			
			//TODO Use proj like the PrincipleComponents filter in PCAMethod.java
			Instances rpData = proj.getOutputFormat(); //The resulting transformed data from the proj object
			
			for(int i = 0; i < data.size(); i++){
				proj.input(data.instance(i));
			}
			
			proj.batchFinished();
			Instance newData;
			while((newData = proj.output())!= null){
				rpData.add(newData);
			}

			//Find the best clustering on it (the right number of clusters may change as a result of the projection)
			EM ensembleMemberClusterer = getBestClusterer(rpData);
			int numOfCluster = ensembleMemberClusterer.getNumClusters();
			double sumProb = 0.0;
			for(int i = 0; i < rpData.size(); i++){
				for(int j = 0; j <rpData.size(); j++){
					double pi[] = ensembleMemberClusterer.distributionForInstance(rpData.get(i));
					double pj[] = ensembleMemberClusterer.distributionForInstance(rpData.get(j));
					for(int k = 0; k < numOfCluster; k++){
						sumProb = sumProb+ pi[k]*pj[k];
					}
					
					if(lcv == 0){
						simValues.set(i, j, (sumProb/(double)n));
					}
					else{
						double sim = simValues.get(i, j);
						simValues.set(i, j, sim+(sumProb/(double)n));
					}
					
				}
			}
			
			//TODO Update the simValues data structure according to the description in the paper
		}

		//Return the similarity value object
		return simValues;
	}

	/**
	 * This method determines the appropriate number of clusters to use in an EM algorithm by finding the number of
	 * clusters where BIC is maximized. 
	 * @param data must not be null
	 * @return the EM object that achieves the best BIC.
	 * @throws Exception don't worry about these - it shouldn't happen if the input data is correct and you did everything right.
	 */
	public EM getBestClusterer(Instances data) throws Exception{
		//TODO Figure out the appropriate number of clusters to use based on the BIC.
		double sses[] = new double[9];
		EM[] collectionClusterers = new EM[9];
		double eps = Math.PI/18;
		EM best = null;
		for(int i = 2; i <=10; i++){
			EM clusterer = new EM();
			clusterer.setNumClusters(i);
			clusterer.buildClusterer(data);
			sses[i - 2] = Utils.computeSSE(data, clusterer);
			collectionClusterers[i -2] = clusterer;
			if(i >= 4){
				double theta1 = Math.atan(sses[i - 1 - 2] - sses[i - 2 - 2]);
				double theta2 = Math.atan(sses[i - 2 ] - sses[i - 1 - 2]);
				if(Math.abs(theta2 - theta1) <= eps){
					best = collectionClusterers[i - 1 -2];
					break;
				}
			}
		}
		//In theory, you start from 1 cluster and increment the number of clusters until
		//BIC does not improve. In practice, BIC may be a bit more unpredictable so I would
		//suggest computing numClusters from 2 to some value M and return the EM algorithm that
		//produced the best BIC score.
		//TODO: While debugging you may just want to fix the number of clusters to a single value		
		return best;
	}
	
	public EM getBestClustererBIC(Instances data) throws Exception{
		//TODO Figure out the appropriate number of clusters to use based on the BIC.
		//In theory, you start from 1 cluster and increment the number of clusters until
		//BIC does not improve. In practice, BIC may be a bit more unpredictable so I would
		//suggest computing numClusters from 2 to some value M and return the EM algorithm that
		//produced the best BIC score.
		//TODO: While debugging you may just want to fix the number of clusters to a single value.
		EM best = null;
		EM tmp = null;
		int bestC = 1;
		for (int numOfC = 1; numOfC < data.size(); numOfC++){
			best.setNumClusters(bestC);
			tmp.setNumClusters(numOfC);
			best.buildClusterer(data);
			tmp.buildClusterer(data);
			//double bestResult = Utils.computeBIC(data, best);
			//double tmpResult = Utils.computeBIC(data, tmp);
			//if(bestResult <= tmpResult){
				//bestC = numOfC;
			//}
			
		}
		best.setNumClusters(bestC);
		best.buildClusterer(data);
		return best;
	}


	/**
	 * Conduct a hierarchical clustering on the data points using the simValues to inform distance as described on the
	 * right column of page 3 in http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf . 
	 * 
	 * @param data non-null data
	 * @param simValues the previously computed similarity values (called P in the paper)
	 * @param desiredResultingClusters the number of clusters to return from the hierarchical clustering process
	 * @return The clustering found by the HierarchicalClustering process
	 * @throws Exception don't worry about proper handling of errors here
	 */
	public List<Set<Integer>> mergeClusters(Instances data, PairwiseStorage simValues, int desiredResultingClusters) throws Exception{
		//Need to pull off 10% of the least similar entities (potential noise elements) from this data according to
		//last paragraph of page 3
		//Keep track of each Instance's best similarity value
		List<Pair<Integer, Double>> maxSims = new ArrayList<>(data.numInstances());
		for(int i = 0; i < data.numInstances(); ++i)
			maxSims.add(new Pair<>(i, -Double.MAX_VALUE));
		
		//TODO Find the maximum similarity value for each instance
		//Use the get and set methods from Pair to accomplish this
		
		for (int i = 0; i<maxSims.size(); i++){
			double maxSim = 0.0;
			for (int j = 0; j < data.size(); j++){
				double tmp = simValues.get(i, j);
				if (tmp >= maxSim){
					maxSim = tmp;
				}
			}
			maxSims.get(i).setValue(maxSim);
		}
		
		//Sort the Pair objects 
		Collections.sort(maxSims);
		
		//TODO Figure out how many instances to excluded from the data to be clustered.
		//TODO Create another Instances object with the included data to use in the clustering
		//TODO Be sure to store the indices of the excluded points elsewhere
		
		int numOfNoise = (int) (maxSims.size() * 0.1);
		Instances reduced = new Instances(data,data.size()-numOfNoise);
		List<Integer> noiseList = new ArrayList<Integer>();
		for(int i = 0; i < numOfNoise; i++){
			noiseList.add(maxSims.get(i).getIndex());
		}
		System.out.println(data.size());
		for(int i = 0; i < data.size(); i++){
			if(!noiseList.contains(i)){
				reduced.add(data.get(i));
			}
		}
		//Create the RPEnsembleDistanceFunction on the FULL DATA passed to this method:

		//Do the hierarchical clustering
		HierarchicalClusterer agglomerativeHC = new HierarchicalClusterer();
		//TODO set the distance function
		//TODO set the desired number of clusters (see the appropriate field)
		
		RPEnsembleDistanceFunction rp = new RPEnsembleDistanceFunction(data, simValues);
		agglomerativeHC.setDistanceFunction(rp);
		agglomerativeHC.setNumClusters(desiredResultingClusters);
		
		//Use COMPLETE link - weird call caused by not having an appropriate API for this algorithm, but it's right.
		agglomerativeHC.setLinkType(new SelectedTag(1, HierarchicalClusterer.TAGS_LINK_TYPE));
		
		//TODO build the cluster on the reduced data
		agglomerativeHC.buildClusterer(reduced);

		//Now get the clustering assignments of the points used to build the clusterer!
		List<Set<Integer>> assignments = Utils.getClustering(agglomerativeHC, reduced);
		
		Instances noiseData = new Instances(data, numOfNoise);
		for(int i = 0; i< noiseList.size();i++){
			noiseData.add(data.get(noiseList.get(i)));
		}
		
		List<Set<Integer>> noisyAssignmetns = Utils.getClustering(agglomerativeHC, noiseData);
		for (int i = 0;i< assignments.size(); i++ ){
			if(noisyAssignmetns.get(i) != null){
				assignments.get(i).addAll(noisyAssignmetns.get(i));
			}
		}

		//Determine the assignment of the "noise" points to the appropriate clusters
		//TODO Keep track of the assignment of the noise points to their most similar cluster - do NOT change the cluster membership
		//as you go.

		//TODO Finally, update the cluster membership found by the algorithm to include the noise points
	
		return assignments;
	}

	@Override
	public List<Set<Integer>> getClusters(Instances data, int run) throws Exception {
		//Implements the ClusteringMethod interface. This method must be completed by calling the appropriate two
		//methods from this class. It will take either 1 or 2 lines of code. 
		PairwiseStorage simM = this.doMultipleRandomProjections(data, this.numEnsembleMembers);	
		return this.mergeClusters(data, simM, this.numFinalClusters);
	}
}
