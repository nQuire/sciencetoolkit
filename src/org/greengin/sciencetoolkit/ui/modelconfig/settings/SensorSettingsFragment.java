package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.ui.modelconfig.sensors.SensorConfigViewCreator;
import android.view.View;

public class SensorSettingsFragment extends AbstractSensorSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		SensorConfigViewCreator.createView(this, view, sensor);
	}

}
