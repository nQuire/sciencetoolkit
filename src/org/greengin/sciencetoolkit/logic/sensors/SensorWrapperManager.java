package org.greengin.sciencetoolkit.logic.sensors;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.logic.sensors.device.DeviceSensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.location.LocationGpsSensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.signal.CdmaSensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.signal.GsmSensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.sound.SoundSensorWrapper;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

public class SensorWrapperManager {
	public static final int CUSTOM_SENSOR_TYPE_SOUND = 1001;
	public static final int CUSTOM_SENSOR_TYPE_GPS_LOCATION = 1002;
	
	public static final int CUSTOM_SENSOR_TYPE_GSM = 1003;
	public static final int CUSTOM_SENSOR_TYPE_CDMA = 1004;

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

	
	@SuppressWarnings("deprecation")
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

		addSensor(new GsmSensorWrapper(applicationContext));
		addSensor(new CdmaSensorWrapper(applicationContext));

		if (!SettingsManager.get().get("sensor_initial_selection").getBool("filtered")) {
			
			SettingsManager.get().get("sensor_initial_selection").setBool("filtered", true);
			SettingsManager.get().get("sensor_initial_selection").setBool("ack", false);
			Vector<Integer> types = new Vector<Integer>();
			for (Entry<String, SensorWrapper> entry : sensors.entrySet()) {
				int type = entry.getValue().getType();
				boolean show = false;
				
				switch (type) {
				case Sensor.TYPE_ACCELEROMETER:
				case Sensor.TYPE_LINEAR_ACCELERATION:
				case Sensor.TYPE_AMBIENT_TEMPERATURE:
				case Sensor.TYPE_ORIENTATION:
				case Sensor.TYPE_GYROSCOPE:
				case Sensor.TYPE_LIGHT:
				case Sensor.TYPE_PRESSURE:
				case Sensor.TYPE_RELATIVE_HUMIDITY:
				case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
				case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION:
					if (!types.contains(type)) {
						show = true;
						types.add(type);
					}
					break;
				}
				
				SettingsManager.get().get("sensor_list").setBool(entry.getKey(), show);
			}	
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

	public List<SensorWrapper> getSensorsOfType(String type) {
		return getSensorsOfType(intType(type));
	}

	public List<SensorWrapper> getSensorsOfType(int type) {
		Vector<SensorWrapper> list = new Vector<SensorWrapper>();
		for (Entry<String, SensorWrapper> entry : sensors.entrySet()) {
			if (entry.getValue().getType() == type) {
				list.add(entry.getValue());
			}
		}
		return list;
	}

	public Vector<SensorWrapper> getShownSensors() {
		Model settings = SettingsManager.get().get("sensor_list");
		Vector<SensorWrapper> shownSensors = new Vector<SensorWrapper>();
		for (String sensorId : SensorWrapperManager.get().getSensorsIds()) {
			if (settings.getBool(sensorId, true)) {
				shownSensors.add(getSensor(sensorId));
			}
		}
		return shownSensors;
	}

	public Vector<String> getShownSensorIds() {
		Model settings = SettingsManager.get().get("sensor_list");
		Vector<String> sensorIds = new Vector<String>();
		for (String sensorId : SensorWrapperManager.get().getSensorsIds()) {
			if (settings.getBool(sensorId, true)) {
				sensorIds.add(sensorId);
			}
		}
		return sensorIds;
	}

	public synchronized String getId(int type) {
		String typeStr = SensorWrapperManager.type(type);
		String format = typeStr + ":%d";

		for (int i = 0;; i++) {
			String id = String.format(format, i);
			if (!sensors.containsKey(id)) {
				sensors.put(id, null);
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
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM:
			return "gsm";
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_CDMA:
			return "cdma";
		default:
			return "uknown";
		}
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public static int intType(String type) {
		if ("snd".equals(type)) {
			return SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND;
		} else if ("gps".equals(type)) {
			return SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION;
		} else if ("gsm".equals(type)) {
			return SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM;
		} else if ("cdma".equals(type)) {
			return SensorWrapperManager.CUSTOM_SENSOR_TYPE_CDMA;
		} else if ("pro".equals(type)) {
			return Sensor.TYPE_PROXIMITY;
		} else if ("lig".equals(type)) {
			return Sensor.TYPE_LIGHT;
		} else if ("mag".equals(type)) {
			return Sensor.TYPE_MAGNETIC_FIELD;
		} else if ("gyr".equals(type)) {
			return Sensor.TYPE_GYROSCOPE;
		} else if ("orn".equals(type)) {
			return Sensor.TYPE_ORIENTATION;
		} else if ("acc".equals(type)) {
			return Sensor.TYPE_ACCELEROMETER;
		} else if ("pre".equals(type)) {
			return Sensor.TYPE_PRESSURE;
		} else if ("tmp".equals(type)) {
			return Sensor.TYPE_TEMPERATURE;
		}

		if (Build.VERSION.SDK_INT >= 9) {
			if ("gra".equals(type)) {
				return Sensor.TYPE_GRAVITY;
			} else if ("rot".equals(type)) {
				return Sensor.TYPE_ROTATION_VECTOR;
			} else if ("lacc".equals(type)) {
				return Sensor.TYPE_LINEAR_ACCELERATION;
			}
		}
		
		if (Build.VERSION.SDK_INT >= 14) {
			if ("relhum".equals(type)) {
				return Sensor.TYPE_RELATIVE_HUMIDITY;
			} else if ("ambtmp".equals(type)) {
				return Sensor.TYPE_AMBIENT_TEMPERATURE;
			}
		}

		if (Build.VERSION.SDK_INT >= 18) {
			if ("magunc".equals(type)) {
				return Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;

			} else if ("gyrunc".equals(type)) {
				return Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
			} else if ("gamerot".equals(type)) {
				return Sensor.TYPE_GAME_ROTATION_VECTOR;
			} else if ("sigmot".equals(type)) {
				return Sensor.TYPE_SIGNIFICANT_MOTION;
			}
		}

		if (Build.VERSION.SDK_INT >= 19) {
			if ("georot".equals(type)) {
				return Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
			} else if ("stpcnt".equals(type)) {
				return Sensor.TYPE_STEP_COUNTER;
			} else if ("stpdet".equals(type)) {
				return Sensor.TYPE_STEP_DETECTOR;
			}
		}

		return -1;
	}

}
