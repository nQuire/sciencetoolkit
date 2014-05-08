package org.greengin.sciencetoolkit.common.ui.base.modelconfig;



import org.greengin.sciencetoolkit.common.ui.base.Arguments;
import org.greengin.sciencetoolkit.common.ui.base.modelconfig.settings.AbstractSettingsFragment;
import org.greengin.sciencetoolkit.common.ui.base.modelconfig.settings.AppSettingsFragment;

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

		if (fragmentType != null) {
			if (fragmentType.equals("app")) {
				fragment = new AppSettingsFragment();

			} 
			
			if (fragment != null) {
				Bundle args = new Bundle();
				args.putString(Arguments.ARG_SETTINGS, settingsId);

				fragment.setArguments(args);
			}
		}

		return fragment;
	}
}
