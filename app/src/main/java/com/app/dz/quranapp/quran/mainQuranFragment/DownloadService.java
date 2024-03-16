package com.app.dz.quranapp.quran.mainQuranFragment;


import static com.app.dz.quranapp.Communs.Constants.AppfolderName;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.DownloadListeners;
import com.app.dz.quranapp.Services.DownloadTask;
import com.app.dz.quranapp.Util.PublicMethods;

import java.io.File;

public class DownloadService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "Download_Channel";
    private static final String TAG = "DownloadService";
    private DownloadTask downloadTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String fileUrl = intent.getStringExtra("fileUrl");
        String fileName = intent.getStringExtra("fileName");


        // Create a notification
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_download)
                .build();

        // Start foreground service
        startForeground(NOTIFICATION_ID, notification);

        // Start download in a new thread
        new Thread(() -> {
            try {
                downloadFile(fileUrl,fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Download Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void updateNotificationProgress(int progress) {
        // Update the notification to show the download progress
    }

    private void updateNotificationDownloadComplete(int i) {
        // Update the notification to show that the download is complete
    }
    private void updateNotificationDownloadError(String error) {
        // Update the notification to show that the download is complete
    }


    public void downloadFile(String url, String fileName) {
        Log.d(TAG, "the download file name is " + fileName);

        // Permission is granted, create the folder
        File folderFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), AppfolderName);
        if (!folderFile2.exists()) {
            if (folderFile2.mkdirs()) {
                Log.d(TAG, "Folder created successfully");
            } else {
                Log.e(TAG, "Failed to create folder");
            }
        } else {
            Log.d(TAG, "Folder already exists");
        }

        PublicMethods publicMethods = PublicMethods.getInstance();
        File file = publicMethods.getFile(fileName, this);

        downloadTask = new DownloadTask(url, file, new DownloadListeners() {
            private int lastProgress = -1;

            @Override
            public void onProgressUpdate(int downloadedSize, int totalSize) {
                if (downloadedSize == 0) return;
                int progress = Math.round(((float) downloadedSize / totalSize) * 100);
                if (progress != lastProgress) {
                    lastProgress = progress;
                    // Report progress.
                    updateNotificationProgress(progress);
                }
            }

            @Override
            public void onDownloadComplete(File outputFile) {
                updateNotificationDownloadComplete(100);
            }



            @Override
            public void onDownloadError(String error) {
                Log.e(TAG, "download error " + error);
                updateNotificationDownloadError(error);
            }

            @Override
            public void onDownloadCanceled(String reason) {
                Log.e(TAG, "download canceled " + reason);
            }
        });
        downloadTask.execute();
    }

}