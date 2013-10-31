package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.ui.SensorUIData;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.app.Activity;
import android.view.View;

public class LivePlotSettingsFragment extends AbstractSettingsFragment {

	SensorWrapper sensor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sensor = SensorWrapperManager.getInstance().getSensor(getArguments().getString(SettingsFragmentManager.ARG_SENSOR));
	}

	@Override
	protected void createConfigOptions(View view) {

		addOptionNumber("samples", "Sample count", "The number of samples shown in the plot.", false, false, ModelDefaults.LIVEPLOT_SAMPLES, 5, 500);
		addOptionNumber("period", "Sample period", "Time period between plot samples (ms).", false, false, ModelDefaults.LIVEPLOT_PERIOD, 10, null);

		String[] labels = SensorUIData.getValueLabels(sensor.getType());
		for (int i = 0; i < sensor.getValueCount(); i++) {
			addOptionCheckbox("show:" + i, "Show '" + labels[i] + "'", "Show or hide a plot series.", true);
		}
	}

}
