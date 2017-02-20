package org.greengin.sciencetoolkit.logic.streams;

public class DataInputOutput extends DataOutput implements DataInput {

	@Override
	public void value(float[] values, int valueCount) {
		fireInput(values, valueCount);
	}

}
