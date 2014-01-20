package org.greengin.sciencetoolkit.ui.base.modelconfig.sensors;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.base.modelconfig.ModelFragment;

import android.view.View;

public class SensorConfigViewCreator {
	
	public static void createView(ModelFragment fragment, View container, SensorWrapper sensor) {
		if (sensor.getType() == SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND) {
			SoundSensorConfigViewCreator.createView(fragment, container, sensor);
		} else if (sensor.getType() == SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION) {
			LocationSensorConfigViewCreator.createView(fragment, container, sensor);
		} else {
			DeviceSensorConfigViewCreator.createView(fragment, container, sensor);
		}
	}
	
	public static void addEmptyWarning(ModelFragment fragment) {
		fragment.addText("This sensor does not have any configuration options.");
	}
}
