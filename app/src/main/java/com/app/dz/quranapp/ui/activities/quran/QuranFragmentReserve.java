package com.app.dz.quranapp.ui.activities.quran;

import static com.app.dz.quranapp.Communs.Statics.ACTION.BACK_SURA_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.CHANGE_READER_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.NEXT_SURA_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.SEEK_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.STOP_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_FINISHED_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PAUSE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PLAYING_PROGRESS_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PREPARING_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_PROGRESS_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_RESUME_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_SELECT_AYA_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_START_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_STOP_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_PREPAREING_FILES_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;
import static com.app.dz.quranapp.Util.QuranInfoManager.getSuraList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.FilterButtomSheetclass;
import com.app.dz.quranapp.MushafParte.AdapterStartFragments;
import com.app.dz.quranapp.MushafParte.BottomSheetDialogReadersFragments;
import com.app.dz.quranapp.MushafParte.MessageEvent;
import com.app.dz.quranapp.MushafParte.ModuleFragments;
import com.app.dz.quranapp.MushafParte.MyViewModel;
import com.app.dz.quranapp.MushafParte.OnFragmentListeners;
import com.app.dz.quranapp.MushafParte.OnQuranFragmentListeners;
import com.app.dz.quranapp.MushafParte.ReadersAdapter;
import com.app.dz.quranapp.MushafParte.ReadingPosition;
import com.app.dz.quranapp.MushafParte.TafsirParte.QuranPageTafsirFragment;
import com.app.dz.quranapp.MushafParte.hafs_parte.QuranPageFragment;
import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.MushafParte.riwayat_parte.RiwayaType;
import com.app.dz.quranapp.MushafParte.warsh_parte.QuranPageFragmentMultipleRiwayat;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.QuranServices.ForegroundDownloadAudioService;
import com.app.dz.quranapp.Services.QuranServices.PAudioServiceNoSelection;
import com.app.dz.quranapp.Services.QuranServices.PAudioServiceSelection;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.data.room.Daos.AyaDao;
import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.Riwaya;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.data.room.Entities.SuraAudio;
import com.app.dz.quranapp.data.room.Entities.SuraDownload;
import com.app.dz.quranapp.data.room.MushafDatabase;
import com.app.dz.quranapp.databinding.DialogOnlineOfflineBinding;
import com.app.dz.quranapp.databinding.FragmentQuranNewBinding;
import com.app.dz.quranapp.ui.activities.QuranSearchParte.ActivitySearchQuran;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class QuranFragmentReserve extends Fragment implements OnFragmentListeners,
        FilterButtomSheetclass.Bottomsheetlistener,
        EasyPermissions.PermissionCallbacks {


    public static final int DOWNLOAD_TYPE_AUDIO = 1;
    private final static String TAG = QuranFragmentReserve.class.getSimpleName();
    private final ArrayList<ModuleFragments> list = new ArrayList<>();
    private int LastPage;
    private int MinPage;
    private AdapterStartFragments adapterPagerForSign;
    private FragmentQuranNewBinding binding;
    //private String selectedReader = "Shuraym";
    private int selectedAyaCountInSura = 1;
    private boolean isPlayFromLocal = true;
    private Sura currantSura;
    private Sura playingSura;
    private AyaDao dao;
    private BroadcastReceiver AudioReceiver;
    private BroadcastReceiver DownloadReceiver;
    private int startPage;
    private Aya selectedAya;
    private MyViewModel viewModel;
    private PublicMethods publicMethods;
    private final int WRITE_REQUEST_CODE_DOWNLOAD = 1;
    private Riwaya selectedRiwaya;
    private static ReaderAudio selectedReader;
    private float audioPlayingSpeed = 1f;
    private OnQuranFragmentListeners listener;
    private final float[] speedValues = {1f, 1.5f, 2f, 2.5f};
    private ReadingPosition readingPosition;
    private Riwaya previousRiwaya;
    private ReadersAdapter adapter_Readers;
    private SuraAdapterNew adapterSuraList;
    private List<ReaderAudio> warshAudioList = new ArrayList<>();
    private List<ReaderAudio> hafsAudioList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuranNewBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeStutsBarColored();
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        publicMethods = PublicMethods.getInstance();

        if (getArguments() != null)
            startPage = getArguments().getInt("page");
        else startPage = 1;


        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(requireActivity());
        viewModel.setReaderWithId(sharedPreferenceManager.getSelectedReaderId());
        viewModel.setReadersList();

        selectedRiwaya = SharedPreferenceManager.getInstance(requireActivity()).getLastRiwaya();
        binding.tvChangeRiwaya.setText(selectedRiwaya.name);

        MushafDatabase database = MushafDatabase.getInstance(requireActivity());
        dao = database.getAyaDao();

        setListenrs();
        setRiwayaListListeners();

        setObservers();
        PrepareAdapterPages(startPage);
        handleSavedPageView();
        manageAndUpdatePageInfo();
        viewModel.setAllSuraList();


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
                Log.e("juza_tag", "manageAndUpdatePageInfo called from onPageSelected position " + position);
                manageAndUpdatePageInfo();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        showReadersList();
        AudioReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null) return;
                if (intent.getAction().equals("AUDIO_FINISHED")) {
                    String action = intent.getStringExtra("action");
                    if (action == null) return;

                    switch (action) {
                        case AUDIO_PROGRESS_ACTION -> {
                            binding.includeAudioPlaying.pregress.setIndeterminate(true);
                            binding.includeAudioPlaying.progressPreparingAudio.setVisibility(View.VISIBLE);
                            Log.e(TAG, "we recieve PROGRESS");
                        }
                        case AUDIO_START_ACTION -> {
                            binding.includeAudioPlaying.pregress.setIndeterminate(false);
                            binding.includeAudioPlaying.progressPreparingAudio.setIndeterminate(false);
                            binding.includeAudioPlaying.progressPreparingAudio.setVisibility(View.GONE);
                            Log.e(TAG, "we recieve start");
                            playingSura = currantSura;
                            startIcons();
                        }
                        case AUDIO_PREPARING_ACTION -> {
                            Log.e(TAG, "we recieve preparing");

                        }
                        case AUDIO_PAUSE_ACTION -> {
                            Log.e(TAG, "we recieve pause");
                            binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        }
                        case AUDIO_RESUME_ACTION -> {
                            Log.e(TAG, "we recieve resume");
                            binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                        }
                        case AUDIO_SELECT_AYA_ACTION -> {
                            // select option avialable only in smart mode
                            if (selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name())) {
                                Aya selectedaya = (Aya) intent.getSerializableExtra("selctedaya");
                                if (selectedaya == null) return;

                                Fragment f = getCurrentFragment();
                                QuranPageFragment quranPageFragment = (QuranPageFragment) f;
                                int currantPage = quranPageFragment.getCurrantPage();
                                if (currantPage == selectedaya.getPage()) {
                                    //requireActivity() is the fragment that we need
                                    quranPageFragment.selectThisAya(selectedaya);
                                } else if (currantPage + 1 == selectedaya.getPage()) {
//                                    move to next fragment that we need
                                    int o = binding.viewPager.getCurrentItem() - 1;
                                    Log.e("lifecycle", "next fragment " + o);
                                    if (o >= 0 && o < adapterPagerForSign.getCount()) {
                                        Log.e("lifecycle", "here 2");
                                        binding.viewPager.setCurrentItem(o);
                                        Fragment nextFragement = getCurrentFragment();
                                        QuranPageFragment quranPageNextFragment = (QuranPageFragment) nextFragement;
                                        quranPageNextFragment.selectThisAya(selectedaya);
                                    } else Log.e("lifecycle", "here 3");
                                }
                            }
                        }

                        case AUDIO_FINISHED_ACTION -> {
                            //unSelectView();
                            currantSura = null;
                            playingSura = null;
                            Audiofinished();
                        }
                        case AUDIO_STOP_ACTION -> {
                            currantSura = null;
                            playingSura = null;
                            Log.e(TAG, "we receive stop");
                            Audiofinished();
                        }
                        case AUDIO_ERROR_ACTION -> {
                            Toast.makeText(context, "حدث خطأ ما", Toast.LENGTH_SHORT).show();

                            Log.e(TAG, "we receive error");
                            binding.includeAudioPlaying.pregress.setIndeterminate(true);
                            binding.includeAudioPlaying.progressPreparingAudio.setVisibility(View.VISIBLE);

                            currantSura = null;
                            playingSura = null;
                            Audiofinished();
                        }
                        case CHANGE_READER_ACTION -> {
                            Log.e(TAG, "fragment  receive change reader so play audio again");
                            playAudioButtonClicked();
                        }
                        case AUDIO_PLAYING_PROGRESS_ACTION -> {
                            //Log.e(TAG, "we recieve audio playing progress");
                            int max = intent.getIntExtra("maxProgress", 0);
                            int progress = intent.getIntExtra("progress", 0);
                            displayTime(max, progress);
                            //check privious max
                            if (max != binding.includeAudioPlaying.pregress.getMax())
                                binding.includeAudioPlaying.pregress.setMax(max);
                            binding.includeAudioPlaying.pregress.setProgress(progress);
                        }
                    }
                }
            }
        };
        DownloadReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null) return;
                if (intent.getAction().equals("DOWNLOAD_FINISHED")) {
                    String action = intent.getStringExtra("action");
                    if (action == null) return;
                    switch (action) {
                        case PROGRESS_ACTION -> {
                            int progress = intent.getIntExtra("progress", 0);
                            binding.tvDownloadProgress.setText(String.valueOf(progress));
                            binding.progressBar.setProgress(progress);
                        }
                        case DOWNLOAD_CANCEL_ACTION -> {
                            Log.e(TAG, "we recieve " + DOWNLOAD_CANCEL_ACTION);
                            HandleDownloadStopIcon();
                        }
                        case DOWNLOAD_ERROR_ACTION -> {
                            Log.e(TAG, "we recieve " + DOWNLOAD_ERROR_ACTION);
                            HandleDownloadStopIcon();
                        }
                        case DOWNLOAD_COMPLETE_ACTION -> {
                            int downloadType = intent.getIntExtra("type", 0);
                            binding.tvDownloadTitle.setText("اكتمل التحميل");

                            //binding.downloadLinear.setVisibility(View.GONE);
                            //binding.playLinear.setVisibility(View.VISIBLE);

                            //if (downloadType == DOWNLOAD_TYPE_AUDIO) PreparAudio();
                        }

                        case DOWNLOAD_PREPAREING_FILES_ACTION -> {
                            Log.e(TAG, "we set progress indeterminate ");
                            binding.tvDownloadTitle.setText("جاري تحضير الملفات ...");
                            binding.progressBar.setIndeterminate(true);
                        }
                    }

                }
            }
        };


    }

    @SuppressLint("SetTextI18n")
    private void manageAndUpdatePageInfo() {
        Log.e("juza_tag", "manageAndUpdatePageInfo called");
        PageInfo pageInfo = getCurrentPageInfo();
        /*
        binding.tvPageNumber.setText("ص " + pageInfo.page);
        binding.tvJuzNumber.setText(pageInfo.justName);
        binding.tvSuraName.setText(" " + pageInfo.suraName);
*/
        if (readingPosition.page == pageInfo.page) {
            Glide.with(requireActivity()).load(R.drawable.ic_save).into(binding.imgSave);
        } else {
            Glide.with(requireActivity()).load(R.drawable.ic_save_new).into(binding.imgSave);
        }
    }

    private void displayTime(int maxMillis, int progressMillis) {
        String maxTime = convertMillisToTime(maxMillis);
        String currentTime = convertMillisToTime(progressMillis);
        binding.includeAudioPlaying.itemCurrentTime.setText(currentTime);
        binding.includeAudioPlaying.itemAudioTime.setText(maxTime);
    }

    public String convertMillisToTime(int millis) {
        int hours = (millis / (1000 * 60 * 60));
        int minutes = ((millis / (1000 * 60)) % 60);
        int seconds = (millis / 1000) % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    private void handleSavedPageView() {
        readingPosition = SharedPreferenceManager.getInstance(requireActivity()).getReadinPosition();
    }

    private void setObservers() {

        viewModel.getIsOnBackClicked().observe(getViewLifecycleOwner(), isOnBackClicked -> {
            if (isOnBackClicked) {
                if (binding.linearRiwayaList.getVisibility() == View.VISIBLE) {
                    binding.linearRiwayaList.setVisibility(View.GONE);
                }

                if (binding.relativeReaderList.getVisibility() == View.VISIBLE) {
                    binding.relativeReaderList.setVisibility(View.GONE);
                }

            }
        });

        viewModel.getData().observe(getViewLifecycleOwner(), isfullModeActive -> {
            if (!isfullModeActive) {
                exitFullMode();
            }
        });

        viewModel.getIsFragmentClicked().observe(getViewLifecycleOwner(), isFragmentClicked -> {
            if (isFragmentClicked) {
                if (binding.linearRiwayaList.isShown()) {
                    binding.linearRiwayaList.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getReader().observe(requireActivity(), readerAudio -> {
            Log.e("trak_page", "we recieve the reader " + readerAudio.toString());
            selectedReader = readerAudio;
            manageReaderImage(selectedReader.getReaderImage());
            binding.includeAudioPlaying.tvReaderName.setText(selectedReader.getName());
        });

        viewModel.getAllSura().observe(getViewLifecycleOwner(), suraList -> {
            if (suraList != null && suraList.size() > 0) showSuraList(suraList);
        });
    }

    @SuppressLint("SetTextI18n")
    private void HandleDownloadStopIcon() {
        /*binding.downloadLinear.setVisibility(View.GONE);
        binding.playLinear.setVisibility(View.VISIBLE);
        binding.tvDownloadProgress.setText("" + 0);
        binding.progressBar.setProgress(0);
        */
    }

    private void startIcons() {
        binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_pause_24);

        /*if (selectedReader.isThereSelection()) {
            binding.imgNext.setImageResource(R.drawable.ic_next33);
            binding.imgBack.setImageResource(R.drawable.ic_next2);
        } else {
            binding.imgNext.setImageResource(R.drawable.ic_next);
            binding.imgBack.setImageResource(R.drawable.ic_back);
        }*/

        binding.includeAudioPlaying.imgStop.setVisibility(View.VISIBLE);
        binding.includeAudioPlaying.imgPreviousSura.setVisibility(View.VISIBLE);
        binding.includeAudioPlaying.imgNextSura.setVisibility(View.VISIBLE);

    }

    private void makeStutsBarColored() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.blan));
        }
    }

    private void makeStutsBarWhite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.white));
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

        //todo need reviews
        for (int page = LastPage; page >= MinPage; page--) {
            if (page > 0 && page < 605) {

                // check the selected riwaya to start the correct fragment
                if (selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name()))
                    list.add(new ModuleFragments("", QuranPageFragment.newInstance(page)));
                else if (selectedRiwaya.tag.equals(RiwayaType.TAFSIR_QURAN.name())) {
                    list.add(new ModuleFragments("", QuranPageTafsirFragment.newInstance(page)));
                } else
                    list.add(new ModuleFragments("", QuranPageFragmentMultipleRiwayat.newInstance(page, selectedRiwaya)));

            }
        }

        adapterPagerForSign = new AdapterStartFragments(requireActivity().getSupportFragmentManager());
        adapterPagerForSign.addlist(list);
        binding.viewPager.setAdapter(adapterPagerForSign);
        binding.viewPager.setCurrentItem(currantPage);

        binding.viewPager.setVisibility(View.VISIBLE);
        binding.linearLoading.setVisibility(View.GONE);

    }

    @SuppressLint("SetTextI18n")
    private void setListenrs() {

        binding.imgSearch.setOnClickListener(v -> startActivity(new Intent(requireActivity(), ActivitySearchQuran.class)));

        binding.includeAudioPlaying.imgNextSura.setOnClickListener(v -> changeSura(NEXT_SURA_ACTION));

        binding.includeAudioPlaying.imgPreviousSura.setOnClickListener(v -> changeSura(BACK_SURA_ACTION));

        binding.includeAudioPlaying.pregress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) seekPlayer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // This method is called when the user starts moving the thumb.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // This method is called when the user stops moving the thumb.
            }
        });

        binding.includeAudioPlaying.imgStop.setOnClickListener(v -> changeAudioAction(STOP_ACTION));

        binding.imgMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.END); // Use GravityCompat.START for left-to-right locales
        });

        binding.imgTafsir.setOnClickListener(v -> changeRiwayaToTafsir());

        binding.tvChangeRiwaya.setOnClickListener(v -> {
            if (binding.linearRiwayaList.getVisibility() == View.VISIBLE) {
                binding.linearRiwayaList.setVisibility(View.GONE);
            } else {
                binding.linearRiwayaList.setVisibility(View.VISIBLE);
            }
        });

        binding.includeAudioPlaying.tvReaderName.setSelected(true);
        binding.tvChangeRiwaya.setSelected(true);


        binding.includeAudioPlaying.imgChangeSpeed.setOnClickListener(v -> changeSpeed());
/*
        binding.imgMoveback.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() < adapterPagerForSign.getCount() - 1) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
            }
        });

        binding.imgMovenext.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() > 0) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1);
            }
        });
*/
        binding.imgCloseAudioLayout.setOnClickListener(v -> {
            /*if (binding.recyclerViewReaders.getVisibility() == View.VISIBLE) {
                binding.includeAudioPlaying.getRoot().setVisibility(View.VISIBLE);
                binding.recyclerViewReaders.setVisibility(View.GONE);
                binding.includeAudioPlaying.tvReaderName.setSelected(true);
            } else {

                binding.includeAudioPlaying.getRoot().setVisibility(View.GONE);
                binding.recyclerViewReaders.setVisibility(View.VISIBLE);
            }

            */
        });

        binding.imgOpenReaders.setOnClickListener(v -> {
            if (binding.linearReaders.getVisibility() == View.VISIBLE) {

                Glide.with(requireActivity()).load(R.drawable.ic_navigat3).into(binding.imgExpand);
                binding.linearReaders.setVisibility(View.GONE);
                binding.imgExpand.setVisibility(View.GONE);
                //binding.recyclerViewReaders.setVisibility(View.GONE);
                binding.bottomLinear.setBackgroundResource(R.drawable.shape_card);
            } else {
                binding.bottomLinear.setBackgroundResource(R.drawable.shape_card_bottom);
                Glide.with(requireActivity()).load(R.drawable.round_keyboard_arrow_down_24).into(binding.imgExpand);

                binding.linearReaders.setVisibility(View.VISIBLE);
                binding.imgExpand.setVisibility(View.VISIBLE);

                binding.includeAudioPlaying.tvReaderName.setSelected(true);
                binding.includeAudioPlaying.getRoot().setVisibility(View.VISIBLE);
                // binding.recyclerViewReaders.setVisibility(View.GONE);

                binding.imgCloseAudioLayout.setVisibility(View.GONE);
            }

        });

        binding.btnWarshReaders.setOnClickListener(v -> {
            if (warshAudioList.size() == 0)
                warshAudioList = getReaderAudioList(requireActivity(), RiwayaType.WARCH.name());

            setCorrectTextColor(RiwayaType.WARCH.name());
            binding.btnWarshReaders.setText("ورش ( " + warshAudioList.size() + " )");
            adapter_Readers.setNewList(warshAudioList);
        });

        binding.btnHafsReaders.setOnClickListener(v -> {
            if (hafsAudioList.size() == 0)
                warshAudioList = getReaderAudioList(requireActivity(), RiwayaType.HAFS.name());

            setCorrectTextColor(RiwayaType.HAFS.name());
            binding.btnHafsReaders.setText("حفص ( " + hafsAudioList.size() + " )");
            adapter_Readers.setNewList(hafsAudioList);
        });

        binding.includeAudioPlaying.readerImage.setOnClickListener(v -> {
            //binding.includeAudioPlaying.getRoot().setVisibility(View.GONE);
            //binding.imgCloseAudioLayout.setVisibility(View.VISIBLE);

            binding.relativeReaderList.setVisibility(View.VISIBLE);
            setCorrectTextColor(selectedRiwaya.tag);
            if (selectedRiwaya.tag.contains(RiwayaType.WARCH.name())) {
                //warch
                if (warshAudioList.size() == 0)
                    warshAudioList = getReaderAudioList1(requireActivity());

                binding.btnWarshReaders.setText("ورش ( " + warshAudioList.size() + " )");
                adapter_Readers.setNewList(warshAudioList);
            } else {
                //hafs
                if (hafsAudioList.size() == 0)
                    hafsAudioList = getReaderAudioList1(requireActivity());

                binding.btnHafsReaders.setText("حفص ( " + hafsAudioList.size() + " )");
                adapter_Readers.setNewList(hafsAudioList);
            }
        });

        binding.btnDismis.setOnClickListener(v -> {
            //binding.includeAudioPlaying.getRoot().setVisibility(View.GONE);
            binding.relativeReaderList.setVisibility(View.GONE);
            //binding.imgCloseAudioLayout.setVisibility(View.VISIBLE);
        });

        binding.imgExpand.setOnClickListener(v -> {
            if (binding.linearMore.getVisibility() == View.VISIBLE) {
                Glide.with(requireActivity()).load(R.drawable.ic_navigat3).into(binding.imgExpand);
                binding.linearReaders.setVisibility(View.GONE);
                binding.imgExpand.setVisibility(View.GONE);
            } else {
                //first click
                binding.bottomLinear.setBackgroundResource(R.drawable.shape_card);
                Glide.with(requireActivity()).load(R.drawable.round_keyboard_arrow_down_24).into(binding.imgExpand);
                binding.imgExpand.setVisibility(View.GONE);
                binding.linearMore.setVisibility(View.VISIBLE);
                listener.onHideBottomBar();
            }
        });

        binding.imgFullScreen.setOnClickListener(v -> {
            makeItFullScreen();
        });


        /* binding.tvShare.setOnClickListener(v -> {
            if (selectedAya != null)
                shareAyaTafsir(selectedAya.getText() + " \n " + " التفسير " + "\n" + selectedAya.getTafseer(), "مشاركة");
        }); */

        binding.imgSave.setOnClickListener(v -> saveAyaOrPage());

        /*binding.tvTafsir.setOnClickListener(v -> {
            if (selectedAya != null) dialog_tafsir();
        });*/

        binding.includeAudioPlaying.imgPlay.setOnClickListener(v -> playAudioButtonClicked());

        //binding.imgNext.setOnClickListener(v -> changeAudioAction(Statics.ACTION.NEXT_AYA_ACTION));
        //binding.imgBack.setOnClickListener(v -> changeAudioAction(Statics.ACTION.BACK_AYA_ACTION));

        binding.imgCancelDownload.setOnClickListener(v -> StopTheDownload());

    }

    private void changeSura(String action) {
        if (currantSura == null) {
            // change only sura page
            int page = getCurrentPageNumber();
            Log.e(TAG,"moving to page "+page);
            moveTpage(page);
        } else {
            //change sura page and audio
            int suraId = currantSura.getId();
            if (suraId > 0 && suraId < 115) {
                if (action.equals(NEXT_SURA_ACTION)) {
                    suraId++;
                } else {
                    suraId--;
                }
                if (adapterSuraList != null && adapterSuraList.getSuraItem(suraId) != null) {

                    currantSura = adapterSuraList.getSuraItem(suraId);
                    getSuraPage(currantSura.getId());
                }
                changeAudioAction(action);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSpeed() {
        if (audioPlayingSpeed == speedValues[0]) {
            audioPlayingSpeed = speedValues[1];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[1]);
        } else if (audioPlayingSpeed == speedValues[1]) {
            audioPlayingSpeed = speedValues[2];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[2]);
        } else if (audioPlayingSpeed == speedValues[2]) {
            audioPlayingSpeed = speedValues[3];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[3]);
        } else {
            audioPlayingSpeed = speedValues[0];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[0]);
        }

        ChangePlayingAudioSpeed();
    }

    private void saveAyaOrPage() {
        ReadingPosition readingPosition;
        if (selectedAya != null) {
            readingPosition = new ReadingPosition(selectedAya.getSura(), selectedAya.getSuraAya(), selectedAya.getPage(), selectedAya.getText());
            Toast.makeText(requireActivity(), "تم حفظ الاية", Toast.LENGTH_SHORT).show();
        } else {

            int page = getCurrentPageNumber();
            readingPosition = new ReadingPosition(1, 1, page, "no");
            Glide.with(requireActivity()).load(R.drawable.ic_save).into(binding.imgSave);
            Toast.makeText(requireActivity(), "تم حفظ الصفحة", Toast.LENGTH_SHORT).show();
        }
        SharedPreferenceManager.getInstance(requireActivity()).saveReadingPosition(readingPosition);
        this.readingPosition = readingPosition;
    }

    private void exitFullMode() {
        if (binding.relativeToolBar.getVisibility() == View.GONE) {
            binding.relativeToolBar.setVisibility(View.VISIBLE);
            binding.relativePages.setVisibility(View.VISIBLE);
            binding.bottomLinear.setVisibility(View.VISIBLE);
        }

        makeStutsBarColored();
        Log.e("fullmode", "exit full mode");

    }

    private void makeItFullScreen() {
        makeStutsBarWhite();
        if (binding.relativeToolBar.getVisibility() == View.VISIBLE) {
            binding.relativeToolBar.setVisibility(View.GONE);
            binding.relativePages.setVisibility(View.GONE);
            binding.bottomLinear.setVisibility(View.GONE);
        } else {
            binding.bottomLinear.setVisibility(View.VISIBLE);
            binding.relativePages.setVisibility(View.VISIBLE);
            binding.relativeToolBar.setVisibility(View.VISIBLE);
        }


        viewModel.setData(true);

    }

    private void Audiofinished() {
        binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);

        binding.includeAudioPlaying.imgStop.setVisibility(View.GONE);
        binding.includeAudioPlaying.imgPreviousSura.setVisibility(View.GONE);
        binding.includeAudioPlaying.imgNextSura.setVisibility(View.GONE);

        binding.includeAudioPlaying.itemCurrentTime.setText("");
        binding.includeAudioPlaying.itemAudioTime.setText("");
        binding.includeAudioPlaying.pregress.setProgress(0);

    }

    @SuppressLint("CheckResult")
    private void InisilizeCurrantSura(int suraId) {
        Disposable disposable = dao.getSuraWithId(suraId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Sura -> {
                    Log.e(TAG, "we receive sura " + Sura.getName());
                    currantSura = Sura;
                    ButtonClicked();
                }, e -> {
                    Log.e("checkdata", "1 data error   " + e.getMessage());
                });

        // When you're done with the observable, call
        //disposable.dispose();

    }


    private void PreparAudio() {
        File file = publicMethods.getFile(requireActivity(), selectedReader.getReaderTag(), currantSura.getId());
        Log.e(TAG, "is file exist " + file.exists() + " path " + file.getPath());
        if (file.exists() && file.canRead()) lunchAudio();
        else displayAudioSourceDialog();
    }

    private void checkDownloadPermissionAndStart() {
        if (isStoragePermissionGranted())
            startDownload();
        else
            requestStoragePermission();

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE_DOWNLOAD);
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //check if we have permission
            return EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            return true;
            //we do not need permission
        }
    }

    private void startDownload() {
        Log.e(TAG, "Received start Intent ");

        //should manage download layout in notification or layout
        
        /* binding.downloadLinear.setVisibility(View.VISIBLE);
           binding.playLinear.setVisibility(View.GONE);
        */

        Toast.makeText(requireActivity(), "جاري تحميل السورة", Toast.LENGTH_SHORT).show();

        SuraDownload suraDownload = new SuraDownload(String.valueOf(selectedReader.getId()), currantSura.getAyas(), currantSura.getId(), selectedReader.isThereSelection());
        suraDownload.SuraPage = getCurrentPageNumber();
        Intent startIntent = new Intent(requireActivity(), ForegroundDownloadAudioService.class);
        startIntent.setAction(Statics.ACTION.START_ACTION);
        startIntent.putExtra("sura", suraDownload);
        startIntent.putExtra("reader", selectedReader);
        ContextCompat.startForegroundService(requireActivity(), startIntent);
    }

    public void lunchAudio() {
        Intent startIntent = getIntentActiveService();
        startIntent.setAction(Statics.ACTION.START_ACTION);

        SuraAudio suraAudio = new SuraAudio(
                String.valueOf(selectedReader.getId()),
                selectedAyaCountInSura,
                currantSura.getId(),
                isPlayFromLocal,
                selectedReader.isThereSelection());

        //set the page to use it on open the activity from the notification
        suraAudio.SuraPage = getCurrentPageNumber();

        startIntent.putExtra("suraAudio", suraAudio);
        startIntent.putExtra("selectedReader", selectedReader);
        startIntent.putExtra("speed", audioPlayingSpeed);
        requireActivity().startService(startIntent);
    }

    private void PlayTheAudio() {
        if (!PublicMethods.isInternetAvailable(requireActivity())) {
            publicMethods.showNoInternetDialog(requireActivity(), "تحقق من الاتصال بالانترنت");
            return;
        }
        binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        sendActionToService(getIntentActiveService(), Statics.ACTION.PLAY_ACTION);
    }

    private void ChangePlayingAudioSpeed() {
        if (getStateActiveService() == Statics.STATE_SERVICE.PLAY) {
            Intent intent = getIntentActiveService();
            intent.setAction(Statics.ACTION.CHANGE_SPEED_ACTION);
            intent.putExtra("speed", audioPlayingSpeed);
            PendingIntent lPendingSpeedIntent = PendingIntent.getService(requireActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            try {
                lPendingSpeedIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(requireActivity(), "it is not playing", Toast.LENGTH_SHORT).show();
        }
    }

    private void PauseTheAudio() {
        binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        sendActionToService(getIntentActiveService(), Statics.ACTION.PAUSE_ACTION);
    }

    private void changeAudioAction(String action) {
        //if (getStateActiveService() != Statics.STATE_SERVICE.NOT_INIT)
        sendActionToService(getIntentActiveService(), action);
    }

    private void StopTheDownload() {
        sendActionToService(new Intent(requireActivity(), ForegroundDownloadAudioService.class), Statics.ACTION.STOP_ACTION);
    }

    private void PauseDownload() {
        sendActionToService(new Intent(requireActivity(), ForegroundDownloadAudioService.class), Statics.ACTION.STOP_ACTION);
    }

    private void seekPlayer(int seek) {
        if (getStateActiveService() != Statics.STATE_SERVICE.NOT_INIT)
            sendSeekToService(getIntentActiveService(), seek);
    }

    @SuppressLint("SetTextI18n")
    public void dialog_tafsir() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());

        LayoutInflater inflater = LayoutInflater.from(requireActivity());
        View layoutView = inflater.inflate(R.layout.dialog_tafsir, null);

        TextView tvAya = layoutView.findViewById(R.id.tv_aya);
        TextView tvTafsir = layoutView.findViewById(R.id.tv_tafsir);
        TextView tvHide = layoutView.findViewById(R.id.tv_hide);

        dialogBuilder.setView(layoutView);
        AlertDialog dialog = dialogBuilder.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);
        dialog.show();

        tvAya.setText(selectedAya.getText());
        tvTafsir.setText(selectedAya.getTafseer());

        tvHide.setOnClickListener(v -> dialog.dismiss());

    }


    @Override
    public void onTextSizeChanged(int textsize) {
        createNewFragments();
    }

    private void createNewFragments() {
        startPage = getCurrentPageNumber();
        moveTpage(startPage);
    }

    @Override
    public void onTypeChanged(Riwaya newRiwaya) {
        if (!newRiwaya.equals(selectedRiwaya)) {
            selectedRiwaya = newRiwaya;
            createNewFragments();
        }
    }

    @Override
    public void onDownloadWarsh(int type) {

    }

    @Override
    public void onAyaClick(Aya aya) {
        // show aya info layout
        selectedAya = aya;

       /* if (binding.downloadLinear.getVisibility() != View.VISIBLE) {
            binding.tvTafsir.setVisibility(View.VISIBLE);
            binding.linearAyaInfo.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public void onHideAyaInfo() {
        // hide aya info layout
        //binding.linearAyaInfo.setVisibility(View.GONE);
    }

    @Override
    public void onSaveAndShare(Aya aya) {
        selectedAya = aya;
        //binding.tvTafsir.setVisibility(View.GONE);
        //binding.linearAyaInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAyaTouch() {

    }

    @Override
    public void onScreenClick() {
        exitFullMode();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onPageChanged(int page) {
       // binding.tvPageNumber.setText("ص " + page);
    }


    private void manageReaderImage(String readerImage) {
        if (isAdded())
            Glide.with(requireActivity()).load(readerImage).into(binding.includeAudioPlaying.readerImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("AUDIO_FINISHED");
        requireActivity().registerReceiver(AudioReceiver, filter);

        IntentFilter filter_download = new IntentFilter("DOWNLOAD_FINISHED");
        requireActivity().registerReceiver(DownloadReceiver, filter_download);

        final int serviceState = getStateActiveService();
        if (serviceState == Statics.STATE_SERVICE.PLAY) {
            startIcons();
        } else if (serviceState == Statics.STATE_SERVICE.PAUSE) {
            startIcons();
            binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }

        final int serviceDownloadState = ForegroundDownloadAudioService.getState();
        if (serviceDownloadState == Statics.STATE_SERVICE.PLAY) {
            //file is downloading
            //binding.downloadLinear.setVisibility(View.VISIBLE);
            //binding.playLinear.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(AudioReceiver);
        requireActivity().unregisterReceiver(DownloadReceiver);
    }

    public void showButtomSheet() {
        FilterButtomSheetclass bottomSheet = new FilterButtomSheetclass(this, requireActivity());
        bottomSheet.show(requireActivity().getSupportFragmentManager(), "tag");
    }

    public void shareAyaTafsir(String message, String title) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void OpenButtomSheet() {
        BottomSheetDialogReadersFragments bottomSheet = new BottomSheetDialogReadersFragments(selectedRiwaya, reader -> {
            selectedReader = reader;
            manageReaderImage(reader.getReaderImage());
        });
        bottomSheet.show(requireActivity().getSupportFragmentManager(), "exampleBottomSheet");
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
            }
        }


    }

    private void displayWhichSuraDialog(List<Sura> suraList) {

        String[] stringArray = new String[suraList.size()];
        int i = 0;
        for (Sura sura : suraList) {
            stringArray[i++] = "سورة " + sura.getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("اختر");
        builder.setSingleChoiceItems(stringArray, -1, (dialog, which) -> {
            Log.e(TAG, "which " + which);
            currantSura = suraList.get(which);
        });
        builder.setPositiveButton("حسنا", (dialog, which) -> {
            ButtonClicked();
            dialog.dismiss();
        });

        builder.setNegativeButton("الغاء", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.e("logtag", "onMessageEvent called exit full mode");
        exitFullMode();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnQuranFragmentListeners) {
            listener = (OnQuranFragmentListeners) context;
        } else {
            Log.e("log", "activity dont implimaents Onclicklistnersenttoactivity");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public List<ReaderAudio> getReaderAudioList1(Context context) {
        return PublicMethods.getReadersAudiosListWithRiwaya(context, selectedRiwaya.tag);
    }

    public List<ReaderAudio> getReaderAudioList(Context context, String riwaya) {
        return PublicMethods.getReadersAudiosListWithRiwaya(context, riwaya);
    }

    private void showReadersList() {
        adapter_Readers = new ReadersAdapter(getReaderAudioList1(requireActivity()), requireActivity(), new ReadersAdapter.OnAdapterClickListener() {
            @Override
            public void onClick(ReaderAudio reader, int position) {

            }

            @Override
            public void onAudioPlayClicked(ReaderAudio reader, int position) {
                //reader audio play changed

                selectedReader = reader;
                SharedPreferenceManager.getInstance(requireActivity()).saveSelectedReaderId(reader.getId());
                manageReaderImage(selectedReader.getReaderImage());
                binding.includeAudioPlaying.tvReaderName.setText(selectedReader.getName());

                binding.relativeReaderList.setVisibility(View.GONE);
                binding.includeAudioPlaying.getRoot().setVisibility(View.VISIBLE);
                binding.imgCloseAudioLayout.setVisibility(View.VISIBLE);

                changeAudioAction(CHANGE_READER_ACTION);

            }
        });
        binding.recyclerViewReaders.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerViewReaders.setHasFixedSize(true);
        binding.recyclerViewReaders.setAdapter(adapter_Readers);

        // Create a LinearSnapHelper and attach it to RecyclerView.
        /*LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerViewReaders);
        */
    }

    private void playAudioButtonClicked() {
        Aya currantAya;
        if (getCurrentFragment() instanceof QuranPageFragment) {

            Fragment f = getCurrentFragment();
            QuranPageFragment quranPageFragment = (QuranPageFragment) f;
            currantAya = quranPageFragment.getCurrantSura();
            selectedAyaCountInSura = currantAya.getSuraAya();

            if (currantSura != null) {
                if (currantSura.getId() != currantAya.getSura()) {
                    InisilizeCurrantSura(currantAya.getSura());
                } else {
                    ButtonClicked();
                }
            } else {
                InisilizeCurrantSura(currantAya.getSura());
            }
        } else if (getCurrentFragment() instanceof QuranPageFragmentMultipleRiwayat) {
            int currantPage = getCurrentPageNumber();

            selectedAyaCountInSura = 1;

            if (currantSura != null && playingSura != null) {
                if (currantSura.getId() != playingSura.getId()) {
                    List<Sura> suraList = getSuraList(currantPage);
                    if (suraList.size() == 1) {
                        currantSura = suraList.get(0);
                        ButtonClicked();
                    } else {
                        displayWhichSuraDialog(suraList);
                    }
                } else ButtonClicked();
            } else {
                List<Sura> suraList = getSuraList(currantPage);
                if (suraList.size() == 1) {
                    currantSura = suraList.get(0);
                    ButtonClicked();
                } else {
                    displayWhichSuraDialog(suraList);
                }
            }
        } else {
            Fragment f = getCurrentFragment();
            QuranPageTafsirFragment fragment = (QuranPageTafsirFragment) f;
            currantAya = fragment.getCurrantSura();

            selectedAyaCountInSura = currantAya.getSuraAya();

            if (currantSura != null) {
                if (currantSura.getId() != currantAya.getSura())
                    InisilizeCurrantSura(currantAya.getSura());
                else ButtonClicked();
            } else {
                InisilizeCurrantSura(currantAya.getSura());
            }
        }
    }

    @NonNull
    private Fragment getCurrentFragment() {
        return adapterPagerForSign.getItem(binding.viewPager.getCurrentItem());
    }

    private void changeRiwayaToTafsir() {
        if (selectedRiwaya.tag.equals(RiwayaType.TAFSIR_QURAN.name()) && previousRiwaya != null) {
            selectedRiwaya = previousRiwaya;
        } else {
            previousRiwaya = selectedRiwaya;
            //this is tafsir riwaya
            selectedRiwaya = SharedPreferenceManager.getInstance(requireActivity()).getAllRiwayaList().get(6);
        }
        updateRiwaya();
    }

    private void riwayaChanged(Riwaya riwaya) {
        selectedRiwaya = riwaya;
        updateRiwaya();
    }

    private void updateRiwaya() {
        binding.tvChangeRiwaya.setText(selectedRiwaya.name);
        adapter_Readers.setNewList(getReaderAudioList1(requireActivity()));
        createNewFragments();
        SharedPreferenceManager.getInstance(requireActivity()).saveLastRiwaya(selectedRiwaya);
        binding.linearRiwayaList.setVisibility(View.GONE);
    }

    private void setRiwayaListListeners() {
        List<Riwaya> list = SharedPreferenceManager.getInstance(requireActivity()).getAllRiwayaList();
        binding.tvRiwaya1.setOnClickListener(v -> riwayaChanged(list.get(0)));
        binding.tvRiwaya2.setOnClickListener(v -> riwayaChanged(list.get(1)));
        binding.tvRiwaya3.setOnClickListener(v -> riwayaChanged(list.get(2)));
        binding.tvRiwaya4.setOnClickListener(v -> riwayaChanged(list.get(3)));
        binding.tvRiwaya5.setOnClickListener(v -> riwayaChanged(list.get(4)));
        binding.tvRiwaya6.setOnClickListener(v -> riwayaChanged(list.get(5)));
    }


    private int getCurrentPageNumber() {
        Fragment f = getCurrentFragment();
        if (adapterPagerForSign.getItem(binding.viewPager.getCurrentItem()) instanceof QuranPageFragmentMultipleRiwayat) {
            QuranPageFragmentMultipleRiwayat quranPageFragmentWarsh = (QuranPageFragmentMultipleRiwayat) f;
            return quranPageFragmentWarsh.getCurrantPage();

        } else if (adapterPagerForSign.getItem(binding.viewPager.getCurrentItem()) instanceof QuranPageFragment) {
            QuranPageFragment quranPageFragment = (QuranPageFragment) f;
            return quranPageFragment.getCurrantPage();

        } else {
            QuranPageTafsirFragment quranPageTafsirFragment = (QuranPageTafsirFragment) f;
            return quranPageTafsirFragment.getCurrantPage();
        }
    }


    private PageInfo getCurrentPageInfo() {
        Fragment f = getCurrentFragment();
        if (!selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name()) && !selectedRiwaya.tag.equals(RiwayaType.TAFSIR_QURAN.name())) {
            QuranPageFragmentMultipleRiwayat fragment = (QuranPageFragmentMultipleRiwayat) f;

            Log.e("juza_tag", "PageInfo current page number" + getCurrentPageNumber() + " fragment.getCurrantPage() " + fragment.getCurrantPage());

            return new PageInfo(fragment.getSuraName(getCurrentPageNumber()), fragment.getCurrantPage(), fragment.getJuzaName());

        } else if (selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name())) {
            QuranPageFragment fragment = (QuranPageFragment) f;
            return new PageInfo(fragment.getSuraName(), fragment.getCurrantPage(), fragment.getJuzaName());

        } else {
            QuranPageTafsirFragment fragment = (QuranPageTafsirFragment) f;
            return new PageInfo(fragment.getSuraName(), fragment.getCurrantPage(), fragment.getJuzaName());
        }
    }

    static class PageInfo {
        public String suraName;
        public int page;
        public String justName;

        public PageInfo(String suraName, int page, String justName) {
            this.suraName = suraName;
            this.page = page;
            this.justName = justName;
        }
    }

    private void showSuraList(List<Sura> suraList) {
        adapterSuraList = new SuraAdapterNew(suraList, requireActivity(), model -> {
            if (currantSura != null)
                if (currantSura.getId() == model.getId()) {
                    binding.drawerLayout.closeDrawer(GravityCompat.END); // Use GravityCompat.START for left-to-right locales
                    return;
                }


            binding.drawerLayout.closeDrawer(GravityCompat.END); // Use GravityCompat.START for left-to-right locales

            Log.e("logtag", "sura : " + model.toString());
            currantSura = model;
            getSuraPage(currantSura.getId());
        });
        binding.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.nestedRecyclerView.setAdapter(adapterSuraList);
    }


    public void getSuraPage(int sura) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Log.e("logtag", "getFirstAyaInSuraObservable");
        compositeDisposable.add(dao.getFirstAyaInSuraObservable(sura)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aya -> {
                    Log.e("logtag", "1 data coming  " + aya.getPureText());
                    moveTpage(aya.getPage());
                }, e -> {
                    Log.e("logtag", "1 data error   " + e.getMessage());
                }));

        //compositeDisposable.clear();
    }

    private void moveTpage(int page) {
        PrepareAdapterPages(page);
        manageAndUpdatePageInfo();
    }

    private int getStateActiveService() {
        if (PAudioServiceSelection.getState() == Statics.STATE_SERVICE.NOT_INIT && PAudioServiceNoSelection.getState() == Statics.STATE_SERVICE.NOT_INIT) {
            //the two services are not active so check riwaya
            if (selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name()))
                return PAudioServiceSelection.getState();
            else
                return PAudioServiceNoSelection.getState();
        } else if (PAudioServiceSelection.getState() != Statics.STATE_SERVICE.NOT_INIT) {
            return PAudioServiceSelection.getState();
        } else {
            return PAudioServiceNoSelection.getState();
        }
    }

    @NonNull
    private Intent getIntentActiveService() {
        if (PAudioServiceSelection.getState() == Statics.STATE_SERVICE.NOT_INIT && PAudioServiceNoSelection.getState() == Statics.STATE_SERVICE.NOT_INIT) {
            //the two services are not active so check riwaya
            if (selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name()))
                return new Intent(requireActivity(), PAudioServiceSelection.class);
            else
                return new Intent(requireActivity(), PAudioServiceNoSelection.class);
        } else if (PAudioServiceSelection.getState() != Statics.STATE_SERVICE.NOT_INIT) {
            return new Intent(requireActivity(), PAudioServiceSelection.class);
        } else {
            return new Intent(requireActivity(), PAudioServiceNoSelection.class);
        }
    }

    private boolean isThereServiceActive() {
        return PAudioServiceSelection.getState() != Statics.STATE_SERVICE.NOT_INIT ||
                PAudioServiceNoSelection.getState() != Statics.STATE_SERVICE.NOT_INIT;
    }

    private void sendActionToService(Intent IntentActiveService, String pauseAction) {
        IntentActiveService.setAction(pauseAction);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(requireActivity(), 0, IntentActiveService, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void sendSeekToService(Intent IntentActiveService, int seek) {
        IntentActiveService.putExtra("seek", seek);
        IntentActiveService.setAction(SEEK_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(requireActivity(), 0, IntentActiveService, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void ButtonClicked() {
        final int serviceState = getStateActiveService();
        Log.e(TAG, "service state " + serviceState);

        if (serviceState == Statics.STATE_SERVICE.NOT_INIT)
            PreparAudio();
        else if (serviceState == Statics.STATE_SERVICE.PREPARE) {
            Toast.makeText(requireActivity(), "Preparing", Toast.LENGTH_SHORT).show();
            //do nothing
        } else if (serviceState == Statics.STATE_SERVICE.PLAY)
            PauseTheAudio();
        else if (serviceState == Statics.STATE_SERVICE.PAUSE)
            PlayTheAudio();
    }


    public void displayAudioSourceDialog() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());

        DialogOnlineOfflineBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.dialog_online_offline, null, false);
        dialogBuilder.setView(binding.getRoot());
        AlertDialog dialog = dialogBuilder.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);
        dialog.show();

        binding.linearDownload.setOnClickListener(v -> userChooseAudioSource(dialog, true));

        binding.linearPlay.setOnClickListener(v -> userChooseAudioSource(dialog, false));
    }

    private void userChooseAudioSource(AlertDialog dialog, boolean isPlayFromLoc) {
        dialog.dismiss();

        if (!PublicMethods.isInternetAvailable(requireActivity())) {
            dialog.dismiss();
            publicMethods.showNoInternetDialog(requireActivity(), "تحقق من الاتصال بالانترنت");
            return;
        }
        if (isPlayFromLoc) {
            downloadSuraAudiosThePlayAudio();
            this.isPlayFromLocal = false;
            lunchAudio();
        } else {
            this.isPlayFromLocal = false;
            lunchAudio();
        }
    }

    private void downloadSuraAudiosThePlayAudio() {
        //binding.linearAyaInfo.setVisibility(View.GONE);
        final int serviceState = ForegroundDownloadAudioService.getState();
        if (serviceState == Statics.STATE_SERVICE.NOT_INIT)
            checkDownloadPermissionAndStart();
        else if (serviceState == Statics.STATE_SERVICE.PREPARE || serviceState == Statics.STATE_SERVICE.PLAY)
            PauseDownload();
    }

    private void setCorrectTextColor(String riwaya) {
        if (riwaya.contains(RiwayaType.WARCH.name())) {
            //warch
            binding.btnWarshReaders.setTextColor(requireActivity().getResources().getColor(R.color.purple_500));
            binding.btnHafsReaders.setTextColor(requireActivity().getResources().getColor(R.color.text_color));
        } else {
            //hafs
            binding.btnWarshReaders.setTextColor(requireActivity().getResources().getColor(R.color.text_color));
            binding.btnHafsReaders.setTextColor(requireActivity().getResources().getColor(R.color.purple_500));
        }
    }

}
