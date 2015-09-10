package cs691.assignment04;

/**
 * NO CHANGES ARE NECESSARY IN THIS FILE
 * 
 * A simple utility class that pairs up two objects (nominally called an index and a value).
 * The only requirement is that the "value" must implement the Comparable interface. Pair
 * objects also have a natural sorting order - the same sorting order preferred by the "value"
 * compareTo method. When comparing two Pair objects, a null one will be "less than" a non-null
 * one, and elements with null "value" fields will also be "less than" ones with non-null "value"
 * fields. The ordering among the null Pair objects and null value fields within non-null Pair objects
 * is unspecified. (Avoid comparing uninitialized Pair objects!)
 * 
 * @author sloscal1
 *
 * @param <T> Any object, nominally called the "index" in this work.
 * @param <U> Any object that has a natural ordering, the "value" in this work.
 */
public class Pair<T, U extends Comparable<U>> implements Comparable<Pair<T,U>> {
	private T index;
	private U value;
	
	/**
	 * Construct a new Pair object with the index field set to the first constructor
	 * argument and the value field set to the second constructor argument.
	 * @param index should not be null
	 * @param value should not be null, must be a Comparable type
	 */
	public Pair(T index, U value){
		this.index = index;
		this.value = value;
	}
	
	/**
	 * @return get the index element (first constructor param) of this object
	 */
	public T getIndex(){
		return index;
	}
	
	public void setIndex(T index){
		this.index = index;
	}
	
	/**
	 * @return get the value element (second constructor param) of this object
	 */
	public U getValue(){
		return value;
	}
	
	public void setValue(U value){
		this.value = value;
	}
	
	@Override
	public int compareTo(Pair<T,U> o) {
		return o == null || o.value == null || value == null ? -1 : value.compareTo(o.value);
	}


}
