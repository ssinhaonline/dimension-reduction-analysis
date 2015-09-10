/**
 * <h1>Assignment 4</h1><br>
 * <h2>Overview:</h2>
 * <br><br>
 * DUE: 11:59PM Last class day!<br>
 * YOU MAY work in teams of two if desired.
 * <p>
 * This assignment has been designed to build from some of the topics that we've recently covered and introduce
 * some new ones that we will be covering through the end of class. It will also test your ability to verify
 * results that have been reported in the literature.
 * <p>
 * The overall idea is to demonstrate one way in which an ensemble of clustering results can outperform a single clustering
 * algorithm, even one that uses a known good way of pre-processing the data to improve clustering results.
 * <p>
 * You will be striving to implement the RP+EM algorithm (Random Projections with Expectation Maximization) described in the
 * paper:
 * <br>
 * Fern, X., Brodley, C., "Random Projection for High Dimensional Data Clustering: A Cluster Ensemble Approach", in the Proceedings
 * of the 20th International Conference on Machine Learning (ICML) 2003.
 * <p>
 * You will need to read various sections of the paper to get a better idea on how to implement certain components of the algorithm,
 * and I have attempted to refer you to specific sections where appropriate to cut down on your search time. You will also be
 * implementing the evaluation metrics they used, as well as the alternative approach (PCA+EM, Principle Components Analysis). For the
 * most part, Weka has usable implementations of the various algorithms you will be using (PCA, EM, RandomProjections, and
 * HierarchicalClusterer), but there is still substantial amounts of work to do on your part.
 * <p>
 *  You will need to complete the following files in this package:
 *  <ul>
 *  	<li>Utils.java - various utility functions that are used in preprocessing, evaluation, etc.</li>
 *  	<li>PCAMethod.java - the PCA+EM method described in the paper</li>
 *  	<li>PairwiseStorage - a utility class to store pairwise similarity values for the RP+EM algorithm</li>
 *  	<li>RPEnsembleDistanceFunction.java - an adapter class to enable the similarity values to be directly used
 *          in Weka's HierarchicalClustering algorithm</li>
 *  	<li>ClusterEnsemble.java - the RP+EM algorithm</li>
 *  	<li>Experiment.java - the main function that is used to produce results like in the paper to compare the performance of the
 *      various algorithms.</li>
 *  </ul>
 *  There are several additional files which are used but you DO NOT need to modify:
 *  <ul>
 *  	<li>Pair.java - a utility class that encapsulates two objects; useful to use with Java's sorting routines</li>
 *  	<li>RandomClustering.java - an algorithm that produces random clusterings. Useful in sanity checking that the
 *          algorithms implemented from the paper are actually doing something!</li>
 *  	<li>ClusteringMethod.java - an interface that the different clustering approaches implement so that they can all use
 *  	    the same Experiment.java class.</li>
 *  	<li>AbstractDistanceMeasure.java - a simple base class to hide a lot of unused methods from you in the RPEnsembleDistanceFunction
 *  		class</li>
 * 	</ul>
 * I recommend reading through the comments in all of the files.
 * <br><br>
 * <h2>Part I Instructions</h2>
 * <ol>
 * 	<li>Complete all the methods in Utils.java. Read the referenced documents where appropriate - you may want to construct some simple
 *      tests to verify that they work before moving forward. I think Bayesian Information Criterion will be the most challenging.</li>
 *  <li>Complete the PCAMethod.java class.</li>
 *  <li>Complete Experiment.java. You should now test the PCAMethod class using the CHART.arff data file and Experiment.java - it is the same 
 *  	as was used in the paper so you should observe similar trends (though not necessarily the same exact numbers).</li>
 *  <li>Complete PairwiseStorage.java. You can use a matrix to do this, but if you want to push your skills a little further, you can
 *      use about half as much storage since Sim(i,j) = Sim(j,i), and Sim(i,i) is never evaluated. </li>
 *  <li>Complete RPEnsembleDistanceFunction.java. There is not much to be done there, it is primarily just to get you to read about how
 *      you sometimes need to work around library code when it doesn't exactly fit your needs.</li>
 *  <li>Complete ClusterEnsemble.java. This is the RP+EM algorithm and has a few different moving parts. You may want to implement each
 *      individually and test them separately to facilitate debugging.</li>
 *  <li>Verify that ClusterEnsemble works by using it in the Experiment class and observing that you get an improvement as shown in
 *      the paper's experimental results section (specifically the work on Table 4). </li>
 *  </ol>
 *  <br><br>
 *  <h2>Part II Instructions</h2>
 *  Use the code written in Part I to show graphs of CE and NMI for each of the algorithms (and the RandomClustering class) as
 *  the number of clusters increases by 1 from 2 to 20. The plots should be line graphs with error bars showing +- 1 standard
 *  deviation. Modify the Experiment.java file to use the t-test code written for Assignment 3 so that you can highlight when one
 *  method was statistically significantly better than the rest on the CHART dataset.
 *  <p>
 *  In your report, also discuss which algorithm you would prefer from a time complexity (or running time) perspective, and how you
 *  could improve the RP+EM's running time. Briefly describe how you could use k-Means (or one of its variants like PAM) instead of EM in an
 *  ensemble - be sure to mention how you would compute the pairwise similarity values without having the cluster probabilities as
 *  in EM.
 *  <br><br>
 *  <h2>Submission Instructions</h2>
 *  Submit all code and the report in one zip file to Blackboard. The report must have all team member's names on it, and the report
 *  should clearly document any parts of the assignment that were not completed or attempted.
 * 
 */
package cs691.assignment04;
