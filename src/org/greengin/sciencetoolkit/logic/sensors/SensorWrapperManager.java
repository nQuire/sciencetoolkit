package org.greengin.sciencetoolkit.logic.sensors;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.logic.sensors.device.DeviceSensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.location.LocationGpsSensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.sound.SoundSensorWrapper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorWrapperManager {
	public static final int CUSTOM_SENSOR_TYPE_SOUND = 1001;
	public static final int CUSTOM_SENSOR_TYPE_GPS_LOCATION = 1002;

	private static SensorWrapperManager instance;

	public static SensorWrapperManager get() {
		return instance;
	}

	public static void init(Context applicationContext) {
		SensorWrapperManager.instance = new SensorWrapperManager();
		SensorWrapperManager.instance.loadSensors(applicationContext);
	}

	HashMap<String, SensorWrapper> sensors;
	Vector<String> sensorIds;

	private SensorWrapperManager() {
		this.sensors = new HashMap<String, SensorWrapper>();
		this.sensorIds = new Vector<String>();
	}
	
	private void loadSensors(Context applicationContext) {
		SensorManager sensorManager = (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);


		for (Sensor sensor : deviceSensors) {
			addSensor(new DeviceSensorWrapper(sensor, sensorManager));
		}

		if (SoundSensorWrapper.isAvailable(applicationContext)) {
			addSensor(new SoundSensorWrapper(applicationContext));
		}
		
		if (LocationGpsSensorWrapper.isAvailable(applicationContext)) {
			addSensor(new LocationGpsSensorWrapper(applicationContext));
		}
	}

	private void addSensor(SensorWrapper sensor) {
		this.sensors.put(sensor.getId(), sensor);
		this.sensorIds.add(sensor.getId());
	}

	public HashMap<String, SensorWrapper> getSensors() {
		return this.sensors;
	}

	public Vector<String> getSensorsIds() {
		return this.sensorIds;
	}

	public SensorWrapper getSensor(Object key) {
		return this.sensors.get(key);
	}
	
	public synchronized String getId(int type) {
		String typeStr = SensorWrapperManager.type(type);
		String format = typeStr + ":%d";
		
		for (int i = 0;; i++) {
			String id = String.format(format, i);
			if (!sensors.containsKey(id)) {
				sensors.put(id, null);
				Log.d("stk sensors", "new id: " + id);
				return id;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String type(int type) {
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			return "acc";
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return "lacc";
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return "sigmot";
		case Sensor.TYPE_STEP_COUNTER:
			return "stpcnt";
		case Sensor.TYPE_STEP_DETECTOR:
			return "stpdet";
		case Sensor.TYPE_TEMPERATURE:
			return "tmp";
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return "ambtmp";
		case Sensor.TYPE_ORIENTATION:
			return "orn";
		case Sensor.TYPE_ROTATION_VECTOR:
			return "rot";
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return "gamerot";
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return "georot";
		case Sensor.TYPE_GRAVITY:
			return "gra";
		case Sensor.TYPE_GYROSCOPE:
			return "gyr";
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return "gyrunc";
		case Sensor.TYPE_LIGHT:
			return "lig";
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "mag";
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return "magunc";
		case Sensor.TYPE_PRESSURE:
			return "pre";
		case Sensor.TYPE_PROXIMITY:
			return "pro";
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return "relhum";
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return "snd";
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION:
			return "gps";
		default:
			return "uknown";
		}
	}

}
