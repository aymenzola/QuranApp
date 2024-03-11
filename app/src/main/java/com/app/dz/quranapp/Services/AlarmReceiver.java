package com.app.dz.quranapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiverTag";
    private static final String ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED = "ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED";
    private static final String FROM_ALARM = "from alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED:
                Log.e(TAG,"RECEIVED ALARM PERMISSION");
                Toast.makeText(context, "RECEIVED ALARM PERMISSION",Toast.LENGTH_LONG).show();
                break;
            case FROM_ALARM:
                Log.e(TAG,"ALARM FIRED");
                Toast.makeText(context, "ALARM FIRED",Toast.LENGTH_LONG).show();
                break;
        }
    }
}