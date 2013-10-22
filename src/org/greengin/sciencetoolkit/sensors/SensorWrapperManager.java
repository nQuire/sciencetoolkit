package org.greengin.sciencetoolkit.sensors;


import java.util.HashMap;
import java.util.List;

import org.greengin.sciencetoolkit.sensors.sound.SoundSensorWrapper;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;



public class SensorWrapperManager {
	public static final String accelerometer = "accelerometer";
	public static final String thermometer = "thermometer";
	public static final String orientation = "orientation";
	public static final String compass = "compass";
	public static final String timer = "timer";
	public static final String sound_intensity= "sound_intensity";
	
	public static final int CUSTOM_SENSOR_TYPE_SOUND = 1001;
	
	private static SensorWrapperManager instance; 
	
    public static SensorWrapperManager getInstance() {
        return instance;
    }
    
    public static void init(Context context) {
    	SensorWrapperManager.instance = new SensorWrapperManager(context);
    }
    
    HashMap<String, SensorWrapper> sensors;
	
	private SensorWrapperManager(Context context) {
		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		
		this.sensors = new HashMap<String, SensorWrapper>();
		
		for(Sensor sensor : deviceSensors) {
			this.sensors.put(sensor.getName(), new RawSensorWrapper(context, sensor, sensorManager));
		}
		
		this.sensors.put("sound", new SoundSensorWrapper(context));
	}	
	
	public HashMap<String, SensorWrapper> getSensors() {
		return this.sensors;
	}
	
	public SensorWrapper getSensor(Object key) {
		return this.sensors.get(key);
	}

	 
}
