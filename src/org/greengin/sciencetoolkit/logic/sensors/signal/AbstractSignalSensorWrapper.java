package org.greengin.sciencetoolkit.logic.sensors.signal;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public abstract class AbstractSignalSensorWrapper extends SensorWrapper implements ModelNotificationListener {

	public static boolean isAvailable(Context applicationContext) {
		return true;
	}

	Model settings;
	float[] values;
	TelephonyManager manager;
	
	PhoneStateListener listener;

	public AbstractSignalSensorWrapper(Context applicationContext) {
		super (SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM);
		manager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
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
			manager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
	}

	public void onInputRemoved(boolean empty, int inputCount) {
		if (empty) {
			manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		}
	}

	@Override
	public int getValueCount() {
		return 1;
	}

	@Override
	public void modelNotificationReceived(String msg) {
	}
	
}
