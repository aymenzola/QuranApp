package com.app.dz.quranapp.Services;

import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_PREPAREING_FILES_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;
import static com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.FragmentLibraryList.COLLECTION_DOWNLOAD;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.BookCollection;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExtractBooksService extends Service {

    // Indicate that we would like to update download progress
    public static final int BUFFER_SIZE = 4096;
    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id_1";
    private final static String TAG = ExtractBooksService.class.getSimpleName();
    static private int mStateService = Statics.STATE_SERVICE.NOT_INIT;
    private NotificationManager mNotificationManager;
    private WifiManager.WifiLock mWiFiLock;
    private PowerManager.WakeLock mWakeLock;
    private BookCollection bookCollection;
    private int LastProgressVlaue = -1;
    private int DownloadCount = 1;
    private int ZipFilesNumber = 12;
    private boolean isCanceled = false;
    private String errorMassage="";


    public ExtractBooksService() {
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
        Log.d(ExtractBooksService.class.getSimpleName(), "onCreate()");
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


        String action = intent.getAction();
        if (action == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        switch (intent.getAction()) {
            case Statics.ACTION.START_ACTION -> {
                bookCollection = (BookCollection) intent.getSerializableExtra("BookCollection");
                mStateService = Statics.STATE_SERVICE.PREPARE;
                startForeground(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(PROGRESS_ACTION, 0));
                download();
            }
            case Statics.ACTION.STOP_ACTION -> {
                isCanceled = true;
                Log.i(TAG, "Received Stop Intent");
                toldTheActivty(DOWNLOAD_CANCEL_ACTION, 0);
                stopDownload();
            }
            default -> {
                isCanceled = true;
                stopForeground(true);
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    private void stopDownload() {
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(DOWNLOAD_CANCEL_ACTION, 0));
        unlockWifiAndCpu();
        stopForeground(true);
        stopSelf();


    }


    @Override
    public void onDestroy() {
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

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lStopIntent = new Intent(this, ExtractBooksService.class);
        lStopIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingStopIntent = PendingIntent.getService(this, 0, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        RemoteViews lRemoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_ai);
//        lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);

        switch (state) {
            case PROGRESS_ACTION:
                int p = progressVlaue * 1000 / 4884;
                lRemoteViews.setProgressBar(R.id.progress_bar_ai, 1000, p, false);
                String message = "جاري تحميل الملفات ...  " + p + "/1000";
//                lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);
                lRemoteViews.setTextViewText(R.id.tv_download_title_ai,message);
                break;
            case DOWNLOAD_PREPAREING_FILES_ACTION:
                Log.e(TAG, "notification prepare files ");
                String message1 = "جاري تحضير الملفات ... "+progressVlaue;
                lRemoteViews.setProgressBar(R.id.progress_bar_ai, 100, progressVlaue, true);
//                lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);
                lRemoteViews.setTextViewText(R.id.tv_download_title_ai,message1);
                break;
            case DOWNLOAD_COMPLETE_ACTION:
                String message3 = "تم التحميل بنجاح";
                lRemoteViews.setProgressBar(R.id.progress_bar_ai, 100, 100, false);
//                lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);
                lRemoteViews.setTextViewText(R.id.tv_download_title_ai, message3);
                break;
            case DOWNLOAD_CANCEL_ACTION:
                String message4 = "تم الغاء العملية";
                lRemoteViews.setProgressBar(R.id.progress_bar_ai, 100, 0, false);
//                lRemoteViews.setOnClickPendingIntent(R.id.close_button, lPendingStopIntent);
                lRemoteViews.setTextViewText(R.id.tv_download_title_ai, message4);
                break;
        }

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
        toldTheActivty(DOWNLOAD_PREPAREING_FILES_ACTION, 1);
        Log.e(TAG, "download start");
        mStateService = Statics.STATE_SERVICE.PLAY;
        lockWifiAndCpu();
        ExtractFile();
    }

    private void lockCPU() {
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getSimpleName());
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);
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
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF, ExtractBooksService.class.getSimpleName());
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
            Log.e(TAG, "told activty preaparing ");
            mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(DOWNLOAD_PREPAREING_FILES_ACTION, downloadProgress));
            Intent intent2 = new Intent(COLLECTION_DOWNLOAD);
            intent2.putExtra("action", action);
            intent2.putExtra("progress", downloadProgress);
            intent2.putExtra("progress_max", 100);
            sendBroadcast(intent2);
        } else if (action.equals(PROGRESS_ACTION)) {
            if (LastProgressVlaue != downloadProgress) {
                LastProgressVlaue = downloadProgress;
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(PROGRESS_ACTION, downloadProgress));
                Intent intent2 = new Intent(COLLECTION_DOWNLOAD);
                intent2.putExtra("action",action);
                intent2.putExtra("progress",downloadProgress);
                sendBroadcast(intent2);
            }
        } else if (action.equals(DOWNLOAD_ERROR_ACTION)) {
            Intent intent2 = new Intent(COLLECTION_DOWNLOAD);
            intent2.putExtra("action",action);
            intent2.putExtra("error",errorMassage);
            sendBroadcast(intent2);
        } else {
            mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification(action, 0));
            Intent intent2 = new Intent(COLLECTION_DOWNLOAD);
            intent2.putExtra("action", action);
            intent2.putExtra("progress", downloadProgress);
            sendBroadcast(intent2);
        }

    }

    public String getCollectionFileNameJson(BookCollection bookCollection) {
        String d = bookCollection.CollectionName + ".json";
        Log.e("booksn", " getCollectionFileNameJson " + d);
        return d;
    }


    public String getBooksFileNameJson(BookCollection bookCollection) {
        String s = bookCollection.CollectionName + "_books.json";
        Log.e("booksn", " getBooksFileNameJson " + s);
        return s;
    }

    public void ExtractFile() {
        File currant_Collection_Csv_file = PublicMethods.getInstance().getFile(getCollectionFileNameJson(bookCollection), ExtractBooksService.this);
        if (currant_Collection_Csv_file.exists()) {
            ExtractFinishedSaveToRoom();
        } else
            try {
                unzipCSV(ExtractBooksService.this, "appfiles.zip", PublicMethods.getInstance().getAppFolder(ExtractBooksService.this).getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void unzipCSV(Context context, String zipFileName, String destDirectory) throws IOException {
        Log.e("zip", "zip start");
        InputStream is = context.getAssets().open(zipFileName);
        ZipInputStream zipIn = new ZipInputStream(is);
        File destDir = new File(destDirectory);
        Log.e("zip", "destDirectory  path" + destDir.getAbsolutePath());
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            Log.e("zip", "looping ");
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                Log.e("zip", "the entry is a file");
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
                DownloadCount++;
            } else {
                Log.e("zip", "the entry is a directory");
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            Log.e(TAG, "working ");
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        Log.e(TAG, "zip finished");

        if (DownloadCount == ZipFilesNumber)
            ExtractFinishedSaveToRoom();
    }

    private void ExtractFinishedSaveToRoom() {
        Log.e(TAG, "we dwonload two books and start preparing ");
        new CollectionDownloadDevAsyncTask().execute();
    }


    class CollectionDownloadDevAsyncTask extends AsyncTask<String, Void, String> {

        AppDatabase db = DatabaseClient.getInstance(ExtractBooksService.this).getAppDatabase();
        BookDao dao = db.getBookDao();

        @Override
        protected String doInBackground(String... names) {
            // Perform background task here

            int size = dao.countCollectionHadiths(bookCollection.CollectionName);
            Log.e("booksn", "table size " + size);
            if (size < bookCollection.itemsCount) {
                File file = PublicMethods.getInstance().getFile(getCollectionFileNameJson(bookCollection), ExtractBooksService.this);
                if (!file.exists() || !file.canRead()) {
                    return "Collection cant read the file or does not exist :" + file.exists() + " canRead :" + file.canRead()+" path "+file.getPath();
                } else {
                    List<Hadith> hadithList;



                    try {
                        InputStream inputStream = new FileInputStream(file);
                        Gson gson = new Gson();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        hadithList = gson.fromJson(bufferedReader, new TypeToken<List<Hadith>>() {}.getType());

                        inputStream.close();

                        //change item that equals bab to bab 1
                        for (Hadith hadith : hadithList) {
                            if (hadith.chapterTitle.equals("باب")) {
                                hadith.chapterTitle = "باب " + hadith.chapterId + " كتاب " + hadith.bookNumber;
                            }
                        }

                        dao.insertHadithList(hadithList);
                        toldTheActivty(PROGRESS_ACTION, 100);
                        return "we save the json data successfully";
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "book error";
                    }
                }
            } else {
                Log.e("booksn", "table size : " + size);
                //collection already added
                return "book exist";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("booksn", "onPostExecute result " + result);
            if (result.equals("book exist") || result.equals("we save the json data successfully")) {
                Log.e("booksn", "onPostExecute result " + result);
                new BookDownloadDevAsyncTask().execute();
            } else {
                setError(result);
                Toast.makeText(ExtractBooksService.this, "" + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class BookDownloadDevAsyncTask extends AsyncTask<String, Void, String> {

        AppDatabase db = DatabaseClient.getInstance(ExtractBooksService.this).getAppDatabase();
        BookDao dao = db.getBookDao();

        @Override
        protected String doInBackground(String... names) {
            // Perform background task here

            int size = dao.countBooksTable(bookCollection.CollectionName);
            Log.e("booksn", "books table size " + size);
            if (size == 0) {
                File file = PublicMethods.getInstance().getFile(getBooksFileNameJson(bookCollection), ExtractBooksService.this);
                if (!file.exists() || !file.canRead()) {
                    return "cant read the file or does not exist";
                } else {
                    List<Book> bookList;
                    try {
                        InputStream inputStream = new FileInputStream(file);
                        Gson gson = new Gson();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        bookList = gson.fromJson(bufferedReader, new TypeToken<List<Book>>() {
                        }.getType());
                        inputStream.close();
                        dao.insertBookList(bookList);
                        return "we save the books csv data successfully";

                    } catch (IOException e) {
                        e.printStackTrace();
                        return "error";
                    }
                }
            } else return "book table exist";


        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("book table exist") || result.equals("we save the books csv data successfully")) {
                toldTheActivty(DOWNLOAD_COMPLETE_ACTION, 100);
                stopDownload();
            } else {
                setError(result);
            }
        }
    }

    public void setError(String result) {
        errorMassage =result;
        toldTheActivty(DOWNLOAD_ERROR_ACTION,100);
        stopDownload();
    }

    public void getReadJsonHadiths() {
        AppDatabase db = DatabaseClient.getInstance(ExtractBooksService.this).getAppDatabase();
        BookDao dao = db.getBookDao();

        List<Hadith> hadithList;
        try {
            File file = PublicMethods.getInstance().getFile(getCollectionFileNameJson(bookCollection), ExtractBooksService.this);
            InputStream inputStream = new FileInputStream(file);
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            hadithList = gson.fromJson(bufferedReader, new TypeToken<List<Hadith>>() {
            }.getType());
            inputStream.close();
            dao.insertHadithList(hadithList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


