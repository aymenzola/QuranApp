package com.app.dz.quranapp.quran.mainQuranFragment;

import static com.app.dz.quranapp.Communs.Statics.ACTION.BACK_SURA_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.CHANGE_READER_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.NEXT_SURA_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.SEEK_ACTION;
import static com.app.dz.quranapp.Communs.Statics.ACTION.STOP_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_FINISHED_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_AUDIO_ACTION.AUDIO_NOT_AVAILABLE_ACTION;
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
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.app.dz.quranapp.Communs.Statics;
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
import com.app.dz.quranapp.databinding.DialogDownloadProgressBinding;
import com.app.dz.quranapp.databinding.DialogOnlineOfflineBinding;
import com.app.dz.quranapp.databinding.FragmentQuranNewBinding;
import com.app.dz.quranapp.quran.TafsirParte.TafsirFragment;
import com.app.dz.quranapp.quran.adapters.AdapterStartFragments;
import com.app.dz.quranapp.quran.adapters.ReadersAdapter;
import com.app.dz.quranapp.quran.adapters.SuraAdapterNew;
import com.app.dz.quranapp.quran.foreignRiwayat.FragmentForeignRiwayat;
import com.app.dz.quranapp.quran.hafs_parte.QuranPageFragment;
import com.app.dz.quranapp.quran.listeners.OnFragmentListeners;
import com.app.dz.quranapp.quran.listeners.OnQuranFragmentListeners;
import com.app.dz.quranapp.quran.models.ModuleFragments;
import com.app.dz.quranapp.quran.models.ReaderAudio;
import com.app.dz.quranapp.quran.models.ReadingPosition;
import com.app.dz.quranapp.quran.models.RiwayaType;
import com.app.dz.quranapp.quran.viewmodels.MyViewModel;
import com.app.dz.quranapp.quran.warsh_parte.FragmentMultipleRiwayat;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.DownloadWorker;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.DrawerMatnParentAdapter;
import com.app.dz.quranapp.ui.activities.QuranSearchParte.ActivitySearchQuran;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class QuranMainFragment extends Fragment implements OnFragmentListeners {


    public static final int DOWNLOAD_TYPE_AUDIO = 1;
    private final static String TAG = QuranMainFragment.class.getSimpleName();
    private static final int FOREIGN_BOOK_WRITE_REQUEST_CODE = 102;
    private static final String QURAN_DOWNLOAD = "quran_download";
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
    private CompositeDisposable compositeDisposable;
    private OneTimeWorkRequest downloadRequest;
    private AlertDialog dialog_download_foreign;
    private Riwaya globalRequestedRiwaya;
    private DialogDownloadProgressBinding binding_download_foreign_version;

    private boolean shouldMoveToPage = false;
    private boolean isSavedPage = false;
    private List<Sura> globalSuraList = new ArrayList<>();
    private DrawerMatnParentAdapter adapterDrawerForeignRiwaya;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Riwaya downloadedRiwaya;
    private BroadcastReceiver downloadForeignReceiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuranNewBinding.inflate(getLayoutInflater(), container, false);
        Log.e("checkQuranTag", "MainQuranFragment onCreateView");
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    private void manageAndUpdatePageInfo() {
        int page = getCurrentPageInfo();
        Log.e("checkSaveTag", "manageAndUpdatePageInfo readingPosition.page is " + readingPosition.toString() + " current page is " + page);
        if (readingPosition.page == null) {
            isSavedPage = false;
            Glide.with(requireActivity()).load(R.drawable.ic_save_new).into(binding.imgSave);
            return;
        }
        if (readingPosition.page == page) {
            isSavedPage = true;
            Glide.with(requireActivity()).load(R.drawable.ic_save).into(binding.imgSave);
        } else {
            isSavedPage = false;
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


        viewModel.getBookMarks().observe(getViewLifecycleOwner(), bookmarks -> {
            if (adapterPagerForSign != null)
                if (bookmarks != null && adapterPagerForSign.getCount() > 0) {
                    loadComplete(bookmarks);
                }
        });


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

        viewModel.getIsForeignPageSaved().observe(getViewLifecycleOwner(), isForeignPageSaved -> {
            if (isForeignPageSaved) {
                isSavedPage = true;
                Glide.with(requireActivity()).load(R.drawable.ic_save).into(binding.imgSave);
            } else {
                isSavedPage = false;
                Glide.with(requireActivity()).load(R.drawable.ic_save_new).into(binding.imgSave);
            }
        });

        viewModel.getData().observe(getViewLifecycleOwner(), isfullModeActive -> {
            if (!isfullModeActive) {
                exitFullMode();
            }
        });

        viewModel.getIsFragmentClicked().observe(getViewLifecycleOwner(), isFragmentClicked -> {
            if (isFragmentClicked) {
                if (binding.linearRiwayaList.isShown())
                    binding.linearRiwayaList.setVisibility(View.GONE);
                if (binding.linearReaders.getVisibility() == View.VISIBLE) hideAudioControlLinear();

            }
        });

        viewModel.getReader().observe(requireActivity(), readerAudio -> {
            Log.e("trak_page", "we recieve the reader " + readerAudio.toString());
            selectedReader = readerAudio;
            manageReaderImage(selectedReader.getReaderImage());
            binding.includeAudioPlaying.tvReaderName.setText(selectedReader.getName());
        });

        viewModel.getAllSura().observe(getViewLifecycleOwner(), suraList -> {
            if (suraList != null && suraList.size() > 0) {
                globalSuraList = suraList;
                showSuraList(suraList);
            }
        });
    }

    private void startIcons() {
        binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_pause_24);

        //show reading progress linear layout
        binding.includeAudioPlaying.progressReadingLinear.setVisibility(View.VISIBLE);

        //hide speed icon at bottom and show stop icon in its place
        binding.includeAudioPlaying.imgChangeSpeed2.setVisibility(View.GONE);
        binding.includeAudioPlaying.imgStop.setVisibility(View.VISIBLE);
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
        if (!isForeignRiwaya() && startPageLocaly > 604) startPageLocaly = 1;
        startPage = startPageLocaly;

        if (isForeignRiwaya()) {
            changeToForeignRiwaya(selectedRiwaya, startPageLocaly);
            return;
        }

        binding.viewPager.setVisibility(View.GONE);

        adapterPagerForSign = null;
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
                    list.add(new ModuleFragments("", TafsirFragment.newInstance(page)));
                } else
                    list.add(new ModuleFragments("", FragmentMultipleRiwayat.newInstance(page, selectedRiwaya)));

            }
        }

        adapterPagerForSign = new AdapterStartFragments(requireActivity().getSupportFragmentManager());
        adapterPagerForSign.addlist(list);

        try {
            binding.viewPager.setAdapter(adapterPagerForSign);
            binding.viewPager.setCurrentItem(currantPage);
        } catch (Exception e) {
            Toast.makeText(requireActivity(), ".", Toast.LENGTH_SHORT).show();
            Log.e("checkError", "error in setting adapter " + e.getMessage());
        }

        binding.viewPager.setVisibility(View.VISIBLE);

    }

    @SuppressLint("SetTextI18n")
    private void setListenrs() {

        binding.imgSearch.setOnClickListener(v -> {
                    if (!isForeignRiwaya()) {
                        startActivity(new Intent(requireActivity(), ActivitySearchQuran.class));
                    } else {
                        Toast.makeText(requireActivity(), "لا يمكن البحث في هده الرواية", Toast.LENGTH_SHORT).show();
                    }
                }
        );

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

        binding.imgMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.END));

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
        binding.includeAudioPlaying.imgChangeSpeed2.setOnClickListener(v -> changeSpeed());

        binding.imgSave.setOnLongClickListener(v -> {
            createNewFolder();
            return true;
        });

        binding.imgOpenReaders.setOnClickListener(v -> {

            if (isForeignRiwaya()) {
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.msg_cannot_play_audio), Toast.LENGTH_SHORT).show();
                return;
            }


            if (binding.linearReaders.getVisibility() == View.VISIBLE) {
                hideAudioControlLinear();
            } else {
                //should change imgOpenReaders src tint
                binding.imgOpenReaders.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.purple_500), android.graphics.PorterDuff.Mode.SRC_IN);

                binding.bottomLinear.setBackgroundResource(R.drawable.shape_card_bottom);
                Glide.with(requireActivity()).load(R.drawable.round_keyboard_arrow_down_24).into(binding.imgExpand);

                binding.linearReaders.setVisibility(View.VISIBLE);
                binding.imgExpand.setVisibility(View.VISIBLE);

                binding.includeAudioPlaying.tvReaderName.setSelected(true);
                binding.includeAudioPlaying.getRoot().setVisibility(View.VISIBLE);

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
                hafsAudioList = getReaderAudioList(requireActivity(), RiwayaType.HAFS.name());

            setCorrectTextColor(RiwayaType.HAFS.name());
            binding.btnHafsReaders.setText("حفص ( " + hafsAudioList.size() + " )");
            adapter_Readers.setNewList(hafsAudioList);
        });

        binding.includeAudioPlaying.readerImage.setOnClickListener(v -> {

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

        binding.btnDismis.setOnClickListener(v -> binding.relativeReaderList.setVisibility(View.GONE));

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

        binding.imgFullScreen.setOnClickListener(v -> makeItFullScreen());

        binding.imgSave.setOnClickListener(v -> saveAyaOrPage());

        binding.includeAudioPlaying.imgPlay.setOnClickListener(v -> playAudioButtonClicked());

    }

    private void hideAudioControlLinear() {
        binding.imgOpenReaders.setColorFilter(null);

        Glide.with(requireActivity()).load(R.drawable.ic_navigat3).into(binding.imgExpand);
        binding.linearReaders.setVisibility(View.GONE);
        binding.imgExpand.setVisibility(View.GONE);
        //binding.recyclerViewReaders.setVisibility(View.GONE);
        binding.bottomLinear.setBackgroundResource(R.drawable.shape_card);
    }

    private void changeSura(String action) {
        if (currantSura == null) {
            // change only sura page
            int page = getCurrentPageNumber();
            Log.e(TAG, "we are moving current page is : " + page);

            if (page < 604 && page > 0)
                getAndMoveToNextSuraPage(page, action.equals(NEXT_SURA_ACTION) ? +1 : -1);


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
                if (isThereServiceActive()) changeAudioAction(action);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSpeed() {
        if (audioPlayingSpeed == speedValues[0]) {
            audioPlayingSpeed = speedValues[1];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[1]);
            binding.includeAudioPlaying.imgChangeSpeed2.setText("x" + speedValues[1]);

        } else if (audioPlayingSpeed == speedValues[1]) {
            audioPlayingSpeed = speedValues[2];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[2]);
            binding.includeAudioPlaying.imgChangeSpeed2.setText("x" + speedValues[2]);
        } else if (audioPlayingSpeed == speedValues[2]) {
            audioPlayingSpeed = speedValues[3];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[3]);
            binding.includeAudioPlaying.imgChangeSpeed2.setText("x" + speedValues[3]);
        } else {
            audioPlayingSpeed = speedValues[0];
            binding.includeAudioPlaying.imgChangeSpeed.setText("x" + speedValues[0]);
            binding.includeAudioPlaying.imgChangeSpeed2.setText("x" + speedValues[0]);
        }

        ChangePlayingAudioSpeed();
    }

    private void saveAyaOrPage() {
        if (isSavedPage) {
            Glide.with(requireActivity()).load(R.drawable.ic_save_new).into(binding.imgSave);
            SharedPreferenceManager.getInstance(requireActivity()).clearReadingPosition();
            handleSavedPageView();
            if (isForeignRiwaya()) {
                FragmentForeignRiwayat fragment = (FragmentForeignRiwayat) getCurrentFragment();
                fragment.updateReadingPosition();
            }
            return;
        }
        ReadingPosition readingPosition;
        if (selectedAya != null) {
            readingPosition = new ReadingPosition(selectedAya.getSura(), selectedAya.getSuraAya(), selectedAya.getPage(), selectedAya.getText(), selectedRiwaya.tag);
            Toast.makeText(requireActivity(), "تم حفظ الاية", Toast.LENGTH_SHORT).show();
        } else if (isForeignRiwaya()) {

            FragmentForeignRiwayat fragment = (FragmentForeignRiwayat) getCurrentFragment();
            int page = fragment.getCurrantPage();
            readingPosition = new ReadingPosition(1, 1, page, "no", selectedRiwaya.tag);
            fragment.setReadingPosition(readingPosition);
            Toast.makeText(requireActivity(), "تم حفظ الصفحة", Toast.LENGTH_SHORT).show();

        } else {

            int page = getCurrentPageNumber();
            readingPosition = new ReadingPosition(1, 1, page, "no", selectedRiwaya.tag);
            Toast.makeText(requireActivity(), "تم حفظ الصفحة", Toast.LENGTH_SHORT).show();

        }
        Glide.with(requireActivity()).load(R.drawable.ic_save).into(binding.imgSave);
        SharedPreferenceManager.getInstance(requireActivity()).saveReadingPosition(readingPosition);
        Log.e("checkSaveTag", "we saving page " + readingPosition.page);

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

        binding.includeAudioPlaying.pregress.setIndeterminate(false);
        binding.includeAudioPlaying.progressPreparingAudio.setIndeterminate(false);
        binding.includeAudioPlaying.progressPreparingAudio.setVisibility(View.GONE);

        binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);

        binding.includeAudioPlaying.imgStop.setVisibility(View.GONE);

        //hide reading progress
        binding.includeAudioPlaying.progressReadingLinear.setVisibility(View.GONE);

        //show speed icon at bottom
        binding.includeAudioPlaying.imgChangeSpeed2.setVisibility(View.VISIBLE);

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

        boolean isAvailable = PublicMethods.getInstance().isAvailableFile(currantSura.getId(), selectedReader.getReaderTag());
        Log.e(TAG, "is file available " + isAvailable + " Tag " + selectedReader.getReaderTag() + " sura " + currantSura.getId());
        if (!isAvailable) {
            Toast.makeText(requireActivity(), "الملف غير متاح", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = publicMethods.getFile(requireActivity(), selectedReader.getReaderTag(), currantSura.getId());
        Log.e(TAG, "is file exist " + file.exists() + " path " + file.getPath());
        if (file.exists() && file.canRead()) lunchAudio();
        else displayAudioSourceDialog();
    }

    private void checkDownloadPermissionAndStart() {

    }


    private void startDownload() {
        Log.e(TAG, "Received start Intent ");

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(startIntent);
        }else {
            requireActivity().startService(startIntent);
        }
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

    private void createNewFragments() {
        startPage = getCurrentPageNumber();
        if (startPage > 604) {
            startPage = 1;
        }
        moveTpage(startPage);
        if (adapterSuraList == null) {
            adapterDrawerForeignRiwaya = null;
            showSuraList(globalSuraList);
        }
    }

    @Override
    public void onAyaClick(Aya aya) {
        selectedAya = aya;
    }

    @Override
    public void onHideAyaInfo() {
    }

    @Override
    public void onSaveAndShare(Aya aya) {
        selectedAya = aya;
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
    }

    private void manageReaderImage(String readerImage) {
        if (isAdded())
            Glide.with(requireActivity()).load(readerImage).into(binding.includeAudioPlaying.readerImage);
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
        } else if (getCurrentFragment() instanceof FragmentMultipleRiwayat) {
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
        } else if (getCurrentFragment() instanceof TafsirFragment) {
            Fragment f = getCurrentFragment();
            TafsirFragment fragment = (TafsirFragment) f;
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
            binding.imgTafsir.setColorFilter(null);
            selectedRiwaya = previousRiwaya;
        } else {
            binding.imgTafsir.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.purple_500), android.graphics.PorterDuff.Mode.SRC_IN);
            previousRiwaya = selectedRiwaya;
            //this is tafsir riwaya
            selectedRiwaya = SharedPreferenceManager.getInstance(requireActivity()).getAllRiwayaList().get(6);
        }
        updateRiwaya(true);
    }

    private void riwayaChanged(Riwaya riwaya) {
        selectedRiwaya = riwaya;
        updateRiwaya(false);
    }

    private void updateRiwaya(boolean isTafsir) {
        binding.tvChangeRiwaya.setText(selectedRiwaya.name);
        adapter_Readers.setNewList(getReaderAudioList1(requireActivity()));
        createNewFragments();
        if (!isTafsir)
            SharedPreferenceManager.getInstance(requireActivity()).saveLastRiwaya(selectedRiwaya);
        binding.linearRiwayaList.setVisibility(View.GONE);
    }

    private void setRiwayaListListeners() {
        List<Riwaya> listRiwaya = SharedPreferenceManager.getInstance(requireActivity()).getAllRiwayaList();
        binding.tvRiwaya1.setOnClickListener(v -> riwayaChanged(listRiwaya.get(0)));
        binding.tvRiwaya2.setOnClickListener(v -> riwayaChanged(listRiwaya.get(1)));
        binding.tvRiwaya3.setOnClickListener(v -> riwayaChanged(listRiwaya.get(2)));
        binding.tvRiwaya4.setOnClickListener(v -> riwayaChanged(listRiwaya.get(3)));

        binding.tvRiwaya5.setOnClickListener(v -> {
            foreignRiwayaClicked(listRiwaya.get(4));
        });

        binding.tvRiwaya6.setOnClickListener(v -> {
            foreignRiwayaClicked(listRiwaya.get(5));
        });
    }

    private void foreignRiwayaClicked(Riwaya riwaya) {
        if (PublicMethods.getInstance().checkIfThisExist(riwaya.fileName, requireActivity())) {
            //file exist
            SharedPreferenceManager.getInstance(requireActivity()).saveLastRiwaya(riwaya);
            changeToForeignRiwaya(riwaya, 1);
        } else {
            //should show ask dialog , check internet
            binding.linearRiwayaList.setVisibility(View.GONE);
            showAskToDownloadDialog(riwaya);
        }
    }


    private int getCurrentPageNumber() {
        Fragment f = getCurrentFragment();
        if (adapterPagerForSign.getItem(binding.viewPager.getCurrentItem()) instanceof FragmentMultipleRiwayat) {
            FragmentMultipleRiwayat quranPageFragmentWarsh = (FragmentMultipleRiwayat) f;
            return quranPageFragmentWarsh.getCurrantPage();

        } else if (adapterPagerForSign.getItem(binding.viewPager.getCurrentItem()) instanceof QuranPageFragment) {
            QuranPageFragment quranPageFragment = (QuranPageFragment) f;
            return quranPageFragment.getCurrantPage();

        } else if (adapterPagerForSign.getItem(binding.viewPager.getCurrentItem()) instanceof TafsirFragment) {
            TafsirFragment quranPageTafsirFragment = (TafsirFragment) f;
            return quranPageTafsirFragment.getCurrantPage();
        } else {
            FragmentForeignRiwayat foreignRiwayat = (FragmentForeignRiwayat) f;
            return foreignRiwayat.getCurrantPage();
        }
    }


    private int getCurrentPageInfo() {
        Fragment f = getCurrentFragment();
        if (!selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name()) && !selectedRiwaya.tag.equals(RiwayaType.TAFSIR_QURAN.name())
                && !selectedRiwaya.tag.equals(RiwayaType.ENGLISH_QURAN.name())
                && !selectedRiwaya.tag.equals(RiwayaType.FRENCH_QURAN.name())
        ) {
            FragmentMultipleRiwayat fragment = (FragmentMultipleRiwayat) f;

            Log.e("juza_tag", "PageInfo current page number" + getCurrentPageNumber() + " fragment.getCurrantPage() " + fragment.getCurrantPage());

            return fragment.getCurrantPage();

        } else if (selectedRiwaya.tag.equals(RiwayaType.HAFS_SMART.name())) {
            QuranPageFragment fragment = (QuranPageFragment) f;
            return fragment.getCurrantPage();

        } else if (selectedRiwaya.tag.equals(RiwayaType.TAFSIR_QURAN.name())) {
            TafsirFragment fragment = (TafsirFragment) f;
            return fragment.getCurrantPage();
        } else {
            FragmentForeignRiwayat fragment = (FragmentForeignRiwayat) f;
            return fragment.getCurrantPage();
        }
    }


    private void showSuraList(List<Sura> suraList) {
        adapterSuraList = new SuraAdapterNew(suraList, requireActivity(), model -> {
            if (currantSura != null) {
                Log.e(TAG, "drawer currantSura not null check if user click the sura id ");
                if (currantSura.getId() == model.getId()) {
                    Log.e(TAG, "drawer user click on same the sura id");
                    binding.drawerLayout.closeDrawer(GravityCompat.END);
                    return;
                }
                Log.e(TAG, "drawer user click on different  sura id");
            }


            binding.drawerLayout.closeDrawer(GravityCompat.END);

            Log.e(TAG, "sura : " + model.toString());
            currantSura = model;
            if (isForeignRiwaya()) {
                int pdfSuraPage = PublicMethods.getInstance().getForignSuraPage(selectedRiwaya.tag, currantSura.getId());
                FragmentForeignRiwayat foreignRiwayat = (FragmentForeignRiwayat) getCurrentFragment();
                foreignRiwayat.changePdfPage(pdfSuraPage);

            } else {
                getSuraPage(currantSura.getId());
            }

        });
        binding.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.nestedRecyclerView.setAdapter(adapterSuraList);

    }

    private boolean isForeignRiwaya() {
        return RiwayaType.FRENCH_QURAN.name().equals(selectedRiwaya.tag) || RiwayaType.ENGLISH_QURAN.name().equals(selectedRiwaya.tag);
    }


    public void getSuraPage(int sura) {
        compositeDisposable.add(dao.getFirstAyaInSuraObservable(sura)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aya -> moveTpage(aya.getPage())));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();
    }

    public void getAndMoveToNextSuraPage(int page, int direction) {
        MushafDatabase database = MushafDatabase.getInstance(requireActivity());
        AyaDao dao = database.getAyaDao();

        compositeDisposable.add(dao.getLastAyaInPage(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aya -> {
                    Log.e(TAG, "we find the current sura is " + aya.getSura());
                    if (aya.getSura() < 114 && aya.getSura() > 0) {
                        int nextOrBackSura = aya.getSura() + direction;
                        Log.e(TAG, "asking for sura number " + aya.getSura() + " page ?");
                        getNextBackSuraPage(nextOrBackSura);
                    } else {
                        Log.e(TAG, "we are in the last or first sura");
                    }
                }));
    }

    public void getNextBackSuraPage(int sura) {
        compositeDisposable.add(dao.getFirstAyaInSuraObservable(sura)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aya -> {
                    Log.e(TAG, "we find the page of " + sura + " its : " + aya.getPage() + " we are moving to it ...");
                    moveTpage(aya.getPage());
                }, e -> {
                    Log.e(TAG, "new page data error   " + e.getMessage());
                }));
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
        startDownload();
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


    private void resetViewsState() {
        currantSura = null;
        playingSura = null;
        Audiofinished();
    }


    /**
     * download foreign versions
     **/


    private void checkIfWeAreDownloadingAndStartDownload(Riwaya riwaya) {
        downloadedRiwaya = riwaya;
        if (!PublicMethods.getInstance().checkNetworkConnection(requireActivity())) {
            Toast.makeText(requireActivity(), "تحقق من الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!askNotificationPermission()) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                Toast.makeText(requireActivity(), "يجب السماح بظهور الاشعارات", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        WorkManager workManager = WorkManager.getInstance(requireActivity());
        ListenableFuture<List<WorkInfo>> workInfos = workManager.getWorkInfosByTag("downloadTag");

        workInfos.addListener(() -> {
            try {
                List<WorkInfo> workInfoList = workInfos.get();
                for (WorkInfo workInfo : workInfoList) {
                    WorkInfo.State state = workInfo.getState();
                    if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                        // Inform the user that a download is already in progress
                        Toast.makeText(requireActivity(), "من فظلك انتظر حتى تكتمل عملية التحميل الحالية", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // If no download is in progress, start a new one
                downloadRequestedRiwaya(riwaya);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
    }

    private void downloadRequestedRiwaya(Riwaya riwaya) {
        downloadedRiwaya =riwaya;
        showDownloadProgress();
        // Start the Worker to download the book.
        downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .addTag("downloadTag")
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(new Data.Builder()
                        .putString("fileUrl", riwaya.getFileUrl())
                        .putString("fileName", riwaya.fileName)
                        .putInt("notifyId", riwaya.id)
                        .putString("action",QURAN_DOWNLOAD)
                        .putString("fileTitle", riwaya.name).build())
                .build();
        WorkManager.getInstance(requireActivity()).enqueue(downloadRequest);

        /*WorkManager.getInstance(requireActivity()).getWorkInfoByIdLiveData(downloadRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null) {
                        int progress = workInfo.getProgress().getInt("progress", 0);
                        String error = workInfo.getProgress().getString("error");
                        Log.e(TAG, "progress  equals " + progress + " error " + error);
                        if (error != null) {
                            Log.e(TAG, " error not null " + error);
                            String message = PublicMethods.getInstance().getUserFriendlyErrorMessage(error);
                            if (dialog_download_foreign != null) {
                                Log.e(TAG, " dialog_download_foreign  not null " + message);

                                binding_download_foreign_version.progressDownload.setVisibility(View.GONE);
                                binding_download_foreign_version.btnCancel.setVisibility(View.GONE);
                                binding_download_foreign_version.tvTitle.setText("تنبيه");
                                binding_download_foreign_version.btnDone.setText("حسنا");
                                binding_download_foreign_version.tvMessage.setText(message);
                            } else {
                                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                            }

                            return;
                        }

                        if (dialog_download_foreign != null) {
                            //should show percentage
                            binding_download_foreign_version.tvMessage.setText("جاري التحميل " + progress + "%");
                            binding_download_foreign_version.progressDownload.setProgress(progress);
                            Log.e(TAG, "progress  equals " + progress);
                            if (progress == 100) {
                                binding_download_foreign_version.progressDownload.setProgress(progress);
                                Log.e(TAG, "progress  equals 100 ");
                                if (dialog_download_foreign != null)
                                    dialog_download_foreign.dismiss();
                                Toast.makeText(requireActivity(), "تم التحميل ", Toast.LENGTH_SHORT).show();
                                if (selectedRiwaya.id == riwaya.id) {
                                    //user stile in the downloaded riwaya so show it
                                    SharedPreferenceManager.getInstance(requireActivity()).saveLastRiwaya(selectedRiwaya);
                                    changeToForeignRiwaya(riwaya, 1);
                                } else {
                                    //user changed the riwaya so just notify him that download finished
                                    //todo send notifcation to user
                                    SharedPreferenceManager.getInstance(requireActivity()).saveLastRiwaya(selectedRiwaya);
                                    changeToForeignRiwaya(riwaya, 1);
                                }
                            }
                        }
                    }
                });
        */
    }


    public void showDownloadProgress() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());

        binding_download_foreign_version = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()),
                R.layout.dialog_download_progress, null, false);
        dialogBuilder.setView(binding_download_foreign_version.getRoot());
        dialog_download_foreign = dialogBuilder.create();

        if (dialog_download_foreign.getWindow() != null)
            dialog_download_foreign.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog_download_foreign.setCancelable(false);
        dialog_download_foreign.show();

        binding_download_foreign_version.progressDownload.setProgress(0);

        binding_download_foreign_version.btnDone.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "سيتم ارسال اشعار لك عند انتهاء التحميل", Toast.LENGTH_LONG).show();
            dialog_download_foreign.dismiss();
        });

        binding_download_foreign_version.btnCancel.setOnClickListener(v -> {
            WorkManager.getInstance(requireActivity()).cancelWorkById(downloadRequest.getId());
            dialog_download_foreign.dismiss();
        });


    }

    private void showAskToDownloadDialog(Riwaya riwaya) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
        dialogBuilder.setTitle("هده النسخة غير محملة");
        dialogBuilder.setMessage("هل تريد تحميل النسخة الان ؟");
        dialogBuilder.setPositiveButton("نعم", (dialog, which) -> {
            dialog.dismiss();
            checkIfWeAreDownloadingAndStartDownload(riwaya);
        });
        dialogBuilder.setNegativeButton("لا", (dialog, which) -> {
            binding.linearRiwayaList.setVisibility(View.GONE);
            dialog.dismiss();
        });
        dialogBuilder.show();
        binding.linearRiwayaList.setVisibility(View.GONE);
    }

    private void changeToForeignRiwaya(Riwaya riwaya, int startPageLocaly) {
        selectedRiwaya = riwaya;


        adapterPagerForSign = null;
        if (list.size() > 0) list.clear();
        int sura = currantSura == null ? 1 : currantSura.getId();
        FragmentForeignRiwayat foreignRiwayat = FragmentForeignRiwayat.newInstance(sura, selectedRiwaya);
        list.add(new ModuleFragments("", foreignRiwayat));

        adapterPagerForSign = new AdapterStartFragments(requireActivity().getSupportFragmentManager());
        adapterPagerForSign.addlist(list);
        binding.viewPager.setAdapter(adapterPagerForSign);
        shouldMoveToPage = startPageLocaly != 1;

        binding.linearRiwayaList.setVisibility(View.GONE);
        binding.linearReaders.setVisibility(View.GONE);

        binding.tvChangeRiwaya.setText(selectedRiwaya.name);

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("checkQuranTag", "MainQuranFragment onResume " + startPage);

        PrepareAdapterPages(startPage);
        manageAndUpdatePageInfo();

        IntentFilter filter = new IntentFilter("AUDIO_FINISHED");
        requireActivity().registerReceiver(AudioReceiver, filter);


        IntentFilter filter_download = new IntentFilter(QURAN_DOWNLOAD);
        requireActivity().registerReceiver(downloadForeignReceiver,filter_download);

        final int serviceState = getStateActiveService();
        if (serviceState == Statics.STATE_SERVICE.PLAY) {
            startIcons();
        } else if (serviceState == Statics.STATE_SERVICE.PAUSE) {
            startIcons();
            binding.includeAudioPlaying.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("checkQuranTag", "MainQuranFragment onPause");
        requireActivity().unregisterReceiver(AudioReceiver);
        requireActivity().unregisterReceiver(downloadForeignReceiver);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        publicMethods = PublicMethods.getInstance();
        selectedRiwaya = SharedPreferenceManager.getInstance(requireActivity()).getLastRiwaya();
        if (getArguments() != null) startPage = getArguments().getInt("page", 1);
        else startPage = 1;
        compositeDisposable = new CompositeDisposable();
        dao = MushafDatabase.getInstance(requireActivity()).getAyaDao();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeStutsBarColored();

        viewModel.setReaderWithId(SharedPreferenceManager.getInstance(requireActivity()).getSelectedReaderId());
        viewModel.setReadersList();
        binding.tvChangeRiwaya.setText(selectedRiwaya.name);

        setListenrs();
        setRiwayaListListeners();

        setObservers();
        //PrepareAdapterPages(startPage);
        handleSavedPageView();

        viewModel.setAllSuraList();

        Log.e("checkQuranTag", "MainQuranFragment onViewCreated PrepareAdapterPages " + startPage);


        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.e("checkSaveTag", "onPageSelected " + position + " " + adapterPagerForSign.getCount());
                manageAndUpdatePageInfo();
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

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        showReadersList();


        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (downloadedRiwaya != null)
                            checkIfWeAreDownloadingAndStartDownload(downloadedRiwaya);
                    } else {
                        //permission denied
                        Toast.makeText(requireActivity(), "تم الرفض", Toast.LENGTH_SHORT).show();
                    }
                });

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

                        case AUDIO_FINISHED_ACTION, AUDIO_STOP_ACTION, AUDIO_NOT_AVAILABLE_ACTION -> {
                            resetViewsState();
                        }
                        case AUDIO_ERROR_ACTION -> {

                            String message = intent.getStringExtra("message");
                            if (message != null) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "we receive error" + message);
                            }
                            resetViewsState();
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
                            //check previous max
                            if (max != binding.includeAudioPlaying.pregress.getMax())
                                binding.includeAudioPlaying.pregress.setMax(max);
                            binding.includeAudioPlaying.pregress.setProgress(progress);
                        }
                    }
                }
            }
        };


        downloadForeignReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) return;
                if (action.equals(QURAN_DOWNLOAD)) {
                    String actionType = intent.getStringExtra("type");
                    if (actionType == null) return;

                    switch (actionType) {
                        case DOWNLOAD_CANCEL_ACTION -> handleError("تم الغاء التحميل");
                        case DOWNLOAD_ERROR_ACTION -> handleError(intent.getStringExtra("message"));
                        case DOWNLOAD_COMPLETE_ACTION -> downloadCompleted();
                        case PROGRESS_ACTION -> updateDialogProgress(intent.getIntExtra("progress", 0));
                    }
                }
            }
        };
    }


    public void loadComplete(List<PdfDocument.Bookmark> bookmarks) {
        Log.e(TAG, "we recieve book marks  isForeignRiwaya " + isForeignRiwaya());
        if (isForeignRiwaya()) {
            adapterSuraList = null;
            Fragment f = getCurrentFragment();
            FragmentForeignRiwayat foreignRiwayat = (FragmentForeignRiwayat) f;
            adapterDrawerForeignRiwaya = new DrawerMatnParentAdapter(bookmarks, (bookmark, position) -> {
                foreignRiwayat.changePdfPage((int) bookmark.getPageIdx());
                binding.drawerLayout.closeDrawer(GravityCompat.END);
            });

            binding.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            binding.nestedRecyclerView.setAdapter(adapterDrawerForeignRiwaya);
        }
    }

    public void createNewFolder() {
        // Replace "folderName" with the name of your folder
        File folder = new File(requireActivity().getFilesDir(), "Books");
        // Check if the folder exists
        if (!folder.exists()) {
            // Create the folder
            boolean result = folder.mkdirs();
            Log.e("checkStorageTag", result ? "Folder created" : "Folder not created");
        }

        Log.e("checkStorageTag", "folder path exists" + folder.exists() + " path " + folder.getAbsolutePath());
    }

    private boolean askNotificationPermission() {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openNotificationSetting() {
        Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, QuranMainFragment.this.getActivity().getPackageName());
        startActivity(settingsIntent);
    }


    private void handleError(String error) {
        String message = PublicMethods.getInstance().getUserFriendlyErrorMessage(error);
        if (dialog_download_foreign != null) {
            binding_download_foreign_version.progressDownload.setVisibility(View.GONE);
            binding_download_foreign_version.btnCancel.setVisibility(View.GONE);
            binding_download_foreign_version.tvTitle.setText("تنبيه");
            binding_download_foreign_version.btnDone.setText("حسنا");
            binding_download_foreign_version.tvMessage.setText(message);
        } else {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("SetTextI18n")
    private void updateDialogProgress(int progress) {
        if (binding_download_foreign_version == null) return;
        binding_download_foreign_version.tvMessage.setText("جاري التحميل " + progress + "%");
        binding_download_foreign_version.progressDownload.setProgress(progress);
    }

    private void downloadCompleted() {
        Toast.makeText(requireActivity(), "تم التحميل ", Toast.LENGTH_SHORT).show();

        if (dialog_download_foreign != null && dialog_download_foreign.isShowing())
            dialog_download_foreign.dismiss();

        SharedPreferenceManager.getInstance(requireActivity()).saveLastRiwaya(downloadedRiwaya);
        changeToForeignRiwaya(downloadedRiwaya,1);
    }



}
