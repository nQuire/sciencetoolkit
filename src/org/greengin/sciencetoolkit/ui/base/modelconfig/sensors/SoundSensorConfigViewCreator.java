package org.greengin.sciencetoolkit.ui.base.modelconfig.sensors;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.ui.base.modelconfig.ModelFragment;

import android.view.View;

public class SoundSensorConfigViewCreator {

	public static void createView(ModelFragment fragment, View container, SensorWrapper sensor) {
		fragment.addOptionNumber("record_period", "Record period", "The duration of the recording period (ms).", false, false, ModelDefaults.SOUND_SENSOR_PERIOD, ModelDefaults.SOUND_SENSOR_PERIOD_MIN, null);
	}
}
