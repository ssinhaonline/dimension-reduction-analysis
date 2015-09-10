package cs691.assignment04;

import java.util.List;
import java.util.Set;

import weka.clusterers.EM;
import weka.core.Instance;
import weka.core.Instances;
//import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;

/**
 * This class can be used to conduct the experiments in http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf regarding
 * the PCA+EM algorithm (the baseline in their experiments). It should be used to verify that you have correctly
 * implemented the methods in the Utils class before moving on to the more challenge ClusterEnsemble work.
 * 
 * @author sloscal1
 *
 */
public class PCAMethod implements ClusteringMethod {
	
	/** The number of components to use in PCA (set as in Table 3 of http://www.aaai.org/Papers/ICML/2003/ICML03-027.pdf ) */
	private int pcaComps = 5;
	/** The number of clusters to use in the EM algorithm. */
	private int emNumClusters = 6;

	/**
	 * Given the desired dimensionality of the PCA-reduced data and the number of clusters to select when using
	 * EM, construct the PCA+EM algorithm object.
	 * 
	 * @param pcaComps must be positive
	 * @param emNumClusters must be positive
	 */
	public PCAMethod(int pcaComps, int emNumClusters){
		this.pcaComps = pcaComps;
		this.emNumClusters = emNumClusters;
	}

	/**
	 * This method does a Principle Components Analysis on the given data to reduce its dimensionality down to
	 * the specified size. 
	 * 
	 * @param data a non-null data set to base the transformation on
	 * @param numComps the desired resulting dimensionality of the transformation
	 * @return a new data set that has as many instances as the given data but with numComps number of attributes
	 * 
	 * @throws Exception if soemthing goes wrong during the transformation
	 */
	public Instances doPCA(Instances data, int numComps) throws Exception{
		//Use weka's weka.filters.unsupervised.attribute.PrincipalComponents class
		PrincipalComponents pca = new PrincipalComponents();
		pca.setInputFormat(data);
		//Set the number of attributes
		pca.setMaximumAttributes(numComps);
		//TODO Follow the example in Weka's API to apply the filter to data and produce the result:
		// http://weka.sourceforge.net/doc.dev/weka/filters/Filter.html
		for(int i = 0; i < data.size(); i++){
			pca.input(data.instance(i));
		}
		pca.batchFinished();
		//TODO You will need to create a new Instances object and return it - do not modify argument data!
		
		Instances pcaData = pca.getOutputFormat();
		Instance newData ;
		while((newData = pca.output())!= null){
			pcaData.add(newData);
		}
		//Instances pcaData = Filter.useFilter(data, pca);
		//System.out.println("Leaving doPCA...");
		return pcaData;
	}


	@Override
	public List<Set<Integer>> getClusters(Instances data, int run) throws Exception {
		//Reduce the data dimensionality using PCA
		Instances reducedData = doPCA(data, pcaComps);
		//TODO Create an EM algorithm with:
		//a desired number of clusters (use the emNumClusters value), 
		//seed (the run argument),
		//and build the clusterer on the PCA reduced data
		EM clusterer = new EM();
		clusterer.setNumClusters(emNumClusters);
		/*String[] options = new String[2];
		options[0] = "-S";                 
		options[1] = Integer.toString(run);
		clusterer.setOptions(options);*/
		clusterer.setMaxIterations(run+1);
		clusterer.buildClusterer(reducedData);
		
		//Get the results of the clustering
		return Utils.getClustering(clusterer, reducedData);
	}
}
