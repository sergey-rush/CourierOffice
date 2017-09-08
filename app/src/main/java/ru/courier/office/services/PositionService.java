package ru.courier.office.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import java.util.Timer;
import java.util.TimerTask;

import ru.courier.office.web.PositionManager;

/**
 * Created by rash on 07.09.2017.
 */

public class PositionService extends Service implements LocationListener {

    Location _location;
    LocationManager locationManager;
    boolean gpsOnNow = false;

    Timer _timer;

    public void onCreate() {
        super.onCreate();
        startOrResetAppActiveTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        setLocationDetecting();
        getCoordinates();
        return START_STICKY;
    }

    private void initLocationManager(boolean useGPS) {

        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            sendLocationServicesDisabledError();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopLocationService();
            return;
        }

        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) 30000, (float) 100.0, this);
        }

        gpsOnNow = false;
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER) && useGPS) {
            gpsOnNow = true;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 30000, (float) 100.0, this);
        }
    }

    private void sendLocationServicesDisabledError() {
        stopLocationService();
    }

    private void stopLocationService() {

        resetLocationManager();
        stopSelf();
    }

    private void resetLocationManager() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    public void onDestroy() {
        resetLocationManager();
        stopAppActiveTimer();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void stopAppActiveTimer() {
        if (_timer != null) {
            _timer.cancel();
        }
    }

    final Handler myHandler = new Handler();

    private void startOrResetAppActiveTimer() {

        if (_timer != null) {
            _timer.cancel();
        }
        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.post(myRunnable);
            }
        }, 3000);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            setLocationDetecting();
        }
    };

    public void setLocationDetecting() {

        if (locationManager != null) return;

        initLocationManager(true);
    }

    private void sendLocation(Location location) {
        PositionManager positionManager = new PositionManager(location);
        positionManager.execute();
        //stopLocationService();
    }

    private void getCoordinates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopLocationService();
            return;
        }

        if (locationManager == null) {
            return;
        }

        _location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (_location == null)
            _location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (_location != null) {
            sendLocation(_location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        _location = location;
        sendLocation(_location);
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