package org.greengin.sciencetoolkit.logic.sensors.device;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.NotificationListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DeviceSensorWrapper extends SensorWrapper implements SensorEventListener, NotificationListener {

	public static final int[] DELAY_MODE_VALUES = new int[] { SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_NORMAL };

	// public static final String[] DELAY_MODE_LABELS = new String[] { null,
	// "50 samples/s", "16 samples/s", "5 samples/s" };

	SensorManager sensorManager;
	Sensor sensor;
	int currentDelay;
	Model settings;
	int valueCount;
	boolean isRegistered;

	public DeviceSensorWrapper(Sensor sensor, SensorManager sensorManager) {
		super();

		this.sensor = sensor;
		this.sensorManager = sensorManager;
		this.valueCount = getValueCount();
		this.currentDelay = -1;
		this.isRegistered = false;

		String settingsId = "sensor:" + this.sensor.getName();
		this.settings = SettingsManager.getInstance().get(settingsId);
		this.updateDelay();
		SettingsManager.getInstance().registerDirectListener(settingsId, this);
	}

	@Override
	protected void onInputAdded(boolean first, int inputCount) {
		if (first) {
			register();
		}
	}

	@Override
	protected void onInputRemoved(boolean empty, int inputCount) {
		if (empty) {
			unregister();
		}
	}

	private void register() {
		if (!isRegistered) {
			sensorManager.registerListener(this, this.sensor, DELAY_MODE_VALUES[this.currentDelay]);
			isRegistered = true;
		}
	}

	private void unregister() {
		sensorManager.unregisterListener(this);
		isRegistered = false;
	}

	private void updateDelay() {
		int newDelay = settings.getInt("delay", ModelDefaults.SENSOR_DELAY);
		if (newDelay != this.currentDelay) {
			this.currentDelay = newDelay;
			if (isRegistered) {
				unregister();
			}
			register();
		}
	}

	@Override
	public String getName() {
		return this.sensor.getName();
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
		this.fireInput(event.values, this.valueCount);
	}

	@Override
	public float getMaxRange() {
		return this.sensor.getMaximumRange();
	}

	@Override
	public int getType() {
		return this.sensor.getType();
	}

	@Override
	public void notificationReveiced(String msg) {
		this.updateDelay();
	}
}
