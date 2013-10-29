package org.greengin.sciencetoolkit.ui.fragments.sensors;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.settings.SettingsManager;
import org.greengin.sciencetoolkit.ui.fragments.settings.SettingsFragment;

import android.app.Activity;
import android.view.View;

public class SensorListSettingsFragment extends SettingsFragment {

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setSettings(SettingsManager.getInstance().get("sensor_list"));
	}

	@Override
	protected void createConfigOptions(View view) {
		for (String sensorId : SensorWrapperManager.getInstance().getSensorsIds()) {
			addOptionCheckbox(sensorId, "Show " + sensorId, null, true);
		}
	}
	

}
