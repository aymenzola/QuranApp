package com.app.dz.quranapp.ui.activities.AdkarParte;

import static com.app.dz.quranapp.Util.SharedPreferenceManager.SHARED_PREF_NAME;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.work.WorkManager;

import com.app.dz.quranapp.Services.adhan.AlarmBroadcastReceiver;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class AdkarCountsHelper {

    private static final String AL_READY_APPEARED_NOTIFICATION_COUNT_KEY = "notificationCount";
    private static final String SCHEDULED_NOTIFICATION_COUNT_KEY = "scheduledNotificationCount";
    private static final int ADKAR_ALARM_REQUEST_CODE = 88;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static AdkarCountsHelper mInstance;
    private final String[] adkarSabah = {"حَسْبِـيَ اللّهُ لا إلهَ إلاّ هُوَ عَلَـيهِ تَوَكَّـلتُ وَهُوَ رَبُّ العَرْشِ العَظـيم. \n" +
            "من قالها كفاه الله ما أهمه من أمر الدنيا والأخرة.",
            "بِسـمِ اللهِ الذي لا يَضُـرُّ مَعَ اسمِـهِ شَيءٌ في الأرْضِ وَلا في السّمـاءِ وَهـوَ السّمـيعُ العَلـيم.",
            "اللّهُـمَّ بِكَ أَصْـبَحْنا وَبِكَ أَمْسَـينا ، وَبِكَ نَحْـيا وَبِكَ نَمُـوتُ وَإِلَـيْكَ النُّـشُور. ",
            "أَصْبَـحْـنا عَلَى فِطْرَةِ الإسْلاَمِ، وَعَلَى كَلِمَةِ الإِخْلاَصِ، وَعَلَى دِينِ نَبِيِّنَا مُحَمَّدٍ صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ، وَعَلَى مِلَّةِ أَبِينَا إبْرَاهِيمَ حَنِيفاً مُسْلِماً وَمَا كَانَ مِنَ المُشْرِكِينَ",
            "سُبْحـانَ اللهِ وَبِحَمْـدِهِ عَدَدَ خَلْـقِه ، وَرِضـا نَفْسِـه ، وَزِنَـةَ عَـرْشِـه ، وَمِـدادَ كَلِمـاتِـه. ",
            "اللّهُـمَّ عافِـني في بَدَنـي ، اللّهُـمَّ عافِـني في سَمْـعي ، اللّهُـمَّ عافِـني في بَصَـري ، لا إلهَ إلاّ أَنْـتَ. ",
            "اللّهُـمَّ إِنّـي أَعـوذُ بِكَ مِنَ الْكُـفر ، وَالفَـقْر ، وَأَعـوذُ بِكَ مِنْ عَذابِ القَـبْر ، لا إلهَ إلاّ أَنْـتَ. "
    };


    private final String[] adkarMasa = {
            "اللّهُـمَّ إِنِّـي أَمسيتُ أُشْـهِدُك ، وَأُشْـهِدُ حَمَلَـةَ عَـرْشِـك ، وَمَلَائِكَتَكَ ، وَجَمـيعَ خَلْـقِك ، أَنَّـكَ أَنْـتَ اللهُ لا إلهَ إلاّ أَنْـتَ وَحْـدَكَ لا شَريكَ لَـك ، وَأَنَّ ُ مُحَمّـداً عَبْـدُكَ وَرَسـولُـك.",
            "اللّهُـمَّ ما أَمسى بي مِـنْ نِعْـمَةٍ أَو بِأَحَـدٍ مِـنْ خَلْـقِك ، فَمِـنْكَ وَحْـدَكَ لا شريكَ لَـك ، فَلَـكَ الْحَمْـدُ وَلَـكَ الشُّكْـر.",
            "حَسْبِـيَ اللّهُ لا إلهَ إلاّ هُوَ عَلَـيهِ تَوَكَّـلتُ وَهُوَ رَبُّ العَرْشِ العَظـيم.",
            "بِسـمِ اللهِ الذي لا يَضُـرُّ مَعَ اسمِـهِ شَيءٌ في الأرْضِ وَلا في السّمـاءِ وَهـوَ السّمـيعُ العَلـيم.",
            "اللّهُـمَّ بِكَ أَمْسَـينا وَبِكَ أَصْـبَحْنا، وَبِكَ نَحْـيا وَبِكَ نَمُـوتُ وَإِلَـيْكَ الْمَصِيرُ.",
            "يَا حَيُّ يَا قيُّومُ بِرَحْمَتِكَ أسْتَغِيثُ أصْلِحْ لِي شَأنِي كُلَّهُ وَلاَ تَكِلْنِي إلَى نَفْسِي طَـرْفَةَ عَيْنٍ.",
            "أَمْسَيْنا وَأَمْسَى الْمُلْكُ للهِ رَبِّ الْعَالَمَيْنِ، اللَّهُمَّ إِنَّي أسْأَلُكَ خَيْرَ هَذَه اللَّيْلَةِ فَتْحَهَا ونَصْرَهَا، ونُوْرَهَا وبَرَكَتهَا، وَهُدَاهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فيهِا وَشَرَّ مَا بَعْدَهَا.",
            "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ على نَبِيِّنَا مُحمَّد.",
            "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنْ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنْ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ، وَقَهْرِ الرِّجَالِ."
    };

    private final String[] adkarBeforeSleping = {
            "بِاسْمِكَ رَبِّـي وَضَعْـتُ جَنْـبي ، وَبِكَ أَرْفَعُـه، فَإِن أَمْسَـكْتَ نَفْسـي فارْحَـمْها ، وَإِنْ أَرْسَلْتَـها فاحْفَظْـها بِمـا تَحْفَـظُ بِه عِبـادَكَ الصّـالِحـين. ",
            "اللّهُـمَّ إِنَّـكَ خَلَـقْتَ نَفْسـي وَأَنْـتَ تَوَفّـاهـا لَكَ ممَـاتـها وَمَحْـياها ، إِنْ أَحْيَيْـتَها فاحْفَظْـها ، وَإِنْ أَمَتَّـها فَاغْفِـرْ لَـها . اللّهُـمَّ إِنَّـي أَسْـأَلُـكَ العـافِـيَة.",
            "اللّهُـمَّ قِنـي عَذابَـكَ يَـوْمَ تَبْـعَثُ عِبـادَك.",
            "الـحَمْدُ للهِ الَّذي أَطْـعَمَنا وَسَقـانا، وَكَفـانا، وَآوانا، فَكَـمْ مِمَّـنْ لا كـافِيَ لَـهُ وَلا مُـؤْوي.",
            "اللّهُـمَّ عالِـمَ الغَـيبِ وَالشّـهادةِ فاطِـرَ السّماواتِ وَالأرْضِ رَبَّ كُـلِّ شَـيءٍ وَمَليـكَه، أَشْهـدُ أَنْ لا إِلـهَ إِلاّ أَنْت، أَعـوذُ بِكَ مِن شَـرِّ نَفْسـي، وَمِن شَـرِّ الشَّيْـطانِ وَشِـرْكِه، وَأَنْ أَقْتَـرِفَ عَلـى نَفْسـي سوءاً أَوْ أَجُـرَّهُ إِلـى مُسْـلِم.",
            "اللّهُـمَّ أَسْـلَمْتُ نَفْـسي إِلَـيْكَ، وَفَوَّضْـتُ أَمْـري إِلَـيْكَ، وَوَجَّـهْتُ وَجْـهي إِلَـيْكَ، وَأَلْـجَـاْتُ ظَهـري إِلَـيْكَ، رَغْبَـةً وَرَهْـبَةً إِلَـيْكَ، لا مَلْجَـأَ وَلا مَنْـجـا مِنْـكَ إِلاّ إِلَـيْكَ، آمَنْـتُ بِكِتـابِكَ الّـذي أَنْزَلْـتَ وَبِنَبِـيِّـكَ الّـذي أَرْسَلْـت.",
    };


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
        Random random = new Random();

        if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
            int randomIndex = random.nextInt(adkarSabah.length);
            return adkarSabah[randomIndex];
        } else if (calendar.get(Calendar.HOUR_OF_DAY) < 16) {
            int randomIndex = random.nextInt(adkarMasa.length);
            return adkarMasa[randomIndex];
        } else {
            int randomIndex = random.nextInt(adkarBeforeSleping.length);
            return adkarBeforeSleping[randomIndex];
        }
    }

    public void setIsFirstNotification(boolean isFirstNotification) {
        editor.putBoolean("isFirstNotification", isFirstNotification);
        editor.apply();
    }

    public boolean getIsFirstNotification() {
        return sharedPreferences.getBoolean("isFirstNotification", true);
    }

    public void scheduleDikrAlarm(Context context) {
        //cancel previous alarm if exists
        cancelPrevDikrAlarm(context);
        int level = AdkarCountsHelper.getInstance(context).getAdkarLevel();

        // Get the current hour and minute
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        int safePeriod = 22 - level;
        if (currentHour >= 8 && currentHour < safePeriod) {
            scheduleNextAlarm(context, level);
        } else {
            //should schedule the next alarm tomorrow at 8:00 am
            scheduleTomorrowAlarm(context);
        }
    }

    private void scheduleTomorrowAlarm(Context context) {

        long timeTo8Am = getMissingTimeTo8Am(context);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ADKAR_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeTo8Am, pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeTo8Am, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeTo8Am, pendingIntent);
            }
        }
    }

    private long getMissingTimeTo8Am(Context context) {
        Calendar now = Calendar.getInstance();
        long currentTimeMillis = now.getTimeInMillis();

        //Set the calendar instance to 8 AM tomorrow
        now.add(Calendar.DAY_OF_YEAR, 1); // Move to tomorrow
        now.set(Calendar.HOUR_OF_DAY, 8); // Set hour to 8 AM
        now.set(Calendar.MINUTE, 0); // Set minute to 0
        now.set(Calendar.SECOND, 0); // Set second to 0
        now.set(Calendar.MILLISECOND, 0); // Set millisecond to 0

        long eightAmTomorrowMillis = now.getTimeInMillis();

        return eightAmTomorrowMillis - currentTimeMillis;
    }

    private static void scheduleNextAlarm(Context context, int level) {
        long nextDikrDelay = (long) level * 60 * 60 * 1000;

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ADKAR_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextDikrDelay, pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextDikrDelay, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextDikrDelay, pendingIntent);
            }
        }
    }


    public static void cancelPrevDikrAlarm(Context context) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ADKAR_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
