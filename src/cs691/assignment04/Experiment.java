package cs691.assignment04;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.StatUtils;

//import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.*;

public class Experiment {
	/** The number of runs to conduct the desired algorithm when gathering results */
	private static int numRuns = 30;
	
	/**
	 * The entry point to this program, can be used to generate results like those found in table 4 of the reference
	 * paper.
	 * 
	 * @param args[0] can be the data file name to use, if it is not given we assume that "./Data/CHART.arff" contains the
	 * data to use for this experiment.
	 * 
	 * @throws Exception because no error handling is done at all in the program. Sorry.
	 */
	public static void main(String[] args) throws Exception {
		//Set some debugging defaults...
		String fileName = "Data/CHART.arff";

		//If there are arguments to the program, use those...
		if(args.length == 1)
			fileName = args[0];

		//Read in a data set
		Instances data = new Instances(new BufferedReader(new FileReader(new File(fileName))));

		//Store the class labels for later verification purposes
		List<Integer> classLabels = Utils.extractClassLabels(data);
		
		//Remove the class label (some of Weka's clustering algorithms break if the class label is detected)
		data = Utils.removeClassLabel(data);
		Normalize n = new Normalize(); //FIXME not sure if need to set range
		n.setInputFormat(data);
		Instances newData = Filter.useFilter(data, n);
		data = new Instances(newData);
		//make sure data is the new normalized data
		//TODO Create the appropriate clustering method you'd like (or add them all to a list etc.)
		double varCEs[] = new double [19];
		double cess[] = new double [19];
		double varNMIs[] = new double [19];
		double nmiss[] = new double [19];
		for(int numCl = 2; numCl <= 20; numCl++)
		{
			int numFinalClusters = numCl;
			ClusteringMethod cm = new PCAMethod(5, numFinalClusters);
			//ClusteringMethod cm = new ClusterEnsemble(30, numFinalClusters, 10.0);
			//ClusteringMethod cm = new RandomClustering(numFinalClusters);
		
			//Create some room to store the evaluation results
			double[] nmis = new double[numRuns];
			double[] ces = new double[numRuns];
			
			//For each run:
			for(int i = 0; i < /*2*/numRuns; ++i){ //FIXME
				System.out.println("On numCl: " + numCl + ",run: " + i);
				//Get the clusters from whatever method has been selected
				List<Set<Integer>> clustering = cm.getClusters(data, i);
				//Compute the statistics as done in the paper (use the Utils class!)
				
				nmis[i] = Utils.computeNormalizedMutualInformation(clustering, classLabels);
				ces[i] = Utils.computeConditionalEntropy(clustering, classLabels);
			}
		
			//Print the mean and variance of nmis and ces - make sure you get the same trends as seen on page 6 of
			// http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf
			//System.out.println("CES Array size:" + ces.length);
			/*for(int i = 0; i < ces.length; i++){
				System.out.print(ces[i] + " ");
			}*/
			//System.out.println("NMIS Array size:" + nmis.length);
			double varCE = StatUtils.variance(ces);
			varCEs[numCl - 2] = varCE;
			cess[numCl - 2] = StatUtils.mean(ces);
			System.out.println("Conditional Entropy: "+StatUtils.mean(ces)+"+/-"+Math.sqrt(varCE));
			double varNMI = StatUtils.variance(nmis);
			varNMIs[numCl - 2] = varNMI;
			nmiss[numCl - 2] = StatUtils.mean(nmis);
			System.out.println("Normalized Mutual Information: "+StatUtils.mean(nmis)+"+/-"+Math.sqrt(varNMI));
		}
		for(int numCl = 2; numCl <=20; numCl++){
			System.out.println(numCl + ": " + cess[numCl - 2] + ", " + Math.sqrt(varCEs[numCl - 2]) + ", " + nmiss[numCl - 2] + ", " + Math.sqrt(varNMIs[numCl - 2]));
		}
	}
}
