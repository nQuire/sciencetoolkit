package org.greengin.sciencetoolkit.ui.settings.fragments;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.settings.SettingsFragment;

import android.view.View;

public class SensorListSettingsFragment extends SettingsFragment {

	
	@Override
	protected void createConfigOptions(View view) {
		for (String sensorId : SensorWrapperManager.getInstance().getSensorsIds()) {
			addOptionCheckbox(sensorId, "Show " + sensorId, null, true);
		}
	}
	

}
