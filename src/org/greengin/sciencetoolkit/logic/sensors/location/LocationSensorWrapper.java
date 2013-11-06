package org.greengin.sciencetoolkit.logic.sensors.location;

import java.util.List;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationSensorWrapper extends SensorWrapper implements LocationListener {

	Context applicationContext;
	LocationManager locationManager;
	List<String> providers;

	public LocationSensorWrapper(Context applicationContext) {
		this.applicationContext = applicationContext;
		this.locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
		providers = this.locationManager.getAllProviders();
		for (String p : providers) {
			Log.d("stk location", p);
		}

	}

	@Override
	public int getType() {
		return SensorWrapperManager.CUSTOM_SENSOR_TYPE_LOCATION;
	}

	@Override
	public String getName() {
		return "Geolocation";
	}

	@Override
	public int getValueCount() {
		return 4;
	}

	@Override
	public float getResolution() {
		return 0;
	}

	@Override
	public int getMinDelay() {
		return 0;
	}

	@Override
	public float getMaxRange() {
		return 0;
	}

	protected void onInputAdded(boolean first, int inputCount) {
		if (first) {
			for (String p : providers) {
				this.locationManager.requestLocationUpdates(p, 250, 0, this);
			}
		}
	}

	protected void onInputRemoved(boolean empty, int inputCount) {
		if (empty) {
			this.locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("stk location", location.getProvider() + " " + location.getAccuracy());
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
