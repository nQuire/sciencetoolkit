package org.greengin.sciencetoolkit.ui.modelconfig.profile;

import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;

import android.view.View;


public class ProfileSensorPeriodModelFragment extends AbstractProfileConfigFragment {

	@Override
	protected Model fetchProfileConfigModel() {
		String profileSensorId = arguments[2];
		return profile.getModel("sensors").getModel(profileSensorId);
	}

	@Override
	protected void createConfigOptions(View view) {
		addOptionNumber("period", "Period", "The time (ms.) between samples logged for this sensor.", false, false, ModelDefaults.DATA_LOGGING_PERIOD, 10, null);
	}

	
}
