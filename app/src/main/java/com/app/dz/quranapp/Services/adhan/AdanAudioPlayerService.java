package com.app.dz.quranapp.Services.adhan;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.app.dz.quranapp.R;

import java.io.IOException;

public class AdanAudioPlayerService extends Service {
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_STOP = "ACTION_STOP";

    private MediaPlayer mediaPlayer = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clearMediaPlayer();

        if (intent != null && ACTION_PLAY.equals(intent.getAction())) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(this,R.raw.adan1);
            mediaPlayer.start();
        }

        return START_STICKY;
    }

    private void clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        clearMediaPlayer();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}