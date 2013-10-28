package org.greengin.sciencetoolkit.logic.streams.filters;

import java.util.Timer;
import java.util.TimerTask;

import org.greengin.sciencetoolkit.logic.streams.DataInput;
import org.greengin.sciencetoolkit.logic.streams.DataOutput;

public class FixedRateDataFilter extends DataOutput implements DataInput {

	float[] currentValue;
	int currentValueCount;
	int period;
	Timer timer;
	
	public FixedRateDataFilter(int period) {
		this.timer = null;
		this.currentValue = new float[3];
	}
	
	public void setPeriod(int period) {
		this.period = period;
		if (hasInputs()) {
			start();
		}
	}
	
	public int getPeriod() {
		return this.period;
	}
	
	
	@Override
	public void value(float[] values, int valueCount) {
		for (int i = 0; i < valueCount; i++) {
			this.currentValue[i] = values[i];
		}
		this.currentValueCount = valueCount;
	}
	
	@Override
	protected void onInputAdded(boolean first, int inputCount) {
		if (first) {
			start();
		}
	}
	
	protected void onInputRemoved(boolean empty, int inputCount) {
		if (empty) {
			stop();
		}
	}
	
	private void start() {
		if (timer != null) {
			stop();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				fireInput(currentValue, currentValueCount);
			}

		}, 0, this.period);
	}
	
	private void stop() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

}
