package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.model.ModelDefaults;

import android.view.View;

public class LiveViewSettingsFragment extends AbstractSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		addOptionNumber("update_rate", "Update value ", "Times per second that the value is updated.", true, false, ModelDefaults.LIVEVIEW_UPDATE_RATE, ModelDefaults.LIVEVIEW_UPDATE_RATE_MIN, ModelDefaults.LIVEVIEW_UPDATE_RATE_MAX);
	}

}
