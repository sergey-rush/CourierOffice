package ru.courier.office.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import ru.courier.office.web.PositionManager;

/**
 * Created by rash on 07.09.2017.
 */
public class PositionService implements LocationListener {

    LocationManager locationManager;
    Context _context;

    public PositionService(Context context) {
        _context = context;
    }

    public void startLocationManager(boolean useGPS) {

        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }

        locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            stopLocationManager();
            return;
        }

        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopLocationManager();
            return;
        }

        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) 30000, (float) 100.0, this);
        }


        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER) && useGPS) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 30000, (float) 100.0, this);
        }
    }

    public void stopLocationManager() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        PositionManager positionManager = new PositionManager(location);
        positionManager.execute();
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
}