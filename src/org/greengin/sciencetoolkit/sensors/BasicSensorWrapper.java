package org.greengin.sciencetoolkit.sensors;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.os.Bundle;

public abstract class BasicSensorWrapper extends SensorWrapper {

	protected boolean configFilterEnabled;
	protected long configFilterPeriod;
	private Vector<TimeValue> configFilterValues;
	protected float[] lastValue;

	public BasicSensorWrapper(Context context) {
		super(context);
		this.configFilterEnabled = false;
		this.configFilterPeriod = 100;
		this.configFilterValues = new Vector<TimeValue>();
		this.lastValue = new float[]{0, 0, 0};
	}

	@Override
	public float[] getValue() {
		return this.lastValue;
	}
	
	@Override
	public List<Bundle> getOptions() {
		Vector<Bundle> list = new Vector<Bundle>();

		Bundle filter = new Bundle();
		filter.putString("key", "filter");
		filter.putString("name", "Filter data");
		filter.putString("description", "Enables a moving average filter that smooths short-term fluctuations.");
		filter.putString("type", "toggle");
		list.add(filter);

		Bundle filterPeriod = new Bundle();
		filterPeriod.putString("key", "filter_period");
		filterPeriod.putString("requires", "filter");
		filterPeriod.putString("name", "Filter period");
		filterPeriod.putString("description", "The duration of the filtering period (ms).");
		filterPeriod.putString("type", "number");
		filterPeriod.putDouble("min", this.getMinDelay());
		list.add(filterPeriod);

		return list;
	}
	

	public Object getOptionValue(String key) {
		if (key == "filter") {
			return this.configFilterEnabled;
		} else if (key == "filter_period"){
			return this.configFilterPeriod;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean setOptionValue(String key, Object value) {
		if (key == "filter") {
			this.configFilterEnabled = (Boolean) value;
			return true;
		} else if (key == "filter_period"){
			this.configFilterPeriod = (Integer) value;
			return true;
		} 
		
		return false;
	}

	protected void updateCurentValue() {
		if (this.configFilterEnabled) {
			long currentTime = System.currentTimeMillis();
			long th = currentTime - this.configFilterPeriod;
			
			Iterator<TimeValue> it = this.configFilterValues.iterator();
			while (it.hasNext()) {
				if (it.next().time < th) {
					it.remove();
				} else {
					break;
				}
			}
			
			this.configFilterValues.add(new TimeValue(System.currentTimeMillis(), this.getValue()));
			
			for (int i = 0; i < this.getValueCount(); i++) {
				double v = 0;
				for (TimeValue tv : this.configFilterValues) {
					v += tv.value[i];
				}
				this.lastValue[i] = (float) (v / this.configFilterValues.size());
			}			
		}
	}
}
