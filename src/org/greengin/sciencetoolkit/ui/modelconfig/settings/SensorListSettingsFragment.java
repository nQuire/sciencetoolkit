package org.greengin.sciencetoolkit.ui.modelconfig.settings;


import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.view.View;

public class SensorListSettingsFragment extends AbstractSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		for (String sensorId : SensorWrapperManager.get().getSensorsIds()) {
			addOptionCheckbox(sensorId, "Show " + sensorId, null, true);
		}
	}

}
