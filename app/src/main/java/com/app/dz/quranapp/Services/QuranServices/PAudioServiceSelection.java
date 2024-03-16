package com.app.dz.quranapp.Services.QuranServices;

import static com.app.dz.quranapp.Communs.Statics.ACTION.CHANGE_READER_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_FINISHED_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_NOT_AVAILABLE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PAUSE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PLAYING_PROGRESS_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PREPARING_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PROGRESS_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_RESUME_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_SELECT_AYA_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_START_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_STOP_ACTION;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.RequiresApi;

import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.AyaAudioLimitDao;
import com.app.dz.quranapp.data.room.Daos.AyaDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaAudioLimits;
import com.app.dz.quranapp.data.room.Entities.AyaAudioLimitsFirebase;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.data.room.Entities.SuraAudio;
import com.app.dz.quranapp.data.room.Entities.SuraAudioFirebase;
import com.app.dz.quranapp.data.room.MushafDatabase;
import com.app.dz.quranapp.quran.models.ReaderAudio;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

public class PAudioServiceSelection extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    private ReaderAudio selectedReader;
    DecimalFormat decimalFormat = new DecimalFormat("000");
    private final static String TAG = PAudioServiceSelection.class.getSimpleName();
    static private int mStateService = Statics.STATE_SERVICE.NOT_INIT;
    private final Uri mUriRadioDefault = Uri.parse("audio.mp3");
    private final Object mLock = new Object();
    private final Handler mHandler = new Handler();
    private MediaPlayer mPlayer;
    private Uri mUriRadio;
    private NotificationManager mNotificationManager;
    private WifiManager.WifiLock mWiFiLock;
    private PowerManager.WakeLock mWakeLock;
    private Handler mTimerUpdateHandler = new Handler();
    private SuraAudio suraAudio;
    private int currantAudio = 1;
    private CountDownTimer countDownTimer;
    private long timeRemaining = 0;
    private int currentSelectedAyaNumber = 0;
    private int lastNotifiedSelectedAya = -1;
    private PublicMethods publicMethods;
    private QuranInfoManager quranInfoManager;
    private Bitmap bitmapIcon;

    private float audioReadingSpeed = 1f;

    private Runnable mTimerUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
            mTimerUpdateHandler.postDelayed(this, Statics.DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE);
        }
    };


    private int currentAudioPosition = 0;
    private MediaSessionCompat mediaSession;
    private AyaDao dao;
    private AyaAudioLimitDao daoAppDB;
    private List<Aya> AyatList;
    private String suraArabicName;
    private final Handler handlerAudioProgress = new Handler();
    ;

    public PAudioServiceSelection() {
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
        Log.d(PAudioServiceSelection.class.getSimpleName(), "onCreate()");

        mediaSession = new MediaSessionCompat(this, "MyMediaSession");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

      //  mediaSession.setCallback(new PAudioServiceSelection.MySessionCallback());
        mediaSession.setActive(true);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        PlaybackStateCompat state = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .build();
        mediaSession.setPlaybackState(state);



        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        publicMethods = PublicMethods.getInstance();
        quranInfoManager = QuranInfoManager.getInstance();
        bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        MushafDatabase database = MushafDatabase.getInstance(this);
        AppDatabase db = DatabaseClient.getInstance(PAudioServiceSelection.this).getAppDatabase();
        daoAppDB = db.getAyaAudioLimitsDao();
        dao = database.getAyaDao();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mUriRadio = mUriRadioDefault;


    }


    /*private class MySessionCallback extends MediaSessionCompat.Callback {
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
    */

    Runnable runnableAudioProgress = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null) {
                sendAudioProgress(mPlayer.getCurrentPosition(), mPlayer.getDuration());
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
                mStateService = Statics.STATE_SERVICE.PREPARE;
                this.suraAudio = (SuraAudio) intent.getSerializableExtra("suraAudio");
                this.selectedReader = (ReaderAudio) intent.getSerializableExtra("selectedReader");
                this.audioReadingSpeed = intent.getFloatExtra("speed", 1f);

                if (!PublicMethods.getInstance().isAvailableFile(suraAudio.SuraNumber, selectedReader.getReaderTag())) {
                    //send to fragment that the sura is not available
                    if (this.suraAudio != null) NotifyBroadcastHelper.sendPreparingStateToFragment(getBaseContext(),AUDIO_NOT_AVAILABLE_ACTION);
                    destroyPlayer();
                    stopForeground(true);
                    stopSelf();

                } else {
                    //avialbe reader and suran file
                    Log.e(TAG, "Received start Intent " + selectedReader);

                    //getting sura arabic name and reader image
                    suraArabicName = quranInfoManager.getSuraName(suraAudio.SuraNumber - 1);
                    new Thread(() -> bitmapIcon = getReaderBitmap()).start();
                    toldTheActivty(AUDIO_PROGRESS_ACTION, currantAudio);
                    sendPreparingStateToFragment(AUDIO_PREPARING_ACTION);

                    getSuraFromId(suraAudio);
                }
            }
            case Statics.ACTION.PAUSE_ACTION -> {
                toldTheActivty(AUDIO_PAUSE_ACTION, currantAudio);
                mStateService = Statics.STATE_SERVICE.PAUSE;
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                PausePlayer();
            }
            //TODO PAUSE mHandler.postDelayed(mDelayedShutdown, Statics.DELAY_SHUTDOWN_FOREGROUND_SERVICE);
            case Statics.ACTION.BACK_AYA_ACTION -> BackAyaPlayer();
            case Statics.ACTION.NEXT_AYA_ACTION -> NextAyaPlayer();
            case Statics.ACTION.NEXT_SURA_ACTION -> NextOrBacKSuraPlayer(1);
            case Statics.ACTION.BACK_SURA_ACTION -> NextOrBacKSuraPlayer(-1);
            case Statics.ACTION.SEEK_ACTION -> seekPlayer(intent);

            case Statics.ACTION.PLAY_ACTION -> {
                toldTheActivty(AUDIO_RESUME_ACTION, currantAudio);
                mStateService = Statics.STATE_SERVICE.PREPARE;
                this.audioReadingSpeed = intent.getFloatExtra("speed", 1f);
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                Log.i(TAG, "Clicked Play");
                destroyPlayer();
                initPlayer();
                play();
            }
            case Statics.ACTION.STOP_ACTION -> {
                Log.i(TAG, "Received Stop Intent");
                if (this.suraAudio != null)
                    toldTheActivty(AUDIO_STOP_ACTION, currantAudio);

                destroyPlayer();
                stopForeground(true);
                stopSelf();
            }
            case Statics.ACTION.CHANGE_SPEED_ACTION -> {
                Log.i(TAG, "Received change speed Intent");
                this.audioReadingSpeed = intent.getFloatExtra("speed", 1f);
                PausePlayer();
                initPlayer();
                play();
            }
            case CHANGE_READER_ACTION -> {
                Log.i(TAG, "Received change reader Intent");
                destroyPlayer();
                stopForeground(true);
                stopSelf();
                sendPreparingStateToFragment(CHANGE_READER_ACTION);
            }
            default -> {
                stopForeground(true);
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }


    private void seekPlayer(Intent intent) {
        int seek = intent.getIntExtra("seek", 0);
        if (mPlayer != null && mPlayer.isPlaying()) mPlayer.seekTo(seek);
    }


    @SuppressLint("CheckResult")
    private void getSuraFromId(SuraAudio suraAudio) {

        new Thread(() -> {
            AyatList = dao.getAyatWithSuraId(suraAudio.SuraNumber);
            Sura sura = dao.getSuraWithIdNoObserver(suraAudio.SuraNumber);
            Log.e(TAG, "we recieve sura " + sura.getName());
            getSuraLimits(suraAudio, sura);
        }).start();

    }

    @SuppressLint("CheckResult")
    private void getSuraLimits(SuraAudio suraAudio, Sura sura) {

        new Thread(() -> {
            int count = daoAppDB.getSuraAyatLimitsCount(suraAudio.SuraNumber, selectedReader.getReaderTag());
            Log.e(TAG, "getSuraLimits count : " + count);
            if ((!suraAudio.isFromLocal && count != sura.getAyas()) || count == 0) {

                Log.e(TAG, "we are getting limits from room count : " + count + " ayat count " + sura.getAyas()
                        + " isfromlocal " + suraAudio.isFromLocal + " suranumber " + suraAudio.SuraNumber + " readername " + selectedReader.getReaderTag());
                getAyatFromFireBase(suraAudio, sura);
            } else getAyatLimitsFromRoom(suraAudio);
        }).start();


    }

    private void getAyatLimitsFromRoom(SuraAudio suraAudio) {
        new Thread(() -> {
            suraAudio.ayaAudioList = daoAppDB.getSuraAyatLimitsWithId(suraAudio.SuraNumber, selectedReader.getReaderTag());
            Log.e(TAG, "sura audio limits size : " + suraAudio.ayaAudioList.size());
            prepareSuraAudio(suraAudio);
        }).start();
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
        Log.d(TAG, "onDestroy()");
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
                currentAudioPosition = mPlayer.getCurrentPosition();
                if (suraAudio.isThereSelection) cancelTimer();
                mPlayer.reset();
                mPlayer.release();
                Log.e(TAG, "Player Paused with position " + currentAudioPosition + " and currant audio " + currantAudio);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mPlayer = null;
            }
        }
        unlockWiFi();
        unlockCPU();
    }

    private void PausePlayerSelection() {
        if (mPlayer != null) {
            try {
                toldTheActivty(AUDIO_PAUSE_ACTION, currantAudio);
                currentAudioPosition = mPlayer.getCurrentPosition();
                cancelTimer();
                mPlayer.reset();
                mPlayer.release();
                Log.e(TAG, "Player Paused with position " + currentAudioPosition + " and currant audio " + currantAudio);
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
            Log.e(TAG, "wait i am preaparing the next sura ");
            return;
        }
        //check if we are in first or in last sura if that dont do anything
        if (suraAudio.SuraNumber == 1 && plus == -1 || suraAudio.SuraNumber == 114 && plus == 1)
            return;
        mStateService = Statics.STATE_SERVICE.PREPARE;
        destroyPlayer();
        initPlayer();
        currentAudioPosition = 0;
        currentSelectedAyaNumber = 0;
        cancelTimer();

        SuraAudio suraAudiolocal = new SuraAudio();
        suraAudiolocal.SuraNumber = suraAudio.SuraNumber + plus;
        suraAudiolocal.startAya = 1;
        suraAudiolocal.readerName = suraAudio.readerName;
        suraAudiolocal.isThereSelection = selectedReader.isThereSelection();
        suraArabicName = quranInfoManager.getSuraName(suraAudiolocal.SuraNumber - 1);
        File file = publicMethods.getFile(this, selectedReader.getReaderTag(), suraAudiolocal.SuraNumber);
        Log.e(TAG, "is file exist " + file.exists() + " path " + file.getPath());
        suraAudiolocal.isFromLocal = file.exists() && file.canRead();

        this.suraAudio = suraAudiolocal;
        if (suraAudio.isThereSelection) getSuraFromId(suraAudio);
        else prepareSuraAudio(suraAudio);
    }


    private void BackAyaPlayer() {
        if (mPlayer != null) {
            try {
                //If there no selection
                if (!suraAudio.isThereSelection) {
                    int currantp = mPlayer.getCurrentPosition();
                    if (currantp >= 5000) {
                        mPlayer.seekTo(currantp - 5000);
                        return;
                    } else {
                        mPlayer.seekTo(0);
                        return;
                    }
                }


                //selection case
                currentAudioPosition = (int) suraAudio.ayaAudioList.get(currentSelectedAyaNumber - 1).startAyaTime;
                cancelTimer();
                mPlayer.seekTo(currentAudioPosition);
                currentSelectedAyaNumber = currentSelectedAyaNumber - 1;
                setTimerCountDownTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void NextAyaPlayer() {
        if (mPlayer != null) {
            try {
                //If there no selection
                if (!suraAudio.isThereSelection) {
                    int currantp = mPlayer.getCurrentPosition();
                    if (currantp + 5000 < mPlayer.getDuration()) {
                        mPlayer.seekTo(currantp + 5000);
                        return;
                    } else {
                        return;
                    }
                }


                //selection case
                currentAudioPosition = (int) suraAudio.ayaAudioList.get(currentSelectedAyaNumber + 1).startAyaTime;
                cancelTimer();
                mPlayer.seekTo(currentAudioPosition);
                currentSelectedAyaNumber = currentSelectedAyaNumber + 1;
                setTimerCountDownTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void destroyPlayer() {
        if (mPlayer != null) {
            try {
                if (suraAudio.isThereSelection) cancelTimer();
                mPlayer.reset();
                mPlayer.release();
                handlerAudioProgress.removeCallbacks(runnableAudioProgress);
                Log.d(TAG, "Player destroyed");
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
        Log.d(TAG, "Player onError() what:" + what);
        destroyPlayer();
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        sendPreparingStateToFragment(AUDIO_ERROR_ACTION);
        stopForeground(true);
        stopSelf();
        return false;
    }

    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setLooping(true);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnInfoListener((mp, what, extra) -> {
            Log.d(TAG, "Player onInfo(), what:" + what + ", extra:" + extra);
            return false;
        });
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
                //TODO
                mPlayer.reset();
                mPlayer.setVolume(1.0f, 1.0f);

                Log.d(TAG, "played getting url or path ");
                String urlOrPath = publicMethods.getCorrectUrlOrPath(selectedReader.getId(), suraAudio.SuraNumber, suraAudio.isFromLocal, this);

                mPlayer.setDataSource(this, Uri.parse(urlOrPath));

                Log.d(TAG, "the started Url is " + urlOrPath);
                mPlayer.prepareAsync();

            } catch (Exception e) {
                destroyPlayer();
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "Player onPrepared()");
        mStateService = Statics.STATE_SERVICE.PLAY;
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        try {
            mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setPlaybackSpeed(audioReadingSpeed);
        }

        Log.e(TAG, "currant position " + currentAudioPosition + " start audio " + suraAudio.startAya);
        boolean isResume = currentAudioPosition != 0;
        if (suraAudio.isThereSelection) {
            if (currentAudioPosition == 0 && suraAudio.startAya != 1) {
                currentSelectedAyaNumber = suraAudio.startAya - 1;
                currentAudioPosition = (int) suraAudio.ayaAudioList.get(currentSelectedAyaNumber).startAyaTime;
                Log.e(TAG, ">>>>> get aya start time" + currentSelectedAyaNumber + " start time is " + currentAudioPosition);
            }
        }


        mPlayer.seekTo(currentAudioPosition);
        mPlayer.start();

        Log.e(TAG, "call sendAudioProgress in onPrepared");

        sendAudioProgress(0, mp.getDuration());
        handlerAudioProgress.postDelayed(runnableAudioProgress, 500);

        Log.e(TAG, "currant position : " + currentAudioPosition);
        mTimerUpdateHandler.postDelayed(mTimerUpdateRunnable, 0);
        if (!suraAudio.isThereSelection) {
            toldTheActivty(AUDIO_START_ACTION, currantAudio);
            return;
        }


        if (!isResume) {
            new Thread(() -> {
                Looper.prepare();
                toldTheActivty(AUDIO_START_ACTION, currantAudio);
                Log.e(TAG, "we call start timer ");
                setTimerCountDownTimer();
                Looper.loop();
            }).start();

        } else new Thread(() -> {
            Looper.prepare();
            Log.e(TAG, "we call resume timer ");
            ResumeTimer();
            Looper.loop();
        }).start();

    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        toldTheActivty(AUDIO_FINISHED_ACTION, -1);
        Log.e(TAG, "we have to stop service");
        destroyPlayer();
        stopForeground(true);
        stopSelf();
    }

    private void toldTheActivty(String audioFinishedAction, int currantAya) {
        Log.e(TAG, "action " + audioFinishedAction + " currant aya " + currantAya);
        if (suraAudio.isThereSelection && !audioFinishedAction.equals(AUDIO_PROGRESS_ACTION)) {
            if (audioFinishedAction.equals(AUDIO_SELECT_AYA_ACTION) && lastNotifiedSelectedAya == currantAya)
                return;
            Intent intent2 = new Intent("AUDIO_FINISHED");
            intent2.putExtra("action", audioFinishedAction);
            intent2.putExtra("aya", currantAya);
            if (currantAya - 1 < AyatList.size() && audioFinishedAction.equals(AUDIO_SELECT_AYA_ACTION))
                intent2.putExtra("selctedaya", AyatList.get(currantAya - 1));
            sendBroadcast(intent2);
            lastNotifiedSelectedAya = currantAya;
        } else {
            Intent intent2 = new Intent("AUDIO_FINISHED");
            intent2.putExtra("action", audioFinishedAction);
            sendBroadcast(intent2);
        }
    }

    private void sendAudioProgress(int progress, int maxProgress) {
        Intent intent2 = new Intent("AUDIO_FINISHED");
        intent2.putExtra("action", AUDIO_PLAYING_PROGRESS_ACTION);
        intent2.putExtra("progress", progress);
        intent2.putExtra("maxProgress", maxProgress);
        sendBroadcast(intent2);
    }

    private void sendPreparingStateToFragment(String state) {
        Log.i(TAG, "tell the fragemnt that service is " + state);
        Intent intent = new Intent("AUDIO_FINISHED");
        intent.putExtra("action", state);
        sendBroadcast(intent);
    }

    public void setTimerCountDownTimer() {
        Log.e(TAG, "we set the timer " + currentSelectedAyaNumber + " sura size " + suraAudio.ayaAudioList.size());
        if (currentSelectedAyaNumber >= suraAudio.ayaAudioList.size()) return;

        long ayaDurationInMillis;

        //get aya duration
        if (currentSelectedAyaNumber == 0) //the first aya in sura case
            ayaDurationInMillis = suraAudio.ayaAudioList.get(currentSelectedAyaNumber).endAyaTime;
        else {
            ayaDurationInMillis = suraAudio.ayaAudioList.get(currentSelectedAyaNumber).endAyaTime - suraAudio.ayaAudioList.get(currentSelectedAyaNumber).startAyaTime;
        }

        //notify the activity
        toldTheActivty(AUDIO_SELECT_AYA_ACTION, currentSelectedAyaNumber + 1);

        //timer to know when aya audio finished

        long DurationTimesSpeed = (long) ((ayaDurationInMillis + 250) / audioReadingSpeed);

        Log.d(TAG, "setting timer aya duration " + ayaDurationInMillis + " aya with speed " + audioReadingSpeed + " DurationTimesSpeed " + DurationTimesSpeed);

        countDownTimer = new CountDownTimer(DurationTimesSpeed, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                //we need remaining time for to resume the aya from the time that paused at
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                //set next aya number
                currentSelectedAyaNumber = currentSelectedAyaNumber + 1;
                Log.e(TAG, "timer aya playing finished next is " + currentSelectedAyaNumber);

                if (currentSelectedAyaNumber >= suraAudio.ayaAudioList.size()) return;
                setTimerCountDownTimer();
            }
        }.start();

    }

    public void cancelTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
    }

    public void ResumeTimer() {
        Log.e(TAG, "we resume the timer");
        toldTheActivty(AUDIO_SELECT_AYA_ACTION, currentSelectedAyaNumber + 1);
        countDownTimer = new CountDownTimer(timeRemaining + 250, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                currentSelectedAyaNumber = currentSelectedAyaNumber + 1;
                Log.e(TAG, "timer run " + currentSelectedAyaNumber);

                if (currentSelectedAyaNumber >= suraAudio.ayaAudioList.size()) return;

                setTimerCountDownTimer();
            }
        }.start();
    }


    private void getAyatFromFireBase(SuraAudio suraAudio, Sura sura) {

        String reader = selectedReader.getReaderTag();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("suraaudios")
                .document(reader).collection(reader + "Audio").document(String.valueOf(suraAudio.SuraNumber));

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        SuraAudioFirebase suraAudioFirebase = documentSnapshot.toObject(SuraAudioFirebase.class);
                        List<AyaAudioLimitsFirebase> ayaAudioLimitsFirebaseList = suraAudioFirebase.ayaAudioList;


                        new Thread(() -> {
                            for (AyaAudioLimitsFirebase ayaAudioLimitsFirebase : ayaAudioLimitsFirebaseList) {

                                String id = decimalFormat.format(suraAudio.SuraNumber) + "" + decimalFormat.format(ayaAudioLimitsFirebase.suraAya);
                                String RoomId = id + "_" + selectedReader.getReaderTag();

                                AyaAudioLimits ayaAudioLimits = new AyaAudioLimits(RoomId, suraAudio.SuraNumber, ayaAudioLimitsFirebase.suraAya,
                                        ayaAudioLimitsFirebase.startAyaTime, ayaAudioLimitsFirebase.endAyaTime, reader);

                                suraAudio.ayaAudioList.add(ayaAudioLimits);

                                try {
                                    daoAppDB.insert(ayaAudioLimits);
                                    Log.d(TAG, "insert in local db");
                                } catch (Exception e) {
                                    Log.d(TAG, "1 error " + e.getMessage());
                                }
                            }
                            prepareSuraAudio(suraAudio);

                        }).start();


                    } else {
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "get failed with " + e));

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    public class HeadsetButtonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "user clicked on headsetButton 1");
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
                    Log.e(TAG, "user clicked on headsetButton");
                    // Headphone button pressed, pause media player
                    // Get reference to your media player and call pause method
                }
            }
        }
    }


    private Notification prepareNotification() {
        Log.d("notification_tag", "notification called ");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
                mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.title_Books);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("page", suraAudio.SuraPage);

        /*
        notificationIntent.setAction(Statics.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lPauseIntent = new Intent(this, PAudioServiceSelection.class);
        lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent playIntent = new Intent(this, PAudioServiceSelection.class);
        playIntent.setAction(Statics.ACTION.PLAY_ACTION);
        PendingIntent lPendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lStopIntent = new Intent(this, PAudioServiceSelection.class);
        lStopIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingStopIntent = PendingIntent.getService(this, 0, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lNextSuraIntent = new Intent(this, PAudioServiceSelection.class);
        lNextSuraIntent.setAction(Statics.ACTION.NEXT_SURA_ACTION);
        PendingIntent lPendingNextIntent = PendingIntent.getService(this, 0, lNextSuraIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lBackIntent = new Intent(this, PAudioServiceSelection.class);
        lBackIntent.setAction(Statics.ACTION.BACK_SURA_ACTION);
        PendingIntent lPendingBackIntent = PendingIntent.getService(this, 0, lBackIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);


        androidx.core.app.NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new androidx.core.app.NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            builder = new androidx.core.app.NotificationCompat.Builder(this);
        }

        /*updateWidgetImage(R.id.image_reader, bitmapIcon);
        updateWidget("", R.id.tv_state);
        updateWidget("سورة " + suraArabicName, R.id.tv_sura_name);
        updateWidget(selectedReader.getName(), R.id.tv_reader_name);
*/
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


        switch (mStateService) {
            case Statics.STATE_SERVICE.PLAY:
                builder.addAction(R.drawable.ic_baseline_pause_24, "pause", lPendingPauseIntent);
                break;
            default:
                builder.addAction(R.drawable.ic_baseline_play_arrow_24, "Play", lPendingPlayIntent);
                break;
        }


        builder.addAction(R.drawable.ic_baseline_skip_next_24, "Next", lPendingNextIntent)
                .addAction(R.drawable.ic_baseline_close_24, "close", lPendingStopIntent);
        builder.setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC);

        return builder.build();

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
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF, PAudioServiceSelection.class.getSimpleName());
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

    /*private void updateWidget(String title, int id) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        views.setTextViewText(id, title);
        // Update the widget
        AppWidgetManager.getInstance(PAudioServiceSelection.this).updateAppWidget(
                new ComponentName(PAudioServiceSelection.this, MyWidgetProvider.class), views);
    }
    private void updateWidgetImage(int imageViewId, Bitmap bitmapIcon) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        views.setImageViewBitmap(imageViewId, bitmapIcon);
        // Update the widget
        AppWidgetManager.getInstance(PAudioServiceSelection.this).updateAppWidget(
                new ComponentName(PAudioServiceSelection.this, MyWidgetProvider.class), views);
    }

    */

}
