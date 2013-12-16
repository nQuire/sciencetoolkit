package org.greengin.sciencetoolkit.logic.sensors.location;


import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class AbstractGpsSensorWrapper extends SensorWrapper implements LocationListener, ModelNotificationListener {

	Context applicationContext;
	LocationManager locationManager;
	String provider;
	
	int minPeriod;
	int minDistance;
	
	Model settings;
	
	
	public static boolean isAvailable(Context applicationContext) {
		return applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");	
	}

	public AbstractGpsSensorWrapper(Context applicationContext) {
		super (SensorWrapperManager.CUSTOM_SENSOR_TYPE_GPS_LOCATION);
		
		this.applicationContext = applicationContext;
		this.locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
		this.provider = LocationManager.GPS_PROVIDER;
		
		this.minDistance = -1;
		this.minPeriod = -1;
		
		String settingsId = "sensor:" + getId();
		this.settings = SettingsManager.get().get(settingsId);
		this.updateConfig();
		SettingsManager.get().registerDirectListener(settingsId, this);
		
	}
	
	private void updateConfig() {
		int period = settings.getInt("min_period", ModelDefaults.GPS_MIN_PERIOD);
		int distance = settings.getInt("min_distance", ModelDefaults.GPS_MIN_DISTANCE);
		
		if (period != minPeriod || distance != minDistance) {
			minPeriod = period;
			minDistance = distance;
			
			if (hasInputs()) {
				stop();
				start();
			}
		}
		
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
	
	private void start() {
		this.locationManager.requestLocationUpdates(provider, minPeriod, minDistance, this);
	}
	
	private void stop() {
		this.locationManager.removeUpdates(this);
	}

	protected void onInputAdded(boolean first, int inputCount) {
		if (first) {
			start();
		}
	}

	protected void onInputRemoved(boolean empty, int inputCount) {
		if (empty) {
			stop();
		}
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
	
	@Override
	public void modelNotificationReceived(String msg) {
		this.updateConfig();
	}
}
