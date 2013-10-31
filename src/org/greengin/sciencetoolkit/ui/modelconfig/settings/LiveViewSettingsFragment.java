package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.model.ModelDefaults;

import android.view.View;

public class LiveViewSettingsFragment extends AbstractSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		addOptionNumber("period", "Update period", "Time period between value updates (ms).", false, false, ModelDefaults.LIVEVIEW_PERIOD, 10, null);
	}

}
