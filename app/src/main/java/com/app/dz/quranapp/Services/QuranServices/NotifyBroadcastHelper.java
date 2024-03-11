package com.app.dz.quranapp.Services.QuranServices;

import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PLAYING_PROGRESS_ACTION;

import android.content.Context;
import android.content.Intent;

public class NotifyBroadcastHelper {

    public NotifyBroadcastHelper(Context context) {

    }

    public static void sendAudioProgress(Context context,int progress, int maxProgress) {
        Intent intent2 = new Intent("AUDIO_FINISHED");
        intent2.putExtra("action", AUDIO_PLAYING_PROGRESS_ACTION);
        intent2.putExtra("progress", progress);
        intent2.putExtra("maxProgress", maxProgress);
        context.sendBroadcast(intent2);
    }



    public static void sendPreparingStateToFragment(Context context,String state) {
        Intent intent = new Intent("AUDIO_FINISHED");
        intent.putExtra("action", state);
        context.sendBroadcast(intent);
    }

    public static void sendErrorMessage(Context context,String message) {
        Intent intent = new Intent("AUDIO_FINISHED");
        intent.putExtra("action",AUDIO_ERROR_ACTION);
        intent.putExtra("message",message);
        context.sendBroadcast(intent);
    }
}