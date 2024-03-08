package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;

import static com.app.dz.quranapp.Services.QuranServices.ForegroundDownloadAudioService.AppfolderName;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.DownloadListeners;
import com.app.dz.quranapp.Services.DownloadTask;
import com.app.dz.quranapp.Util.PublicMethods;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class DownloadWorker extends Worker {

    private static final String TAG = "DownloadWorker";
    private static final String DOWNLOAD_CHANNEL_ID = "Download_Quran_Channel";
    private static  int NOTIFICATION_DOWNLOAD_ID = 102;
    private DownloadTask downloadTask;
    private String fileTitle = "";
    private int notifyId;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        String fileUrl = getInputData().getString("fileUrl");
        String fileName = getInputData().getString("fileName");
        fileTitle = getInputData().getString("fileTitle");
        NOTIFICATION_DOWNLOAD_ID = getInputData().getInt("notifyId",1);

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
                        downloadResult("تم إلغاء التحميل");
                        downloadTask.cancelDownload();
                        return;
                    }

                    if (downloadedSize == 0) return;
                    int progress = Math.round(((float) downloadedSize / totalSize) * 100);
                    if (progress != lastProgress) {
                        lastProgress = progress;
                        // Report progress.
                        sendProgress(progress);
                    }
                }

                @Override
                public void onDownloadComplete(File outputFile) {
                    downloadFinished();
                    downloadResult("تم تحميل الملف بنجاح");
                    sendProgress(100);
                    result.set(Result.success());
                    latch.countDown();
                }


                @Override
                public void onDownloadError(String error) {
                    downloadResult(error);
                    sendError(error);
                }

                @Override
                public void onDownloadCanceled(String reason) {
                    Log.e(TAG, "download canceled " + reason);
                    downloadResult(reason);
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
        File file = publicMethods.getFile(fileName);

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
                /*if (isCanceled) return;
                toldTheActivty(DOWNLOAD_ERROR_ACTION, 0);
                stopDownload();
                */
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
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
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
                .setProgress(100, 0, false)
                .build();

        // Create a ForegroundInfo object with the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new ForegroundInfo(NOTIFICATION_DOWNLOAD_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE);
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
        Log.e(TAG, "updateNotificationProgress " + progress);
        updateNotification(notification);
    }


    private void downloadResult(String result) {
        // Create a new Notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), DOWNLOAD_CHANNEL_ID)
                .setContentTitle("تحميل القرآن الكريم")
                .setContentText(result)
                .setSmallIcon(R.drawable.ic_download)
                .build();

        Log.e(TAG, "update notifcation state " + result);
        updateNotification(notification);
    }


    private void updateNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_DOWNLOAD_ID, notification);
        }
    }


    private void downloadFinished() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download) // replace with your own icon
                .setContentTitle(fileTitle)
                .setContentText("تم التحميل بنجاح")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Step 3: Send the Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        notificationManager.notify(1,builder.build());
    }
}