package com.app.dz.quranapp.Services;

import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_FINISHED_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PAUSE_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PROGRESS_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_RESUME_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_SELECT_AYA_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_START_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_STOP_ACTION;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.Entities.AyaAudioLimits;
import com.app.dz.quranapp.Entities.AyaAudioLimitsFirebase;
import com.app.dz.quranapp.Entities.Sura;
import com.app.dz.quranapp.Entities.SuraAudio;
import com.app.dz.quranapp.Entities.SuraAudioFirebase;
import com.app.dz.quranapp.MushafParte.QuranActivity;
import com.app.dz.quranapp.PlayerAudioNotification.Statics;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.AyaAudioLimitDao;
import com.app.dz.quranapp.room.Daos.AyaDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.app.dz.quranapp.room.MushafDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;

public class ForegroundPlayAudioService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    DecimalFormat decimalFormat = new DecimalFormat("000");
    private final static String TAG = ForegroundPlayAudioService.class.getSimpleName();
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
    private int TimerCercle = 0;
    private int lastNotifiedSelectedAya = -1;
    private PublicMethods publicMethods;
    private QuranInfoManager quranInfoManager;
    private int suraStartPage = -1;
    private Bitmap bitmapIcon ;

    private Runnable mTimerUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
            mTimerUpdateHandler.postDelayed(this, Statics.DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE);
        }
    };
    private Runnable mDelayedShutdown = () -> {
        unlockWiFi();
        unlockCPU();
        stopForeground(true);
        stopSelf();
    };
    private int currentPosition = 0;
    private MediaSessionCompat mediaSession;
    private int count = 0;
    final Handler handler = new Handler();
    Timer timer = new Timer();
    private AyaDao dao;
    private AyaAudioLimitDao daoAppDB;
    private List<Aya> AyatList;
    private String suraArabicName;

    public ForegroundPlayAudioService() {
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
        Log.d(ForegroundPlayAudioService.class.getSimpleName(), "onCreate()");
        mediaSession = new MediaSessionCompat(ForegroundPlayAudioService.this, "tag");
        mStateService = Statics.STATE_SERVICE.NOT_INIT;
        publicMethods = PublicMethods.getInstance();
        quranInfoManager= QuranInfoManager.getInstance();
        bitmapIcon = BitmapFactory.decodeResource(getResources(),R.mipmap.icon);
        MushafDatabase database = MushafDatabase.getInstance(this);
        AppDatabase db = DatabaseClient.getInstance(ForegroundPlayAudioService.this).getAppDatabase();
        daoAppDB = db.getAyaAudioLimitsDao();
        dao = database.getAyaDao();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mUriRadio = mUriRadioDefault;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        switch (intent.getAction()) {
            case Statics.ACTION.START_ACTION:
                Log.e(TAG, "Received start Intent");
                mStateService = Statics.STATE_SERVICE.PREPARE;
                SuraAudio suraAudio = new SuraAudio();
                suraAudio.SuraNumber = (int) intent.getSerializableExtra("SuraNumber");
                suraAudio.startAya = (int) intent.getSerializableExtra("startAya");
                suraAudio.readerName = (String) intent.getSerializableExtra("readerName");
                suraAudio.isFromLocal = (Boolean) intent.getSerializableExtra("isFromLocal");
                suraAudio.isThereSelection = (Boolean) intent.getSerializableExtra("isThereSelection");
                this.suraAudio = suraAudio;
                suraArabicName = quranInfoManager.getSuraName(suraAudio.SuraNumber-1);
                new Thread(() -> bitmapIcon = getReaderBitmap(suraAudio.readerName)).start();
                initializeSuraPage(suraAudio.SuraNumber);
                toldTheActivty(AUDIO_PROGRESS_ACTION, currantAudio);
                if (suraAudio.isThereSelection) getSuraFromId(suraAudio);
                else prepareSuraAudio(suraAudio);
                break;

            case Statics.ACTION.PAUSE_ACTION:
                toldTheActivty(AUDIO_PAUSE_ACTION, currantAudio);
                mStateService = Statics.STATE_SERVICE.PAUSE;
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                Log.i(TAG, "Clicked Pause");
                PausePlayer();
                //TODO PAUSE mHandler.postDelayed(mDelayedShutdown, Statics.DELAY_SHUTDOWN_FOREGROUND_SERVICE);
                break;

            case Statics.ACTION.BACK_AYA_ACTION:
                BackAyaPlayer();
                break;
            case Statics.ACTION.NEXT_AYA_ACTION:
                NextAyaPlayer();
                break;
            case Statics.ACTION.NEXT_SURA_ACTION:
                NextOrBacKSuraPlayer(1);
                break;
            case Statics.ACTION.BACK_SURA_ACTION:
                NextOrBacKSuraPlayer(-1);
                break;

            case Statics.ACTION.PLAY_ACTION:
                toldTheActivty(AUDIO_RESUME_ACTION, currantAudio);
                mStateService = Statics.STATE_SERVICE.PREPARE;
                mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                Log.i(TAG, "Clicked Play");
                destroyPlayer();
                initPlayer();
                play();
                break;

            case Statics.ACTION.STOP_ACTION:
                Log.i(TAG, "Received Stop Intent");
                if (this.suraAudio != null)
                    toldTheActivty(AUDIO_STOP_ACTION, currantAudio);
                destroyPlayer();
                stopForeground(true);
                stopSelf();
                break;

            default:
                stopForeground(true);
                stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void initializeSuraPage(int SuraNumber) {
        new Thread(() -> {
            Aya aya = dao.getFirstAyaInSura(SuraNumber);
            suraStartPage = aya.getPage();
        }).start();
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
            int count = daoAppDB.getSuraAyatLimitsCount(suraAudio.SuraNumber, suraAudio.readerName);
            Log.e(TAG, "getSuraLimits count : "+count);
            if ((!suraAudio.isFromLocal && count != sura.getAyas()) || count == 0) {

                Log.e(TAG, "we are getting limits from room count : " + count + " ayat count " + sura.getAyas()
                        + " isfromlocal " + suraAudio.isFromLocal + " suranumber " + suraAudio.SuraNumber + " readername " + suraAudio.readerName);
                getAyatFromFireBase(suraAudio, sura);
            } else getAyatLimitsFromRoom(suraAudio);
        }).start();


    }

    private void getAyatLimitsFromRoom(SuraAudio suraAudio) {
        new Thread(() -> {
            suraAudio.ayaAudioList = daoAppDB.getSuraAyatLimitsWithId(suraAudio.SuraNumber, suraAudio.readerName);
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
                currentPosition = mPlayer.getCurrentPosition();
                if (suraAudio.isThereSelection) cancelTimer();
                mPlayer.reset();
                mPlayer.release();
                Log.e(TAG, "Player Paused with position " + currentPosition + " and currant audio " + currantAudio);
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
                currentPosition = mPlayer.getCurrentPosition();
                cancelTimer();
                mPlayer.reset();
                mPlayer.release();
                Log.e(TAG, "Player Paused with position " + currentPosition + " and currant audio " + currantAudio);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mPlayer = null;
            }
        }
        unlockWiFi();
        unlockCPU();
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
                currentPosition = (int) suraAudio.ayaAudioList.get(TimerCercle - 1).startAyaTime;
                cancelTimer();
                mPlayer.seekTo(currentPosition);
                TimerCercle = TimerCercle - 1;
                setTimerCountDownTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        currentPosition = 0;
        TimerCercle = 0;
        cancelTimer();

        SuraAudio suraAudiolocal = new SuraAudio();
        suraAudiolocal.SuraNumber = suraAudio.SuraNumber + plus;
        suraAudiolocal.startAya = 1;
        suraAudiolocal.readerName = suraAudio.readerName;
        suraAudiolocal.isThereSelection = publicMethods.isReaderSelectionAvailable(suraAudio.readerName);
        suraArabicName = quranInfoManager.getSuraName(suraAudiolocal.SuraNumber-1);
        File file = publicMethods.getSuraFile(suraAudio.readerName, suraAudiolocal.SuraNumber);
        Log.e(TAG, "is file exist " + file.exists() + " path " + file.getPath());
        suraAudiolocal.isFromLocal = file.exists() && file.canRead();

        this.suraAudio = suraAudiolocal;
        initializeSuraPage(suraAudio.SuraNumber);
        if (suraAudio.isThereSelection) getSuraFromId(suraAudio);
        else prepareSuraAudio(suraAudio);
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
                currentPosition = (int) suraAudio.ayaAudioList.get(TimerCercle + 1).startAyaTime;
                cancelTimer();
                mPlayer.seekTo(currentPosition);
                TimerCercle = TimerCercle + 1;
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
        mHandler.postDelayed(mDelayedShutdown, Statics.DELAY_SHUTDOWN_FOREGROUND_SERVICE);
        mNotificationManager.notify(Statics.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        mStateService = Statics.STATE_SERVICE.PAUSE;
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
                Log.d(TAG, "played " + currantAudio);
                mPlayer.setDataSource(this, Uri.parse(publicMethods.getCorrectUrlOrPath(suraAudio.readerName, suraAudio.SuraNumber, suraAudio.isFromLocal)));
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


        Log.e(TAG, "currant position " + currentPosition + " start audio " + suraAudio.startAya);
        boolean isResume = currentPosition != 0;
        if (suraAudio.isThereSelection) {
            if (currentPosition == 0 && suraAudio.startAya != 1) {
                TimerCercle = suraAudio.startAya - 1;
                currentPosition = (int) suraAudio.ayaAudioList.get(TimerCercle).startAyaTime;
            }
        }


        mPlayer.seekTo(currentPosition);
        mPlayer.start();


        Log.e(TAG, "currant position : " + currentPosition);
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
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "Player onBufferingUpdate():" + percent);
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
                mWiFiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(
                        WifiManager.WIFI_MODE_FULL_HIGH_PERF, ForegroundPlayAudioService.class.getSimpleName());
            } else {
                mWiFiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(
                        WifiManager.WIFI_MODE_FULL, ForegroundPlayAudioService.class.getSimpleName());
            }
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

    public void setTimerCountDownTimer() {
        Log.e(TAG, "we set the timer " + TimerCercle + " sura size " + suraAudio.ayaAudioList.size());
        if (TimerCercle >= suraAudio.ayaAudioList.size()) return;

        long triggerAtMillis;

        if (TimerCercle == 0)
            triggerAtMillis = suraAudio.ayaAudioList.get(TimerCercle).endAyaTime;
        else {
            triggerAtMillis = suraAudio.ayaAudioList.get(TimerCercle).endAyaTime - suraAudio.ayaAudioList.get(TimerCercle).startAyaTime;
        }
        toldTheActivty(AUDIO_SELECT_AYA_ACTION, TimerCercle + 1);
        countDownTimer = new CountDownTimer(triggerAtMillis + 250, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                TimerCercle = TimerCercle + 1;
                Log.e(TAG, "timer run " + TimerCercle);

                if (TimerCercle >= suraAudio.ayaAudioList.size()) return;
                setTimerCountDownTimer();
            }
        }.start();
    }

    public void cancelTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
    }

    public void ResumeTimer() {
        Log.e(TAG, "we resume the timer");
        toldTheActivty(AUDIO_SELECT_AYA_ACTION, TimerCercle + 1);
        countDownTimer = new CountDownTimer(timeRemaining + 250, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                TimerCercle = TimerCercle + 1;
                Log.e(TAG, "timer run " + TimerCercle);

                if (TimerCercle >= suraAudio.ayaAudioList.size()) return;

                setTimerCountDownTimer();
            }
        }.start();
    }

    private void getAyatFromFireBase(SuraAudio suraAudio, Sura sura) {

        String reader = publicMethods.getReaderTag(suraAudio.readerName);

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
                                String RoomId = id + "_" + publicMethods.getReaderTag(suraAudio.readerName);

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
        Intent notificationIntent = new Intent(this, QuranActivity.class);
        if (suraStartPage != -1)
            notificationIntent.putExtra("page", suraStartPage);
        /*
        notificationIntent.setAction(Statics.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lPauseIntent = new Intent(this, ForegroundPlayAudioService.class);
        lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent playIntent = new Intent(this, ForegroundPlayAudioService.class);
        playIntent.setAction(Statics.ACTION.PLAY_ACTION);
        PendingIntent lPendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lStopIntent = new Intent(this, ForegroundPlayAudioService.class);
        lStopIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingStopIntent = PendingIntent.getService(this, 0, lStopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lNextSuraIntent = new Intent(this, ForegroundPlayAudioService.class);
        lNextSuraIntent.setAction(Statics.ACTION.NEXT_SURA_ACTION);
        PendingIntent lPendingNextIntent = PendingIntent.getService(this, 0, lNextSuraIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent lBackIntent = new Intent(this, ForegroundPlayAudioService.class);
        lBackIntent.setAction(Statics.ACTION.BACK_SURA_ACTION);
        PendingIntent lPendingBackIntent = PendingIntent.getService(this, 0, lBackIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);





        androidx.core.app.NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new androidx.core.app.NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            builder = new androidx.core.app.NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.drawable.ic_mashaf)
                .setContentTitle("سورة " +suraArabicName)
                .setContentText(suraAudio.readerName)
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

    public Bitmap getReaderBitmap(String readerName) {
        switch (readerName) {
            case "Alafasy":
                return BitmapFactory.decodeResource(getResources(),R.drawable.alafasy);
            case "Shuraym":
                return BitmapFactory.decodeResource(getResources(),R.drawable.sharum);
            case "Sudais":
                return BitmapFactory.decodeResource(getResources(),R.drawable.sudais);
            case "Mohammad_al_Tablaway_128kbps":
                return BitmapFactory.decodeResource(getResources(),R.drawable.khalil_hosary);
            default:
                return BitmapFactory.decodeResource(getResources(),R.drawable.abd_baset);
        }
    }



}
