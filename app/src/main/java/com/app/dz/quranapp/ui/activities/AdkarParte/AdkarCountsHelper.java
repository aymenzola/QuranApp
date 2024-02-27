package com.app.dz.quranapp.ui.activities.AdkarParte;

import static com.app.dz.quranapp.Util.SharedPreferenceManager.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.UUID;

public class AdkarCountsHelper {

    private static final String AL_READY_APPEARED_NOTIFICATION_COUNT_KEY = "notificationCount";
    private static final String SCHEDULED_NOTIFICATION_COUNT_KEY = "scheduledNotificationCount";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static AdkarCountsHelper mInstance;


    public static synchronized AdkarCountsHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AdkarCountsHelper(context);
        }
        return mInstance;
    }

    public AdkarCountsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAdkarLevel(int level) {
        editor.putInt("adkarLevel", level);
        editor.apply();
    }

    public int getAdkarLevel() {
        return sharedPreferences.getInt("adkarLevel", 4);
    }


    public void incrementAlreadyAppearedNotificationCount() {
        int count = sharedPreferences.getInt(AL_READY_APPEARED_NOTIFICATION_COUNT_KEY, 0);
        editor.putInt(AL_READY_APPEARED_NOTIFICATION_COUNT_KEY, count + 1);
        editor.apply();
    }

    public int getAlreadyAppearedNotificationCount() {
        return sharedPreferences.getInt(AL_READY_APPEARED_NOTIFICATION_COUNT_KEY, 0);
    }

    public int getScheduledNotificationCount() {
        return sharedPreferences.getInt(SCHEDULED_NOTIFICATION_COUNT_KEY, 0);
    }

    public void saveScheduledNotificationCount(int count) {
        editor.putInt(SCHEDULED_NOTIFICATION_COUNT_KEY, count);
        editor.apply();
    }

    public void resetAlreadyAppearedNotificationCount() {
        editor.putInt(AL_READY_APPEARED_NOTIFICATION_COUNT_KEY, 0);
        editor.apply();
    }

    public void saveAdkarWorkId(String string) {
        editor.putString("adkarWorkId", string);
        editor.apply();
    }

    public String getAdkarWorkId() {
        return sharedPreferences.getString("adkarWorkId", null);
    }

    public void resetAllAdkarData(Context context) {
        String workId = getAdkarWorkId();
        if (workId != null)
            WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workId));
        editor.putInt(AL_READY_APPEARED_NOTIFICATION_COUNT_KEY, 0);
        editor.putInt(SCHEDULED_NOTIFICATION_COUNT_KEY, 0);
        editor.putString("adkarWorkId", null);
        editor.putBoolean("isFirstNotification", true);
        editor.apply();
    }

    public void saveAdkarState(boolean isChecked) {
        editor.putBoolean("adkarState", isChecked);
        editor.apply();
    }

    public boolean getAdkarState() {
        return sharedPreferences.getBoolean("adkarState", false);
    }

    public long getNumberOfNotifications() {

        Calendar now = Calendar.getInstance();

        // Get the current hour and minute
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        // Calculate the remaining minutes today
        int remainingMinutesTo20PmToday = (20 * 60) - (currentHour * 60 + currentMinute);
        // Convert the total minutes to hours
        long remainingHours = remainingMinutesTo20PmToday / 60;
        Log.e("adkarLog", "we have remaining hours = " + remainingHours + " hours until 20:00");

        double numberOfNotificationsDouble = (double) remainingHours / getAdkarLevel();
        long numberOfNotifications = (long) Math.floor(numberOfNotificationsDouble);
        Log.e("adkarLog", "so we can schedule " + numberOfNotifications + " notifications until 20:00 with interval of " + getAdkarLevel() + " hours");
        return numberOfNotifications;
    }


    public String getDikrDependOnCurrentTime() {
        //should check current time and return the correct dikr
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
            return "الأذكار الصباحية";
        } else if (calendar.get(Calendar.HOUR_OF_DAY) < 16) {
            return "الأذكار المسائية";
        } else if (calendar.get(Calendar.HOUR_OF_DAY) < 20) {
            return "الأذكار بعد العصر";
        } else {
            return "الأذكار القبل النوم";
        }
    }

    public void setIsFirstNotification(boolean isFirstNotification) {
        editor.putBoolean("isFirstNotification", isFirstNotification);
        editor.apply();
    }

    public boolean getIsFirstNotification() {
        return sharedPreferences.getBoolean("isFirstNotification", true);
    }
}
