package org.greengin.sciencetoolkit.ui.base.modelconfig.settings;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.SenseItArguments;
import org.greengin.sciencetoolkit.ui.base.modelconfig.DataLoggerDependentModelFragment;

import android.app.Activity;

public abstract class AbstractSensorSettingsFragment extends DataLoggerDependentModelFragment {

	SensorWrapper sensor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sensor = SensorWrapperManager.get().getSensor(getArguments().getString(SenseItArguments.ARG_SENSOR));
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
