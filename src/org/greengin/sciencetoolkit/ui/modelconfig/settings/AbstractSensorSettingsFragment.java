package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.Arguments;
import org.greengin.sciencetoolkit.ui.modelconfig.settings.AbstractSettingsFragment;

import android.app.Activity;

public abstract class AbstractSensorSettingsFragment extends AbstractSettingsFragment {

	SensorWrapper sensor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sensor = SensorWrapperManager.get().getSensor(getArguments().getString(Arguments.ARG_SENSOR));
		return;
	}

	@Override
	protected boolean settingsEnabledWhileLoggingData() {
		Model profile = ProfileManager.get().getActiveProfile();
		if (profile != null) {
			Model sensors = profile.getModel("sensors");
			if (sensors != null) {
				return sensors.getModel(sensor.getId()) == null;
			}
		}

		return true;
	}
}
