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
import android.widget.Toast;

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
			Log.d("stk location", location.getProvider());
			Log.d("stk location", "acc " + location.getAccuracy());
			Log.d("stk location", "alt " + location.getAltitude());
			Log.d("stk location", "lat " + location.getLatitude());
			Log.d("stk location", "lon " + location.getLongitude());
			/*
			 * if (best == null || location.getAccuracy() <= best.getAccuracy()
			 * || location.getTime() - best.getTime() > 20000) { }
			 */
			if (this.best != null) {
				double d2r = Math.PI /180.;
				double r = 6.371e6;
				double dlon = d2r * (location.getLongitude() - this.best.getLongitude());
				double dlat = d2r * (location.getLatitude() - this.best.getLatitude());
				double lat0 = d2r * this.best.getLatitude();
				double lat1 = d2r * location.getLatitude();
				
				double a = Math.pow(Math.sin(dlat/2), 2) +
				        Math.pow(Math.sin(dlon/2), 2) * Math.cos(lat0) * Math.cos(lat1);
				double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
				double d = r * c;
				
				Toast.makeText(this.applicationContext, location.getProvider() + "\n" + d, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this.applicationContext, "new location", Toast.LENGTH_SHORT).show();
			}
			
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
