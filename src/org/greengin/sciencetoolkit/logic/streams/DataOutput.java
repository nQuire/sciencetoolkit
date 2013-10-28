package org.greengin.sciencetoolkit.logic.streams;

import java.util.Vector;

public class DataOutput {
	
	Vector<DataInput> inputs;
	
	public DataOutput() {
		inputs = new Vector<DataInput>();
	}	
	
	public void addInput(DataInput input) {
		if (!inputs.contains(input) && input != null) {
			inputs.add(input);
			onInputAdded(inputs.size() == 1, inputs.size());
		}
	}
	
	public void removeInput(DataInput input) {
		if (inputs.remove(input)) {
			onInputRemoved(inputs.size() == 0, inputs.size());
		}
	}
	
	protected void fireInput(float[] values, int valueCount) {
		for (DataInput input : inputs) {
			input.value(values, valueCount);
		}
	}
	
	protected boolean hasInputs() {
		return this.inputs.size() > 0;
	}
	
	
	protected void onInputAdded(boolean first, int inputCount) {
	}
	
	protected void onInputRemoved(boolean empty, int inputCount) {
	}
	
	

}
