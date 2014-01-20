package org.greengin.sciencetoolkit.ui.base.modelconfig;

import org.greengin.sciencetoolkit.ui.base.Arguments;
import org.greengin.sciencetoolkit.ui.base.modelconfig.settings.AbstractSettingsFragment;
import org.greengin.sciencetoolkit.ui.base.modelconfig.settings.AppSettingsFragment;
import org.greengin.sciencetoolkit.ui.base.modelconfig.settings.LivePlotSettingsFragment;
import org.greengin.sciencetoolkit.ui.base.modelconfig.settings.SensorListSettingsFragment;
import org.greengin.sciencetoolkit.ui.base.modelconfig.settings.SensorSettingsFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class SettingsFragmentManager {

	public static void insert(FragmentManager manager, int container, String settingsId) {
		String[] parts = settingsId.split(":", 2);
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

		boolean sensorArg = false;
		boolean profileArg = false;

		if (fragmentType != null) {
			if (fragmentType.equals("app")) {
				fragment = new AppSettingsFragment();

			} else if (fragmentType.equals("sensor_list")) {
				fragment = new SensorListSettingsFragment();

			} else if (fragmentType.equals("sensor")) {
				sensorArg = true;
				fragment = new SensorSettingsFragment();

			} else if (fragmentType.equals("liveplot")) {
				sensorArg = true;
				fragment = new LivePlotSettingsFragment();

			} else if (fragmentType.equals("profile_data_variables")) {
				profileArg = true;
			}

			if (fragment != null) {
				Bundle args = new Bundle();
				args.putString(Arguments.ARG_SETTINGS, settingsId);

				if (sensorArg) {
					args.putString(Arguments.ARG_SENSOR, getKeyParam(settingsId));
				} else if (profileArg) {
					args.putString(Arguments.ARG_PROFILE, getKeyParam(settingsId));
				}

				fragment.setArguments(args);
			}
		}

		return fragment;
	}

	private static String getKeyParam(String key) {
		String[] parts = key.split(":", 2);
		return parts.length > 1 ? parts[1] : null;
	}
}
