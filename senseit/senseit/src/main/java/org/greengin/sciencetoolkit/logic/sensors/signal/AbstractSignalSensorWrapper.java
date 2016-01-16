package org.greengin.sciencetoolkit.logic.sensors.signal;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.signal.SignalStrengthListener;
import org.greengin.sciencetoolkit.logic.signal.SignalStrengthManager;

import android.content.Context;
import android.util.Log;

public abstract class AbstractSignalSensorWrapper extends SensorWrapper implements ModelNotificationListener, SignalStrengthListener {

	public static boolean isAvailable(Context applicationContext) {
		return true;
	}

	Model settings;
	float[] values;
	
	public AbstractSignalSensorWrapper(Context applicationContext) {
		super (SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM);
		values = new float[] {0};
	}

	@Override
	public float getResolution() {
		return 1;
	}

	@Override
	public int getMinDelay() {
		return 100;
	}

	@Override
	public float getMaxRange() {
		return 1000;
	}

	public void onInputAdded(boolean first, int inputCount) {
		if (first) {
			SignalStrengthManager.get().addListener(this);
			fire();
		}
	}

	public void onInputRemoved(boolean empty, int inputCount) {
		if (empty) {
			SignalStrengthManager.get().removeListener(this);
		}
	}
	
	protected abstract String network();

	@Override
	public void signalStrengthChange() {
		fire();
	}
	
	private void fire() {
		values[0] = SignalStrengthManager.get().getSignalStrength(network());
		fireInput(values, 1);
		Log.d("stk signal", "sensor " + network() + " " + values[0]);
	}

	@Override
	public int getValueCount() {
		return 1;
	}

	@Override
	public void modelNotificationReceived(String msg) {
	}
	
	@Override
	public float[] lastValue() {
		return values;
	}
}
