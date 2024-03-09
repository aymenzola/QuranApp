package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.DownloadListeners;
import com.app.dz.quranapp.Services.DownloadTask;
import com.app.dz.quranapp.Util.PublicMethods;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class DownloadWorker extends Worker {

    private static final String TAG = "DownloadWorker";
    private static final String DOWNLOAD_CHANNEL_ID = "Download_Quran_Channel";
    private static int NOTIFICATION_DOWNLOAD_ID = 102;
    private DownloadTask downloadTask;
    private String fileTitle = "";
    private int notifyId;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        NOTIFICATION_DOWNLOAD_ID = getInputData().getInt("notifyId", 1);
        Log.d(TAG, "DownloadWorker called with id " + NOTIFICATION_DOWNLOAD_ID);
    }


    @NonNull
    @Override
    public Result doWork() {
        String fileUrl = getInputData().getString("fileUrl");
        String fileName = getInputData().getString("fileName");
        fileTitle = getInputData().getString("fileTitle");


        Log.e("Download", "the download file fileUrl is " + fileUrl);

        if (fileUrl == null) return Result.failure();

        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<Result> result = new AtomicReference<>(Result.failure());

            downloadFile(fileUrl, fileName, new DownloadListeners() {
                private int lastProgress = -1;

                @Override
                public void onProgressUpdate(int downloadedSize, int totalSize) {
                    if (isStopped()) {
                        // The work has been cancelled
                        // Perform necessary actions after cancellation
                        // For example, you can stop the download here
                        showDownloadCompleteNotification("تم إلغاء التحميل");
                        downloadTask.cancelDownload();
                        return;
                    }

                    if (downloadedSize == 0) return;
                    int progress = Math.round(((float) downloadedSize / totalSize) * 100);
                    if (progress != lastProgress) {
                        lastProgress = progress;
                        // Report progress.

                        if (progress == 1) showDownloadCompleteNotification("تم بدء التحميل");
                        sendProgress(progress);
                    }
                }

                @Override
                public void onDownloadComplete(File outputFile) {
                    showDownloadCompleteNotification("تم التحميل بنجاح");
                    sendProgress(100);
                    result.set(Result.success());
                    latch.countDown();
                }


                @Override
                public void onDownloadError(String error) {
                    showDownloadCompleteNotification("حدث خطأ أثناء التحميل " + error);
                    sendError(error);
                }

                @Override
                public void onDownloadCanceled(String reason) {
                    Log.e(TAG, "download canceled " + reason);
                    showDownloadCompleteNotification("تم إلغاء التحميل");
                    result.set(Result.failure());
                    latch.countDown();
                }
            });

            latch.await();  // Wait here until the latch is counted down in onPostExecute()

            return result.get();
        } catch (Exception e) {
            return Result.failure();
        }
    }


    public void downloadFile(String url, String fileName, DownloadListeners listener) {
        Log.d(TAG, "the download file name is " + fileName);

        File file = PublicMethods.getInstance().getFile(fileName,getApplicationContext());
        if (!file.exists()) {
            boolean result = false;
            try {
                result = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.d(TAG, "the creation file result is " + result);

        }
        downloadTask = new DownloadTask(url, file, new DownloadListeners() {
            private int lastProgress = -1;

            @Override
            public void onProgressUpdate(int downloadedSize, int totalSize) {
                listener.onProgressUpdate(downloadedSize, totalSize);
            }

            @Override
            public void onDownloadComplete(File outputFile) {
                listener.onDownloadComplete(outputFile);
            }

            @Override
            public void onDownloadError(String error) {
                listener.onDownloadError(error);
            }

            @Override
            public void onDownloadCanceled(String reason) {
                listener.onDownloadCanceled(reason);
            }
        });
        downloadTask.execute();
    }


    private void sendProgress(int progress) {
        updateNotificationProgress(progress);
        Data progressData = new Data.Builder().putInt("progress", progress).build();
        setProgressAsync(progressData);
    }

    private void sendError(String error) {
        Log.e(TAG, "error body " + error);
        Data errorData = new Data.Builder().putString("error", error).build();
        setProgressAsync(errorData);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    DOWNLOAD_CHANNEL_ID,
                    "تحميل القرآن الكريم",
                    NotificationManager.IMPORTANCE_HIGH
            );

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            // Enable lights for the notification
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Enable vibration for the notification
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            // Register the channel with the system
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    @Override
    @NonNull
    public ForegroundInfo getForegroundInfo() {
        // Create a notification channel
        createNotificationChannel();

        // Create a notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), DOWNLOAD_CHANNEL_ID)
                .setContentTitle("تحميل القرآن الكريم")
                .setContentText(fileTitle)
                .setSmallIcon(R.drawable.ic_download)
                .setProgress(100, 0, false).setDefaults(NotificationCompat.DEFAULT_ALL)
                .build();

        // Create a ForegroundInfo object with the notification
        Log.e("checkNotifyIdTag", "NOTIFICATION_DOWNLOAD_ID " + NOTIFICATION_DOWNLOAD_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new ForegroundInfo(NOTIFICATION_DOWNLOAD_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            return new ForegroundInfo(NOTIFICATION_DOWNLOAD_ID, notification);
        }
    }


    private void updateNotificationProgress(int progress) {
        // Create a new Notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), DOWNLOAD_CHANNEL_ID)
                .setContentTitle("تحميل القرآن الكريم")
                .setContentText(fileTitle)
                .setSmallIcon(R.drawable.ic_download)
                .setProgress(100, progress, false)
                .build();
        updateNotification(notification, NOTIFICATION_DOWNLOAD_ID);
    }


    private void updateNotification(Notification notification, int notifyId) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            Log.e("checkNotifyIdTag", "updateNotification id  " + NOTIFICATION_DOWNLOAD_ID);
            notificationManager.notify(notifyId, notification);
        }
    }


    public void showDownloadCompleteNotification(String message) {
        String channelId = "quran_download_channel1";
        String channelName = "Download Complete Notifications1";

        // Create a notification channel
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for download completion");
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_download) // replace with your own icon
                .setContentTitle(fileTitle)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // for heads-up notification
                .setAutoCancel(true); // notification will disappear after click

        builder.setDefaults(Notification.DEFAULT_ALL);

        // Show the notification
        notificationManager.notify(19, builder.build());
    }

}