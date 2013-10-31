package org.greengin.sciencetoolkit.logic.sensors;

import org.greengin.sciencetoolkit.logic.streams.DataOutput;

public abstract class SensorWrapper extends DataOutput {
	
	public boolean isEnabled() {
		return this.hasInputs();
	}
	
	abstract public int getType();

	abstract public String getName();

	abstract public int getValueCount();

	abstract public float getResolution();

	abstract public int getMinDelay();

	abstract public float getMaxRange();
}
