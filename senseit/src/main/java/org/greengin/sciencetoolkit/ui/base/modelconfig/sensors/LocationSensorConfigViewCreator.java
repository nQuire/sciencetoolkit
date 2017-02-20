package org.greengin.sciencetoolkit.ui.base.modelconfig.sensors;

import org.greengin.sciencetoolkit.common.ui.base.modelconfig.ModelFragment;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;

import android.view.View;

public class LocationSensorConfigViewCreator {
	public static void createView(ModelFragment fragment, View container, SensorWrapper sensor) {
		SensorConfigViewCreator.addEmptyWarning(fragment);
	}
}
