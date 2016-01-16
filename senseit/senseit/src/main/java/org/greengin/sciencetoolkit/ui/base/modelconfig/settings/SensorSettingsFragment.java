package org.greengin.sciencetoolkit.ui.base.modelconfig.settings;

import org.greengin.sciencetoolkit.ui.base.modelconfig.sensors.SensorConfigViewCreator;

import android.view.View;

public class SensorSettingsFragment extends AbstractSensorSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		SensorConfigViewCreator.createView(this, view, sensor);
	}

}
