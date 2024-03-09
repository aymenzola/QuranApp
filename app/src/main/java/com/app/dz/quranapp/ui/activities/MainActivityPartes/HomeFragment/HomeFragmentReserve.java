package com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.app.dz.quranapp.Communs.PrayerTimesHelper;
import com.app.dz.quranapp.Communs.PrayerTimesPreference;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.Util.UserLocation;
import com.app.dz.quranapp.data.room.Daos.AyaDao;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.data.room.MushafDatabase;
import com.app.dz.quranapp.databinding.FragmentHomeBinding;
import com.app.dz.quranapp.quran.models.ReadingPosition;
import com.app.dz.quranapp.ui.activities.AboutActivity;
import com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte.ChapterUtils;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.TimeParte.PrayerTimes;
import com.app.dz.quranapp.ui.activities.mahfodat.ActivityMahfodatList;
import com.app.dz.quranapp.ui.activities.subha.AdkarSubhaUtils;
import com.app.dz.quranapp.ui.activities.subha.SubhaActivity;

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
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class HomeFragmentReserve extends Fragment {

    public final static String QURAN_TAG = "quran_tag";
    public final static String TAG = "FragmentQuranList";
    public static final int ADKAR_TYPE = 0;
    public static final int BOOKS_TYPE = 1;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private final Calendar calendar = Calendar.getInstance();

    private OnListenerInterface listener;
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private int lastPage = 1;
    private final int lastPagemh = 2;

    private DayPrayerTimes TodayTimesG;
    private DayPrayerTimes NextDayTimesG;
    private long MidnightTimeInMillis_Today;
    private long MidnightTimeInMillis_NextDAY;
    private String nextSalatName;
    private long countdownDuration;
    private static CountDownTimer count;
    private DayPrayerTimes PreviousDayTimesG;
    private boolean isThereSavedBook = false;
    //    private List<AdkarModel> globalAdkarList;
    private int adkarCouner = 0;


    public HomeFragmentReserve() {
        // Required empty public constructor
    }


    public static HomeFragmentReserve newInstance() {
        HomeFragmentReserve fragment = new HomeFragmentReserve();
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
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.blan));
        }
        setListeners();
        setObservers();
        viewModel.setDayPrayer();
        viewModel.setRandomDikr();
    }

    private void setListeners() {

        String dikr = AdkarSubhaUtils.getLastDikr(requireActivity());
        binding.includeFastAdkarCard.tvFastAdkarText.setText(dikr);

        binding.includeFastAdkarCard.btnFastAdkar.setOnClickListener(v -> {
            adkarCouner = adkarCouner + 1;
            binding.includeFastAdkarCard.btnFastAdkar.setText(String.valueOf(adkarCouner));
        });

        binding.includeFastAdkarCard.tvFastAdkarText.setSelected(true);
        binding.includeHomeHorButtonsCard.cardviewQibla.setOnClickListener(v -> moveToQublaFragment());
        binding.includeHomeHorButtonsCard.cardviewAdkar.setOnClickListener(v -> moveToAdkarFragment());
        binding.includeHomeHorButtonsCard.cardviewMahfodaat.setOnLongClickListener(v -> {
            moveToQuranFragment();
            return true;
        });

        binding.includeHomeHorButtonsCard.cardviewSobha.setOnClickListener(v -> moveToSubhaFragment());

        binding.includeHomeHorButtonsCard.cardviewMahfodaat.setOnClickListener(v -> moveToMahfodatActivity());


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

    private void moveToMahfodatActivity() {
        startActivity(new Intent(getActivity(), ActivityMahfodatList.class));
    }

    private void cardLibraryClciked() {
        if (isThereSavedBook) {
            MoveToHadithDetails();
        } else {
            moveToCollectionFragment(BOOKS_TYPE);
        }
    }

    private void moveToAdkarFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        navController.navigate(R.id.action_fragment_home_to_adkarFragment);
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


    private void moveToQuranFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        navController.navigate(R.id.action_fragment_home_to_quranFragmentDev);
    }

    private void moveToSubhaFragment() {
        startActivity(new Intent(getActivity(), SubhaActivity.class));
    }

    private void moveToQublaFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        navController.navigate(R.id.action_fragment_home_to_qublaFragment);
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

            if (!readingPosition.ayaText.equals("no")) {
                binding.tvLastAya.setText(readingPosition.ayaText);
            } else {
                //should get first aya in the page and display it
                getFirstAyaInPagePage(lastPage);
            }

        } else {
            //default case
            binding.tvLastAyaTitle.setVisibility(View.GONE);
            binding.tvLastAya.setText("ستظهر هنا اخر اية قمت بحفظها");
        }

        DisplayCardHadith();
        checkLocationViability();
    }

    private void checkLocationViability() {
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
        Bundle bundle = new Bundle();
        bundle.putInt("page", startPage);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        navController.navigate(R.id.action_fragment_home_to_quranFragmentDev, bundle);
    }

    private void MoveToHadithDetails() {
        ChapterUtils.moveToChapterDetails(requireActivity(), ChapterUtils.getLastSavedChapter(requireActivity()));
    }

    private String getDateArabicName(String inputDate) {
        DateFormatSymbols arabicSymbols = new DateFormatSymbols(new Locale("ar"));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormatDayName = new SimpleDateFormat("EEE", arabicSymbols);

        Date date;
        String hijriDateStr = inputDate;
        try {
            date = dateFormat.parse(inputDate);
            hijriDateStr = outputDateFormatDayName.format(date) + " " + getHijriDate(inputDate) + " الموافق ل " + inputDate;
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }
        return hijriDateStr;
    }

    private void DisplayCardHadith() {
        //get the last saved book
        Chapter lastChapter = ChapterUtils.getLastSavedChapter(requireActivity());
        if (lastChapter != null) Log.e("quran_tag", "last chapter " + lastChapter.toString());
        if (lastChapter != null && lastChapter.collectionName != null) {
            Log.e("quran_tag", "last chapter " + lastChapter.chapterTitle_no_tachkil);
            isThereSavedBook = true;
            String destination = getCollectionArabicName(lastChapter.collectionName) + " > " + lastChapter.bookName + " > ";
            binding.tvDestination.setText(destination);
            binding.tvChapter.setText(lastChapter.chapterTitle_no_tachkil);
        } else {
            Log.e("quran_tag", "last chapter is null");
            isThereSavedBook = false;
            binding.tvChapter.setVisibility(View.GONE);
            binding.tvDestination.setText("سيظهر هنا اخر كتاب قمت بحفظه");
        }
    }

    @SuppressLint("SetTextI18n")
    private void DisplayDateinInTimeCard() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        String dateString = dateFormat.format(calendar.getTime());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvDate.setText(getDateArabicName(dateString));
        } else {
            try {
                //there no hijri date
                DateFormatSymbols arabicSymbols = new DateFormatSymbols(new Locale("ar"));
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE d MMMM yyyy", arabicSymbols);
                binding.tvDate.setText(outputDateFormat.format(dateString));
            } catch (Exception e) {
                Log.e(TAG, "error in hijri date " + e.getMessage());
            }
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
            getTheNextPrayer(PrayerTimesToday,FajrTimeTommorow);
        });

        viewModel.getRandomDikr().observe(getViewLifecycleOwner(), adkarModel -> {
            if (adkarModel != null) {
                binding.tvCategory.setText(adkarModel.getCategory());
                binding.tvDikrBody.setText(adkarModel.getDikr());
            } else {
                binding.cardviewDikr.setVisibility(View.GONE);
            }
        });

        /*viewModel.getFastDikr().observe(getViewLifecycleOwner(), adkarModelList -> {
            if (adkarModelList != null && adkarModelList.size() > 0) {
                globalAdkarList = adkarModelList;
                adkarCouner = 1;
                binding.includeFastAdkarCard.tvFastAdkarText.setText(adkarModelList.get(0).getDikr());
            }
        });
        */
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

        String text = "بقي على أذان " + nextSalatName + " : ";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), text.indexOf(nextSalatName),
                text.indexOf(nextSalatName) + nextSalatName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvSalatMessage.setText(spannableString);


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
                String text = "حان الان موعد صلاة " + nextSalatName + " : ";

                Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.ffshamel_family_bold);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new CustomTypefaceSpan("", typeface), text.indexOf(nextSalatName),
                        text.indexOf(nextSalatName) + nextSalatName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvSalatMessage.setText(spannableString);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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

    public void getFirstAyaInPagePage(int page) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        MushafDatabase database = MushafDatabase.getInstance(requireActivity());
        AyaDao dao = database.getAyaDao();

        compositeDisposable.add(dao.getFirstAyaInPage(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aya -> {
                    Log.e("logtag", "1 data coming  " + aya.getPureText());
                    binding.tvLastAya.setText(aya.getText());
                }, e -> {
                    Log.e("logtag", "1 data error   " + e.getMessage());
                }));

        //compositeDisposable.clear();
    }

    public String getCollectionArabicName(String collectionName) {
        switch (collectionName) {
            case "bukhari":
                return "صحيح البخاري";
            case "muslim":
                return "صحيح مسلم";
            case "nasai":
                return "سنن النسائي";
            case "ibnmajah":
                return "سنن ابن ماجة";
            case "hisn":
                return "حصن المسلم";
            default:
                return "سنن أبي داود";
        }
    }


    private static class UpdateChapterTitleTask extends AsyncTask<Void, Void, Void> {
        private BookDao bookDao;

        UpdateChapterTitleTask(BookDao bookDao) {
            this.bookDao = bookDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            bookDao.updateChapterTitle();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("quran_position_tag1", "finished");
        }
    }



    private void managePrayerTime(){

        PrayerTimesHelper prayerTimesHelper = new PrayerTimesHelper(requireActivity());
        prayerTimesHelper.setListener(new PrayerTimesHelper.TimesListener() {
            @Override
            public void onPrayerTimesResult(Map<String,DayPrayerTimes> TimesMap) {

            }

            @Override
            public void onNextPrayerNameAndTimeResult(PrayerTimesPreference.PrayerInfo prayerInfo) {

            }

            @Override
            public void onError(String error) {

            }
        });
        prayerTimesHelper.getDayPrayerTimes();

    }

}



