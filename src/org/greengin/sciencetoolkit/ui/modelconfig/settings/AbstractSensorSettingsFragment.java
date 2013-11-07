package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.AbstractSettingsFragment;

import android.app.Activity;

public abstract class AbstractSensorSettingsFragment extends AbstractSettingsFragment {

	SensorWrapper sensor;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sensor = SensorWrapperManager.getInstance().getSensor(getArguments().getString(SettingsFragmentManager.ARG_SENSOR));
	}
	
	
	@Override
	protected boolean settingsEnabledWhileLoggingData() {
		Model profile = ProfileManager.getInstance().getActiveProfile();
		if (profile != null) {
			Model sensors = profile.getModel("sensors");
			if (sensors != null) {
				return sensors.getModel(sensor.getName()) == null;
			}
		}
		
		return true;
	}
}
