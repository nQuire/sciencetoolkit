package org.greengin.sciencetoolkit.logic.sensors.sound;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.SenseItModelDefaults;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class SoundSensorFFTWrapper extends SensorWrapper implements ModelNotificationListener {
	public static final String STK_SOUND_SENSOR_NEWVALUE = "STK_SOUND_SENSOR_NEWVALUE";

	public static boolean isAvailable(Context applicationContext) {
		return applicationContext.getPackageManager().hasSystemFeature("android.hardware.microphone");
	}

	String settingsId;
	Model settings;
	SoundSensorFFTRunnable runnable;
	float[] values;

	public SoundSensorFFTWrapper(Context applicationContext) {
		super (SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND);
		
		this.runnable = new SoundSensorFFTRunnable(applicationContext);
		this.runnable.setFreq(44100);
		
		this.settingsId = "sensor:" + getId();
		
		this.settings = SettingsManager.get().get(settingsId);
		this.runnable.setLength(settings.getInt("record_period", SenseItModelDefaults.SOUND_SENSOR_PERIOD));

		values = new float[2];

		LocalBroadcastManager.getInstance(applicationContext).registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				setSoundLevel(intent.getFloatExtra("value", 0), intent.getFloatExtra("maxfreq", 0));
			}

		}, new IntentFilter(SoundSensorFFTWrapper.STK_SOUND_SENSOR_NEWVALUE));
		
		SettingsManager.get().registerDirectListener(settingsId, this);
	}

	@Override
	public String getName() {
		return "Sound";
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

	@Override
	public void modelNotificationReceived(String msg) {
		this.runnable.setLength(settings.getInt("record_period", SenseItModelDefaults.SOUND_SENSOR_PERIOD));
	}

}
