package kr.nexters.onepage.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by ohjaehwan on 2017. 1. 24..
 */

public class LastLocationManager {

    private LocationManager locationManager;
    private OnReceiveLastLocationListener onReceiveLastLocationListener;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(onReceiveLastLocationListener != null) {
                onReceiveLastLocationListener.onReceive(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    interface OnReceiveLastLocationListener {
        void onReceive(Location newLocation);
    }

    public LastLocationManager(Context context, OnReceiveLastLocationListener onReceiveLastLocationListener) {
        this.onReceiveLastLocationListener = onReceiveLastLocationListener;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAltitudeRequired(false);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || !locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            return;
        }
//        locationManager.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);

        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mLocationListener);


        if(onReceiveLastLocationListener != null) {
            Location lastLoc = locationManager.getLastKnownLocation(bestProvider);

            if(lastLoc == null) {
                criteria.setAccuracy(Criteria.ACCURACY_COARSE); // Use network Provider
                String provider = locationManager.getBestProvider(criteria, true);
                lastLoc = locationManager.getLastKnownLocation(provider);
                Log.i("Main", "getLastLocation" + lastLoc.getLatitude() + " / " + lastLoc.getLongitude());
            }
            onReceiveLastLocationListener.onReceive(lastLoc);
        }

    }
}
