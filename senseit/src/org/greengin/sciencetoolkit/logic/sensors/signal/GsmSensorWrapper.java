package org.greengin.sciencetoolkit.logic.sensors.signal;


import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.content.Context;

public class GsmSensorWrapper extends AbstractSignalSensorWrapper {

	public GsmSensorWrapper(Context applicationContext) {
		super(applicationContext);
	}


	@Override
	public int getType() {
		return SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM;
	}
	
	@Override
	protected String network() {
		return "gsm";
	}

	@Override
	public String getName() {
		return "GSM signal strength";
	}
}
