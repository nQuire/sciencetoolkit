package org.greengin.sciencetoolkit;

import java.util.HashMap;

import org.greengin.sciencetoolkit.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.sensors.SettingsListener;

public class SettingsManager {

	private static SettingsManager instance;

	public static SettingsManager getInstance() {
		return instance;
	}

	public static void init() {
		SettingsManager.instance = new SettingsManager();
	}

	HashMap<String, SettingsListener> settings;

	private SettingsManager() {
		this.settings = new HashMap<String, SettingsListener>();
		for (SensorWrapper sensor : SensorWrapperManager.getInstance().getSensors().values()) {
			this.settings.put(sensor.getName(), sensor);
			this.settings.put("monitor:" + sensor.getName(), sensor.getMonitor());
		}
	}

	
	public SettingsListener getSettings(String key) {
		return this.settings.get(key);
	}
	
	public void registerSettings(String key, SettingsListener listener) {
		this.settings.put(key, listener);
	}
	
	public boolean exists(String key) {
		return this.settings.containsKey(key);
	}

}
