package com.app.dz.quranapp.ui.activities.qibla_parte;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;


public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;
    public final static String TAG = "QublaFragment";

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private LocationListener listener;
    private boolean isLocationAvialable = false;

    public GPSTracker(Context context, LocationListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                Log.e(TAG, "getLocation: return canGetLocation true ");
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.e(TAG, "getLocation: Network Enabled 1");
                    if (locationManager != null) {
                        Log.e(TAG, "getLocation: Network Enabled 2");
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.e(TAG, "getLocation: Network Enabled 3");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            isLocationAvialable = true;
                            listener.onNewLocation(latitude, longitude,location);
                        }
                    }
                } else {
                    Log.e(TAG, "getLocation: Network not enabled");
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        Log.e(TAG, "getLocation: GPS Enabled 1");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.e("GPS Enabled", "GPS Enabled 2");
                        if (locationManager != null) {
                            Log.e(TAG, "getLocation: GPS Enabled 3");
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                Log.e(TAG, "getLocation: GPS Enabled 4");
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                isLocationAvialable = true;
                                listener.onNewLocation(latitude, longitude,location);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "getLocation: GPS not enabled");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getLocation: Exception " + e.getMessage());
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // no network provider is enabled
        this.canGetLocation = isGPSEnabled && isNetworkEnabled;

        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(mContext.getResources().getString(R.string.gps_settings_title));
        alertDialog.setMessage(mContext.getResources().getString(R.string.gps_settings_text));

        alertDialog.setPositiveButton("حسنا", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        });
        alertDialog.setNegativeButton("الغاء", (dialog, which) -> dialog.cancel());
        alertDialog.show();

    }

    public boolean isLocationAvialable() {
        return isLocationAvialable;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "gps class onLocationChanged get called");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            isLocationAvialable = true;
            listener.onNewLocation(latitude, longitude,location);
        } else
            Log.e(TAG, "gps class onLocationChanged get called with null location");


// TODO Auto-generated method stub
    }

    @Override
    public void onProviderDisabled(String provider) {
// TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e(TAG, "onProviderEnabled");

// TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// TODO Auto-generated method stub
    }

    @Override
    public IBinder onBind(Intent intent) {
// TODO Auto-generated method stub
        return null;
    }

    public interface LocationListener {
        void onNewLocation(double latitude, double longitude,Location location);
    }

}
