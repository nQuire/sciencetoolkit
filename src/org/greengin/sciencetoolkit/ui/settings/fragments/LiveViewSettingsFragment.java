package org.greengin.sciencetoolkit.ui.settings.fragments;

import org.greengin.sciencetoolkit.ui.settings.SettingsFragment;

import android.view.View;

public class LiveViewSettingsFragment extends SettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		addOptionNumber("period", "Update period", "Time period between value updates (ms).", false, false, 100, 10, null);
	}

}
