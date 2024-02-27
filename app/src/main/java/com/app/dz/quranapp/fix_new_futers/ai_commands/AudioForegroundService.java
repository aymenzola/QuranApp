package com.app.dz.quranapp.fix_new_futers.ai_commands;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.data.room.Entities.SuraAudio;
import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.QuranServices.PAudioServiceSelection;
import com.app.dz.quranapp.Util.CsvReader;
import com.app.dz.quranapp.data.room.Daos.AyaDao;
import com.app.dz.quranapp.data.room.MushafDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AudioForegroundService extends Service {

    private Sura currantSura;
    private static ReaderAudio selectedReader;
    private float audioPlayingSpeed = 1f;
    private static final String CHANNEL_ID = "AudioForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new LocalBinder();
    private SpeechRecognizer speechRecognizer;
    private int selectedAyaCountInSura = 1;
    private boolean isPlayFromLocal = false;
    private MediaPlayer mediaPlayer;

    public class LocalBinder extends Binder {
        AudioForegroundService getService() {
            return AudioForegroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        mediaPlayer = MediaPlayer.create(this, R.raw.ok);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
                // Send a broadcast to indicate that the service has started
                //Intent broadcastIntent = new Intent("com.example.app.ACTION_AUDIO_SERVICE_STARTED");
                //sendBroadcast(broadcastIntent);

                updateWidgetImage(R.id.image_start_listening, R.drawable.ic_micro);
                updateWidget("تحدث الأن ...", R.id.tv_state);
                updateNotification("تحدث الأن ...");
            }

            @Override
            public void onBeginningOfSpeech() {
                // Handle the beginning of speech
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Handle RMS changes
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Handle buffer received
            }

            @Override
            public void onEndOfSpeech() {
                updateWidgetImage(R.id.image_start_listening, R.drawable.bot);
                // Handle the end of speech
            }

            @Override
            public void onError(int error) {
                // Handle recognition errors
                updateWidget("Error " + getErrorMessage(error), R.id.tv_state);
                updateNotification("Error " + getErrorMessage(error));
            }

            @Override
            public void onResults(Bundle results) {
                List<String> resultList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (resultList != null && !resultList.isEmpty()) {
                    String result = resultList.get(0);
                    makeVollyCall(result);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Handle partial recognition results
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Handle speech recognition events
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "START_RECOGNITION".equals(intent.getAction())) {
            startRecognition();
        }
        startForeground(NOTIFICATION_ID, buildNotification("Service is running"));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecognition();
    }

    private void startRecognition() {
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar");
        speechRecognizer.startListening(recognizerIntent);
    }

    private void stopRecognition() {
        speechRecognizer.stopListening();
        speechRecognizer.cancel();
        speechRecognizer.destroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+
            // because the NotificationChannel class is new and not in the support library
            CharSequence name = "AudioForegroundServiceChannel";
            String description = "Channel for AudioForegroundService";
            int importance = android.app.NotificationManager.IMPORTANCE_LOW;
            android.app.NotificationChannel channel = new android.app.NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String contentText) {
        Intent notificationIntent = new Intent(this, AudioForegroundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent startRecognitionIntent = new Intent(this, AudioForegroundService.class);
        startRecognitionIntent.setAction("START_RECOGNITION");
        PendingIntent startRecognitionPendingIntent = PendingIntent.getService(this, 0, startRecognitionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("AudioForegroundService")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_headphones)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_play_arrow_24, "Start Recognition", startRecognitionPendingIntent)
                .build();
    }





    @SuppressLint("CheckResult")
    private void manageResultAndStartAudio(String suraName,String readerName,int readSpeed) {
        audioPlayingSpeed = getCorrectReadSpeed(readSpeed);
        MushafDatabase database = MushafDatabase.getInstance(this);
        AyaDao dao = database.getAyaDao();

        List<ReaderAudio> audioList = CsvReader.readReaderAudioListFromCsv(this, "audio.csv");


        if (!suraName.equals("No") && !readerName.equals("No")) {

            //both changed
            for (ReaderAudio audio : audioList) {
                if (audio.getName().contains(readerName) && (audio.getAudioType() == 1 || audio.getAudioType() == 3)) {
                    Log.e("steptag", "reader is " + audio.getName());
                    selectedReader = audio;
                    break;
                }
            }

            if (selectedReader == null) selectedReader = audioList.get(0);

            dao.getSuraName(suraName).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Sura -> {
                Log.e("steptag", "we receive sura" + Sura.getName() + " number " + Sura.getId() + " Ename " + Sura.getEname() + " getTname " + Sura.getTname());
                currantSura = Sura;

                String title = "جاري تشغيل " + suraName + " بصوت " + selectedReader.getName();

                updateWidget(title, R.id.tv_state);
                updateNotification(title);

                lunchAudio();
            }, e -> {
                Log.e("steptag", "0 data error   " + e.getMessage());
            });


        } else if (!readerName.equals("No")) {
            // only reader changed

            for (ReaderAudio audio : audioList) {
                if (audio.getName().contains(readerName) && (audio.getAudioType() == 1 || audio.getAudioType() == 3)) {
                    Log.e("steptag", "reader is " + audio.getName());
                    selectedReader = audio;
                    break;
                }
            }

            if (selectedReader == null) selectedReader = audioList.get(0);

            updateWidget("جاري تغيير القارئ " + suraName + selectedReader.getName(), R.id.tv_state);
            updateNotification(" " + suraName + " بصوت " + selectedReader.getName());
            lunchAudio();

        } else if (!suraName.equals("No")) {

            if (selectedReader == null) selectedReader = audioList.get(0);

            dao.getSuraName(suraName).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Sura -> {
                Log.e("steptag", "we receive sura" + Sura.getName() + " number " + Sura.getId() + " Ename " + Sura.getEname() + " getTname " + Sura.getTname());
                currantSura = Sura;

                updateWidget("جاري تشغيل " + suraName + " بصوت " + selectedReader.getName(), R.id.tv_state);
                updateNotification("جاري تشغيل " + suraName + " بصوت " + selectedReader.getName());

                lunchAudio();
            }, e -> {
                Log.e("steptag", "3 data error   " + e.getMessage());
            });

        }


    }

    private void makeVollyCall(String inputWord) {

        WitAiRequest witAiRequest = new WitAiRequest();
        witAiRequest.makeWitAiRequest(this, inputWord, new WitAiRequest.ListenersAudio() {
            @Override
            public void prepareCall() {
                mediaPlayer.start();

                updateWidget(inputWord, R.id.tv_state);
                updateNotification(inputWord);
            }

            @Override
            public void getResult(String suraName, String readerName, int readSpeed) {
                // Convert Unicode escaped string to Arabic text
                Log.e("steptag", "result value : " + "sura : " + suraName + " reader " + readerName+" read speed "+readSpeed);
                manageResultAndStartAudio(suraName,readerName,readSpeed);
                //binding.tvResult.setText(""+suraName);
            }

            @Override
            public void getError(String errorBody) {
                updateWidget(errorBody, R.id.tv_state);
                updateNotification(errorBody);
            }
        });

    }


    private float getCorrectReadSpeed(int readSpeed) {
        return switch (readSpeed) {
            case 2 -> 2f;
            case 3 -> 3f;
            case 4 -> 4f;
            case 5 -> 5f;
            default -> 1f;
        };
    }

    private void updateNotification(String contentText) {
        Notification notification = buildNotification(contentText);
        android.app.NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = getSystemService(android.app.NotificationManager.class);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void updateWidget(String title, int id) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        views.setTextViewText(id, title);
        // Update the widget
        AppWidgetManager.getInstance(AudioForegroundService.this).updateAppWidget(new ComponentName(AudioForegroundService.this, MyWidgetProvider.class), views);
    }

    private void updateWidgetImage(int imageViewId, int drawableId) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        views.setImageViewResource(imageViewId, drawableId);
        // Update the widget
        AppWidgetManager.getInstance(AudioForegroundService.this).updateAppWidget(
                new ComponentName(AudioForegroundService.this, MyWidgetProvider.class), views);
    }

    private String getErrorMessage(int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "خطأ في تسجيل الصوت";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "خطأ في الجانب العميل";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "صلاحيات غير كافية";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "خطأ في الشبكة";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "انتهت مهلة الشبكة";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "لم يتم العثور على تطابق";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "خدمة التعرف مشغولة";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "خطأ في الخادم";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "لا توجد مدخلات صوتية";
                break;
            default:
                errorMessage = "خطأ غير معروف";
                break;
        }
        return errorMessage;
    }

















    public void lunchAudio() {
        Intent startIntent = new Intent(this, PAudioServiceSelection.class);
        startIntent.setAction(Statics.ACTION.START_ACTION);

        SuraAudio suraAudio = new SuraAudio(String.valueOf(selectedReader.getId()),selectedAyaCountInSura,currantSura.getId(),isPlayFromLocal,
                selectedReader.isThereSelection());

        startIntent.putExtra("suraAudio",suraAudio);
        startIntent.putExtra("selectedReader",selectedReader);
        startIntent.putExtra("speed",audioPlayingSpeed);
        startService(startIntent);
    }



}
