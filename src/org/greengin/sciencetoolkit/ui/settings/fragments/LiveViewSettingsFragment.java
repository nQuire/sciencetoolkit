package org.greengin.sciencetoolkit.ui.settings.fragments;

import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.ui.settings.SettingsFragment;

import android.view.View;

public class LiveViewSettingsFragment extends SettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		addOptionNumber("period", "Update period", "Time period between value updates (ms).", false, false, ModelDefaults.LIVEVIEW_PERIOD, 10, null);
	}

}
