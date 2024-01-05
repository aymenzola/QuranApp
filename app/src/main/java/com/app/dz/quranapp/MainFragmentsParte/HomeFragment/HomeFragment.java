package com.app.dz.quranapp.MainFragmentsParte.HomeFragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.app.dz.quranapp.CollectionParte.HadithDetailsParte.ActivityHadithDetailsListDev;
import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.LocationParte.AboutActivity;
import com.app.dz.quranapp.MainFragmentsParte.TimeParte.PrayerTimes;
import com.app.dz.quranapp.MushafParte.QuranActivity;
import com.app.dz.quranapp.MushafParte.ReadingPosition;
import com.app.dz.quranapp.MushafParte.mushaf_list.MushafListActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.Util.UserLocation;
import com.app.dz.quranapp.databinding.FragmentHomeBinding;
import com.app.dz.quranapp.qibla_parte.QiblaFinder;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {

    public final static String QURAN_TAG = "quran_tag";
    public final static String TAG = "FragmentQuranList";
    public static final int ADKAR_TYPE = 0;
    public static final int BOOKS_TYPE = 1;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private Calendar calendar = Calendar.getInstance();

    private OnListenerInterface listener;
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private int lastPage = 1;

    private DayPrayerTimes TodayTimesG;
    private DayPrayerTimes NextDayTimesG;
    private long MidnightTimeInMillis_Today;
    private long MidnightTimeInMillis_NextDAY;
    private String nextSalatName;
    private long countdownDuration;
    private static CountDownTimer count;
    private DayPrayerTimes PreviousDayTimesG;
    private boolean isThereSavedBook = false;


    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListenerInterface) {
            listener = (OnListenerInterface) context;
        } else {
            Log.e("log", "activity dont implimaents Onclicklistnersenttoactivity");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.blan));
        }
        setListeners();
        setObservers();
        viewModel.setDayPrayer();
        viewModel.setRandomDikr();
    }

    private void setListeners() {

        binding.tvMoveLibrary.setOnLongClickListener(v->{
            /*Intent intent = new Intent(getActivity(), QiblaFinder.class);
            startActivity(intent);
            */

            Intent intent = new Intent(getActivity(), MushafListActivity.class);
            startActivity(intent);

            return true;
        });

        //books card clickes

        binding.tvMoveLibrary.setOnClickListener(v -> cardLibraryClciked());
        binding.tvDestination.setOnClickListener(v -> cardLibraryClciked());
        binding.tvChapter.setOnClickListener(v -> cardLibraryClciked());


        binding.tvMove.setOnClickListener(view1 -> OpenMushaf(lastPage));
        binding.tvAbout.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), AboutActivity.class)));

        //adkar clickes
        binding.tvMoveDikr.setOnClickListener(v -> moveToCollectionFragment(ADKAR_TYPE));
        binding.tvDikrBody.setOnClickListener(v -> moveToCollectionFragment(ADKAR_TYPE));
        binding.tvCategory.setOnClickListener(v -> moveToCollectionFragment(ADKAR_TYPE));


        //time card Clickes
        binding.cardviewTime.setOnClickListener(v -> moveToAdhanFragment());
        binding.tvNextPrayerCountdown.setOnClickListener(v -> moveToAdhanFragment());
        binding.tvLocation.setOnClickListener(v -> moveToAdhanFragment());
        binding.tvDate.setOnClickListener(v -> moveToAdhanFragment());
    }

    private void cardLibraryClciked() {
        if (isThereSavedBook) {
            MoveToHadithDetails();
        } else {
            moveToCollectionFragment(BOOKS_TYPE);
        }
    }

    private void moveToCollectionFragment(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        navController.navigate(R.id.action_fragment_home_to_fragments_collection, bundle);
    }

    private void moveToAdhanFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
//        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.fragment_home, false).build();
        navController.navigate(R.id.action_fragment_home_to_navigation_Prayer);
    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        Log.e(QURAN_TAG, "quran onResume");
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        if (sharedPreferenceManager.iSThereAyaSaved()) {
            //we saved aya
            binding.tvLastAyaTitle.setVisibility(View.VISIBLE);
            ReadingPosition readingPosition = sharedPreferenceManager.getReadinPosition();
            lastPage = readingPosition.page;
            binding.tvLastAya.setText("" + readingPosition.ayaText);
            /*int progress = (int) quranSuraNames.getReadPercentage(readingPosition.sura - 1, readingPosition.aya - 1);
            binding.progressBar.setProgress(progress);
            binding.tvProgress.setText("" + progress + " % ");
            */
        } else {
            //default case
            binding.tvLastAyaTitle.setVisibility(View.GONE);
            binding.tvLastAya.setText("ستظهر هنا اخر اية قمت بحفظها");
        }

        DisplayCardHadith();
        checkLocationAviablity();
    }

    private void checkLocationAviablity() {
        boolean alreadyExist = SharedPreferenceManager.getInstance(getActivity()).iSLocationAvialable();
        if (alreadyExist) {
            binding.tvLocation.setVisibility(View.VISIBLE);
            UserLocation prevUserLocation = SharedPreferenceManager.getInstance(getActivity()).getUserLocation();
            binding.tvLocation.setText(prevUserLocation.address);

            try {
                DisplayDateinInTimeCard();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            //no location case
            binding.cardviewTime.setVisibility(View.GONE);
        }
    }

    private void OpenMushaf(int startPage) {
        Intent intent = new Intent(getActivity(), QuranActivity.class);
        intent.putExtra("page", startPage);
        startActivity(intent);
    }

    private void MoveToHadithDetails() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String bookName = sharedPreferences.getString("bookName", "");
        String collectionName = sharedPreferences.getString("collectionName", "");
        String bookNumber = sharedPreferences.getString("bookNumber", "");
        int CurrantPosition = sharedPreferences.getInt("CurrantPosition", 0);


        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), ActivityHadithDetailsListDev.class);
        bundle.putString("collectionName", collectionName);
        bundle.putString("bookNumber", bookNumber);
        bundle.putString("bookName", bookName);
        bundle.putInt("position", CurrantPosition);

        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    private String getDateArabicName(String inputDate) {
        DateFormatSymbols arabicSymbols = new DateFormatSymbols(new Locale("ar"));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormatDayName = new SimpleDateFormat("EEE", arabicSymbols);

        Date date;
        String hijriDateStr = inputDate;
        try {
            date = dateFormat.parse(inputDate);
            hijriDateStr = outputDateFormatDayName.format(date) + " " + getHijriDate(inputDate) + " الموافق ل " + inputDate;
        }catch (Exception e){
         Log.e(TAG,"error "+e.getMessage());
        }
        return hijriDateStr;
    }

    @SuppressLint("SetTextI18n")
    public void DisplayCardHadith() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String destination = sharedPreferences.getString("destination", "");
        String chapterName = sharedPreferences.getString("chapterName", "");
        if (chapterName.isEmpty()) {
            isThereSavedBook = false;
            binding.tvChapter.setVisibility(View.GONE);
            binding.tvDestination.setText("سيظهر هنا اخر كتاب قمت بحفظه");
            return;
        }
        isThereSavedBook = true;
        binding.tvDestination.setText("" + destination);
        binding.tvChapter.setText("" + chapterName);
    }

    @SuppressLint("SetTextI18n")
    private void DisplayDateinInTimeCard() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        String dateString = dateFormat.format(calendar.getTime());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            binding.tvDate.setText(getDateArabicName(dateString));
        } else {
            //there no hijri date
            DateFormatSymbols arabicSymbols = new DateFormatSymbols(new Locale("ar"));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE d MMMM yyyy", arabicSymbols);
            binding.tvDate.setText(outputDateFormat.format(dateString));
        }


    }

    @SuppressLint("SetTextI18n")
    private void setObservers() {

        viewModel.getDayPrayer().observe(getViewLifecycleOwner(), dayPrayerTimes -> {
            if (TodayTimesG != null) {
                Log.e(TAG, "time object not null retern the observer");
                return;
            }
            if (dayPrayerTimes != null) {
                TodayTimesG = dayPrayerTimes;
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
            if (count != null) {
                Log.e(TAG, "count already working does not null return");
                return;
            } else Log.e(TAG, "count null create null one");
            getTheNextPrayer(PrayerTimesToday, FajrTimeTommorow);
        });

        viewModel.getRandomDikr().observe(getViewLifecycleOwner(), adkarModel -> {
            if (adkarModel != null) {
                binding.tvCategory.setText("" + adkarModel.getCategory());
                binding.tvDikrBody.setText("" + adkarModel.getDikr());
            } else {
                binding.cardviewDikr.setVisibility(View.GONE);
            }
        });
    }

    public void checkLocation() {
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        if (sharedPreferenceManager.iSLocationAvialable()) {
            long longitude = sharedPreferenceManager.getUserLocation().longitude;
            long latitude = sharedPreferenceManager.getUserLocation().latitude;
            // Log.e(TAG, "we find the location");

        } else Log.e(TAG, "we did not find the location");

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

    public Integer getIntHour(String time) {
        return Integer.parseInt(time.substring(0, 2));
    }

    public Integer getIntMinute(String time) {
        return Integer.parseInt(time.substring(3, 5));
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

    private void displayTime(long millisUntilFinished, String foramt) {
        long hours = millisUntilFinished / (1000 * 60 * 60);
        long minutes = (millisUntilFinished / (1000 * 60)) % 60;
        long seconds = (millisUntilFinished / 1000) % 60;

        // display the remaining time in the TextView
        String countdownText = String.format(foramt, hours, minutes, seconds);
        binding.tvNextPrayerCountdown.setText(countdownText);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        count = null;
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
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
            //binding.tvDay.setVisibility(View.GONE);
            return "";
        }
    }
}



