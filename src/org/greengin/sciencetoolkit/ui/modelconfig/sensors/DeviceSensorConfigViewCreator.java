package org.greengin.sciencetoolkit.ui.modelconfig.sensors;

import java.util.Arrays;
import java.util.List;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.ui.modelconfig.ModelFragment;

import android.view.View;

public class DeviceSensorConfigViewCreator {
	public static void createView(ModelFragment fragment, View container, SensorWrapper sensor) {
		if (sensor != null) {
			int minDelay = sensor.getMinDelay();
			if (minDelay > 0) {
				SensorConfigViewCreator.addOverrideWarning(fragment);

				String minDelayOption = (int) (1000000 / minDelay) + " samples/s";
				List<String> values = Arrays.asList(minDelayOption, "50 samples/s", "16 samples/s", "5 samples/s");
				fragment.addOptionSelect("delay", "Sensor rate", "The rate at which the sensor produces data.\nNote that higher values increase battery drain.", values, ModelDefaults.SENSOR_DELAY);
			} else {
				SensorConfigViewCreator.addEmptyWarning(fragment);
			}
		}
	}
}
