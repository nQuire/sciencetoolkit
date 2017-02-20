package org.greengin.sciencetoolkit.ui.base.modelconfig.sensors;

import org.greengin.sciencetoolkit.common.ui.base.modelconfig.ModelFragment;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.SenseItModelDefaults;

import android.view.View;

public class SoundSensorConfigViewCreator {

	public static void createView(ModelFragment fragment, View container, SensorWrapper sensor) {
		fragment.addOptionNumber("record_period", "Record period", "The duration of the recording period (ms).", false, false, SenseItModelDefaults.SOUND_SENSOR_PERIOD, SenseItModelDefaults.SOUND_SENSOR_PERIOD_MIN, null);
	}
}
