package org.greengin.sciencetoolkit.ui.settings.fragments;

import org.greengin.sciencetoolkit.ui.SensorUIData;

import android.view.View;

public class LivePlotSettingsFragment extends AbstractSensorSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		
		addOptionNumber("samples", "Sample count", "The number of samples shown in the plot.", false, false, 10, 5, 500);
		addOptionNumber("period", "Sample period", "Time period between plot samples (ms).", false, false, 250, 10, null);

		String[] labels = SensorUIData.getValueLabels(sensor.getType());
		for (int i = 0; i < sensor.getValueCount(); i++) {
			addOptionCheckbox("show:" + i, "Show '" + labels[i] + "'", "Show or hide a plot series.", true);
		}
	}

}
