package com.app.dz.quranapp.Services;

import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_PREPAREING_FILES_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.R;

import java.io.File;

public class ForegroundDownloadMushafService extends Service {

    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id_1";
    private final static String TAG = ForegroundDownloadMushafService.class.getSimpleName();
    private static final int DOWNLOAD_TYPE_MUSHAF_IMAGES = 10;
    static private int mStateService = Statics.STATE_SERVICE.NOT_INIT;
    private NotificationManager mNotificationManager;
    private WifiManager.WifiLock mWiFiLock;
    private PowerManager.WakeLock mWakeLock;
    private int LastProgressVlaue = -1;
    private DownloadTask downloadTask;
    private boolean isCanceled = false;

    public ForegroundDownloadMushafService() {
    }

    public static int getState() {
        return mStateService;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ForegroundDownloadMushafService.class.getSimpleName(), "onCreate()");
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        Log.e(TAG, "onStartCommand " + intent.getAction());

        switch (intent.getAction()) {
            case Statics.ACTION.START_ACTION:
                Log.e(TAG, "we send start Intent ");
                String filePath = intent.getStringExtra("path");
                String downloadUrl = intent.getStringExtra("url");
                mStateService = Statics.STATE_SERVICE.PREPARE;
                startForeground(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(PROGRESS_ACTION, 0));
                download(downloadUrl, filePath);
                break;
            case Statics.ACTION.STOP_ACTION:
                isCanceled = true;
                Log.e(TAG, "Received Stop Intent");
                toldTheActivty(DOWNLOAD_CANCEL_ACTION, 0);
                stopDownload();
                break;

            default:
                stopForeground(true);
                stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void stopDownload() {
        try {
            if (downloadTask != null) downloadTask.cancelDownload();
        } catch (Exception e) {

        }

        unlockWifiAndCpu();
        stopForeground(true);
        stopSelf();
    }


    @Override
    public void onDestroy() {
        if (downloadTask != null) downloadTask.cancelDownload();
        Log.e(TAG, "onDestroy()");
        unlockWifiAndCpu();
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        super.onDestroy();
    }

    private void unlockWifiAndCpu() {
        unlockWiFi();
        unlockCPU();
    }

    private void lockWifiAndCpu() {
        lockWiFi();
        lockCPU();
    }

    private Notification prepareNotification(String state, int progressVlaue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.title_Books);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            mChannel.enableVibration(false);
            mChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Statics.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lStopIntent = new Intent(this, ForegroundDownloadBookService.class);
        lStopIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingStopIntent = PendingIntent.getService(this, 0, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        RemoteViews lRemoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_ai);
//        lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);

        switch (state) {
            case PROGRESS_ACTION:
                lRemoteViews.setProgressBar(R.id.progress_bar_ai, 100, progressVlaue, false);
//                lRemoteViews.setTextViewText(R.id.tv_download_progress_ai, "" + progressVlaue);
                //lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);
                lRemoteViews.setTextViewText(R.id.tv_download_title_ai, "جاري تحميل الملفات ... ");
                break;
            case DOWNLOAD_PREPAREING_FILES_ACTION:
                Log.e(TAG, "notification prepare files ");
                lRemoteViews.setProgressBar(R.id.progress_bar_ai, 100, progressVlaue, true);
//                lRemoteViews.setTextViewText(R.id.tv_download_progress_ai, "" + progressVlaue);
                //lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);
                lRemoteViews.setTextViewText(R.id.tv_download_title_ai, "جاري تحضير الملفات ... ");
                break;
            case DOWNLOAD_COMPLETE_ACTION:
                Log.e(TAG, "notification download finished ");
                lRemoteViews.setProgressBar(R.id.progress_bar_ai, 100, 100, false);
//                lRemoteViews.setTextViewText(R.id.tv_download_progress_ai, "100");
                //lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);
                lRemoteViews.setTextViewText(R.id.tv_download_title_ai, "تم التحميل بنجاح");
                break;
        }


        Log.e(TAG, "on Notification prepared " + progressVlaue + " state  " + state);

        Notification.Builder lNotificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lNotificationBuilder = new Notification.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            lNotificationBuilder = new Notification.Builder(this);
        }
        lNotificationBuilder
                .setContent(lRemoteViews)
                .setSmallIcon(R.mipmap.icon_round)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setSound(null, null)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        lNotificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        return lNotificationBuilder.build();
    }

    public void download(String url, String filepath) {
        Log.e(TAG, "download start");
        lockWifiAndCpu();
        DownloadWithAsyncTask(url, filepath);
    }

    private void lockCPU() {
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getSimpleName());
        mWakeLock.acquire();
        Log.d(TAG, "Player lockCPU()");
    }

    private void unlockCPU() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
            Log.d(TAG, "Player unlockCPU()");
        }
    }

    private void lockWiFi() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo lWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (lWifi != null && lWifi.isConnected()) {
            mWiFiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF, ForegroundDownloadMushafService.class.getSimpleName());
            mWiFiLock.acquire();
            Log.e(TAG, "Player lockWiFi()");
        }
    }

    private void unlockWiFi() {
        if (mWiFiLock != null && mWiFiLock.isHeld()) {
            mWiFiLock.release();
            mWiFiLock = null;
            Log.d(TAG, "Player unlockWiFi()");
        }
    }

    private void toldTheActivty(String action, int downloadProgress) {

        if (action.equals(DOWNLOAD_PREPAREING_FILES_ACTION)) {
            if (LastProgressVlaue != downloadProgress) {
                LastProgressVlaue = downloadProgress;
                Intent intent2 = new Intent("DOWNLOAD_FINISHED");
                intent2.putExtra("action", action);
                intent2.putExtra("progress", downloadProgress);
                sendBroadcast(intent2);
            }
        } else if (action.equals(PROGRESS_ACTION)) {
            if (LastProgressVlaue != downloadProgress) {
                LastProgressVlaue = downloadProgress;
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(PROGRESS_ACTION, downloadProgress));
                Intent intent2 = new Intent("DOWNLOAD_FINISHED");
                intent2.putExtra("action", action);
                intent2.putExtra("progress", downloadProgress);
                sendBroadcast(intent2);
            }
        } else {
            mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(action, 0));
            Intent intent2 = new Intent("DOWNLOAD_FINISHED");
            intent2.putExtra("action", action);
            intent2.putExtra("progress", downloadProgress);
            intent2.putExtra("type",DOWNLOAD_TYPE_MUSHAF_IMAGES);
            sendBroadcast(intent2);
        }

    }

    private void DownloadFinished() {
        toldTheActivty(DOWNLOAD_COMPLETE_ACTION, 100);
        stopDownload();
    }

    public void DownloadWithAsyncTask(String url, String filepath) {

        // Permission is granted, create the folder
        /*todo check if file exist
        File folderFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),AppfolderName+"/"+file.getName());
        if (!folderFile2.exists()) {
            if (folderFile2.mkdirs()) {
                Log.d(TAG, "Folder created successfully");
            } else {
                Log.e(TAG, "Failed to create folder");
            }
        } else {
            Log.d(TAG, "Folder already exists");
        }*/

        File file = new File(filepath);
        mStateService = Statics.STATE_SERVICE.PLAY;
        downloadTask = new DownloadTask(url, file, new DownloadListeners() {
            private int lastProgress = -1;

            @Override
            public void onProgressUpdate(int downloadedSize, int totalSize) {
                if (downloadedSize == 0) return;
                int progress = Math.round(((float) downloadedSize / totalSize) * 100);
                if (progress != lastProgress) {
                    lastProgress = progress;
                    toldTheActivty(PROGRESS_ACTION, progress);
                }
            }

            @Override
            public void onDownloadComplete(File outputFile) {
                DownloadFinished();
            }

            @Override
            public void onDownloadCacled(String reason) {
                Log.e(TAG, "download canceled " + reason);
                if (isCanceled) return;
                toldTheActivty(DOWNLOAD_ERROR_ACTION, 0);
                stopDownload();

            }
        });
        downloadTask.execute();
    }

}







