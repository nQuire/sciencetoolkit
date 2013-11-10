package org.greengin.sciencetoolkit.logic.streams;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class DataOutput {
	
	private ReentrantLock lock;
	private Vector<DataInput> inputs;
	
	public DataOutput() {
		lock = new ReentrantLock();
		inputs = new Vector<DataInput>();
	}	
	
	public void addInput(DataInput input) {
		lock.lock();
		
		if (!inputs.contains(input) && input != null) {
			inputs.add(input);
			onInputAdded(inputs.size() == 1, inputs.size());
		}
		
		lock.unlock();
	}
	
	public void removeInput(DataInput input) {
		lock.lock();
		
		if (inputs.remove(input)) {
			onInputRemoved(inputs.size() == 0, inputs.size());
		}
		
		lock.unlock();
	}
	
	protected void fireInput(float[] values, int valueCount) {
		lock.lock();
		
		for (DataInput input : inputs) {
			input.value(values, valueCount);
		}
		
		lock.unlock();
	}
	
	protected boolean hasInputs() {
		return this.inputs.size() > 0;
	}
	
	
	protected void onInputAdded(boolean first, int inputCount) {
	}
	
	protected void onInputRemoved(boolean empty, int inputCount) {
	}
	
	

}
