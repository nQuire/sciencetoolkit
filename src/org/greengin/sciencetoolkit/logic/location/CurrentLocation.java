package org.greengin.sciencetoolkit.logic.location;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class CurrentLocation implements LocationListener {
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

	private CurrentLocation(Context applicationContext) {
		this.applicationContext = applicationContext;
		this.locationManager = (LocationManager) this.applicationContext.getSystemService(Context.LOCATION_SERVICE);
		this.providers = this.locationManager.getAllProviders();
	}

	public void startlocation() {
		best = null;
		for (String provider : providers) {
			Log.d("stk location", " ");
			Log.d("stk location", "listen " + provider);
			this.updateLocation(locationManager.getLastKnownLocation(provider));
			locationManager.requestLocationUpdates(provider, 1000, 0, this);
		}
	}

	private void updateLocation(Location location) {
		Log.d("stk location", " ");
		Log.d("stk location", "" + (location != null));
		if (location != null) {
			
			Log.d("stk location", "best updated");
			this.best = location;
		}
	}

	public void stoplocation() {
		locationManager.removeUpdates(this);
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

}
