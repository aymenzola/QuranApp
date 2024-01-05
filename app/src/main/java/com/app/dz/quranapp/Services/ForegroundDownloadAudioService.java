package com.app.dz.quranapp.Services;

import static com.app.dz.quranapp.MushafParte.QuranActivity.DOWNLOAD_TYPE_AUDIO;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_PREPAREING_FILES_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;

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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.app.dz.quranapp.Entities.AyaAudioLimits;
import com.app.dz.quranapp.Entities.AyaAudioLimitsFirebase;
import com.app.dz.quranapp.Entities.SuraAudioFirebase;
import com.app.dz.quranapp.Entities.SuraDownload;
import com.app.dz.quranapp.PlayerAudioNotification.Statics;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.AyaAudioLimitDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForegroundDownloadAudioService extends Service {

    // Indicate that we would like to update download progress
    private static final int UPDATE_DOWNLOAD_PROGRESS = 1;
    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id_1";
    DecimalFormat decimalFormat = new DecimalFormat("000");
    public static final String AppfolderName = "Kounoz";
    private final static String TAG = ForegroundDownloadAudioService.class.getSimpleName();
    static private int mStateService = Statics.STATE_SERVICE.NOT_INIT;
    private NotificationManager mNotificationManager;
    private WifiManager.WifiLock mWiFiLock;
    private PowerManager.WakeLock mWakeLock;
    private SuraDownload suraDownload;
    private int LastProgressVlaue = -1;
    private boolean downlaodFinished = false;
    private PublicMethods publicMethods;
    private DownloadTask downloadTask;
    private boolean isCanceled = false;

    public ForegroundDownloadAudioService() {
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
        publicMethods = PublicMethods.getInstance();
        Log.d(ForegroundDownloadAudioService.class.getSimpleName(), "onCreate()");
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
                suraDownload = (SuraDownload) intent.getSerializableExtra("sura");
                mStateService = Statics.STATE_SERVICE.PREPARE;
                startForeground(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(PROGRESS_ACTION, 0));
                download();
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

    public void download() {
        Log.e(TAG, "download start");
        lockWifiAndCpu();
        //TODO DOWNLOAD METHDE
        // downloadFile(publicMethods.getCorrectUrlOrPath(suraDownload.readerName, suraDownload.SuraNumber, false));
        DownloadWithAsyncTask(publicMethods.getCorrectUrlOrPath(suraDownload.readerName, suraDownload.SuraNumber, false));
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
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF, ForegroundDownloadAudioService.class.getSimpleName());
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
                intent2.putExtra("progress_max", suraDownload.endAya);
                sendBroadcast(intent2);
            }
        } else if (action.equals(PROGRESS_ACTION)) {
            if (LastProgressVlaue != downloadProgress) {
                Log.e(TAG, "told activty LastProgressVlaue = " + LastProgressVlaue);
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
            intent2.putExtra("progress",downloadProgress);
            intent2.putExtra("type",DOWNLOAD_TYPE_AUDIO);
            sendBroadcast(intent2);
        }

    }

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private final Handler mainHandler = new Handler(Looper.getMainLooper(), msg -> {
        if (msg.what == UPDATE_DOWNLOAD_PROGRESS) {
            int downloadProgress = msg.arg1;
            if (!downlaodFinished) {
                toldTheActivty(PROGRESS_ACTION, downloadProgress);
            }
        }
        return true;
    });

    public String getLocalFileName(String readerName, int suraIndex) {
        return publicMethods.getReaderTag(readerName) + "_" + suraIndex + ".mp3";
    }

    public void saveAyatLimits() {
        AppDatabase db = DatabaseClient.getInstance(ForegroundDownloadAudioService.this).getAppDatabase();
        AyaAudioLimitDao dao = db.getAyaAudioLimitsDao();

        int count = dao.getSuraAyatLimitsCount(suraDownload.SuraNumber, suraDownload.readerName);
        if (count == suraDownload.endAya) {
            DownloadFinished();
        } else {


            DocumentReference docRef = FirebaseFirestore.getInstance().collection("suraaudios")
                    .document(publicMethods.getReaderTag(suraDownload.readerName))
                    .collection(publicMethods.getReaderTag(suraDownload.readerName) + "Audio").document(String.valueOf(suraDownload.SuraNumber));

            docRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            SuraAudioFirebase suraAudioFirebase = documentSnapshot.toObject(SuraAudioFirebase.class);
                            List<AyaAudioLimitsFirebase> ayaAudioLimitsFirebaseList = suraAudioFirebase.ayaAudioList;


                            new Thread(() -> {
                                for (AyaAudioLimitsFirebase ayaAudioLimitsFirebase : ayaAudioLimitsFirebaseList) {

                                    String id = decimalFormat.format(suraDownload.SuraNumber) + "" + decimalFormat.format(ayaAudioLimitsFirebase.suraAya);
                                    String RoomId = id + "_" + publicMethods.getReaderTag(suraDownload.readerName);

                                    AyaAudioLimits ayaAudioLimits = new AyaAudioLimits(RoomId, suraDownload.SuraNumber, ayaAudioLimitsFirebase.suraAya,
                                            ayaAudioLimitsFirebase.startAyaTime, ayaAudioLimitsFirebase.endAyaTime, suraDownload.readerName);

                                    try {
                                        dao.insert(ayaAudioLimits);
                                        Log.d(TAG, "insert in local db");
                                    } catch (Exception e) {
                                        Log.d(TAG, "1 error " + e.getMessage());
                                    }
                                }
                                DownloadFinished();

                            }).start();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "get failed with " + e));


        }

    }

    private void DownloadFinished() {
        toldTheActivty(DOWNLOAD_COMPLETE_ACTION, 100);
        stopDownload();
    }

    public void DownloadWithAsyncTask(String url) {


        // Permission is granted, create the folder
        File folderFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),AppfolderName+"/"+publicMethods.getReaderTag(suraDownload.readerName));
        if (!folderFile2.exists()) {
            if (folderFile2.mkdirs()) {
                Log.d(TAG, "Folder created successfully");
            } else {
                Log.e(TAG, "Failed to create folder");
            }
        } else {
            Log.d(TAG, "Folder already exists");
        }

        mStateService = Statics.STATE_SERVICE.PLAY;
        File file = publicMethods.getFile(getLocalFileName(suraDownload.readerName, suraDownload.SuraNumber), suraDownload.readerName);
        downloadTask = new DownloadTask(url,file, new DownloadListeners() {
            private int lastProgress=-1;

            @Override
            public void onProgressUpdate(int downloadedSize,int totalSize) {
                if (downloadedSize==0) return;
                int progress = Math.round(((float) downloadedSize / totalSize) * 100);
                if (progress != lastProgress) {
                    lastProgress = progress;
                    toldTheActivty(PROGRESS_ACTION,progress);
                }
            }

            @Override
            public void onDownloadComplete(File outputFile) {
                if (suraDownload.isThereSelection)
                    new Thread(ForegroundDownloadAudioService.this::saveAyatLimits).start();
                else DownloadFinished();
            }

            @Override
            public void onDownloadCacled(String reason) {
                Log.e(TAG, "download canceled "+reason);
                if (isCanceled) return;
                toldTheActivty(DOWNLOAD_ERROR_ACTION, 0);
                stopDownload();

            }
        });
        downloadTask.execute();
    }





    /*
    *
    class ExampleAsyncTask2 extends AsyncTask<Void, Void, List<AyaAudioLimits>> {

        MushafDatabase database = MushafDatabase.getInstance(ForegroundDownloadService.this);
        AyaDao ayatDao = database.getAyaDao();

        AppDatabase db = DatabaseClient.getInstance(ForegroundDownloadService.this).getAppDatabase();
        AyaAudioLimitDao dao = db.getAyaAudioLimitsDao();

        private final MediaPlayer mediaPlayer = new MediaPlayer();

        private long Timetotal = 0;

        @Override
        protected List<AyaAudioLimits> doInBackground(Void... voids) {
            // Perform background task here

            int count = dao.getSuraAyatLimitsCount(suraDownload.SuraNumber, suraDownload.readerName);
            if (count == suraDownload.endAya) {
                return dao.getSuraAyatLimitsWithId(suraDownload.SuraNumber, suraDownload.readerName);
                //TODO ADD FIREBASE
            } else {
                List<Aya> inputList = ayatDao.getAyatWithSuraId(suraDownload.SuraNumber);
                List<AyaAudioLimits> ayaAudioList = new ArrayList<>();
                toldTheActivty(DOWNLOAD_PREPAREING_FILES_ACTION, 1);
                for (int i = 0; i < inputList.size(); i++) {
                    Aya aya = inputList.get(i);

                    try {
                        mediaPlayer.setDataSource(ForegroundDownloadService.this, Uri.parse(publicMethods.getCorrectUrlAya(suraDownload.readerName, suraDownload.SuraNumber, aya.getSuraAya())));
                        mediaPlayer.prepare();
                        long duration = mediaPlayer.getDuration();
                        String id = decimalFormat.format(aya.getSura()) + "" + decimalFormat.format(aya.getSuraAya());
                        AyaAudioLimits ayaAudio = new AyaAudioLimits(id, suraDownload.SuraNumber, aya.getSuraAya(), Timetotal, Timetotal + duration, suraDownload.readerName);
                        dao.insert(ayaAudio);
                        ayaAudioList.add(ayaAudio);
                        Timetotal = Timetotal + duration;
                        Log.e(TAG, "ayaAudioLimits" + ayaAudio);
                        mediaPlayer.reset();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return ayaAudioList;
            }
        }


        @Override
        protected void onPostExecute(List<AyaAudioLimits> list) {
            // Update UI thread with the result of the background task
            toldTheActivty(DOWNLOAD_COMPLETE_ACTION, 100);
            stopDownload();
        }
    }

    * */

    /*
    public void downloadFile(String url) {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                + "/" + AppfolderName + "/" + publicMethods.getReaderTag(suraDownload.readerName) + "/";

        String path = AppfolderName + "/" + publicMethods.getReaderTag(suraDownload.readerName) + "/" + getLocalFileName(suraDownload.readerName, suraDownload.SuraNumber);

        File folderFile = new File(folder);

        if (!folderFile.exists()) {
            if (folderFile.mkdir()) {
                Log.d(TAG, "Folder created successfully");
            } else {
                Log.e(TAG, "Failed to create folder");
            }
        } else {
            Log.d(TAG, "Folder already exists");
        }

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path);

        mStateService = Statics.STATE_SERVICE.PLAY;
        downloadId = downloadManager.enqueue(request);

        executor.execute(() -> {
            int progress = 0;
            boolean isDownloadFinished = false;
            while (!isDownloadFinished) {
                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    switch (downloadStatus) {
                        case DownloadManager.STATUS_RUNNING:
                            @SuppressLint("Range") long totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            if (totalBytes > 0) {
                                @SuppressLint("Range") long downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                progress = (int) (downloadedBytes * 100 / totalBytes);
                            }

                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            progress = 100;
                            isDownloadFinished = true;
                            downlaodFinished = true;
                            notificationMessage = "جاري تحضير الملفات";
                            Log.e(TAG, "download finished جاري تحضير الملفات");
                            toldTheActivty(DOWNLOAD_PREPAREING_FILES_ACTION, 1);
                            mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(DOWNLOAD_PREPAREING_FILES_ACTION, 100));
                            saveAyatLimits();
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            //Log.e(TAG, "download paused");
                        case DownloadManager.STATUS_PENDING:
                            // Log.e(TAG, "download pending");
                            break;
                        case DownloadManager.STATUS_FAILED:
                            toldTheActivty(DOWNLOAD_FAILED_ACTION, 100);
                            Log.e(TAG, "download failed ");
                            isDownloadFinished = true;

                            break;
                    }

                    Message message = Message.obtain();
                    message.what = UPDATE_DOWNLOAD_PROGRESS;
                    message.arg1 = progress;
                    mainHandler.sendMessage(message);
                }
            }
        });
    }
*/

}







