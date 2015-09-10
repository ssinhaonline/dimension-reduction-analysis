package cs691.assignment04;

import java.util.Enumeration;

import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.neighboursearch.PerformanceStats;

/**
 * NO CHANGES ARE NECESSARY IN THIS FILE
 * 
 * It exists solely to hide some of the unused functions in the
 * DistanceFunction interface from you to make it cleaner to implement the RPDistanceFunction class.
 * 
 * @author sloscal1
 *
 */
public abstract class AbstractDistanceMeasure implements DistanceFunction {
	//PRETTY LAZY: The clustering algorithm may use one of a few different distance methods,
	//I'm redirecting all of them to the most simplistic one that you must implement (in RPEnsembleDistanceFunction)

	@Override
	public double distance(Instance arg0, Instance arg1, PerformanceStats arg2)
			throws Exception {
		return distance(arg0, arg1);
	}

	@Override
	public double distance(Instance arg0, Instance arg1, double arg2) {
		return distance(arg0, arg1);
	}

	@Override
	public double distance(Instance arg0, Instance arg1, double arg2,
			PerformanceStats arg3) {
		return distance(arg0, arg1);
	}
	
	//REALLY LAZY: Empty implementations of the methods specified in the interface

	@Override
	public String[] getOptions() {
		return null;
	}

	@Override
	public Enumeration<Option> listOptions() {
		return null;
	}

	@Override
	public void setOptions(String[] arg0) throws Exception {}

	@Override
	public void clean() {}
	
	@Override
	public String getAttributeIndices() {
		return null;
	}

	@Override
	public Instances getInstances() {
		return null;
	}

	@Override
	public boolean getInvertSelection() {
		return false;
	}

	@Override
	public void postProcessDistances(double[] arg0) {}

	@Override
	public void setAttributeIndices(String arg0) {}

	@Override
	public void setInstances(Instances arg0) {}

	@Override
	public void setInvertSelection(boolean arg0) {}

	@Override
	public void update(Instance arg0) {	}

}
