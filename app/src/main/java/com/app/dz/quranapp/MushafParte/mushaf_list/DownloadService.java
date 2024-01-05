package com.app.dz.quranapp.MushafParte.mushaf_list;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Map;

public class DownloadService extends Service {

    private Map<Long, Integer> mDownloadProgress = new HashMap<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long itemId = intent.getIntExtra("item_id", -1);

        // Start the download process in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Simulate a long download process
                for (int i = 0; i <= 100; i += 10) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e("tagadapter","service progress "+i);
                    mDownloadProgress.put(itemId, i);
                    broadcastProgress(itemId, i);
                }

                // Mark the item as downloaded
                mDownloadProgress.remove(itemId);
                broadcastProgress(itemId, -1);
            }
        }).start();

        return START_NOT_STICKY;
    }

    private void broadcastProgress(long itemId, int progress) {
        Intent intent = new Intent("download_progress");
        intent.putExtra("item_id", itemId);
        intent.putExtra("progress", progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}