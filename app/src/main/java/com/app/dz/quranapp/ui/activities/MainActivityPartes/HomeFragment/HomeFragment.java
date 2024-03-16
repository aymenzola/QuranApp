package com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.app.dz.quranapp.Services.adhan.AlarmBroadcastReceiver;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.Util.UserLocation;
import com.app.dz.quranapp.data.room.Daos.AyaDao;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.data.room.MushafDatabase;
import com.app.dz.quranapp.databinding.FragmentHomeBinding;
import com.app.dz.quranapp.quran.models.ReadingPosition;
import com.app.dz.quranapp.quran.models.RiwayaType;
import com.app.dz.quranapp.ui.activities.AboutActivity;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.chaptreParte.ChapterUtils;
import com.app.dz.quranapp.ui.activities.mahfodat.ActivityMahfodatList;
import com.app.dz.quranapp.ui.activities.subha.AdkarSubhaUtils;
import com.app.dz.quranapp.ui.activities.subha.SubhaActivity;

import java.text.DateFormatSymbols;
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


public class HomeFragment extends Fragment {

    public final static String QURAN_TAG = "quran_tag";
    public final static String TAG = "FragmentQuranList";
    public static final int BOOKS_TYPE = 1;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private int lastPage = 1;
    private String nextSalatName;
    private long countdownDuration;
    private static CountDownTimer count;
    private boolean isThereSavedBook = false;
    private int adkarCouner = 0;
    private long NextPrayerMillis;
    private String savedRiwaya;
    ActivityResultLauncher<Intent> mStartForResult;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
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
        viewModel.setRandomDikr();
        managePrayerTime();


        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        testAlarm(requireActivity());
                    }
                }
        );
    }

    private void setListeners() {

        binding.tvHomeTitle.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                askAlarmPermission();
            } else {
                testAlarm(requireActivity());
            }
        });

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
        binding.includeLastBook.tvMoveLibrary.setOnClickListener(v -> cardLibraryClciked());
        binding.includeLastBook.tvDestination.setOnClickListener(v -> cardLibraryClciked());
        binding.includeLastBook.tvChapter.setOnClickListener(v -> cardLibraryClciked());

        binding.includeLastSavedQuran.tvMove.setOnClickListener(view1 -> OpenMushaf(lastPage));
        binding.tvAbout.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), AboutActivity.class)));

        //adkar clickes
        binding.tvMoveDikr.setOnClickListener(v -> moveToAdkarFragment());
        binding.tvDikrBody.setOnClickListener(v -> moveToAdkarFragment());
        binding.tvCategory.setOnClickListener(v -> moveToAdkarFragment());

        //time card Clickes
        binding.includeCardviewTime.cardviewTime.setOnClickListener(v -> moveToAdhanFragment());
        binding.includeCardviewTime.tvNextPrayerCountdown.setOnClickListener(v -> moveToAdhanFragment());
        binding.includeCardviewTime.tvLocation.setOnClickListener(v -> moveToAdhanFragment());
        binding.includeCardviewTime.tvDate.setOnClickListener(v -> moveToAdhanFragment());
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        DisplayCardHadith();
        checkLocationViability();

        Log.e(QURAN_TAG, "quran onResume");
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        if (sharedPreferenceManager.iSThereAyaSaved()) {
            //we saved aya
            binding.includeLastSavedQuran.tvLastAyaTitle.setVisibility(View.VISIBLE);
            ReadingPosition readingPosition = sharedPreferenceManager.getReadinPosition();
            Log.e("checkSaveTag", "quran onResume 1 readingPosition " + readingPosition.toString());
            if (readingPosition.page == null) return;
            lastPage = readingPosition.page;
            Log.e("checkSaveTag", "quran onResume " + readingPosition.toString());
            if (readingPosition.riwaya == null) return;
            savedRiwaya = readingPosition.riwaya;

            if (readingPosition.riwaya.equals(RiwayaType.ENGLISH_QURAN.name())) {
                binding.includeLastSavedQuran.tvLastAyaTitle.setText("اخر صفحة قمت بحفظها");
                binding.includeLastSavedQuran.tvLastAya.setText("الترجمة الانجليزية للقرآن الكريم الصفحة رقم " + readingPosition.page);
                Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.ffshamel_book);
                binding.includeLastSavedQuran.tvLastAya.setTypeface(typeface);
                float textSize = getResources().getDimension(R.dimen.tv_small_size);
                binding.includeLastSavedQuran.tvLastAya.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            } else if (readingPosition.riwaya.equals(RiwayaType.FRENCH_QURAN.name())) {
                binding.includeLastSavedQuran.tvLastAyaTitle.setText("اخر صفحة قمت بحفظها");
                binding.includeLastSavedQuran.tvLastAya.setText("الترجمة الفرنسية للقرآن الكريم الصفحة رقم " + readingPosition.page);
                Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.ffshamel_book);
                binding.includeLastSavedQuran.tvLastAya.setTypeface(typeface);
                float textSize = getResources().getDimension(R.dimen.tv_small_size);
                binding.includeLastSavedQuran.tvLastAya.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);


            } else {

                if (!readingPosition.ayaText.equals("no")) {
                    binding.includeLastSavedQuran.tvLastAya.setText(readingPosition.ayaText);
                } else {
                    //should get first aya in the page and display it
                    getFirstAyaInPagePage(lastPage);
                }
            }

        } else {
            //default case
            binding.includeLastSavedQuran.tvLastAyaTitle.setVisibility(View.GONE);
            binding.includeLastSavedQuran.tvLastAya.setText("ستظهر هنا اخر اية قمت بحفظها");
        }


    }

    private void checkLocationViability() {
        boolean alreadyExist = SharedPreferenceManager.getInstance(getActivity()).iSLocationAvialable();
        if (alreadyExist) {
            binding.includeCardviewTime.tvLocation.setVisibility(View.VISIBLE);
            UserLocation prevUserLocation = SharedPreferenceManager.getInstance(getActivity()).getUserLocation();
            binding.includeCardviewTime.tvLocation.setText(prevUserLocation.address);

            try {
                DisplayDateinInTimeCard();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            //no location case
            binding.includeCardviewTime.getRoot().setVisibility(View.GONE);
        }
    }

    private void OpenMushaf(int startPage) {
        Bundle bundle = new Bundle();
        if (savedRiwaya != null) {
            SharedPreferenceManager.getInstance(requireActivity()).saveAsLastRiwayaWithName(savedRiwaya);
            bundle.putInt("page", startPage);
        }
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
            String destination = PublicMethods.getInstance().getCollectionArabicName(lastChapter.collectionName) + " - " + lastChapter.bookName;
            binding.includeLastBook.tvDestination.setText(destination);
            binding.includeLastBook.tvChapter.setText(lastChapter.chapterTitle_no_tachkil);
        } else {
            Log.e("quran_tag", "last chapter is null");
            isThereSavedBook = false;
            binding.includeLastBook.tvChapter.setVisibility(View.GONE);
            binding.includeLastBook.tvDestination.setText("سيظهر هنا اخر كتاب قمت بحفظه");
        }
    }

    @SuppressLint("SetTextI18n")
    private void DisplayDateinInTimeCard() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        String dateString = dateFormat.format(calendar.getTime());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            binding.includeCardviewTime.tvDate.setText(getDateArabicName(dateString));
        } else {
            try {
                //there no hijri date
                DateFormatSymbols arabicSymbols = new DateFormatSymbols(new Locale("ar"));
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE d MMMM yyyy", arabicSymbols);
                binding.includeCardviewTime.tvDate.setText(outputDateFormat.format(dateString));
            } catch (Exception e) {
                Log.e(TAG, "error in hijri date " + e.getMessage());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setObservers() {

        viewModel.getRandomDikr().observe(getViewLifecycleOwner(), adkarModel -> {
            if (adkarModel != null) {
                Log.e(TAG, "random adkarModel " + adkarModel.toString());

                //AdkarCountsHelper.getInstance(requireActivity()).getDikrDependOnCurrentTime();
                binding.tvCategory.setText(adkarModel.getCategory());
                binding.tvDikrBody.setText(adkarModel.getDikr());
            } else {
                binding.cardviewDikr.setVisibility(View.GONE);
            }
        });
    }

    private void displayTime(long millisUntilFinished, String foramt) {
        long hours = millisUntilFinished / (1000 * 60 * 60);
        long minutes = (millisUntilFinished / (1000 * 60)) % 60;
        long seconds = (millisUntilFinished / 1000) % 60;

        // display the remaining time in the TextView
        String countdownText = String.format(foramt, hours, minutes, seconds);
        binding.includeCardviewTime.tvNextPrayerCountdown.setText(countdownText);
    }

    public String getHijriDate(String dateString) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate dt = LocalDate.parse(dateString, formatter);
            HijrahDate hijrahDate = HijrahDate.from(dt);
            //String formatted = formatter.format(hijrahDate); // 07/03/1439

            return hijrahDate.get(ChronoField.DAY_OF_MONTH) +
                    " " + QuranInfoManager.getInstance().convertToHijri(hijrahDate.get(ChronoField.MONTH_OF_YEAR)) + " "
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
                    binding.includeLastSavedQuran.tvLastAya.setText(aya.getText());
                }, e -> {
                    Log.e("logtag", "1 data error   " + e.getMessage());
                }));

        //compositeDisposable.clear();
    }

    private void managePrayerTime() {
        Log.e("checktimeTag", "managePrayerTime called");
        if (count != null && binding.includeCardviewTime.tvNextPrayerCountdown.getText().length() > 0) {
            Log.e("checktimeTag", "managePrayerTime count is not null return");
            return;
        } else {
            Log.e("checktimeTag", "managePrayerTime count is null or length " + binding.includeCardviewTime.tvNextPrayerCountdown.getText().length());
            if (count != null) {
                count.cancel();
                count = null;
            }
        }

        PrayerTimesHelper prayerTimesHelper = new PrayerTimesHelper(requireActivity());
        prayerTimesHelper.setListener(new PrayerTimesHelper.TimesListener() {
            @Override
            public void onPrayerTimesResult(Map<String, DayPrayerTimes> TimesMap) {

            }

            @Override
            public void onNextPrayerNameAndTimeResult(PrayerTimesPreference.PrayerInfo prayerInfo) {
                displayPrayerTime(prayerInfo);
            }

            @Override
            public void onError(String error) {

            }
        });
        prayerTimesHelper.getDayPrayerTimes();

    }

    private void displayPrayerTime(PrayerTimesPreference.PrayerInfo prayerInfo) {
        nextSalatName = prayerInfo.prayer_arabic;
        NextPrayerMillis = prayerInfo.prayer_time;

        String text;
        if (prayerInfo.prayer_english_name.equals(PrayerTimesPreference.PrayerNames.SHOUROK.name())) {
            text = "بقي على ";
        } else
            text = "بقي على أذان ";

        binding.includeCardviewTime.tvSalatMessage.setText(text);
        binding.includeCardviewTime.tvSalatName.setText(nextSalatName);

        countdownDuration = NextPrayerMillis - System.currentTimeMillis();
        if (count != null) count = null;


        Log.e("checktimeTag", "setting value to the timer");
        count = new CountDownTimer(countdownDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                displayTime(millisUntilFinished, "-%02d:%02d:%02d");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                // display a message when the timer finishes

                String text;
                if (prayerInfo.prayer_english_name.equals(PrayerTimesPreference.PrayerNames.SHOUROK.name())) {
                    text = "الان وقت الشروق";
                } else
                    text = "حان الان موعد صلاة";


                binding.includeCardviewTime.tvSalatMessage.setText(text);
                binding.includeCardviewTime.tvSalatName.setText(nextSalatName);
            }
        }.start();
    }

    public void testAlarm(Context context) {

        //after five minutes
        long nextPrayerDelay = 6 * 60 * 1000;

        // Create an intent that points to the BroadcastReceiver that you want to trigger
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        // Create a PendingIntent with that intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextPrayerDelay, pendingIntent);
                    Toast.makeText(context, "test alarm scheduled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "can not ScheduleExactAlarms", Toast.LENGTH_SHORT).show();
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextPrayerDelay, pendingIntent);
                Toast.makeText(context, "test alarm scheduled", Toast.LENGTH_SHORT).show();
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextPrayerDelay, pendingIntent);
                Toast.makeText(context, "test alarm scheduled", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void askAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
            mStartForResult.launch(intent);

        }
    }


}



