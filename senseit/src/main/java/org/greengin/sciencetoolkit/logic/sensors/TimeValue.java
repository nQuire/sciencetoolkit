package org.greengin.sciencetoolkit.logic.sensors;

public class TimeValue {
	public long time;
	public float[] value;	
	
	public TimeValue(long time, float[] value) {
		set(time, value);
	}
	
	public void set(long time, float[] value) {
		this.time = time;
		this.value = value.clone();
	}
}

