package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.AbstractSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.AppSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.LivePlotSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.LiveViewSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.SensorListSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.SensorSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.dataview.ProfileDataRangeSettingsFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.dataview.ProfileDataVisualizationSettingsFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class SettingsFragmentManager {

	public static void insert(FragmentManager manager, int container, String settingsId) {
		String[] parts = settingsId.split(":");
		insert(manager, container, parts[0], settingsId);
	}

	public static void insert(FragmentManager manager, int container, String fragmentType, String settingsId) {
		AbstractSettingsFragment f = get(fragmentType, settingsId);
		if (f != null) {
			manager.beginTransaction().replace(container, f).commit();
		}
	}

	public static AbstractSettingsFragment get(String fragmentType, String settingsId) {
		AbstractSettingsFragment fragment = null;

		int sensorArg = 0;
		int profileArg = 0;

		if (fragmentType != null) {
			if (fragmentType.equals("app")) {
				fragment = new AppSettingsFragment();

			} else if (fragmentType.equals("sensor_list")) {
				fragment = new SensorListSettingsFragment();

			} else if (fragmentType.equals("sensor")) {
				sensorArg = 1;
				fragment = new SensorSettingsFragment();

			} else if (fragmentType.equals("liveview")) {
				fragment = new LiveViewSettingsFragment();

			} else if (fragmentType.equals("liveplot")) {
				sensorArg = 1;
				fragment = new LivePlotSettingsFragment();

			} else if (fragmentType.equals("profile_data_visualization")) {
				profileArg = 1;
				fragment = new ProfileDataVisualizationSettingsFragment();

			} else if (fragmentType.equals("profile_data_range")) {
				profileArg = 1;
				fragment = new ProfileDataRangeSettingsFragment();

			} else if (fragmentType.equals("profile_data_variables")) {
				profileArg = 1;

			}

			if (fragment != null) {
				Bundle args = new Bundle();
				args.putString(Arguments.ARG_SETTINGS, settingsId);

				if (sensorArg > 0) {
					args.putString(Arguments.ARG_SENSOR, getKeyParam(settingsId, sensorArg));
				}

				if (profileArg > 0) {
					args.putString(Arguments.ARG_PROFILE, getKeyParam(settingsId, profileArg));
				}

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
