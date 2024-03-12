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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

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
import java.util.List;

public class PAudioServiceNoSelection extends MediaBrowserServiceCompat
        implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {


    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    private ReaderAudio selectedReader;
    private final static String TAG = PAudioServiceNoSelection.class.getSimpleName();
    static private int mStateService = Statics.STATE_SERVICE.NOT_INIT;
    private final Object mLock = new Object();
    private final Handler mHandler = new Handler();
    private MediaPlayer mPlayer;
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

    public PAudioServiceNoSelection() {
    }

    public static int getState() {
        return mStateService;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(PAudioServiceNoSelection.class.getSimpleName(), "onCreate()");
        //mediaSession = new MediaSessionCompat(PAudioServiceNoSelection.this, "tag");

        mediaSession = new MediaSessionCompat(this, "MyMediaSession");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setCallback(new MySessionCallback());
        mediaSession.setActive(true);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .build();
        mediaSession.setPlaybackState(state);

        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        publicMethods = PublicMethods.getInstance();
        quranInfoManager = QuranInfoManager.getInstance();
        bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


    }


    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onStop() {
            super.onStop();
            Log.e(TAG, "onStop");
            onStopCalled();
        }

        @Override
        public void onPlay() {
            // Implement your play functionality here
            Log.e(TAG, "onPlay");
            onPlayCalled();
        }

        @Override
        public void onPause() {
            Log.e(TAG, "onPause");
            onPauseCalled();
            // Implement your pause functionality here
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            Log.e(TAG, "onSkipToNext");
            NextOrBacKSuraPlayer(1);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            Log.e(TAG, "onSkipToPrevious");
            NextOrBacKSuraPlayer(-1);
        }

        // Implement other media controls methods like onSkipToNext, onSkipToPrevious etc.
    }

    Runnable runnableAudioProgress = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null && (mStateService == Statics.STATE_SERVICE.PLAY || mStateService == Statics.STATE_SERVICE.PREPARE)) {
                NotifyBroadcastHelper.sendAudioProgress(getBaseContext(), mPlayer.getCurrentPosition(), mPlayer.getDuration());
                // Run this every 500 milliseconds.
                handlerAudioProgress.postDelayed(this, 500);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        Log.e(TAG, "onStartCommand ");
        if (intent == null) {
            Log.e(TAG, "Received null Intent stopSelf();");
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (action == null) {
            Log.e(TAG, "Received null action stopSelf();");
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        } else {
            Log.e(TAG, "Received action " + action);
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
                    handleFileNotAvialable();
                } else {

                    //getting sura arabic name and reader image
                    suraArabicName = quranInfoManager.getSuraName(suraAudio.SuraNumber - 1);
                    new Thread(() -> bitmapIcon = getReaderBitmap()).start();
                    sendPreparingStateToFragment(getBaseContext(), AUDIO_PROGRESS_ACTION);
                    prepareSuraAudio(suraAudio);
                }
                startForeground(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());

            }
            case Statics.ACTION.PAUSE_ACTION -> {
                onPauseCalled();
            }
            case Statics.ACTION.NEXT_SURA_ACTION -> NextOrBacKSuraPlayer(1);
            case Statics.ACTION.BACK_SURA_ACTION -> NextOrBacKSuraPlayer(-1);
            case Statics.ACTION.SEEK_ACTION -> seekPlayer(intent);

            case Statics.ACTION.PLAY_ACTION -> onPlayCalled(intent);
            case Statics.ACTION.STOP_ACTION -> onStopCalled();
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
                sendPreparingStateToFragment(getBaseContext(),CHANGE_READER_ACTION);
            }
            default -> {
                //stopForeground(true);
                Log.e(TAG, " default called");

                //stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    private void handleFileNotAvialable() {
        if (this.suraAudio != null)
            sendPreparingStateToFragment(getBaseContext(),AUDIO_NOT_AVAILABLE_ACTION);
        destroyPlayer();
        stopForeground(true);
        stopSelf();
    }

    private void onPlayCalled(Intent intent) {
        sendPreparingStateToFragment(getBaseContext(), AUDIO_RESUME_ACTION);
        mStateService = Statics.STATE_SERVICE.PREPARE;
        this.audioReadingSpeed = intent.getFloatExtra("speed", 1f);
        updateToState(Statics.STATE_SERVICE.PREPARE, PlaybackStateCompat.ACTION_PLAY, PlaybackStateCompat.STATE_BUFFERING);
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());

        destroyPlayer();
        initPlayer();
        play();
    }

/*private void onPlayCalled() {
        sendPreparingStateToFragment(getBaseContext(), AUDIO_RESUME_ACTION);
        mStateService = Statics.STATE_SERVICE.PREPARE;
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());

        if (mPlayer == null) {
            initPlayer();
            play();
        } else if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }*/


    private void onPlayCalled() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updateToState(Statics.STATE_SERVICE.PAUSE, PlaybackStateCompat.ACTION_PLAY, PlaybackStateCompat.STATE_PAUSED);
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
            } else {
                mPlayer.start();
                updateToState(Statics.STATE_SERVICE.PLAY, PlaybackStateCompat.ACTION_PAUSE, PlaybackStateCompat.STATE_PLAYING);
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
            }
        }
    }

    private void updateToState(int play, long nextAction, int currentState) {
        mStateService = play;
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
                .setActions(nextAction | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_STOP)
                .setState(currentState, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0)
                .build();
        mediaSession.setPlaybackState(state);
    }

    private void onStopCalled() {
        Log.i(TAG, "Received Nosele Stop Intent");
        if (this.suraAudio != null)
            sendPreparingStateToFragment(getBaseContext(), AUDIO_STOP_ACTION);

        destroyPlayer();
        stopForeground(true);
        Log.e(TAG, " on stop called stopSelf();");
        stopSelf();
    }

    private void onPauseCalled() {

        PausePlayer();
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
        sendPreparingStateToFragment(getBaseContext(), AUDIO_PAUSE_ACTION);
        mStateService = Statics.STATE_SERVICE.PAUSE;
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());

        if (mPlayer != null) {
            try {
                handlerAudioProgress.removeCallbacks(runnableAudioProgress);
                currentAudioPosition = mPlayer.getCurrentPosition();
                mPlayer.pause();
                updateToState(Statics.STATE_SERVICE.PAUSE, PlaybackStateCompat.ACTION_PLAY, PlaybackStateCompat.STATE_PAUSED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        unlockWiFi();
        unlockCPU();
    }


    private void NextOrBacKSuraPlayer(int plus) {
        int nextSura = suraAudio.SuraNumber + plus;
        if (!PublicMethods.getInstance().isAvailableFile(nextSura, selectedReader.getReaderTag())) {
            handleFileNotAvialable();
            return;
        }


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

    private void destroyPlayer() {
        if (mPlayer != null) {
            try {
                mPlayer.reset();
                mPlayer.release();
                handlerAudioProgress.removeCallbacks(runnableAudioProgress);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mPlayer = null;
            }
        }
        unlockWiFi();
        unlockCPU();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        String message = PublicMethods.getInstance().getErrorMessage(what);
        Log.e(TAG, "Player onError() what:" + message);
        destroyPlayer();
        updateToState(Statics.STATE_SERVICE.NOT_INIT, PlaybackStateCompat.ACTION_STOP, PlaybackStateCompat.STATE_STOPPED);
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        stopForeground(true);
        Log.e(TAG, " onError stopSelf();");

        stopSelf();
        sendErrorMessage(getBaseContext(), message);
        return false;
    }

    private void initPlayer() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setLooping(true);
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
        } else {
            mPlayer.reset();
        }

        lockWiFi();
        lockCPU();
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
                mPlayer.reset();
                mPlayer.setVolume(1.0f, 1.0f);

                Log.d(TAG, "player getting url or path for tag " + selectedReader.getReaderTag() + " id " + selectedReader.getId() + " is from local " + suraAudio.isFromLocal);
                String urlOrPath = publicMethods.getCorrectUrlOrPath(selectedReader.getId(), suraAudio.SuraNumber, suraAudio.isFromLocal, this);
                Log.d(TAG, "the started Url is " + urlOrPath);
                mPlayer.setDataSource(this, Uri.parse(urlOrPath));


                mPlayer.prepareAsync();

            } catch (Exception e) {
                Log.d(TAG, "error : " + e.getMessage());
                destroyPlayer();
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "Player onPrepared()");
        updateToState(Statics.STATE_SERVICE.PLAY, PlaybackStateCompat.ACTION_PAUSE, PlaybackStateCompat.STATE_PLAYING);
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        try {
            mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setPlaybackSpeed(audioReadingSpeed);
        }
        Log.e(TAG, "Duration current " + currentAudioPosition);

        mPlayer.seekTo(currentAudioPosition);
        mPlayer.start();

        if (mPlayer != null && (mStateService == Statics.STATE_SERVICE.PLAY || mStateService == Statics.STATE_SERVICE.PREPARE)) {
            handlerAudioProgress.postDelayed(runnableAudioProgress, 500);
            NotifyBroadcastHelper.sendAudioProgress(getBaseContext(), 0, mPlayer.getDuration());
            Log.e(TAG, "send current minute and max minute to fragment");
        } else
            Log.e(TAG, "we dont send current minute and max minute to fragment");
        /*
        NotifyBroadcastHelper.sendAudioProgress(getBaseContext(),0, mp.getDuration());
        //this used to  current second or minute of audio
        */

        mTimerUpdateHandler.postDelayed(mTimerUpdateRunnable, 0);
        sendPreparingStateToFragment(getBaseContext(), AUDIO_START_ACTION);


    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        sendPreparingStateToFragment(getBaseContext(), AUDIO_FINISHED_ACTION);
        Log.e(TAG, "we have to stop service");
        destroyPlayer();
        stopForeground(true);

        Log.e(TAG, " ononCompletion stopSelf();");

        stopSelf();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setPlaybackSpeed(float speed) {
        audioReadingSpeed = speed;
        Log.d(TAG, "Player setPlaybackSpeed() speed:" + speed);
        try {
            PlaybackParams params = mPlayer.getPlaybackParams();
            params.setSpeed(speed);
            mPlayer.setPlaybackParams(params);
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
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF, PAudioServiceNoSelection.class.getSimpleName());
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

    private Notification prepareNotification() {
        Log.d("notification_tag", "notification called ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.title_Books);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            Toast.makeText(this, "Channel  created " + FOREGROUND_CHANNEL_ID, Toast.LENGTH_SHORT).show();
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        if (suraAudio != null)
            notificationIntent.putExtra("page", suraAudio.SuraPage);

        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.drawable.ic_mashaf)
                .setContentTitle("سورة " + suraArabicName)
                .setContentText(selectedReader.getName())
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
                .setLargeIcon(bitmapIcon)
                .setSound(null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2, 3).setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
                        .setMediaSession(mediaSession.getSessionToken()));


        // Add next button
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_baseline_skip_previous_24, "Back",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));

        if (mStateService == Statics.STATE_SERVICE.PLAY) {
            builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)));

        } else {
            builder.addAction(new NotificationCompat.Action(R.drawable.ic_baseline_play_arrow_24, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY)));
        }

        builder.addAction(new NotificationCompat.Action(R.drawable.ic_baseline_skip_next_24, "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));


        return builder.build();

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    private void seekPlayer(Intent intent) {
        int seek = intent.getIntExtra("seek", 0);
        if (mPlayer != null && mPlayer.isPlaying()) mPlayer.seekTo(seek);
    }


}
