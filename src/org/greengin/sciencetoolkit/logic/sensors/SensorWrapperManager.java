package org.greengin.sciencetoolkit.logic.sensors;


import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.logic.sensors.device.DeviceSensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.sound.SoundSensorWrapper;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;



public class SensorWrapperManager {
	public static final int CUSTOM_SENSOR_TYPE_SOUND = 1001;
	
	private static SensorWrapperManager instance; 
	
    public static SensorWrapperManager getInstance() {
        return instance;
    }
    
    public static void init(Context applicationContext) {
    	SensorWrapperManager.instance = new SensorWrapperManager(applicationContext);
    }
    
    HashMap<String, SensorWrapper> sensors;
    Vector<String> sensorIds;
	
	private SensorWrapperManager(Context applicationContext) {
		SensorManager sensorManager = (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		
		this.sensors = new HashMap<String, SensorWrapper>();
		this.sensorIds = new Vector<String>();
		
		for(Sensor sensor : deviceSensors) {
			this.sensors.put(sensor.getName(), new DeviceSensorWrapper(sensor, sensorManager));
			this.sensorIds.add(sensor.getName());
		}
		
		this.sensors.put("Sound", new SoundSensorWrapper(applicationContext));
		this.sensorIds.add("Sound");
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

	 
}
