package com.app.dz.quranapp.Services.PlayingAudio;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

public class DeviceResourceHandler {

    private static final String TAG = "DeviceResourceHandler";


    public static PowerManager.WakeLock lockCPU(Context context) {
        PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, DeviceResourceHandler.class.getSimpleName());
        wakeLock.acquire();
        Log.d(TAG, "Player lockCPU()");
        return wakeLock;
    }

    public static void unlockCPU(PowerManager.WakeLock wakeLock) {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.d(TAG, "Player unlockCPU()");
        }
    }

    public static WifiManager.WifiLock lockWiFi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo lWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        WifiManager.WifiLock wifiLock = null;
        if (lWifi != null && lWifi.isConnected()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
                wifiLock = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(
                        WifiManager.WIFI_MODE_FULL_HIGH_PERF, DeviceResourceHandler.class.getSimpleName());
            } else {
                wifiLock = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(
                        WifiManager.WIFI_MODE_FULL, DeviceResourceHandler.class.getSimpleName());
            }
            wifiLock.acquire();
            Log.d(TAG, "Player lockWiFi()");
        }
        return wifiLock;
    }

    public static WifiManager.WifiLock unlockWiFi(WifiManager.WifiLock wifiLock) {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
            return null;
        }
        return wifiLock;
    }
}
