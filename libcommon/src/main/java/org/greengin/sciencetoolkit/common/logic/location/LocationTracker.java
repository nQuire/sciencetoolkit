package org.greengin.sciencetoolkit.common.logic.location;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationTracker implements LocationListener {

    Context applicationContext;
    LocationManager locationManager;
    List<String> providers;

    protected Location best;

    public LocationTracker(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.locationManager = (LocationManager) this.applicationContext
                .getSystemService(Context.LOCATION_SERVICE);
        this.providers = this.locationManager.getAllProviders();
    }

    protected void setListening(boolean listening) {
        if (listening) {
            for (String provider : providers) {
                this.updateLocation(locationManager
                        .getLastKnownLocation(provider));
                locationManager.requestLocationUpdates(provider, 1000, 0, this);
            }
        } else {
            locationManager.removeUpdates(this);
        }
    }

    private void updateLocation(Location location) {
        if (location != null) {
            this.best = location;
        }
    }

    protected void locationReceived() {
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
        this.locationReceived();
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
