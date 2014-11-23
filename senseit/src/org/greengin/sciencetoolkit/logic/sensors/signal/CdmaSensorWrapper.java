package org.greengin.sciencetoolkit.logic.sensors.signal;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.content.Context;

public class CdmaSensorWrapper extends AbstractSignalSensorWrapper {

	public CdmaSensorWrapper(Context applicationContext) {
		super(applicationContext);
	}

	@Override
	public int getType() {
		return SensorWrapperManager.CUSTOM_SENSOR_TYPE_CDMA;
	}


	@Override
	public String getName() {
		return "CDMA signal strength";
	}


	@Override
	protected String network() {
		return "cdma";
	}

}
