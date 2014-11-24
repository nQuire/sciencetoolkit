package org.greengin.sciencetoolkit.logic.streams.filters;

import java.util.Timer;
import java.util.TimerTask;

import org.greengin.sciencetoolkit.logic.streams.DataInputOutput;

public class FixedRateDataFilter extends DataInputOutput {

	float[] currentValue;
	int currentValueCount;
	int period;
	Timer timer;
	boolean hasData;

	public FixedRateDataFilter(int period) {
		this.period = period;
		this.timer = null;
		this.currentValue = new float[3];
		hasData = false;
	}

	public void setPeriod(int period) {
		if (this.period != period) {
			this.period = period;
			if (hasInputs()) {
				start();
			}
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
		hasData = true;
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
		if (this.period > 0) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (hasData) {
						fireInput(currentValue, currentValueCount);
					}
				}

			}, 100, this.period);
		}
	}

	private void stop() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

}
