package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.ui.base.Arguments;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;

import android.app.Activity;
import android.view.View;

public class LivePlotSettingsFragment extends AbstractSettingsFragment {

	SensorWrapper sensor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sensor = SensorWrapperManager.get().getSensor(getArguments().getString(Arguments.ARG_SENSOR));
	}

	@Override
	protected void createConfigOptions(View view) {

		addOptionNumber("sample_rate", "Sample rate", "The number of samples per second.", true, false, ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		addOptionNumber("view_period", "Sample window", "Period of time shown.", true, false, ModelDefaults.LIVEPLOT_VIEW_PERIOD, .1, 100);

		String[] labels = SensorUIData.getValueLabels(sensor.getType());
		for (int i = 0; i < sensor.getValueCount(); i++) {
			addOptionCheckbox("show:" + i, "Show '" + labels[i] + "'", "Show or hide a plot series.", true);
		}
	}

}
