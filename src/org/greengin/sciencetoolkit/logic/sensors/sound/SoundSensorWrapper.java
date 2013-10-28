package org.greengin.sciencetoolkit.logic.sensors.sound;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class SoundSensorWrapper extends SensorWrapper {
	public static final String STK_SOUND_SENSOR_NEWVALUE = "STK_SOUND_SENSOR_NEWVALUE";

	SoundSensorRunnable runnable;
	float[] values;

	public SoundSensorWrapper(Context applicationContext) {
		this.runnable = new SoundSensorRunnable(applicationContext);
		this.runnable.setFreq(44100);
		this.runnable.setLength(100);

		values = new float[2];

		LocalBroadcastManager.getInstance(applicationContext).registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				setSoundLevel(intent.getFloatExtra("value", 0), intent.getFloatExtra("maxfreq", 0));
			}

		}, new IntentFilter(SoundSensorWrapper.STK_SOUND_SENSOR_NEWVALUE));
	}

	@Override
	public String getName() {
		return "sound";
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
			Thread th = new Thread(this.runnable);
			th.start();
		}
	}

	public void onInputRemoved(boolean empty, int inputCount) {
		if (empty) {
			this.runnable.stopSensor();
		}
	}

	private void setSoundLevel(float i, float f) {
		values[0] = i;
		values[1] = f;
		fireInput(values, 2);
	}

	@Override
	public int getValueCount() {
		return 2;
	}

	@Override
	public int getType() {
		return SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND;
	}

}
