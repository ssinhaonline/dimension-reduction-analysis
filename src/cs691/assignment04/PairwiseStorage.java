package cs691.assignment04;

import java.util.ArrayList;

/**
 * This class creates and provides access to storage of pairwise statistics.
 * 
 * This class assumes that similarity values are symmetric: sim(i,j) = sim(j,i) for
 * all elements i,j in the data. It further assumes that self-similarity is always equal:
 * sim(i,i) = sim(j,j) for all elements i,j in the data.
 * 
 * You can use a 2-d array if you'd like, or perhaps something more space efficient.
 * @author sloscal1
 *
 */
public class PairwiseStorage{
	//TODO You will need to have another field here to store the similarity values - your choice of data structure, though
	//I would recommend O(1) lookup and updates.
	/** The number of elements in the data */
	@SuppressWarnings("unused")
	private int numElements;
	@SuppressWarnings("rawtypes")
	private ArrayList<ArrayList<Pair>> simList = new ArrayList<>(); 

	/**
	 * Create a storage object capable of holding the pairwise similarity values for all
	 * possible pairs of elements of a given number of elements (numElements).
	 * @param numElements must &gt; 0
	 */
	@SuppressWarnings("rawtypes")
	public PairwiseStorage(int numElements){
		if(numElements <= 0) throw new IllegalArgumentException("numElements must be > 0.");
		this.numElements = numElements;
		//TODO Do anything you might have to do to initialize your data structure
		
		for (int i = 0; i< numElements; i++){
			simList.add(new ArrayList<Pair>());
		}
	}

	/**
	 * Return the similarity value for the pair of elements in the data with id's i and j.
	 * @param i must satisfy 0 &lt; i &lt; numElements
	 * @param j must satisfy 0 &lt; j &lt; numElements
	 * @return the current value stored for the given pair of elements
	 */
	public double get(int i, int j){
		double simValue = 0.0;
		simValue = (Double) simList.get(i).get(j).getValue();
		return simValue;
	}

	/**
	 * Set the similarity value for the pair of elements in the data with id's i and j.
	 * @param i must satisfy 0 &lt; i &lt; numElements
	 * @param j must satisfy 0 &lt; j &lt; numElements
	 * @param value the similarity of elements i and j.
	 */
	public void set(int i, int j, double value){
		//TODO as described in the comments
		Pair<Integer, Double> p = new Pair<Integer, Double>(j, value);
		this.simList.get(i).add(j, p);
		
	}

	//Helper method (depending on your storage choice, you may not need this, delete it if you don't use it)
	//that maps the given indices to the index in storage where the similarity value is stored.
	@SuppressWarnings("unused")
	private int index(int i, int j){
		return 0;
	}
}
