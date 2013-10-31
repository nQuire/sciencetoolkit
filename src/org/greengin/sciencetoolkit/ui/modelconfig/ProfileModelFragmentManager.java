package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.ui.modelconfig.profile.ProfileSensorConfigModelFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.profile.ProfileSensorPeriodModelFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class ProfileModelFragmentManager {
	public static final String ARGS = "ARGS";
	
	public static void insert(FragmentManager manager, int container, String[] args) {
		manager.beginTransaction().replace(container, get(args)).commit();
	}
	
	public static ModelFragment get(String[] args) {
		ModelFragment fragment = null;
		Bundle bundle = new Bundle();
		bundle.putStringArray(ARGS, args);

		if (args != null && args.length >= 2) {
			String type = args[0];

			if ("period".equals(type)) {
				fragment = new ProfileSensorPeriodModelFragment();
			} else if ("sensor".equals(type)) {
				fragment = new ProfileSensorConfigModelFragment();
			} 
			
			if (fragment != null) {
				fragment.setArguments(bundle);
			}
		}

		return fragment;
	}
}
