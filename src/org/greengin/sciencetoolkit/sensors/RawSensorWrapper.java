package org.greengin.sciencetoolkit.sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class RawSensorWrapper extends BasicSensorWrapper implements SensorEventListener {

	public static final int[] DELAY_MODE_VALUES = new int[] { SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_NORMAL };

	public static final String[] DELAY_MODE_LABELS = new String[] { null, "50 samples/s", "16 samples/s", "5 samples/s" };

	SensorManager sensorManager;
	Sensor sensor;
	int delay;

	public RawSensorWrapper(Context context, Sensor sensor, SensorManager sensorManager) {
		super(context);
		this.sensor = sensor;
		this.lastValue = null;
		this.delay = SensorManager.SENSOR_DELAY_GAME;
		this.sensorManager = sensorManager;
		this.updateMaxRange();
		this.createValueUnits();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (enabled != this.enabled) {
			if (enabled) {
				sensorManager.registerListener(this, this.sensor, this.delay);
			} else {
				sensorManager.unregisterListener(this);
			}
		}

		super.setEnabled(enabled);
	}

	@Override
	public String getName() {
		return this.sensor.getName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String[] getValueLabels() {
		switch (sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_LINEAR_ACCELERATION:
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
		case Sensor.TYPE_ORIENTATION:
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_ROTATION_VECTOR:
		case Sensor.TYPE_GRAVITY:
			return new String[] { "x", "y", "z" };
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			return new String[] { "Temperature" };
		case Sensor.TYPE_LIGHT:
			return new String[] { "Light level" };
		case Sensor.TYPE_PRESSURE:
			return new String[] { "Pressure" };
		case Sensor.TYPE_PROXIMITY:
			return new String[] { "Proximity" };
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return new String[] { "Rel. humidity" };
		default:
			return new String[] { "values" };
		}
	}

	@SuppressWarnings("deprecation")
	private void createValueUnits() {
		String[] units;
		switch (sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_LINEAR_ACCELERATION:
		case Sensor.TYPE_GRAVITY:
			units = new String[] { "m/s²", "m/s²", "m/s²" };
			break;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			units = new String[] { "°C" };
			break;
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			units = new String[] { "rad/s", "rad/s", "rad/s" };
			break;
		case Sensor.TYPE_LIGHT:
			units = new String[] { "lx" };
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			units = new String[] { "μT", "μT", "μT" };
			break;
		case Sensor.TYPE_ORIENTATION:
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_ROTATION_VECTOR:
			units = new String[] { "°", "°", "°" };
			break;
		case Sensor.TYPE_PRESSURE:
			units = new String[] { "hPa" };
			break;
		case Sensor.TYPE_PROXIMITY:
			units = new String[] { "cm" };
			break;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			units = new String[] { "%" };
			break;
		default:
			units = new String[] { "?", "?", "?" };
			break;
		}

		this.setValueUnits(units);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getValueCount() {
		switch (sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_LINEAR_ACCELERATION:
		case Sensor.TYPE_GRAVITY:
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
		case Sensor.TYPE_ORIENTATION:
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_ROTATION_VECTOR:
			return 3;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
		case Sensor.TYPE_LIGHT:
		case Sensor.TYPE_PRESSURE:
		case Sensor.TYPE_PROXIMITY:
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return 1;
		default:
			return 3;
		}
	}

	@Override
	public float getResolution() {
		return this.sensor.getResolution();
	}

	@Override
	public int getMinDelay() {
		return this.sensor.getMinDelay();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		this.lastValue = event.values;
		this.updateCurentValue();
		this.fireValueEvent();
	}

	@Override
	public float getMaxRange() {
		return this.sensor.getMaximumRange();
	}

	@Override
	public int getType() {
		return this.sensor.getType();
	}

	public Object getOptionValue(String key) {
		if (key == "rawdelay") {
			return this.delay;
		} else {
			return super.getOptionValue(key);
		}
	}

	@Override
	public boolean setOptionValue(String key, Object value) {
		if (key == "rawdelay") {
			this.delay = (Integer) value;
			if (this.isEnabled()) {
				sensorManager.unregisterListener(this);
				sensorManager.registerListener(this, this.sensor, this.delay);
			}
			return true;
		} else {
			return super.setOptionValue(key, value);
		}
	}

	@Override
	public List<Bundle> getOptions() {
		int minDelay = this.getMinDelay();
		if (minDelay > 0) {			
			Vector<Bundle> list = new Vector<Bundle>();

			Bundle mode = new Bundle();
			mode.putString("key", "rawdelay");
			mode.putString("name", "Sensor rate");
			mode.putString("description", "Select the rate at which the sensor produces new values.");
			mode.putString("type", "select");
			ArrayList<String> labels = new ArrayList<String>(Arrays.asList(RawSensorWrapper.DELAY_MODE_LABELS));
			labels.set(0, ((int)(1000000/minDelay)) + " samples/s");
			mode.putStringArrayList("options", labels);
			list.add(mode);

			list.addAll(super.getOptions());

			return list;
		} else {
			return super.getOptions();
		}
	}

}
