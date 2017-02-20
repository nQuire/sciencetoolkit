package org.greengin.sciencetoolkit.ui.base.modelconfig.settings;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.SenseItModelDefaults;
import org.greengin.sciencetoolkit.ui.base.SenseItArguments;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;
import org.greengin.sciencetoolkit.ui.base.modelconfig.DataLoggerDependentModelFragment;

import android.app.Activity;
import android.view.View;

public class LivePlotSettingsFragment extends DataLoggerDependentModelFragment {

	SensorWrapper sensor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sensor = SensorWrapperManager.get().getSensor(getArguments().getString(SenseItArguments.ARG_SENSOR));
	}

	@Override
	protected void createConfigOptions(View view) {

		addOptionNumber("sample_rate", "Sample rate", "The number of samples per second.", true, false, SenseItModelDefaults.LIVEPLOT_SAMPLING_RATE, SenseItModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, SenseItModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		addOptionNumber("view_period", "Sample window", "Period of time shown.", true, false, SenseItModelDefaults.LIVEPLOT_VIEW_PERIOD, .1, 100);

		String[] labels = SensorUIData.getValueLabels(this.getActivity(), sensor.getType());
		for (int i = 0; i < sensor.getValueCount(); i++) {
			addOptionCheckbox("show:" + i, "Show '" + labels[i] + "'", "Show or hide a plot series.", true);
		}
	}

}
