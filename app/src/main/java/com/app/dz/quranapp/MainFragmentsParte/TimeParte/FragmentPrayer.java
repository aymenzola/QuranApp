package com.app.dz.quranapp.MainFragmentsParte.TimeParte;

import static android.content.Context.ALARM_SERVICE;
import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.IslamicCalendar;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.LocationParte.LocationActivity;
import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.Util.UserLocation;
import com.app.dz.quranapp.databinding.FragmentPrayerTimesBinding;
import com.app.dz.quranapp.qibla_parte.QiblaFinder;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.CalculationParameters;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.Madhab;
import com.batoulapps.adhan.data.DateComponents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class FragmentPrayer extends Fragment {


    public static final String TAG = FragmentPrayer.class.getSimpleName();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    AlarmManager alarmManager;

    private FragmentPrayerTimesBinding binding;
    private PrayerViewModel viewModel;
    private DayPrayerTimes TodayTimesG;
    private DayPrayerTimes NextDayTimesG;
    private DayPrayerTimes PreviousDayTimesG;
    private long countdownDuration;
    private static CountDownTimer count;
    private DayPrayerAdapter adapter;
    private ViewPager2.OnPageChangeCallback changeCallback;
    private Calendar calendar = Calendar.getInstance();
    private long MidnightTimeInMillis_Today;
    private long MidnightTimeInMillis_NextDAY;
    private String nextSalatName = "";


    public FragmentPrayer() {
        // Required empty public constructor
    }


    public static FragmentPrayer newInstance() {
        return new FragmentPrayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPrayerTimesBinding.inflate(getLayoutInflater(), container, false);
        viewModel = new ViewModelProvider(this).get(PrayerViewModel.class);
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        checkLocationAviablity();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.blan));
        }

        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        binding.tvLocation.setOnClickListener(v -> startActivity(new Intent(getActivity(), LocationActivity.class)));

        binding.included.tvTitle.setText("مواقيت الصلاة");
        binding.included.imgSearch.setVisibility(View.GONE);

        setObservers();
        viewModel.setDayPrayer();
        setListeners();
        calculePrayerTime();

        changeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (adapter == null) return;
                String date = adapter.getItem(position).getDate();
                binding.tvDayHijri.setText(getDateArabicName(date));
                binding.tvDay.setText("الموافق ل " + date);
            }
        };

    }

    private void checkLocationAviablity() {
        boolean alreadyExist = SharedPreferenceManager.getInstance(getActivity()).iSLocationAvialable();
        if (alreadyExist) {
            binding.tvLocation.setVisibility(View.VISIBLE);
            UserLocation prevUserLocation = SharedPreferenceManager.getInstance(getActivity()).getUserLocation();
            binding.tvLocation.setText(prevUserLocation.address);
        } else {
            HandleNoLocation();
        }
    }

    private void HandleNoLocation() {
        binding.relativeLocation.setVisibility(View.GONE);
        binding.relativeNoLocation.setVisibility(View.VISIBLE);
        binding.btnGetLocation.setOnClickListener(v -> startActivity(new Intent(getActivity(), LocationActivity.class)));
        binding.imageGetLocation.setOnClickListener(v -> startActivity(new Intent(getActivity(), LocationActivity.class)));
    }


    private void setListeners() {

        binding.included.tvTitle.setOnClickListener(v->startActivity(new Intent(getActivity(),
                QiblaFinder.class)));

        binding.imgBack.setOnClickListener(v -> {
            int currentItem = binding.viewpager.getCurrentItem();
            if (currentItem > 0) {
                binding.viewpager.setCurrentItem(currentItem - 1);
            }
        });

        binding.imgNext.setOnClickListener(v -> {
            if (adapter == null) return;
            int currentItem = binding.viewpager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                binding.viewpager.setCurrentItem(currentItem + 1);
            }
        });
    }

    private void setObservers() {
        viewModel.getResult().observe(getViewLifecycleOwner(), s -> {
            Log.e(TAG, "result : " + s);
            if (s.equals("Finished")) {
                binding.viewpager.setVisibility(View.VISIBLE);
                binding.tvGettingTiming.setVisibility(View.GONE);
                viewModel.setDayPrayer();
            }

        });

        viewModel.getDayPrayer().observe(getViewLifecycleOwner(), dayPrayerTimes -> {
            if (dayPrayerTimes != null) {
                TodayTimesG = dayPrayerTimes;
                getWeekData();
                viewModel.setNextDayPrayer();
            } else {
                // there is no timing for this day
                checkLocation();
                //TODO we have to get this month timing

            }
        });

        viewModel.getNextDayPrayer().observe(getViewLifecycleOwner(), nextDayPrayerTimes -> {
            if (nextDayPrayerTimes != null) NextDayTimesG = nextDayPrayerTimes;
            viewModel.setPreviousDayPrayer();
        });

        viewModel.getPreviousDayPrayer().observe(getViewLifecycleOwner(), previousDayTimesG -> {
            if (previousDayTimesG != null) PreviousDayTimesG = previousDayTimesG;
            PrayerTimes PrayerTimesToday = getConvertTimeMilliSeconds();
            long FajrTimeTommorow = getTommorowFajt();
            //todo find next findTheNextPrayer(PrayerTimesToday, FajrTimeTommorow);
            getTheNextPrayer(PrayerTimesToday, FajrTimeTommorow);
        });

        viewModel.getWeekPrayer().observe(getViewLifecycleOwner(), weekTimesList -> {
            if (weekTimesList != null) {
                initializeArticlesAdapter(weekTimesList);
                for (DayPrayerTimes dayPrayerTimes : weekTimesList) {
                    Log.e(TAG, "we recieve week data " + dayPrayerTimes.getDate());
                }
            }
        });
    }


    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    private void schuduleNextAlarm(Context context) {
        //TODO get the location from sheared preferences

        PrayerTimes PrayerTimesToday = getConvertTimeMilliSeconds();

        long FajrTimeTommorow = getTommorowFajt();

        findTheNextAlarm(context, PrayerTimesToday, FajrTimeTommorow);


    }

    private void findTheNextAlarm(Context context, PrayerTimes PrayerTimesToday, long FajrTimeTommorow) {
        Calendar currant = Calendar.getInstance();

        Log.e("alarm", "finding next alarm");
        if (currant.getTimeInMillis() > PrayerTimesToday.fajr && currant.getTimeInMillis() < PrayerTimesToday.sunrise) {
            setExactAlarm(context, PrayerTimesToday.sunrise, "الشروق");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.sunrise && currant.getTimeInMillis() < PrayerTimesToday.duhr) {
            setExactAlarm(context, PrayerTimesToday.duhr, "الظهر");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.duhr && currant.getTimeInMillis() < PrayerTimesToday.assr) {
            setExactAlarm(context, PrayerTimesToday.assr, "العصر");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.assr && currant.getTimeInMillis() < PrayerTimesToday.maghrib) {
            setExactAlarm(context, PrayerTimesToday.maghrib, "المغرب");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.maghrib && currant.getTimeInMillis() < PrayerTimesToday.ishaa) {
            setExactAlarm(context, PrayerTimesToday.ishaa, "العشاء");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.ishaa && currant.getTimeInMillis() < FajrTimeTommorow) {
            setExactAlarm(context, FajrTimeTommorow, "الفجر");
        } else if (currant.getTimeInMillis() < PrayerTimesToday.fajr) {
            setExactAlarm(context, PrayerTimesToday.fajr, "الفجر");
        }

    }

    private long getTommorowFajt() {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        //tommorow fajr
        Calendar c = Calendar.getInstance();
        if (NextDayTimesG == null) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getFajr()));
            c.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getFajr()));

        } else {
            c.set(Calendar.MONTH, NextDayTimesG.getMonth() - 1);
            c.set(Calendar.DAY_OF_MONTH, NextDayTimesG.getDay());
            c.set(Calendar.HOUR_OF_DAY, getIntHour(NextDayTimesG.getFajr()));
            c.set(Calendar.MINUTE, getIntMinute(NextDayTimesG.getFajr()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = dateFormat.format(c.getTime());
            Log.e("alarm", "next day " + dateString + "   " + NextDayTimesG.toString());
        }
        return c.getTimeInMillis();
    }

    private long getPreviousIshaa() {
        DecimalFormat decimalFormat = new DecimalFormat("00");

        Calendar c = Calendar.getInstance();
        if (PreviousDayTimesG == null) {
            c.add(Calendar.DAY_OF_MONTH, -1);
            c.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getIsha()));
            c.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getIsha()));


            String month = decimalFormat.format(c.get(Calendar.MONTH) + 1);
            String day = decimalFormat.format(c.get(Calendar.DAY_OF_MONTH));
            String date = day + "-" + month + "-" + c.get(Calendar.YEAR);

            Log.e("alarm", "prev is null so previous day is " + date + " ishaa time is " + c.get(Calendar.HOUR_OF_DAY) + " : " + c.get(Calendar.MINUTE));

        } else {
            c.set(Calendar.MONTH, PreviousDayTimesG.getMonth() - 1);
            c.set(Calendar.HOUR_OF_DAY, getIntHour(PreviousDayTimesG.getIsha()));
            c.set(Calendar.DAY_OF_MONTH, PreviousDayTimesG.getDay());
            c.set(Calendar.MINUTE, getIntMinute(PreviousDayTimesG.getIsha()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = dateFormat.format(c.getTime());
            Log.e("alarm", "back day " + dateString + " get month " + PreviousDayTimesG.getMonth() + "   " + PreviousDayTimesG.toString());
        }
        return c.getTimeInMillis();
    }

    public PrayerTimes getConvertTimeMilliSeconds() {
        Log.e("alarm", " ConvertTimeMilliSeconds ");

        PrayerTimes prayerTimes = new PrayerTimes();
        Calendar calendar = Calendar.getInstance();

        //fajr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getFajr()));
        calendar.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getFajr()));
        prayerTimes.fajr = calendar.getTimeInMillis();

        //sunrise
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getSunrise()));
        calendar.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getSunrise()));
        prayerTimes.sunrise = calendar.getTimeInMillis();

        //thuhr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getDhuhr()));
        calendar.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getDhuhr()));
        prayerTimes.duhr = calendar.getTimeInMillis();

        //assr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getAsr()));
        calendar.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getAsr()));
        prayerTimes.assr = calendar.getTimeInMillis();

        //maghrib
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getMaghrib()));
        calendar.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getMaghrib()));
        prayerTimes.maghrib = calendar.getTimeInMillis();

        //ishaa
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TodayTimesG.getIsha()));
        calendar.set(Calendar.MINUTE, getIntMinute(TodayTimesG.getIsha()));
        prayerTimes.ishaa = calendar.getTimeInMillis();

        return prayerTimes;
    }

    private void setExactAlarm(Context context, long time, String title) {
        Log.e("alarm", "setting exact alarm" + title);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("type", "salat");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(time, pendingIntent), pendingIntent);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Toast.makeText(context, " we set the next alarm at " + c.get(Calendar.HOUR_OF_DAY) + " : " + c.get(Calendar.MINUTE) + " صلاة " + title, Toast.LENGTH_LONG).show();
        Log.e("alarm", "we set the next alarm at " + c.get(Calendar.HOUR_OF_DAY) + " : " + c.get(Calendar.MINUTE) + " صلاة " + title);
    }

    public Integer getIntHour(String time) {
        return Integer.parseInt(time.substring(0, 2));
    }

    public Integer getIntMinute(String time) {
        return Integer.parseInt(time.substring(3, 5));
    }

    private void displayTime(long millisUntilFinished, String foramt) {
        long hours = millisUntilFinished / (1000 * 60 * 60);
        long minutes = (millisUntilFinished / (1000 * 60)) % 60;
        long seconds = (millisUntilFinished / 1000) % 60;

        // display the remaining time in the TextView
        String countdownText = String.format(foramt, hours, minutes, seconds);
        binding.tvNextPrayerCountdown.setText(countdownText);
    }

    public void initializeArticlesAdapter(List<DayPrayerTimes> weekTimesList) {
        Collections.sort(weekTimesList, (s1, s2) -> {
            try {
                return dateFormat.parse(s1.getDate()).compareTo(dateFormat.parse(s2.getDate()));
            } catch (ParseException e) {
                Log.e(TAG, "error ranking dates");
                return -1;
            }
        });
        adapter = new DayPrayerAdapter(getActivity(), weekTimesList, dayPrayerTimes -> {
        });
        binding.viewpager.setOrientation(ORIENTATION_HORIZONTAL);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.registerOnPageChangeCallback(changeCallback);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.viewpager.unregisterOnPageChangeCallback(changeCallback);
    }

    public void prepareWeekData() {
        Calendar calendar = Calendar.getInstance();

// Get the current day of the week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

// Calculate the start date of the current week based on the current day of the week
        calendar.add(Calendar.DAY_OF_MONTH, -dayOfWeek + 2);
        Date startDate = calendar.getTime();

// Create a SimpleDateFormat object to format the dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

// Iterate over the days of the week and print each date
        for (int i = 0; i < 7; i++) {
            // Format the date in the desired format (dd-MM-yyyy)
            String dateString = dateFormat.format(startDate);

            // Print the date
            Log.e("alarm", i + " dateString " + dateString);

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
        }
    }

    public void getWeekData() {
        Calendar calendar = Calendar.getInstance();
        String[] nextDates = new String[7];
        for (int i = 0; i < 7; i++) {
            String nextDateStr = dateFormat.format(calendar.getTime());
            nextDates[i] = nextDateStr;
            Log.e(TAG, "day : " + nextDateStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        viewModel.setWeekPrayer(nextDates);
    }

    private String getDateArabicName(String inputDate) {
        DateFormatSymbols arabicSymbols = new DateFormatSymbols(new Locale("ar"));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormatDayName = new SimpleDateFormat("EEE", arabicSymbols);

        Date date;
        String hijriDateStr = inputDate;
        try {
            date = dateFormat.parse(inputDate);
            hijriDateStr = outputDateFormatDayName.format(date) + " " + getHijriDate(inputDate);
        }catch (Exception e){
            Log.e(TAG,"error "+e.getMessage());
        }
        return hijriDateStr;
    }


    public void checkLocation() {
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        if (sharedPreferenceManager.iSLocationAvialable()) {
            long longitude = sharedPreferenceManager.getUserLocation().longitude;
            long latitude = sharedPreferenceManager.getUserLocation().latitude;
            preparePrayerTimes(longitude, latitude);
        } else Log.e(TAG, "we did not find the location");

    }

    public void preparePrayerTimes(long longitude, long latitude) {
        binding.tvGettingTiming.setEnabled(false);
        binding.tvGettingTiming.setVisibility(View.VISIBLE);
        binding.viewpager.setVisibility(View.GONE);
        new Thread(() -> getPrayerTimesFromServer(Calendar.getInstance().get(Calendar.YEAR), latitude, longitude)).start();
    }

    public void getPrayerTimesFromServer(int year, double latitude, double longitude) {

        //adding to database
        AppDatabase db = DatabaseClient.getInstance(getActivity()).getAppDatabase();
        DayPrayerTimesDao dao = db.getDayPrayerTimesDao();

        String urll = "http://api.aladhan.com/v1/calendar/" + year + "?latitude=" + latitude + "&" + "longitude=" + longitude + "&method=" + 3;

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urll, null, response -> {
            try {


                viewModel.setResult("Started saving data");

                List<DayPrayerTimes> prayerTimesList = new ArrayList<>();
                JSONObject data = response.getJSONObject("data");

                for (int j = 1; j <= 12; j++) {
                    JSONArray jsonArrayMonthe = data.getJSONArray(String.valueOf(j));
                    for (int i = 0; i < jsonArrayMonthe.length(); i++) {
                        JSONObject timings = jsonArrayMonthe.getJSONObject(i).getJSONObject("timings");

                        String fajr = timings.getString("Fajr");
                        String Sunrise = timings.getString("Sunrise");
                        String Dhuhr = timings.getString("Dhuhr");
                        String Asr = timings.getString("Asr");
                        String Maghrib = timings.getString("Maghrib");
                        String Isha = timings.getString("Isha");
                        String Imsak = timings.getString("Imsak");


                        String date = jsonArrayMonthe.getJSONObject(i).getJSONObject("date").getJSONObject("gregorian").getString("date");

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = dateFormat.parse(date);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date1);


                        int mont = cal.get(Calendar.MONTH) + 1;
                        Log.e("timing", "cal month : " + mont + "date foramt " + date);

                        DayPrayerTimes dayPrayerTimes = new DayPrayerTimes(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR), fajr.substring(0, 5), Sunrise.substring(0, 5), Dhuhr.substring(0, 5), Asr.substring(0, 5), Maghrib.substring(0, 5), Isha.substring(0, 5), Imsak.substring(0, 5), date);
                        prayerTimesList.add(dayPrayerTimes);
                    }
                }

                new Thread(() -> {
                    try {
                        if (prayerTimesList.size() > 0) dao.deleteAll();
                        dao.insert(prayerTimesList);
                        viewModel.setResult("Finished");

                    } catch (Exception e) {
                        viewModel.setResult("error");
                        Log.e(TAG, "error while inserting time item " + e.getMessage());
                    }
                }).start();

            } catch (JSONException | ParseException e) {
                handleErrorCase();
                e.printStackTrace();
            }
        }, error -> {
            handleErrorCase();
            Log.e("timing", "error " + error.getMessage());
        });
        queue.add(jsonObjectRequest);
    }

    private void handleErrorCase() {
        binding.tvGettingTiming.setEnabled(true);
        binding.tvGettingTiming.setText("اعادة المحاولة");
    }

    public void calculePrayerTime() {
        SharedPreferenceManager shared = SharedPreferenceManager.getInstance(getActivity());
        Coordinates coordinates = new Coordinates(shared.getUserLocation().latitude, shared.getUserLocation().longitude);
        DateComponents date = DateComponents.from(new Date());
        CalculationParameters parameters = CalculationMethod.MUSLIM_WORLD_LEAGUE.getParameters();
        parameters.madhab = Madhab.SHAFI;
        parameters.adjustments.asr = -6;
        parameters.adjustments.isha = -10;

        com.batoulapps.adhan.PrayerTimes prayerTimes = new com.batoulapps.adhan.PrayerTimes(coordinates, date, parameters);
        Log.e(TAG, "fajr " + prayerTimes.fajr);
        Log.e(TAG, "sunrise " + prayerTimes.sunrise);
        Log.e(TAG, "dhuhr " + prayerTimes.dhuhr);
        Log.e(TAG, "asr " + prayerTimes.asr);
        Log.e(TAG, "maghrib " + prayerTimes.maghrib);
        Log.e(TAG, "isha " + prayerTimes.isha);

        SimpleDateFormat formater = new SimpleDateFormat("hh:mm a");
        formater.setTimeZone(TimeZone.getDefault());
        formater.format(prayerTimes.fajr);


    }

    public void getMidnightTimeInMillis() {
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        MidnightTimeInMillis_Today = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        MidnightTimeInMillis_NextDAY = calendar.getTimeInMillis();

    }

    @SuppressLint("SetTextI18n")
    private void getTheNextPrayer(PrayerTimes PrayerTimesToday, long FajrTimeTommorow) {
        long Nextmillseconds;
        Calendar currant = Calendar.getInstance();
        getMidnightTimeInMillis();
        long currantMilliseconds = currant.getTimeInMillis();

        //test
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(MidnightTimeInMillis_NextDAY);
        Log.e(TAG, "middle time today " + c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        Log.e(TAG, "middle time next day " + c.getTime());


        if (currantMilliseconds > PrayerTimesToday.ishaa && currantMilliseconds < MidnightTimeInMillis_NextDAY) {
            Nextmillseconds = FajrTimeTommorow;
            nextSalatName = "الفجر";
        } else if (currantMilliseconds > MidnightTimeInMillis_Today && currantMilliseconds < PrayerTimesToday.fajr) {
            Nextmillseconds = PrayerTimesToday.fajr;
            nextSalatName = "الفجر";
        } else if (currantMilliseconds > PrayerTimesToday.fajr && currantMilliseconds < PrayerTimesToday.sunrise) {
            Nextmillseconds = PrayerTimesToday.sunrise;
            nextSalatName = "الشروق";
        } else if (currantMilliseconds > PrayerTimesToday.sunrise && currantMilliseconds < PrayerTimesToday.duhr) {
            Nextmillseconds = PrayerTimesToday.duhr;
            nextSalatName = "الظهر";
        } else if (currantMilliseconds > PrayerTimesToday.duhr && currantMilliseconds < PrayerTimesToday.assr) {
            Nextmillseconds = PrayerTimesToday.assr;
            nextSalatName = "العصر";
        } else if (currantMilliseconds > PrayerTimesToday.assr && currantMilliseconds < PrayerTimesToday.maghrib) {
            Nextmillseconds = PrayerTimesToday.maghrib;
            nextSalatName = "المغرب";
        } else {
            Nextmillseconds = PrayerTimesToday.ishaa;
            nextSalatName = "العشاء";
        }

        binding.tvSalatMessage.setText("بقي على أذان " + nextSalatName + " : ");

        countdownDuration = Nextmillseconds - System.currentTimeMillis();
        if (count != null) count = null;
        count = new CountDownTimer(countdownDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                displayTime(millisUntilFinished, "-%02d:%02d:%02d");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                // display a message when the timer finishes
                binding.tvSalatMessage.setText("حان الان موعد صلاة " + nextSalatName);
            }
        }.start();

    }

    public String convertToHijri(int month) {
        switch (month) {
            case 1:
                return "محرم";
            case 2:
                return "صفر";
            case 3:
                return "ربيع الأول";
            case 4:
                return "ربيع الثاني";
            case 5:
                return "جمادى الأولى";
            case 6:
                return "جمادى الثانية";
            case 7:
                return "رجب";
            case 8:
                return "شعبان";
            case 9:
                return "رمضان";
            case 10:
                return "شوال";
            case 11:
                return "ذو القعدة";
            case 12:
                return "ذو الحجة";
            default:
                return "";
        }
    }

    public String getHijriDate(String dateString) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate dt = LocalDate.parse(dateString, formatter);
            HijrahDate hijrahDate = HijrahDate.from(dt);
            //String formatted = formatter.format(hijrahDate); // 07/03/1439

            return hijrahDate.get(ChronoField.DAY_OF_MONTH) +
                    " " + convertToHijri(hijrahDate.get(ChronoField.MONTH_OF_YEAR)) + " "
                    + hijrahDate.get(ChronoField.YEAR);
        } else {
            binding.tvDay.setVisibility(View.GONE);
            return "";
        }
    }

    public void getIslamEvenets(){

    }
}




