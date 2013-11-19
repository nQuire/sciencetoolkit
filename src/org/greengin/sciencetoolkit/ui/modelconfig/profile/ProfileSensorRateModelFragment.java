package org.greengin.sciencetoolkit.ui.modelconfig.profile;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;

import android.view.View;

public class ProfileSensorRateModelFragment extends AbstractProfileConfigFragment {

	@Override
	protected Model fetchProfileConfigModel() {
		String profileSensorId = arguments[2];
		return profile.getModel("sensors").getModel(profileSensorId);
	}

	@Override
	protected void createConfigOptions(View view) {
		String[] units = new String[] { getResources().getString(R.string.samples_second), getResources().getString(R.string.samples_min), getResources().getString(R.string.samples_hour) };
		double[] multipliers = new double[] { 1d, 1d / 60d, 1d / 3600d };

		addOptionNumber("sample_rate", "Log", "The number of samples per second logged for this sensor.", true, false, ModelDefaults.DATA_LOGGING_RATE, null, ModelDefaults.DATA_LOGGING_RATE_MAX, units, multipliers);
	}

}
