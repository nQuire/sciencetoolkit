package org.greengin.sciencetoolkit.ui.base.modelconfig.settings;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;
import org.greengin.sciencetoolkit.ui.base.modelconfig.DataLoggerDependentModelFragment;

import android.view.View;

public class SensorListSettingsFragment extends DataLoggerDependentModelFragment implements Comparator<SensorWrapper> {

	Model settings = SettingsManager.get().get("sensor_list");

	@Override
	protected void createConfigOptions(View view) {
		Vector<SensorWrapper> sensors = new Vector<SensorWrapper>();
		for (Entry<String, SensorWrapper> entry : SensorWrapperManager.get().getSensors().entrySet()) {
			sensors.add(entry.getValue());
		}
		
		Collections.sort(sensors, this);
		
		for (SensorWrapper sensor : sensors) {
			addOptionCheckbox(sensor.getId(), sensor.getName(), null, true);
		}
	}

	@Override
	public int compare(SensorWrapper lhs, SensorWrapper rhs) {
		boolean lhsSelected = settings.getBool(lhs.getId(), true);
		boolean rhsSelected = settings.getBool(rhs.getId(), true);

		if (lhsSelected && !rhsSelected) {
			return -1;
		} else if (!lhsSelected && rhsSelected) {
			return 1;
		} else {
			return SensorUIData.getWeight(lhs.getType()) - SensorUIData.getWeight(rhs.getType());
		}
	}

}
