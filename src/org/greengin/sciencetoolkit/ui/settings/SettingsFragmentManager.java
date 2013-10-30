package org.greengin.sciencetoolkit.ui.settings;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.settings.fragments.DeviceSensorSettingsFragment;
import org.greengin.sciencetoolkit.ui.settings.fragments.LivePlotSettingsFragment;
import org.greengin.sciencetoolkit.ui.settings.fragments.LiveViewSettingsFragment;
import org.greengin.sciencetoolkit.ui.settings.fragments.SensorListSettingsFragment;
import org.greengin.sciencetoolkit.ui.settings.fragments.SoundSensorSettingsFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class SettingsFragmentManager {
	public static final String ARG_SETTINGS = "settings";
	public static final String ARG_SENSOR = "sensor";
	
	public static void insert(FragmentManager manager, int container, String settingsId) {
		manager.beginTransaction().replace(container, get(settingsId)).commit();
	}
	
	public static SettingsFragment get(String settingsId) {
		SettingsFragment fragment = null;
		Bundle args = new Bundle();
		args.putString(ARG_SETTINGS, settingsId);

		if (settingsId != null) {
			if (settingsId.equals("sensor_list")) {
				fragment = new SensorListSettingsFragment();
			} else if (settingsId.startsWith("sensor:")) {
				String sensorId = getKeyParam(settingsId, 1);
				args.putString(ARG_SENSOR, sensorId);
				SensorWrapper sensor = SensorWrapperManager.getInstance().getSensor(sensorId);
				if (sensor != null) {
					fragment = sensor.getType() == SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND ? new SoundSensorSettingsFragment() : new DeviceSensorSettingsFragment();
				}
			} else if (settingsId.startsWith("liveview:")) {
				fragment = new LiveViewSettingsFragment();
			} else if (settingsId.startsWith("liveplot:")) {
				String sensorId = getKeyParam(settingsId, 1);
				args.putString(ARG_SENSOR, sensorId);
				fragment = new LivePlotSettingsFragment();
			}
			
			if (fragment != null) {
				fragment.setArguments(args);
			}
		}

		return fragment;
	}
	
	private static String getKeyParam(String key, int index) {
		String[] parts = key.split(":");
		return parts.length > index ? parts[index] : null;
	}
}
