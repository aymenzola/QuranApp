package com.app.dz.quranapp.Services.QuranServices;

import static com.app.dz.quranapp.Communs.Statics.ACTION.CHANGE_READER_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_FINISHED_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_NOT_AVAILABLE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PAUSE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PROGRESS_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_RESUME_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_START_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_STOP_ACTION;
import static com.app.dz.quranapp.Services.QuranServices.NotifyBroadcastHelper.sendErrorMessage;
import static com.app.dz.quranapp.Services.QuranServices.NotifyBroadcastHelper.sendPreparingStateToFragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.data.room.Entities.SuraAudio;
import com.app.dz.quranapp.quran.models.ReaderAudio;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PAudioServiceNoSelectionMedia3 extends Service {

   // private ExoPlayer player;

    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    private ReaderAudio selectedReader;
    private final static String TAG = PAudioServiceNoSelectionMedia3.class.getSimpleName();
    static private int mStateService = Statics.STATE_SERVICE.NOT_INIT;
    private final Object mLock = new Object();
    private final Handler mHandler = new Handler();
    private ExoPlayer mPlayer;
    private NotificationManager mNotificationManager;
    private WifiManager.WifiLock mWiFiLock;
    private PowerManager.WakeLock mWakeLock;
    private final Handler mTimerUpdateHandler = new Handler();
    private SuraAudio suraAudio;
    private PublicMethods publicMethods;
    private QuranInfoManager quranInfoManager;
    private Bitmap bitmapIcon;
    private float audioReadingSpeed = 1f;

    private final Runnable mTimerUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
            mTimerUpdateHandler.postDelayed(this, Statics.DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE);
        }
    };
    private int currentAudioPosition = 0;
    private MediaSessionCompat mediaSession;
    private String suraArabicName;
    private final Handler handlerAudioProgress = new Handler();
    ;

    public PAudioServiceNoSelectionMedia3() {
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
        Log.d(PAudioServiceNoSelectionMedia3.class.getSimpleName(), "onCreate()");
        mediaSession = new MediaSessionCompat(PAudioServiceNoSelectionMedia3.this, "tag");
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        publicMethods = PublicMethods.getInstance();
        quranInfoManager = QuranInfoManager.getInstance();
        bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


    }

    Runnable runnableAudioProgress = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null && (mStateService == Statics.STATE_SERVICE.PLAY || mStateService == Statics.STATE_SERVICE.PREPARE)) {
                NotifyBroadcastHelper.sendAudioProgress(getBaseContext(), (int) mPlayer.getCurrentPosition(), (int) mPlayer.getDuration());
                // Run this every 500 milliseconds.
                handlerAudioProgress.postDelayed(this, 500);


            }
        }
    };

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
        switch (action) {
            case Statics.ACTION.START_ACTION -> {
                Log.e(TAG, "Received Nosele start Intent");
                mStateService = Statics.STATE_SERVICE.PREPARE;
                this.suraAudio = (SuraAudio) intent.getSerializableExtra("suraAudio");
                this.selectedReader = (ReaderAudio) intent.getSerializableExtra("selectedReader");
                this.audioReadingSpeed = intent.getFloatExtra("speed", 1f);

                if (!PublicMethods.getInstance().isAvailableFile(suraAudio.SuraNumber, selectedReader.getReaderTag())) {
                    //send to fragment that the sura is not available
                    if (this.suraAudio != null)
                        sendPreparingStateToFragment(getBaseContext(),AUDIO_NOT_AVAILABLE_ACTION);
                    destroyPlayer();
                    stopForeground(true);
                    stopSelf();

                } else {

                    //getting sura arabic name and reader image
                    suraArabicName = quranInfoManager.getSuraName(suraAudio.SuraNumber - 1);
                    new Thread(() -> bitmapIcon = getReaderBitmap()).start();
                    sendPreparingStateToFragment(getBaseContext(), AUDIO_PROGRESS_ACTION);
                    prepareSuraAudio(suraAudio);
                }
            }
            case Statics.ACTION.PAUSE_ACTION -> {
                sendPreparingStateToFragment(getBaseContext(), AUDIO_PAUSE_ACTION);
                mStateService = Statics.STATE_SERVICE.PAUSE;
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                PausePlayer();
            }
            case Statics.ACTION.NEXT_SURA_ACTION -> NextOrBacKSuraPlayer(1);
            case Statics.ACTION.BACK_SURA_ACTION -> NextOrBacKSuraPlayer(-1);
            case Statics.ACTION.SEEK_ACTION -> seekPlayer(intent);

            case Statics.ACTION.PLAY_ACTION -> {
                sendPreparingStateToFragment(getBaseContext(), AUDIO_RESUME_ACTION);
                mStateService = Statics.STATE_SERVICE.PREPARE;
                this.audioReadingSpeed = intent.getFloatExtra("speed", 1f);
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());

                destroyPlayer();
                initPlayer();
                play();
            }
            case Statics.ACTION.STOP_ACTION -> {
                Log.i(TAG, "Received Nosele Stop Intent");
                if (this.suraAudio != null)
                    sendPreparingStateToFragment(getBaseContext(), AUDIO_STOP_ACTION);

                destroyPlayer();
                stopForeground(true);
                stopSelf();
            }
            case Statics.ACTION.CHANGE_SPEED_ACTION -> {
                Log.i(TAG, "Received Nosele change speed Intent");
                this.audioReadingSpeed = intent.getFloatExtra("speed", 1f);
                PausePlayer();
                initPlayer();
                play();
            }
            case CHANGE_READER_ACTION -> {
                Log.i(TAG, "Received Nosele change reader Intent");
                destroyPlayer();
                stopForeground(true);
                stopSelf();
                sendPreparingStateToFragment(getBaseContext(), CHANGE_READER_ACTION);
            }
            default -> {
                stopForeground(true);
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    private void prepareSuraAudio(SuraAudio suraAudio_local) {
        suraAudio = suraAudio_local;
        startForeground(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        destroyPlayer();
        initPlayer();
        play();
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) mediaSession.release();

        Log.d(TAG, "onDestroy() Nosele");
        destroyPlayer();
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        try {
            mTimerUpdateHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    private void PausePlayer() {
        if (mPlayer != null) {
            try {
                handlerAudioProgress.removeCallbacks(runnableAudioProgress);
                currentAudioPosition = (int) mPlayer.getCurrentPosition();
                mPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mPlayer = null;
            }
        }
        unlockWiFi();
        unlockCPU();
    }


    private void NextOrBacKSuraPlayer(int plus) {
        if (mStateService == Statics.STATE_SERVICE.PREPARE) {
            Log.e(TAG, "wait Nosele preaparing the next sura ");
            return;
        }
        //check if we are in first or in last sura if that dont do anything
        if (suraAudio.SuraNumber == 1 && plus == -1 || suraAudio.SuraNumber == 114 && plus == 1)
            return;
        mStateService = Statics.STATE_SERVICE.PREPARE;
        destroyPlayer();
        initPlayer();
        currentAudioPosition = 0;

        SuraAudio suraAudiolocal = new SuraAudio();
        suraAudiolocal.SuraNumber = suraAudio.SuraNumber + plus;
        suraAudiolocal.startAya = 1;
        suraAudiolocal.readerName = suraAudio.readerName;
        suraAudiolocal.isThereSelection = selectedReader.isThereSelection();
        suraArabicName = quranInfoManager.getSuraName(suraAudiolocal.SuraNumber - 1);
        File file = publicMethods.getFile(this, selectedReader.getReaderTag(), suraAudiolocal.SuraNumber);
        Log.e(TAG, "is file exist " + file.exists());
        suraAudiolocal.isFromLocal = file.exists() && file.canRead();

        this.suraAudio = suraAudiolocal;
        prepareSuraAudio(suraAudio);
    }



    public boolean onError(MediaPlayer mp, int what, int extra) {
        String message = PublicMethods.getInstance().getErrorMessage(what);
        Log.e(TAG, "Player onError() what:" + message);
        destroyPlayer();
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        stopForeground(true);
        stopSelf();
        sendErrorMessage(getBaseContext(), message);
        return false;
    }









    public Bitmap getReaderBitmap() {
        try {
            URL url = new URL(selectedReader.getReaderImage());
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }


    private void setPlaybackSpeed(float speed) {
        audioReadingSpeed = speed;
        Log.d(TAG, "Player setPlaybackSpeed() speed:" + speed);
        try {
            mPlayer.setPlaybackSpeed(speed);
        } catch (Exception e) {
            Log.e(TAG, "error in speed " + e.getMessage());
        }
    }

    private void lockCPU() {
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getSimpleName());
        mWakeLock.acquire();
    }

    private void unlockCPU() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void lockWiFi() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo lWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (lWifi != null && lWifi.isConnected()) {
            mWiFiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF, PAudioServiceNoSelectionMedia3.class.getSimpleName());
            mWiFiLock.acquire();
            Log.d(TAG, "Player lockWiFi()");
        }
    }

    private void unlockWiFi() {
        if (mWiFiLock != null && mWiFiLock.isHeld()) {
            mWiFiLock.release();
            mWiFiLock = null;
            Log.d(TAG, "Player unlockWiFi()");
        }
    }

    private void seekPlayer(Intent intent) {
        int seek = intent.getIntExtra("seek", 0);
        if (mPlayer != null && mPlayer.isPlaying()) mPlayer.seekTo(seek);
    }

    private void play() {
        try {
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (mLock) {
            try {
                if (mPlayer == null) {
                    initPlayer();
                }

                String urlOrPath = publicMethods.getCorrectUrlOrPath(selectedReader.getId(), suraAudio.SuraNumber, suraAudio.isFromLocal, this);
                MediaItem mediaItem = new MediaItem.Builder()
                        .setUri(urlOrPath)
                        .setMediaMetadata(new MediaMetadata.Builder().setTitle("Your Audio Title").build())
                        .build();
                mPlayer.setMediaItem(mediaItem);
                mPlayer.prepare();
                mPlayer.play();

            } catch (Exception e) {
                Log.d(TAG, "error : " + e.getMessage());
                destroyPlayer();
                e.printStackTrace();
            }
        }
    }

    private void destroyPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        handlerAudioProgress.removeCallbacks(runnableAudioProgress);
        unlockWiFi();
        unlockCPU();
    }


    private void initPlayer() {
        if (mPlayer == null) {
            mPlayer = new ExoPlayer.Builder(this).build();
            mPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {

                        Log.d(TAG, "Player onPrepared()");
                        mStateService = Statics.STATE_SERVICE.PLAY;
                        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                        try {
                            mPlayer.setWakeMode(PowerManager.PARTIAL_WAKE_LOCK);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            setPlaybackSpeed(audioReadingSpeed);
                        }
                        Log.e(TAG, "Duration current " + currentAudioPosition);

                        mPlayer.seekTo(currentAudioPosition);
                        mPlayer.play();

                        if (mPlayer != null && (mStateService == Statics.STATE_SERVICE.PLAY || mStateService == Statics.STATE_SERVICE.PREPARE)) {
                            handlerAudioProgress.postDelayed(runnableAudioProgress, 500);
                            NotifyBroadcastHelper.sendAudioProgress(getBaseContext(), 0, (int) mPlayer.getDuration());
                            Log.e(TAG, "send current minute and max minute to fragment");
                        } else
                            Log.e(TAG, "we dont send current minute and max minute to fragment");

                        mTimerUpdateHandler.postDelayed(mTimerUpdateRunnable, 0);
                        sendPreparingStateToFragment(getBaseContext(), AUDIO_START_ACTION);

                    }

                    if (playbackState == Player.STATE_ENDED) {
                        // This is equivalent to onCompletion in MediaPlayer
                        sendPreparingStateToFragment(getBaseContext(), AUDIO_FINISHED_ACTION);
                        Log.e(TAG, "we have to stop service");
                        destroyPlayer();
                        stopForeground(true);
                        stopSelf();
                    }
                }
            });
        } else {
            mPlayer.stop();
        }

        lockWiFi();
        lockCPU();
    }



    private Notification prepareNotification() {
        Log.d("notification_tag", "notification called ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.title_Books);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            Toast.makeText(this, "Channel  created "+FOREGROUND_CHANNEL_ID,Toast.LENGTH_SHORT).show();
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("page", suraAudio.SuraPage);

        PendingIntent pendingIntent;
        Intent lPauseIntent;
        PendingIntent lPendingPauseIntent;
        Intent playIntent;
        PendingIntent lPendingPlayIntent;
        Intent lStopIntent;
        PendingIntent lPendingStopIntent;
        Intent lNextSuraIntent;
        PendingIntent lPendingNextIntent;
        Intent lBackIntent;
        PendingIntent lPendingBackIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            lPauseIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION);
            lPendingPauseIntent = PendingIntent.getService(this, 1, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            playIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            playIntent.setAction(Statics.ACTION.PLAY_ACTION);
            lPendingPlayIntent = PendingIntent.getService(this, 2, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            lStopIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lStopIntent.setAction(Statics.ACTION.STOP_ACTION);
            lPendingStopIntent = PendingIntent.getService(this, 3, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            lNextSuraIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lNextSuraIntent.setAction(Statics.ACTION.NEXT_SURA_ACTION);
            lPendingNextIntent = PendingIntent.getService(this, 4, lNextSuraIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            lBackIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lBackIntent.setAction(Statics.ACTION.BACK_SURA_ACTION);
            lPendingBackIntent = PendingIntent.getService(this, 5, lBackIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            lPauseIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION);
            lPendingPauseIntent = PendingIntent.getService(this, 2, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            playIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            playIntent.setAction(Statics.ACTION.PLAY_ACTION);
            lPendingPlayIntent = PendingIntent.getService(this, 3, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            lStopIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lStopIntent.setAction(Statics.ACTION.STOP_ACTION);
            lPendingStopIntent = PendingIntent.getService(this, 4, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            lNextSuraIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lNextSuraIntent.setAction(Statics.ACTION.NEXT_SURA_ACTION);
            lPendingNextIntent = PendingIntent.getService(this, 5, lNextSuraIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            lBackIntent = new Intent(this, PAudioServiceNoSelectionMedia3.class);
            lBackIntent.setAction(Statics.ACTION.BACK_SURA_ACTION);
            lPendingBackIntent = PendingIntent.getService(this, 6, lBackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        /*
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lPauseIntent = new Intent(this, PAudioServiceNoSelection.class);
        lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent playIntent = new Intent(this, PAudioServiceNoSelection.class);
        playIntent.setAction(Statics.ACTION.PLAY_ACTION);
        PendingIntent lPendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lStopIntent = new Intent(this, PAudioServiceNoSelection.class);
        lStopIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingStopIntent = PendingIntent.getService(this, 0, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lNextSuraIntent = new Intent(this, PAudioServiceNoSelection.class);
        lNextSuraIntent.setAction(Statics.ACTION.NEXT_SURA_ACTION);
        PendingIntent lPendingNextIntent = PendingIntent.getService(this, 0, lNextSuraIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lBackIntent = new Intent(this, PAudioServiceNoSelection.class);
        lBackIntent.setAction(Statics.ACTION.BACK_SURA_ACTION);
        PendingIntent lPendingBackIntent = PendingIntent.getService(this, 0, lBackIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        */


        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);


        builder.setSmallIcon(R.drawable.ic_mashaf)
                .setContentTitle("سورة " + suraArabicName)
                .setContentText(selectedReader.getName())
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setLargeIcon(bitmapIcon)
                .setSound(null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2, 3)
                        .setMediaSession(mediaSession.getSessionToken()))
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", lPendingBackIntent);


        if (mStateService == Statics.STATE_SERVICE.PLAY) {
            builder.addAction(R.drawable.ic_baseline_pause_24, "pause", lPendingPauseIntent);
        } else {
            builder.addAction(R.drawable.ic_baseline_play_arrow_24, "Play", lPendingPlayIntent);
        }


        builder.addAction(R.drawable.ic_baseline_skip_next_24, "Next", lPendingNextIntent)
                .addAction(R.drawable.ic_baseline_close_24, "close", lPendingStopIntent);
        builder.setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC);

        return builder.build();

    }



}
