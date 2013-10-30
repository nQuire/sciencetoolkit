package org.greengin.sciencetoolkit.ui.settings.fragments;

import java.util.Arrays;
import java.util.List;

import android.view.View;

public class DeviceSensorSettingsFragment extends AbstractSensorSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		if (sensor != null) {
			int minDelay = sensor.getMinDelay();
			if (minDelay > 0) {
				addOverrideWarning();
				
				String minDelayOption = (int) (1000000 / minDelay) + " samples/s";
				List<String> values = Arrays.asList(minDelayOption, "50 samples/s", "16 samples/s", "5 samples/s");
				addOptionSelect("delay", "Sensor rate", "The rate at which the sensor produces data.\nNote that higher values increase battery drain.", values, 2);
			} else {
				addEmptyWarning();
			}			
		}
	}
}
