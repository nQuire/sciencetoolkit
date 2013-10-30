package org.greengin.sciencetoolkit.ui.settings.fragments;

import android.view.View;

public class SoundSensorSettingsFragment extends AbstractSensorSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {		
		addOverrideWarning();
		addOptionNumber("record_period", "Record period", "The duration of the recording period (ms).", false, false, 250, 100, null);
	}

}
