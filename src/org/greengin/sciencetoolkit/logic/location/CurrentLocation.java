package org.greengin.sciencetoolkit.logic.location;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.greengin.sciencetoolkit.logic.appstatus.ApplicationStatusListener;
import org.greengin.sciencetoolkit.logic.appstatus.ApplicationStatusManager;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class CurrentLocation implements LocationListener, DataLoggerStatusListener, ApplicationStatusListener, ModelNotificationListener {
	private static CurrentLocation instance;

	public static CurrentLocation get() {
		return instance;
	}

	public static void init(Context applicationContext) {
		CurrentLocation.instance = new CurrentLocation(applicationContext);
	}

	Context applicationContext;
	LocationManager locationManager;
	List<String> providers;
	Location best;
	
	boolean locationRequested;
	Timer timer;
	
	
	private CurrentLocation(Context applicationContext) {
		this.applicationContext = applicationContext;
		this.locationManager = (LocationManager) this.applicationContext.getSystemService(Context.LOCATION_SERVICE);
		this.providers = this.locationManager.getAllProviders();
		
		DataLogger.get().registerStatusListener(this);
		ApplicationStatusManager.get().registerStatusListener(this);
		ProfileManager.get().registerDirectListener(this);
		
		checkLocationRequest();
		
		timer = null;
	}

	private void checkLocationRequest() {
		boolean geolocated = ProfileManager.get().getActiveProfile().getBool("requires_location");
		
		if (geolocated && (DataLogger.get().isRunning() || ApplicationStatusManager.get().isAwake())) {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			
			Log.d("stk location", "true " + (timer != null));
			
			for (String provider : providers) {
				this.updateLocation(locationManager.getLastKnownLocation(provider));
				locationManager.requestLocationUpdates(provider, 1000, 0, this);
			}
		} else {
			Log.d("stk location", "false");
			if (timer == null) {
				timer = new Timer();
				timer.schedule(new TimerTask() {					
					@Override
					public void run() {
						locationManager.removeUpdates(CurrentLocation.this);
						timer = null;
					}
				}, 3000);
			}
		}
	}
	
	

	private void updateLocation(Location location) {
		if (location != null) {
			this.best = location;
		}
	}


	public String locationString() {
		if (best != null) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("lat", best.getLatitude());
				obj.put("lon", best.getLongitude());
				obj.put("alt", best.getAltitude());
				obj.put("acc", best.getAccuracy());
				return obj.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@Override
	public void onLocationChanged(Location location) {
		this.updateLocation(location);
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
	public void applicationStatusEvent(boolean awake) {
		checkLocationRequest();
	}

	@Override
	public void dataLoggerStatusModified(String msg) {
		checkLocationRequest();
	}

	@Override
	public void modelNotificationReceived(String msg) {
		if ("switch".equals(msg)) {
			checkLocationRequest();
		}
	}

}
