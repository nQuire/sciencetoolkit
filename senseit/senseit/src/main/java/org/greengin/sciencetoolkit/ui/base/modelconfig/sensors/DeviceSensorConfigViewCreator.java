package org.greengin.sciencetoolkit.ui.base.modelconfig.sensors;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.greengin.sciencetoolkit.common.ui.base.modelconfig.ModelFragment;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.SenseItModelDefaults;

import android.view.View;

public class DeviceSensorConfigViewCreator {
		static final String[] delayLabels = new String[] {"200,000 µs", "60,000 µs", "20,000 µs"};
	

	public static void createView(ModelFragment fragment, View container, SensorWrapper sensor) {
		if (sensor != null) {
			int minDelay = sensor.getMinDelay();
			if (minDelay > 0) {
				DecimalFormat df = new DecimalFormat("#,###");
				String fastestDelayOption = df.format(minDelay) + " µs";
				int validOptionCount = ModelOperations.getValidDeviceSensorDelayOptions(sensor);
				List<String> values = new ArrayList<String>();
				for (int i = 0; i < validOptionCount; i++) {
					values.add(delayLabels[i]);
				}
				values.add(fastestDelayOption);

				fragment.addOptionSelect("delay", "Sensor delay", "The delay between sensor updates.\nNote that lower values increase battery drain.", values, SenseItModelDefaults.SENSOR_DELAY);
			} else {
				SensorConfigViewCreator.addEmptyWarning(fragment);
			}
		}
	}
}
