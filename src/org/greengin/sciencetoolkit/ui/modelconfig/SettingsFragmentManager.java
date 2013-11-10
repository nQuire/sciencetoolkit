package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.ui.modelconfig.settings.AbstractSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.AppSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.LivePlotSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.LiveViewSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.SensorListSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.SensorSettingsFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class SettingsFragmentManager {
	public static final String ARG_SETTINGS = "settings";
	public static final String ARG_SENSOR = "sensor";
	
	public static void insert(FragmentManager manager, int container, String settingsId) {
		manager.beginTransaction().replace(container, get(settingsId)).commit();
	}
	
	public static AbstractSettingsFragment get(String settingsId) {
		AbstractSettingsFragment fragment = null;
		Bundle args = new Bundle();
		args.putString(ARG_SETTINGS, settingsId);

		if (settingsId != null) {
			if (settingsId.equals("app")) {
				fragment = new AppSettingsFragment();
			} else if (settingsId.equals("sensor_list")) {
				fragment = new SensorListSettingsFragment();
			} else if (settingsId.startsWith("sensor:")) {
				String sensorId = getKeyParam(settingsId, 1);
				args.putString(ARG_SENSOR, sensorId);
				fragment = new SensorSettingsFragment();
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
