package com.app.dz.quranapp.quran.foreignRiwayat;

import static com.app.dz.quranapp.Util.QuranInfoManager.getPageSurasNames;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.Riwaya;
import com.app.dz.quranapp.databinding.FragmentForeignRiwayaBinding;
import com.app.dz.quranapp.quran.listeners.OnFragmentListeners;
import com.app.dz.quranapp.quran.models.ReadingPosition;
import com.app.dz.quranapp.quran.models.RiwayaType;
import com.app.dz.quranapp.quran.viewmodels.MyViewModel;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.util.List;

public class FragmentForeignRiwayat extends Fragment {


    private static final String ARG_SURA_NUMBER = "sura_number";
    private static final String ARG_RIWAYA = "riwaya";
    private final static String TAG = FragmentForeignRiwayat.class.getSimpleName();
    private OnFragmentListeners listener;
    private int suraNumber = 1;
    private Riwaya riwaya;
    private FragmentForeignRiwayaBinding binding;
    private MyViewModel StateViewModel;
    private Boolean isfullModeActiveGlobal = false;
    private List<Aya> globalAyatList;
    private ReadingPosition readingPosition;

    public FragmentForeignRiwayat() {
        // Required empty public constructor
    }


    public static FragmentForeignRiwayat newInstance(int sura, Riwaya riwaya) {
        FragmentForeignRiwayat fragment = new FragmentForeignRiwayat();
        Bundle args = new Bundle();
        args.putInt(ARG_SURA_NUMBER, sura);
        args.putSerializable(ARG_RIWAYA, riwaya);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListeners) {
            listener = (OnFragmentListeners) context;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suraNumber = getArguments().getInt(ARG_SURA_NUMBER);
            riwaya = (Riwaya) getArguments().getSerializable(ARG_RIWAYA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForeignRiwayaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) return;
        StateViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);

        setListeners();
        setObservers();
        //StateViewModel.askPageAyaList(suraNumber);

        readingPosition = SharedPreferenceManager.getInstance(requireActivity()).getReadinPosition();

        showPdfFile(readingPosition.page!=null?readingPosition.page:1);

    }

    private void showPdfFile(int moveTopage) {
        String fileName;
        if (riwaya.tag.equals(RiwayaType.FRENCH_QURAN.name())) {
            fileName = "FrenchQuran.pdf";
        } else {
            fileName = "EnglishQuran.pdf";
        }

        binding.pdfViewer.fromFile(PublicMethods.getInstance().getFile(fileName))
                .onPageChange((page, pageCount) -> {
                    if (readingPosition.page!=null) {
                        Log.d("pageinfoTag", "readingPosition page " + readingPosition.page + " page " + page);
                        StateViewModel.setIsForeignPageSaved(readingPosition.page == page);
                    }
                })
                .onLoad(nbPages -> {})
                .defaultPage(moveTopage)
                .swipeHorizontal(true)
                .fitEachPage(false)
                .onError(t -> Log.e(TAG,"onError: " + t.getMessage()))
                .scrollHandle(new DefaultScrollHandle(requireActivity())).load();

        Log.d("pageinfoTag", "moveTopage readingPosition page " +moveTopage);
        binding.pdfViewer.jumpTo(moveTopage);
    }


    private void setListeners() {
        binding.pdfViewer.setOnClickListener(v -> {
            StateViewModel.setIsFragmentClicked(true);
            if (isfullModeActiveGlobal) {
                StateViewModel.setData(false);
                listener.onScreenClick();
            }
        });
    }


    private void setObservers() {
        StateViewModel.getData().observe(getViewLifecycleOwner(), isfullModeActive -> isfullModeActiveGlobal = isfullModeActive);
    }

    public int getCurrantPage() {

        try {
            return binding.pdfViewer.getCurrentPage();
        } catch (Exception e) {
            Log.e(TAG, "getCurrantPage: " + e.getMessage());
            return 1;
        }
    }

    /**
     * check if the page contains more then sura
     **/

    public String getJuzaName() {
        if (globalAyatList == null) return "";
        return QuranInfoManager.getInstance().getJuzaNameNumber(globalAyatList.get(0).getJuz());
    }

    public void changePdfPage(int page) {
        binding.pdfViewer.jumpTo(page);
    }

    public String getSuraName(int pageNumber) {
        return getPageSurasNames(pageNumber);
    }

}
