package org.greengin.sciencetoolkit.sensors;

public class TimeValue {
	public long time;
	public float[] value;	
	
	public TimeValue(long time, float[] value) {
		this.time = time;
		this.value = value.clone();
	}
}

