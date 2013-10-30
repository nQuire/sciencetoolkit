package org.greengin.sciencetoolkit.ui.settings.fragments;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.settings.SettingsFragment;
import org.greengin.sciencetoolkit.ui.settings.SettingsFragmentManager;

import android.app.Activity;

public abstract class AbstractSensorSettingsFragment extends SettingsFragment {

	SensorWrapper sensor;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sensor = SensorWrapperManager.getInstance().getSensor(getArguments().getString(SettingsFragmentManager.ARG_SENSOR));
	}
	
	protected void addOverrideWarning() {
		addText("Please note that sensor settings will be modified if a data logging profile that specifies a different configuration is activated.");
	}
	
	protected void addEmptyWarning() {
		addText("This sensor does not have any configuration options.");
	}
}
