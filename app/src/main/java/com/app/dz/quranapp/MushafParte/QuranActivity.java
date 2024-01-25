package com.app.dz.quranapp.MushafParte;

import static com.app.dz.quranapp.PlayerAudioNotification.Statics.ACTION.STOP_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_FINISHED_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PAUSE_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PROGRESS_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_RESUME_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_SELECT_AYA_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_START_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_AUDIO_ACTION.AUDIO_STOP_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_PREPAREING_FILES_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;
import static com.app.dz.quranapp.Services.ForegroundDownloadBookService.BUFFER_SIZE;
import static com.app.dz.quranapp.Services.ForegroundDownloadAudioService.AppfolderName;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.Entities.AyaAudioLimitsFirebase;
import com.app.dz.quranapp.Entities.Riwaya;
import com.app.dz.quranapp.Entities.Sura;
import com.app.dz.quranapp.Entities.SuraAudioFirebase;
import com.app.dz.quranapp.Entities.SuraDownload;
import com.app.dz.quranapp.MushafParte.TafsirParte.QuranPageTafsirFragment;
import com.app.dz.quranapp.MushafParte.hafs_parte.QuranPageFragment;
import com.app.dz.quranapp.MushafParte.warsh_parte.QuranPageFragmentMultipleRiwayat;
import com.app.dz.quranapp.PlayerAudioNotification.NetworkHelper;
import com.app.dz.quranapp.PlayerAudioNotification.Statics;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.ForegroundPlayAudioService;
import com.app.dz.quranapp.Services.ForegroundDownloadMushafService;
import com.app.dz.quranapp.Services.ForegroundDownloadAudioService;
import com.app.dz.quranapp.FilterButtomSheetclass;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.adhan.AdhanActivity;
import com.app.dz.quranapp.databinding.QuranActivityBinding;
import com.app.dz.quranapp.room.Daos.AyaDao;
import com.app.dz.quranapp.room.MushafDatabase;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class QuranActivity extends AppCompatActivity implements OnFragmentListeners, FilterButtomSheetclass.Bottomsheetlistener, BottomSheetDialogReaders.BottomSheetListener, EasyPermissions.PermissionCallbacks {

    public static final int DOWNLOAD_TYPE_AUDIO = 1;
    public static final int DOWNLOAD_TYPE_MUSHAF_IMAGES = 2;
    public String DOWNLOAD_LINK = "https://drive.google.com/uc?export=download&id=";

    private final static String TAG = QuranActivity.class.getSimpleName();
    public final static Integer TAFSIR_TYPE = 0;
    public final static Integer QURAN_HAFS_TYPE = 1;
    public final static Integer QURAN_WARSH_TYPE = 2;

    private final ArrayList<ModuleFragments> list = new ArrayList<>();
    private int LastPage;
    private int MinPage;
    private AdapterStartFragments adapterPagerForSign;
    private QuranActivityBinding binding;

    private String selectedReader = "Shuraym";
    private int selectedAyaCountInSura = 1;
    private boolean isPlayFromLocal = true;
    private Sura currantSura;
    private Sura playingSura;
    private long Timetotal = 0;
    private AyaDao dao;
    private int lastVisibleLayoutId;
    private int lastHidenLayoutId;
    private int PageType = QURAN_HAFS_TYPE;
    private BroadcastReceiver AudioReceiver;
    private BroadcastReceiver DownloadReceiver;
    private int startPage;
    private Aya selectedAya;
    private MyViewModel viewModel;
    private PublicMethods publicMethods;
    private final int WRITE_REQUEST_CODE_DOWNLOAD = 1;
    private int DownloadCount = 1;
    private Riwaya riwaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = QuranActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        makeStutsBarColored();
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        Intent intent = getIntent();
        publicMethods = PublicMethods.getInstance();
        startPage = intent.getIntExtra("page", 1);
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        manageReaderImage(sharedPreferenceManager.getSelectedReader());

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        PageType = prefs.getInt("page_type", QURAN_HAFS_TYPE);

        Log.e("testag", "startPage" + startPage);
        MushafDatabase database = MushafDatabase.getInstance(this);
        dao = database.getAyaDao();

        setListenrs();

        PrepareAdapterPages(startPage);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("testtag", "onPageSelected " + position + " " + adapterPagerForSign.getCount());
                if (LastPage == 604 && position == 0) return;
                if (adapterPagerForSign.getCount() == position + 1 && MinPage == 1) return;

                if (adapterPagerForSign.getCount() == position + 1) {
                    Log.e("testtag", "1 onPageSelected we adding new items min " + MinPage);
                    PrepareAdapterPages(MinPage);
                }
                if (position == 0) {
                    Log.e("testtag", "0 onPageSelected we adding new items last " + LastPage);
                    PrepareAdapterPages(LastPage);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewModel.getValue().observe(this, s -> binding.included.tvTitle.setText(s));

        AudioReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction()==null) return;
                if (intent.getAction().equals("AUDIO_FINISHED")) {
                    switch (intent.getStringExtra("action")) {
                        case AUDIO_PROGRESS_ACTION:
                            Log.e(TAG, "we recieve PROGRESS");
                            binding.progress.setVisibility(View.VISIBLE);
                            break;
                        case AUDIO_START_ACTION:
                            Log.e(TAG, "we recieve start");
                            playingSura = currantSura;
                            startIcons();
                            break;
                        case AUDIO_PAUSE_ACTION:
                            Log.e(TAG, "we recieve pause");
                            binding.imgPlayClick.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                            break;
                        case AUDIO_RESUME_ACTION:
                            Log.e(TAG, "we recieve resume");
                            binding.imgPlayClick.setImageResource(R.drawable.ic_baseline_pause_24);
                            break;
                        case AUDIO_SELECT_AYA_ACTION:
                            if (PageType == QURAN_HAFS_TYPE) {
                                Aya selectedaya = (Aya) intent.getSerializableExtra("selctedaya");
                                if (selectedaya == null) return;

                                Fragment f = adapterPagerForSign.getItem(binding.viewPager.getCurrentItem());
                                QuranPageFragment quranPageFragment = (QuranPageFragment) f;
                                int currantPage = quranPageFragment.getCurrantPage();
                                if (currantPage == selectedaya.getPage()) {
                                    //this is the fragment that we need
                                    quranPageFragment.selectThisAya(selectedaya);
                                } else if (currantPage + 1 == selectedaya.getPage()) {
//                                    move to next fragment that we need
                                    int o = binding.viewPager.getCurrentItem() - 1;
                                    Log.e("lifecycle", "next fragment " + o);
                                    if (o >= 0 && o < adapterPagerForSign.getCount()) {
                                        Log.e("lifecycle", "here 2");
                                        binding.viewPager.setCurrentItem(o);
                                        Fragment nextFragement = adapterPagerForSign.getItem(binding.viewPager.getCurrentItem());
                                        QuranPageFragment quranPageNextFragment = (QuranPageFragment) nextFragement;
                                        quranPageNextFragment.selectThisAya(selectedaya);
                                    } else Log.e("lifecycle", "here 3");
                                }
                            }
                            break;
                        case AUDIO_FINISHED_ACTION:
                            //unSelectView();
                            currantSura = null;
                            playingSura = null;
                            Audiofinished();
                            break;
                        case AUDIO_STOP_ACTION:
                            currantSura = null;
                            playingSura = null;
                            Log.e(TAG, "we recieve stop");
                            Audiofinished();
                            break;
                    }
                }
            }
        };
        DownloadReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction()==null) return;
                if (intent.getAction().equals("DOWNLOAD_FINISHED")) {
                    switch (intent.getStringExtra("action")) {
                        case PROGRESS_ACTION:
                            int progress = intent.getIntExtra("progress", 0);
                            binding.tvDownloadProgress.setText("" + progress);
                            binding.progressBar.setProgress(progress);
                            break;
                        case DOWNLOAD_CANCEL_ACTION:
                            Log.e(TAG, "we recieve " + DOWNLOAD_CANCEL_ACTION);
                            HandleDownloadStopIcon();
                            break;

                        case DOWNLOAD_ERROR_ACTION:
                            Log.e(TAG, "we recieve " + DOWNLOAD_ERROR_ACTION);
                            HandleDownloadStopIcon();
                            break;
                        case DOWNLOAD_COMPLETE_ACTION:
                            int downloadType = intent.getIntExtra("type", 0);
                            binding.tvDownloadTitle.setText("اكتمل التحميل");
                            binding.downloadLinear.setVisibility(View.GONE);
                            binding.playLinear.setVisibility(View.VISIBLE);

                            if (downloadType == DOWNLOAD_TYPE_AUDIO) PreperAudio();

                            else if (downloadType == DOWNLOAD_TYPE_MUSHAF_IMAGES) {
                                Log.e(TAG, "mushaf download finished ");
                                boolean isExtracted = MushafDownloadFinished();
                                if (isExtracted) changeScreenType(QURAN_WARSH_TYPE);
                            }

                            break;
                        case DOWNLOAD_PREPAREING_FILES_ACTION:
                            Log.e(TAG, "we set progress indeterminate ");
                            binding.tvDownloadTitle.setText("جاري تحضير الملفات ...");
                            binding.progressBar.setIndeterminate(true);
                            break;
                    }
                }
            }
        };


    }

    @SuppressLint("SetTextI18n")
    private void HandleDownloadStopIcon() {
        binding.downloadLinear.setVisibility(View.GONE);
        binding.playLinear.setVisibility(View.VISIBLE);
        binding.tvDownloadProgress.setText("" + 0);
        binding.progressBar.setProgress(0);
    }

    private void startIcons() {
        binding.progress.setVisibility(View.GONE);
        binding.imgPlayClick.setImageResource(R.drawable.ic_baseline_pause_24);
        if (publicMethods.isReaderSelectionAvailable(selectedReader)) {
            binding.imgNext.setImageResource(R.drawable.ic_next33);
            binding.imgBack.setImageResource(R.drawable.ic_next2);
        } else {
            binding.imgNext.setImageResource(R.drawable.ic_next);
            binding.imgBack.setImageResource(R.drawable.ic_back);
        }
        binding.imgStop.setVisibility(View.VISIBLE);
        binding.imgNext.setVisibility(View.VISIBLE);
        binding.imgBack.setVisibility(View.VISIBLE);

    }

    private void makeStutsBarColored() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }
    }

    private void makeStutsBarWhite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }
    }

    private void PrepareAdapterPages(int startPageLocaly) {
        binding.viewPager.setVisibility(View.GONE);
        binding.linearLoading.setVisibility(View.VISIBLE);

        adapterPagerForSign = null;
        startPage = startPageLocaly;
        if (list.size() > 0) list.clear();

        int currantPage = 1;

        if (startPageLocaly <= 594 && startPageLocaly >= 10) {
            LastPage = startPageLocaly + 10;
            MinPage = startPageLocaly - 10;
            currantPage = 10;
            Log.e("testtag", "page<594 and page>10 we are here new Last Page " + LastPage);
        } else {

            if (startPageLocaly < 10) {
                LastPage = startPageLocaly + 10;
                MinPage = 1;
                currantPage = 10;
                Log.e("testtag", "we are here new Last Page " + LastPage);
            }

            if (startPageLocaly > 594) {
                LastPage = 604;
                MinPage = startPageLocaly - 10;
                currantPage = LastPage - startPageLocaly;
            }
        }

        for (int page = LastPage; page >= MinPage; page--) {
            if (page > 0 && page < 605) {
                if (PageType == QURAN_HAFS_TYPE)
                    list.add(new ModuleFragments("الأولى", QuranPageFragment.newInstance(page)));
                else if (PageType == QURAN_WARSH_TYPE) {
                    list.add(new ModuleFragments("الأولى", QuranPageFragmentMultipleRiwayat.newInstance(page)));
                } else
                    list.add(new ModuleFragments("الأولى", QuranPageTafsirFragment.newInstance(page)));
            }
        }

        adapterPagerForSign = new AdapterStartFragments(getSupportFragmentManager());
        adapterPagerForSign.addlist(list);
        binding.viewPager.setAdapter(adapterPagerForSign);
        binding.viewPager.setCurrentItem(currantPage);

        binding.viewPager.setVisibility(View.VISIBLE);
        binding.linearLoading.setVisibility(View.GONE);

    }

    private void setListenrs() {


        binding.included.imgFilter.setOnClickListener(view -> startActivity(new Intent(QuranActivity.this, AdhanActivity.class)));
        binding.tvShare.setOnClickListener(v -> {
            if (selectedAya != null)
                shareAyaTafsir(selectedAya.getText() + " \n " + " التفسير " + "\n" + selectedAya.getTafseer(), "مشاركة");
        });

        binding.imgStop.setOnClickListener(v -> BackOrNextTheAudio(STOP_ACTION));

        binding.tvSave.setOnClickListener(v -> {
            if (selectedAya != null) {
                SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(QuranActivity.this);
                ReadingPosition readingPosition = new ReadingPosition(selectedAya.getSura(), selectedAya.getSuraAya(), selectedAya.getPage(), selectedAya.getText());
                sharedPreferenceManager.saveReadingPosition(readingPosition);
                Toast.makeText(this, "تم حفظ الاية", Toast.LENGTH_SHORT).show();
                binding.linearAyaInfo.setVisibility(View.GONE);
            }
        });

        binding.tvTafsir.setOnClickListener(v -> {
            if (selectedAya != null) dialog_tafsir();
        });

        //todo binding.included.imgFilter.setOnClickListener(v -> showButtomSheet());
      //  binding.included.imgFilter.setOnClickListener(v -> showButtomSheet());

        binding.imgPlayClick.setOnClickListener(v -> {
            Aya currantAya;
            if (adapterPagerForSign.getItem(binding.viewPager.getCurrentItem()) instanceof QuranPageFragment) {

                Fragment f = adapterPagerForSign.getItem(binding.viewPager.getCurrentItem());
                QuranPageFragment quranPageFragment = (QuranPageFragment) f;
                currantAya = quranPageFragment.getCurrantSura();
                selectedAyaCountInSura = currantAya.getSuraAya();
//            Log.e(TAG, "Currant aya " + currantAya.getText());

                if (currantSura != null) {
                    if (currantSura.getId() != currantAya.getSura())
                        InisilizeCurrantSura(currantAya);
                    else ButtonClicked();
                } else {
                    InisilizeCurrantSura(currantAya);
                }
            }
            else if (adapterPagerForSign.getItem(binding.viewPager.getCurrentItem()) instanceof QuranPageFragmentMultipleRiwayat) {
                Fragment f = adapterPagerForSign.getItem(binding.viewPager.getCurrentItem());
                QuranPageFragmentMultipleRiwayat quranPageFragmentWarsh = (QuranPageFragmentMultipleRiwayat) f;
                int currantPage = quranPageFragmentWarsh.getCurrantPage();

                selectedAyaCountInSura = 1;

                if (currantSura != null && playingSura != null) {
                    if (currantSura.getId() != playingSura.getId()) {
                        List<Sura> suraList = getSuraList(currantPage);
                        if (suraList.size() == 1) {
                            currantSura = suraList.get(0);
                            ButtonClicked();
                        } else {
                            displayWhitchSuraDialog(suraList);
                        }
                    } else ButtonClicked();
                } else {
                    List<Sura> suraList = getSuraList(currantPage);
                    if (suraList.size() == 1) {
                        currantSura = suraList.get(0);
                        ButtonClicked();
                    } else {
                        displayWhitchSuraDialog(suraList);
                    }
                }


            }
            else {
                Fragment f = adapterPagerForSign.getItem(binding.viewPager.getCurrentItem());
                QuranPageTafsirFragment fragment = (QuranPageTafsirFragment) f;
                currantAya = fragment.getCurrantSura();

                selectedAyaCountInSura = currantAya.getSuraAya();

                if (currantSura != null) {
                    if (currantSura.getId() != currantAya.getSura())
                        InisilizeCurrantSura(currantAya);
                    else ButtonClicked();
                } else {
                    InisilizeCurrantSura(currantAya);
                }
            }

        });

        binding.imgReader.setOnClickListener(v -> OpenButtomSheet());

  /*      binding.imgReader.setOnLongClickListener(v -> {
            //startActivity(new Intent(QuranActivity.this, ReaderListActivity.class));
            //TODO adding new reader ayat limits
            // new SaveFirebaseAsyncTask_Devloped().execute();
            return true;
        });
*/
        binding.included.imgFull.setOnClickListener(v -> makeItFullScreen());

        binding.imgNext.setOnClickListener(v -> BackOrNextTheAudio(Statics.ACTION.NEXT_AYA_ACTION));
        binding.imgBack.setOnClickListener(v -> BackOrNextTheAudio(Statics.ACTION.BACK_AYA_ACTION));

        binding.included.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.imgCancelDownload.setOnClickListener(v -> StopTheDownload());

    }

    private boolean MushafDownloadFinished() {
        try {
            unzipCSV(QuranActivity.this, "images.zip",getDesitination().getPath(),getZipDesitination().getPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Sura> getSuraList(int currantPage) {
        List<Sura> suraList = new ArrayList<>();
        if (currantPage == 1) suraList.add(new Sura(1, "الفاتحة", "الفاتحة", "الفاتحة", "", 1, 7));

        if (currantPage >= 2 && currantPage <= 49)
            suraList.add(new Sura(2, "البقرة", "البقرة", "البقرة", "", 2, 286));

        if (currantPage >= 50 && currantPage <= 76)
            suraList.add(new Sura(3, "ال عمران", "", "", "", 2, 200));

        if (currantPage >= 77 && currantPage <= 105)
            suraList.add(new Sura(4, "النساء", "", "", "", 2, 176));

        if (currantPage == 106) {
            suraList.add(new Sura(4, "النساء", "", "", "", 2, 176));
            suraList.add(new Sura(5, "المائدة", "", "", "", 2, 120));
        }

        if (currantPage >= 107 && currantPage <= 127) {
            suraList.add(new Sura(5, "المائدة", "", "", "", 2, 120));
        }
        if (currantPage >= 128 && currantPage <= 150) {
            suraList.add(new Sura(6, "الانعام", "", "", "", 2, 165));
        }

        return suraList;
    }

    private void exitFullMode() {
        makeStutsBarColored();
        viewModel.setData(false);
        Log.e("fullmode", "exit full mode");
        CardView relativeLayoutVisible = findViewById(lastVisibleLayoutId);
        CardView relativeLayoutHiden = findViewById(lastHidenLayoutId);
        relativeLayoutVisible.setVisibility(View.VISIBLE);
        relativeLayoutHiden.setVisibility(View.GONE);

        binding.included.includedRelative.setVisibility(View.VISIBLE);
    }

    private void makeItFullScreen() {
        binding.linearAyaInfo.setVisibility(View.GONE);
        makeStutsBarWhite();
        viewModel.setData(true);
        if (binding.playLinear.getVisibility() == View.VISIBLE) {
            lastVisibleLayoutId = binding.playLinear.getId();
            lastHidenLayoutId = binding.downloadLinear.getId();
        } else {
            lastHidenLayoutId = binding.playLinear.getId();
            lastVisibleLayoutId = binding.downloadLinear.getId();
        }

        CardView relativeLayoutVisible = findViewById(lastVisibleLayoutId);
        CardView relativeLayoutHiden = findViewById(lastHidenLayoutId);
        relativeLayoutVisible.setVisibility(View.GONE);
        relativeLayoutHiden.setVisibility(View.GONE);

        binding.included.includedRelative.setVisibility(View.GONE);
    }

    private void Audiofinished() {
        Log.e(TAG, "we recieve finished");
        Timetotal = 0;

        binding.imgPlayClick.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        binding.imgStop.setVisibility(View.GONE);
        binding.imgNext.setVisibility(View.GONE);
        binding.imgBack.setVisibility(View.GONE);
    }

    @SuppressLint("CheckResult")
    private void InisilizeCurrantSura(Aya currantAya) {
        dao.getSuraWithId(currantAya.getSura()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Sura -> {
            Log.e(TAG, "we recieve sura " + Sura.getName());
            currantSura = Sura;
            ButtonClicked();
            //InisilizeAyatList(currantAya);
        }, e -> {
            Log.e("checkdata", "1 data error   " + e.getMessage());
        });
    }

    private void ButtonClicked() {
        final int serviceState = ForegroundPlayAudioService.getState();
        if (serviceState == Statics.STATE_SERVICE.NOT_INIT) PreperAudio();
        else if (serviceState == Statics.STATE_SERVICE.PREPARE || serviceState == Statics.STATE_SERVICE.PLAY)
            PauseTheAudio();
        else if (serviceState == Statics.STATE_SERVICE.PAUSE) PlayTheAudio();
    }

    private void PreperAudio() {
        File file = publicMethods.getSuraFile(selectedReader, currantSura.getId());
        Log.e(TAG, "is file exist " + file.exists() + " path " + file.getPath());
        if (file.exists() && file.canRead()) lunchAudio();
        else displayAudioSourceDialog();
    }

    private void checkDownlaod_PermissionAnd_Start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(QuranActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //we have permission
                startDownload();
            } else {
                //we ask FOr permissions
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE_DOWNLOAD);
            }
        } else {
            startDownload();
            //we do not need permission
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(QuranActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //we have permission
                return true;
            } else {
                //we ask FOr permissions
                return false;
            }
        } else {
            return true;
            //we do not need permission
        }
    }

    private void startDownload() {
        Log.e(TAG, "Received start Intent ");
        binding.downloadLinear.setVisibility(View.VISIBLE);
        binding.playLinear.setVisibility(View.GONE);
        SuraDownload suraDownload = new SuraDownload(selectedReader, currantSura.getAyas(), currantSura.getId(), publicMethods.isReaderSelectionAvailable(selectedReader));
        Intent startIntent = new Intent(QuranActivity.this, ForegroundDownloadAudioService.class);
        startIntent.setAction(Statics.ACTION.START_ACTION);
        startIntent.putExtra("sura", suraDownload);
        ContextCompat.startForegroundService(QuranActivity.this, startIntent);
    }

    public void lunchAudio() {
        binding.linearAyaInfo.setVisibility(View.GONE);
        Intent startIntent = new Intent(QuranActivity.this, ForegroundPlayAudioService.class);
        startIntent.setAction(Statics.ACTION.START_ACTION);
        startIntent.putExtra("SuraNumber", currantSura.getId());
        startIntent.putExtra("startAya", selectedAyaCountInSura);
        startIntent.putExtra("readerName", selectedReader);
        startIntent.putExtra("isFromLocal", isPlayFromLocal);
        startIntent.putExtra("isThereSelection", publicMethods.isReaderSelectionAvailable(selectedReader));
        startService(startIntent);
    }

    private void PlayTheAudio() {
        if (!NetworkHelper.isInternetAvailable(QuranActivity.this)) {
            publicMethods.showNoInternetDialog(QuranActivity.this, "تحقق من الاتصال بالانترنت");
            return;
        }
        binding.imgPlayClick.setImageResource(R.drawable.ic_baseline_pause_24);
        Intent lPauseIntent = new Intent(QuranActivity.this, ForegroundPlayAudioService.class);
        lPauseIntent.setAction(Statics.ACTION.PLAY_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(QuranActivity.this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void PauseTheAudio() {
        binding.imgPlayClick.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        Intent lPauseIntent = new Intent(QuranActivity.this, ForegroundPlayAudioService.class);
        lPauseIntent.setAction(Statics.ACTION.PAUSE_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(QuranActivity.this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void BackOrNextTheAudio(String action) {
        Intent lPauseIntent = new Intent(QuranActivity.this, ForegroundPlayAudioService.class);
        lPauseIntent.setAction(action);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(QuranActivity.this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void StopTheDownload() {
        Intent lPauseIntent = new Intent(QuranActivity.this, ForegroundDownloadAudioService.class);
        lPauseIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(QuranActivity.this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void PauseDownload() {
        Intent lPauseIntent = new Intent(QuranActivity.this, ForegroundDownloadAudioService.class);
        lPauseIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(QuranActivity.this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


    private void stopMushafDownload() {
        Intent lPauseIntent = new Intent(QuranActivity.this, ForegroundDownloadMushafService.class);
        lPauseIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(QuranActivity.this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void showError() {
        Toast.makeText(getApplicationContext(), "No internet access!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void dialog_tafsir() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View layoutView = inflater.inflate(R.layout.dialog_tafsir, null);

        TextView tvAya = layoutView.findViewById(R.id.tv_aya);
        TextView tvTafsir = layoutView.findViewById(R.id.tv_tafsir);
        TextView tvHide = layoutView.findViewById(R.id.tv_hide);

        dialogBuilder.setView(layoutView);
        AlertDialog dialog = dialogBuilder.create();

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);
        dialog.show();

        tvAya.setText("" + selectedAya.getText());
        tvTafsir.setText("" + selectedAya.getTafseer());

        tvHide.setOnClickListener(v -> dialog.dismiss());

    }

    public void displayAudioSourceDialog() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View layoutView = inflater.inflate(R.layout.dialog_online_offline, null);

        RadioGroup radioGroup = layoutView.findViewById(R.id.radio_group);
        TextView btnOk = layoutView.findViewById(R.id.btn_ok);

        dialogBuilder.setView(layoutView);
        AlertDialog dialog = dialogBuilder.create();

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);
        dialog.show();

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            int id = radioGroup1.getCheckedRadioButtonId();
            if (id == R.id.radio_online) {
                isPlayFromLocal = false;
            } else if (id == R.id.radio_download) {
                isPlayFromLocal = true;
            }
        });

        btnOk.setOnClickListener(v -> {
            if (!NetworkHelper.isInternetAvailable(QuranActivity.this)) {
                dialog.dismiss();
                publicMethods.showNoInternetDialog(QuranActivity.this, "تحقق من الاتصال بالانترنت");
                return;
            }
            dialog.dismiss();
            if (isPlayFromLocal) downloadSuraAudiosThePlayAudio();
            else lunchAudio();


        });
    }

    private void downloadSuraAudiosThePlayAudio() {
        binding.linearAyaInfo.setVisibility(View.GONE);
        Toast.makeText(this, "جاري تحميل السورة", Toast.LENGTH_SHORT).show();
        final int serviceState = ForegroundDownloadAudioService.getState();
        if (serviceState == Statics.STATE_SERVICE.NOT_INIT) checkDownlaod_PermissionAnd_Start();
        else if (serviceState == Statics.STATE_SERVICE.PREPARE || serviceState == Statics.STATE_SERVICE.PLAY)
            PauseDownload();

    }

    public String getLocalFileName(String readerName, int suraIndex) {
        return publicMethods.getReaderTag(readerName) + "_" + suraIndex + ".mp3";
    }

    @Override
    public void onTextSizeChanged(int textsize) {

        createNewFragemnts();

    }

    private void createNewFragemnts() {
        Fragment f = adapterPagerForSign.getItem(binding.viewPager.getCurrentItem());
        if (f instanceof QuranPageFragment) {
            QuranPageFragment fragment = (QuranPageFragment) f;
            startPage = fragment.getCurrantPage();
        } else if (f instanceof QuranPageFragmentMultipleRiwayat) {
            QuranPageFragmentMultipleRiwayat fragment = (QuranPageFragmentMultipleRiwayat) f;
            startPage = fragment.getCurrantPage();
        } else {
            QuranPageTafsirFragment fragment = (QuranPageTafsirFragment) f;
            startPage = fragment.getCurrantPage();
        }
        PrepareAdapterPages(startPage);
    }

    @Override
    public void onTypeChanged(Riwaya riwaya) {
        /*if (riwaya.tag != PageType) {
            PageType = type;
            createNewFragemnts();
        }*/
    }

    @Override
    public void onDownloadWarsh(int type) {
        downloadMushaf();
    }

    @Override
    public void onAyaClick(Aya aya) {
        // show aya info layout
        selectedAya = aya;

        if (binding.downloadLinear.getVisibility() != View.VISIBLE) {
            binding.tvTafsir.setVisibility(View.VISIBLE);
            binding.linearAyaInfo.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onHideAyaInfo() {
        // hide aya info layout
        binding.linearAyaInfo.setVisibility(View.GONE);
    }

    @Override
    public void onSaveAndShare(Aya aya) {
        selectedAya = aya;
        binding.tvTafsir.setVisibility(View.GONE);
        binding.linearAyaInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAyaTouch() {

    }

    @Override
    public void onScreenClick() {
        exitFullMode();
    }



    private void manageReaderImage(String selectedRe) {
        Log.e("bottomsheet", "we recieve selected : " + selectedRe);
        //TODO STRING READER NAME
        selectedReader = selectedRe;
        switch (selectedRe) {
            case "Alafasy":
                Glide.with(this).load(R.drawable.alafasy).into(binding.imgReader);
                break;
            case "Shuraym":
                Glide.with(this).load(R.drawable.sharum).into(binding.imgReader);
                break;
            case "Sudais":
                Glide.with(this).load(R.drawable.sudais).into(binding.imgReader);
                break;
            case "Mohammad_al_Tablaway_128kbps":
                Glide.with(this).load(R.drawable.khalil_hosary).into(binding.imgReader);
                break;
            default:
                Glide.with(this).load(R.drawable.abd_baset).into(binding.imgReader);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("AUDIO_FINISHED");
        registerReceiver(AudioReceiver, filter);

        IntentFilter filter_download = new IntentFilter("DOWNLOAD_FINISHED");
        registerReceiver(DownloadReceiver, filter_download);

        final int serviceState = ForegroundPlayAudioService.getState();
        if (serviceState == Statics.STATE_SERVICE.PLAY) {
            startIcons();
        } else if (serviceState == Statics.STATE_SERVICE.PAUSE) {
            startIcons();
            binding.imgPlayClick.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }

        final int serviceDownloadState = ForegroundDownloadAudioService.getState();
        if (serviceDownloadState == Statics.STATE_SERVICE.PLAY) {
            binding.downloadLinear.setVisibility(View.VISIBLE);
            binding.playLinear.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(AudioReceiver);
        unregisterReceiver(DownloadReceiver);
    }

    public void showButtomSheet() {
        FilterButtomSheetclass bottomSheet = new FilterButtomSheetclass(this, this);
        bottomSheet.show(getSupportFragmentManager(), "tag");
    }

    public void shareAyaTafsir(String message, String title) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void OpenButtomSheet() {
        BottomSheetDialogReaders bottomSheet = new BottomSheetDialogReaders();
        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("checkpermision", "onPermission result   requestCode " + requestCode);

        if (requestCode == WRITE_REQUEST_CODE_DOWNLOAD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have access
                startDownload();
            } else {
                // we do not have access

            }


        }


    }

    private void displayWhitchSuraDialog(List<Sura> suraList) {

        String[] stringArray = new String[suraList.size()];
        int i = 0;
        for (Sura sura : suraList) {
            stringArray[i++] = "سورة " + sura.getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر");
        builder.setSingleChoiceItems(stringArray, -1, (dialog, which) -> {
            Log.e(TAG, "which " + which);
            currantSura = suraList.get(which);
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            ButtonClicked();
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onReaderChanger(int readerId) {

    }


    // Todo this function add new reader limits
    class SaveFirebaseAsyncTask_Devloped extends AsyncTask<Void, Void, String> {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        private final MediaPlayer mediaPlayer = new MediaPlayer();

        @Override
        protected String doInBackground(Void... voids) {
            // Perform background task here

            // Write header row
            try {
                String reader = "Mohammad_al_Tablaway_128kbps";
                Log.e(TAG, "we start " + reader);
                for (int suraNumber = 63; suraNumber <= 69; suraNumber++) {
                    SuraAudioFirebase suraAudioFirebase = new SuraAudioFirebase();
                    suraAudioFirebase.readerName = reader;
                    suraAudioFirebase.SuraNumber = suraNumber;

                    List<Aya> ayaList = new ArrayList<>(dao.getAyatWithSuraId(suraNumber));
                    Log.e(TAG, ".............. new suraNumber : " + suraNumber);
                    Timetotal = 0;
                    for (Aya aya : ayaList) {
                        try {
                            int ayaNumberInSura = aya.getSuraAya();
                            String v = "" + "s" + suraNumber + " az " + ayaList.size() + " a " + ayaNumberInSura;
                            Log.e(TAG, v);
                            viewModel.setValue(v);
                            mediaPlayer.setDataSource(QuranActivity.this, Uri.parse(publicMethods.getCorrectUrlAya(reader, suraNumber, ayaNumberInSura)));
                            mediaPlayer.prepare();
                            long duration = mediaPlayer.getDuration();

                            AyaAudioLimitsFirebase aya1 = new AyaAudioLimitsFirebase();
                            aya1.suraAya = aya.getSuraAya();
                            aya1.startAyaTime = Timetotal;
                            aya1.endAyaTime = Timetotal + duration;
                            suraAudioFirebase.ayaAudioList.add(aya1);

                            Timetotal = Timetotal + duration;

                            mediaPlayer.reset();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    DocumentReference docRef = db.collection("suraaudios").document(publicMethods.getReaderTag(reader)).collection(publicMethods.getReaderTag(reader) + "Audio").document(String.valueOf(suraNumber));

                    int finalSuraNumber = suraNumber;
                    docRef.set(suraAudioFirebase).addOnSuccessListener(aVoid -> Log.d(TAG, "Sura " + finalSuraNumber + " successfully written!")).addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                }


                return "Background task completed";

            } catch (Exception e) {
                e.printStackTrace();
                return "we get error " + e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "result " + result);
            Toast.makeText(QuranActivity.this, "complete" + result, Toast.LENGTH_SHORT).show();
        }
    }
    public void unzipCSV(Context context, String zipFileName, String destDirectory, String zipDestDirectory) throws IOException {
        Log.e("zip", "zip start");
        //InputStream is = context.getAssets().open(zipFileName);

        File file = new File(zipDestDirectory);
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file));
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            Log.e("zip", "looping ");
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                Log.e("zip", "the entry is a file");
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
                DownloadCount++;
            } else {
                Log.e("zip", "the entry is a directory");
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            Log.e(TAG, "working ");
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        Log.e(TAG, "zip finished");

        int zipFilesNumber = 8;
        if (DownloadCount == zipFilesNumber) {
            Log.e(TAG, "we hava finished");
        }
    }

    public File getDesitination() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         */
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName);
        /*} else {
            return new File(Environment.getExternalStorageDirectory().getPath() + "/" + AppfolderName + "/" + filename);
        }*/
    }

    public File getZipDesitination() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         */
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName + "/images.zip");
        /*} else {
            return new File(Environment.getExternalStorageDirectory().getPath() + "/" + AppfolderName + "/" + filename);
        }*/
    }

    public void downloadMushaf() {
        final int serviceState = ForegroundDownloadMushafService.getState();
        if (serviceState == Statics.STATE_SERVICE.NOT_INIT) {
            if (isStoragePermissionGranted()) startDownloadMushaf();
            else requestStoragePermission();
        } else if (serviceState == Statics.STATE_SERVICE.PREPARE || serviceState == Statics.STATE_SERVICE.PLAY)
            stopMushafDownload();
    }

    public void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE_DOWNLOAD);
        }
    }

    private void startDownloadMushaf() {

//        https://drive.google.com/file/d/1WXGzEPAgplkRjLui7C-L7iPJcZ6Vtt98/view?usp=share_link
        String driveLink = "1WXGzEPAgplkRjLui7C-L7iPJcZ6Vtt98";

        Log.e(TAG, "Received start Intent ");
        binding.downloadLinear.setVisibility(View.VISIBLE);
        binding.playLinear.setVisibility(View.GONE);
        Intent startIntent = new Intent(QuranActivity.this, ForegroundDownloadMushafService.class);
        startIntent.setAction(Statics.ACTION.START_ACTION);
        startIntent.putExtra("path", getZipDesitination().getPath());
        startIntent.putExtra("url", DOWNLOAD_LINK + driveLink);
        ContextCompat.startForegroundService(QuranActivity.this, startIntent);
    }

    private void changeScreenType(int newtype){
        if (newtype != PageType) {
            PageType = newtype;
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().putInt("page_type",PageType).apply();
            createNewFragemnts();
        }
    }
}
