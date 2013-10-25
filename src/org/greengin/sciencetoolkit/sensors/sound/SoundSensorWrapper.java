package org.greengin.sciencetoolkit.sensors.sound;


import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.SensorWrapperManager;
import org.greengin.sciencetoolkit.sensors.BasicSensorWrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;


public class SoundSensorWrapper extends BasicSensorWrapper {
	public static final String ACTION_SOUND_SENSOR_VALUE = "ACTION_SOUND_SENSOR_VALUE";

	SoundSensorRunnable runnable;	
	
	public SoundSensorWrapper(Context context) {
		super(context);
		this.runnable = new SoundSensorRunnable(context);
		this.runnable.setFreq(44100);
		this.runnable.setLength(100);
		
		this.lastValue = new float[]{0, 0};
		this.updateMaxRange();
		this.createValueUnits();
		
		LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				setSoundLevel(intent.getFloatExtra("value", 0), intent.getFloatExtra("maxfreq", 0));
			}
			
		}, new IntentFilter(SoundSensorWrapper.ACTION_SOUND_SENSOR_VALUE));
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
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if (enabled) {
			Thread th = new Thread(this.runnable);
	        th.start();
		} else {
			this.runnable.stopSensor();
		}
	}
	
	public void setSoundLevel(float i, float f) {
		this.lastValue[0] = i;
		this.lastValue[1] = f;
		this.updateCurentValue();
		this.fireValueEvent();
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
	public String[] getValueLabels() {
		return new String[]{"Sound level", "Max freq."};
	}

	private void createValueUnits() {
		this.setValueUnits(new String[] {"dB", "Hz"});
	}
	
	@Override
	public List<Bundle> getOptions() {
		Vector<Bundle> list = (Vector<Bundle>)super.getOptions();
		
		Bundle period = new Bundle();
		period.putString("key", "record_period");
		period.putString("name", "Sample period");
		period.putString("description", "The duration of the recording period (ms).");
		period.putString("type", "number");
		period.putDouble("min", this.getMinDelay());
		list.insertElementAt(period, 0);

		return list;
	}
	
	@Override
	public Object getOptionValue(String key) {
		if (key == "record_period") {
			return this.runnable.getLength();
		} else {
			return super.getOptionValue(key);
		}
	}
	
	@Override
	public boolean setOptionValue(String key, Object value) {
		if (key == "record_period") {
			this.runnable.setLength(Math.max(this.getMinDelay(), (Integer) value));
			return true;
		} else {
			return super.setOptionValue(key, value);
		}
	}

}
