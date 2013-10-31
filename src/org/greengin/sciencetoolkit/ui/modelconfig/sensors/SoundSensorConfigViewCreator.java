package org.greengin.sciencetoolkit.ui.modelconfig.sensors;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.ui.modelconfig.ModelFragment;

import android.view.View;

public class SoundSensorConfigViewCreator {

	public static void createView(ModelFragment fragment, View container, SensorWrapper sensor) {
		SensorConfigViewCreator.addOverrideWarning(fragment);
		fragment.addOptionNumber("record_period", "Record period", "The duration of the recording period (ms).", false, false, 250, 100, null);
	}
}
