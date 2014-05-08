package org.greengin.sciencetoolkit.logic.sensors.location;


import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

public class LocationGpsSensorWrapper extends AbstractGpsSensorWrapper implements LocationListener {

	float[] values;
	
	public LocationGpsSensorWrapper(Context applicationContext) {
		super(applicationContext);
		values = new float[2];
	}

	@Override
	public int getType() {
		return SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION;
	}

	@Override
	public String getName() {
		return "GPS Location";
	}

	@Override
	public int getValueCount() {
		return 2;
	}
	

	@Override
	public void onLocationChanged(Location location) {
		values[0] = (float) location.getLatitude();
		values[1] = (float) location.getLongitude();
		
		this.fireInput(values, 2);
	}
}
