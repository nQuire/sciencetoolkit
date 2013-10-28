package org.greengin.sciencetoolkit.ui;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.hardware.Sensor;

public class SensorUIData {

	@SuppressWarnings("deprecation")
	public static int getSensorToggleResource(int type) {
		switch (type ) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_LINEAR_ACCELERATION:
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return R.drawable.acceleration_selector;		
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			return R.drawable.temperature_selector;
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_ROTATION_VECTOR:
			return R.drawable.rotate_selector;
		case Sensor.TYPE_GRAVITY:
			return R.drawable.gravity_selector;
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return R.drawable.gyroscope_selector;
		case Sensor.TYPE_LIGHT:
			return R.drawable.light_selector;
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return R.drawable.magnet_selector;
		case Sensor.TYPE_ORIENTATION:
			return R.drawable.compass_selector;
		case Sensor.TYPE_PRESSURE:
			return R.drawable.pressure_selector;
		case Sensor.TYPE_PROXIMITY:
			return R.drawable.proximity_selector;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return R.drawable.humidity_selector;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return R.drawable.sound_selector;
			default:
				return 0;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String[] getValueLabels(int type) {
		switch (type) {
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
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return new String[] {"Sound level", "Max. freq."};
		default:
			return new String[] { "values" };
		}
	}

	@SuppressWarnings("deprecation")
	public static String[] getValueUnits(int type) {
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_LINEAR_ACCELERATION:
		case Sensor.TYPE_GRAVITY:
			return new String[] { "m/s²", "m/s²", "m/s²" };
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			return new String[] { "°C" };
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return new String[] { "rad/s", "rad/s", "rad/s" };
		case Sensor.TYPE_LIGHT:
			return  new String[] { "lx" };
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return new String[] { "μT", "μT", "μT" };
		case Sensor.TYPE_ORIENTATION:
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_ROTATION_VECTOR:
			return new String[] { "°", "°", "°" };
		case Sensor.TYPE_PRESSURE:
			return new String[] { "hPa" };
		case Sensor.TYPE_PROXIMITY:
			return new String[] { "cm" };
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return new String[] { "%" };
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return new String[] {"dB", "Hz"};
		default:
			return new String[] { "?", "?", "?" };
		}
	}
	
	public static String getValueLabelStr(int type) {
		String[] labels = getValueLabels(type);
		StringBuffer labelstr = new StringBuffer();
		for (int i = 0; i < labels.length; i++) {
			if (i > 0) {
				labelstr.append("\n");
			}
			labelstr.append(labels[i]).append(":");
		}
		
		return labelstr.toString();
	}

}
