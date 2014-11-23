package org.greengin.sciencetoolkit.logic.sensors;

import org.greengin.sciencetoolkit.logic.streams.DataOutput;

public abstract class SensorWrapper extends DataOutput {
	
	private String id;
	
	public SensorWrapper(int type) {
		this.id = SensorWrapperManager.get().getId(type);
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isEnabled() {
		return this.hasInputs();
	}
	
	abstract public int getType();

	abstract public String getName();

	abstract public int getValueCount();

	abstract public float getResolution();

	abstract public int getMinDelay();

	abstract public float getMaxRange();
	
	abstract public float[] lastValue();
}
