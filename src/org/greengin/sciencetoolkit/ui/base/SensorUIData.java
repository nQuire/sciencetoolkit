package org.greengin.sciencetoolkit.ui.base;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.hardware.Sensor;

public class SensorUIData {

	private static final String[][] LABELS = new String[][] { 
		new String[] { "x", "y", "z" }, 
		new String[] { "Temperature" }, 
		new String[] { "Light level" }, 
		new String[] { "Pressure" }, 
		new String[] { "Proximity" }, 
		new String[] { "Rel. humidity" }, 
		new String[] { "Sound level", "Max. freq." }, 
		new String[] { "Latitude", "Longitude" /** ,* "Altitude"* ,* "Precision"*/}, 
		new String[] { "Strength"}, 
		new String[] { "", "", "", "", "", "", "", "" } };

	/*
	 * 
	 * x int TYPE_ACCELEROMETER A constant describing an accelerometer sensor
	 * type.
	 * 
	 * int TYPE_ALL A constant describing all sensor types.
	 * 
	 * x int TYPE_AMBIENT_TEMPERATURE A constant describing an ambient
	 * temperature sensor type.
	 * 
	 * x int TYPE_GAME_ROTATION_VECTOR A constant describing an uncalibrated
	 * rotation vector sensor type.
	 * 
	 * z int TYPE_GEOMAGNETIC_ROTATION_VECTOR A constant describing the
	 * geo-magnetic rotation vector.
	 * 
	 * x int TYPE_GRAVITY A constant describing a gravity sensor type.
	 * 
	 * x int TYPE_GYROSCOPE A constant describing a gyroscope sensor type.
	 * 
	 * x int TYPE_GYROSCOPE_UNCALIBRATED A constant describing an uncalibrated
	 * gyroscope sensor type.
	 * 
	 * x int TYPE_LIGHT A constant describing a light sensor type.
	 * 
	 * x int TYPE_LINEAR_ACCELERATION A constant describing a linear
	 * acceleration sensor type.
	 * 
	 * x int TYPE_MAGNETIC_FIELD A constant describing a magnetic field sensor
	 * type.
	 * 
	 * x int TYPE_MAGNETIC_FIELD_UNCALIBRATED A constant describing an
	 * uncalibrated magnetic field sensor type.
	 * 
	 * x int TYPE_ORIENTATION This constant was deprecated in API level 8. use
	 * SensorManager.getOrientation() instead.
	 * 
	 * x int TYPE_PRESSURE A constant describing a pressure sensor type.
	 * 
	 * x int TYPE_PROXIMITY A constant describing a proximity sensor type.
	 * 
	 * x int TYPE_RELATIVE_HUMIDITY A constant describing a relative humidity
	 * sensor type.
	 * 
	 * x int TYPE_ROTATION_VECTOR A constant describing a rotation vector sensor
	 * type.
	 * 
	 * int TYPE_SIGNIFICANT_MOTION A constant describing a significant motion
	 * trigger sensor.
	 * 
	 * int TYPE_STEP_COUNTER A constant describing a step counter sensor.
	 * 
	 * int TYPE_STEP_DETECTOR A constant describing a step detector sensor.
	 * 
	 * int TYPE_TEMPERATURE This constant was deprecated in API level 14. use
	 * Sensor.TYPE_AMBIENT_TEMPERATURE instead.
	 */

	@SuppressWarnings("deprecation")
	public static int getSensorResource(int type) {
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			return R.drawable.sensor_acc;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return R.drawable.sensor_lacc;
		case Sensor.TYPE_GRAVITY:
			return R.drawable.sensor_gra;
		case Sensor.TYPE_PROXIMITY:
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return R.drawable.sensor_sigmot;
		case Sensor.TYPE_STEP_COUNTER:
			return R.drawable.sensor_ste;
		case Sensor.TYPE_STEP_DETECTOR:
			return R.drawable.sensor_sted;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			return R.drawable.sensor_tmp;
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return R.drawable.sensor_rot_gam;
		case Sensor.TYPE_ROTATION_VECTOR:
			return R.drawable.sensor_rot;
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return R.drawable.sensor_rot_geo;
		case Sensor.TYPE_GYROSCOPE:
			return R.drawable.sensor_gyr;
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return R.drawable.sensor_gyr_u;
		case Sensor.TYPE_LIGHT:
			return R.drawable.sensor_lgt;
		case Sensor.TYPE_MAGNETIC_FIELD:
			return R.drawable.sensor_mag;
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return R.drawable.sensor_mag_u;
		case Sensor.TYPE_ORIENTATION:
			return R.drawable.sensor_ori;
		case Sensor.TYPE_PRESSURE:
			return R.drawable.sensor_pre;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return R.drawable.sensor_hum;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return R.drawable.sensor_snd;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION:
			return R.drawable.sensor_gps;
		default:
			return R.drawable.sensor_ste;
		}
	}

	@SuppressWarnings("deprecation")
	public static int getWeight(int type) {
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			return 0;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return 1;
		case Sensor.TYPE_GRAVITY:
			return 2;
		case Sensor.TYPE_GYROSCOPE:
			return 3;
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return 4;

		case Sensor.TYPE_ORIENTATION:
			return 5;
		case Sensor.TYPE_ROTATION_VECTOR:
			return 6;
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return 7;
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return 8;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION:
			return 9;
		case Sensor.TYPE_PROXIMITY:
			return 10;

		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			return 11;
		case Sensor.TYPE_LIGHT:
			return 12;
		case Sensor.TYPE_PRESSURE:
			return 13;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return 14;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return 15;
		case Sensor.TYPE_MAGNETIC_FIELD:
			return 16;
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return 17;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM:
			return 18;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_CDMA:
			return 19;

		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return 20;
		case Sensor.TYPE_STEP_COUNTER:
			return 21;
		case Sensor.TYPE_STEP_DETECTOR:
			return 22;
		default:
			return 23;
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
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
		case Sensor.TYPE_GRAVITY:
			return LABELS[0];
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			return LABELS[1];
		case Sensor.TYPE_LIGHT:
			return LABELS[2];
		case Sensor.TYPE_PRESSURE:
			return LABELS[3];
		case Sensor.TYPE_PROXIMITY:
			return LABELS[4];
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return LABELS[5];
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return LABELS[6];
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION:
			return LABELS[7];
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM:
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_CDMA:
			return LABELS[8];
		default:
			return LABELS[8];
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
			return new String[] { "lux" };
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return new String[] { "μT", "μT", "μT" };
		case Sensor.TYPE_ORIENTATION:
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_ROTATION_VECTOR:
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return new String[] { "°", "°", "°" };
		case Sensor.TYPE_PRESSURE:
			return new String[] { "mbar" };
		case Sensor.TYPE_PROXIMITY:
			return new String[] { "cm" };
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return new String[] { "%" };
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return new String[] { "dB", "Hz" };
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION:
			return new String[] { "°", "°", "m", "m" };
		default:
			return new String[] { "", "", "", "", "", "", "", "" };
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
