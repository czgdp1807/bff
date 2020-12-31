package com.project.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

public class LocationTrack extends Service implements LocationListener
{
    private LocationManager locationManager;
    private Location location;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 0;

    public LocationTrack(Context mContext)
    {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        location = null;
    }

    public Boolean checkGPS()
    {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    public void setLocation()
    {
        locationManager.requestLocationUpdates
        (
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
        );
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public Location getLocation()
    {
        return location;
    }

    public double getLongitude()
    {
        double longitude = 0.0;
        if( location != null )
        {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public double getLatitude()
    {
        double latitude = 0.0;
        if( location != null )
        {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
    }


    @Override
    public void onProviderEnabled(String s)
    {
    }

    @Override
    public void onProviderDisabled(String s)
    {
    }

}