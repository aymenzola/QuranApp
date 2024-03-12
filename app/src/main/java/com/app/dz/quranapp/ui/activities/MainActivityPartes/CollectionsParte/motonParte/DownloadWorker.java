package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte;

import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
    private Context context;
    private String actionType;
    private String subFolder;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        NOTIFICATION_DOWNLOAD_ID = getInputData().getInt("notifyId", 1);
        actionType = getInputData().getString("action");
        subFolder = getInputData().getString("subFolder");

        Log.d(TAG, "DownloadWorker called with id " + NOTIFICATION_DOWNLOAD_ID);
    }


    @NonNull
    @Override
    public Result doWork() {
        String fileUrl = getInputData().getString("fileUrl");
        String fileName = getInputData().getString("fileName");
        fileTitle = getInputData().getString("fileTitle");


        try {
            setForegroundAsync(getForegroundInfo());
        } catch (Exception e) {
            Log.e(TAG, "error in setForegroundAsync " + e.getMessage());
        }
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
                        notifyActivity(DOWNLOAD_CANCEL_ACTION, "تم إلغاء التحميل");
                        downloadTask.cancelDownload();
                        return;
                    }

                    if (downloadedSize == 0) return;
                    int progress = Math.round(((float) downloadedSize / totalSize) * 100);
                    if (progress != lastProgress) {
                        lastProgress = progress;
                        // Report progress.

                        //if (progress == 1) showDownloadCompleteNotification("تم بدء التحميل");
                        notifyProgressActivity(progress);
                        sendProgress(progress);
                    }
                }

                @Override
                public void onDownloadComplete(File outputFile) {
                    showDownloadCompleteNotification("تم التحميل بنجاح");
                    notifyActivity(DOWNLOAD_COMPLETE_ACTION, "تم التحميل بنجاح");
                    sendProgress(100);
                    result.set(Result.success());
                    latch.countDown();
                }


                @Override
                public void onDownloadError(String error) {
                    notifyActivity(DOWNLOAD_ERROR_ACTION, "حدث خطأ أثناء التحميل " + error);
                    showDownloadCompleteNotification("حدث خطأ أثناء التحميل " + error);
                    sendError(error);
                }

                @Override
                public void onDownloadCanceled(String reason) {
                    Log.e(TAG, "download canceled " + reason);
                    notifyActivity(DOWNLOAD_CANCEL_ACTION, "تم إلغاء التحميل " + reason);
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

        File file;
        if (subFolder != null) {
//            file = PublicMethods.getInstance().getFile(fileName,context);
                file = PublicMethods.getInstance().getFile(context,subFolder,fileName);
//            url = "https://www.dropbox.com/scl/fi/susr5ju1ut9r4rl17qu19/FrenchQuran.pdf?rlkey=ssn775mix8jze0czy1wcwjbhj&dl=1";
        } else {
            file = PublicMethods.getInstance().getFile(fileName,context);
        }

        if (!file.exists()) {
            boolean result;
            try {
                result = file.createNewFile();
                Log.d(TAG, "the creation file result is " + result);
            } catch (IOException e) {
                Log.d(TAG, "error in create file " + e.getMessage());
                throw new RuntimeException(e);
            }
            Log.d(TAG, "the creation file result is " + result);

        }
        Log.d(TAG, "moving to downloadTask with " +url);
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

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    @Override
    @NonNull
    public ForegroundInfo getForegroundInfo() {
        // Create a notification channel
        return getForegroundNotificationInfo();
    }

    @NonNull
    private ForegroundInfo getForegroundNotificationInfo() {
        createNotificationChannel();

        // Create a notification
        Notification notification = new NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
                .setContentText(fileTitle)
                .setSmallIcon(R.drawable.ic_download)
                .setProgress(100, 0, false)
                .build();

        // Create a ForegroundInfo object with the notification
        Log.e("checkNotifyIdTag", "NOTIFICATION_DOWNLOAD_ID " + NOTIFICATION_DOWNLOAD_ID);
        return new ForegroundInfo(NOTIFICATION_DOWNLOAD_ID, notification);
    }


    private void updateNotificationProgress(int progress) {
        // Create a new Notification
        Notification notification = new NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
                .setContentText(fileTitle)
                .setSmallIcon(R.drawable.ic_download)
                .setProgress(100, progress, false).setOnlyAlertOnce(true)
                .build();


        updateNotification(notification, NOTIFICATION_DOWNLOAD_ID);
    }


    private void updateNotification(Notification notification, int notifyId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            Log.e("checkNotifyIdTag", "updateNotification id  " + NOTIFICATION_DOWNLOAD_ID);
            notificationManager.notify(notifyId, notification);
        }
    }


    public void showDownloadCompleteNotification(String message) {
        String channelId = "quran_download_channel1";
        String channelName = "Download Complete Notifications1";

        // Create a notification channel
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for download completion");
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_download) // replace with your own icon
                .setContentTitle(fileTitle)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // for heads-up notification
                .setAutoCancel(true); // notification will disappear after click

        builder.setDefaults(Notification.DEFAULT_ALL);

        // Show the notification
        notificationManager.notify(19, builder.build());
    }

    private void notifyActivity(String action, String message) {
        Intent intent2 = new Intent(actionType);
        intent2.putExtra("type", action);
        intent2.putExtra("message", message);
        context.sendBroadcast(intent2);
    }

    private void notifyProgressActivity(int downloadProgress) {
        Intent intent2 = new Intent(actionType);
        intent2.putExtra("type", PROGRESS_ACTION);
        intent2.putExtra("progress", downloadProgress);
        context.sendBroadcast(intent2);
    }

}