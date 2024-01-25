package com.app.dz.quranapp.MushafParte.warsh_parte;

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

import com.app.dz.quranapp.Entities.Riwaya;
import com.app.dz.quranapp.MushafParte.MyViewModel;
import com.app.dz.quranapp.MushafParte.OnFragmentListeners;
import com.app.dz.quranapp.databinding.QuranPageFragmentWarshBinding;
import com.bumptech.glide.Glide;

public class QuranPageFragmentMultipleRiwayat extends Fragment {


    public static final String QuranWarchFolderName = "small size";
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_RIWAYA = "riwaya";
    private final static String TAG = QuranPageFragmentMultipleRiwayat.class.getSimpleName();
    private OnFragmentListeners listener;
    private int pageNumber = 1;
    private Riwaya riwaya;
    private QuranPageFragmentWarshBinding binding;
    private boolean isThereSelectedAya = false;
    private MyViewModel StateViewModel;
    private Boolean isfullModeActiveGlobal = false;


    public QuranPageFragmentMultipleRiwayat() {
        // Required empty public constructor
    }


    public static QuranPageFragmentMultipleRiwayat newInstance(int pageNumber, Riwaya riwaya) {
        QuranPageFragmentMultipleRiwayat fragment = new QuranPageFragmentMultipleRiwayat();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        args.putSerializable(ARG_RIWAYA,riwaya);
        fragment.setArguments(args);
        return fragment;
    }
    public static QuranPageFragmentMultipleRiwayat newInstance(int pageNumber) {
        QuranPageFragmentMultipleRiwayat fragment = new QuranPageFragmentMultipleRiwayat();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
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
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
            riwaya = (Riwaya) getArguments().getSerializable(ARG_RIWAYA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = QuranPageFragmentWarshBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity()==null) return;
        StateViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        setListeners();
        setObservers();


        displayMushafImage(pageNumber);


    }

    private void setListeners() {
        binding.imageview.setOnClickListener(v -> {
            if (isfullModeActiveGlobal) {
                Log.e("logtag", "onItemClick");
                listener.onScreenClick();
                return;
            }
            //aya Clicked
            if (isThereSelectedAya) {
                //hide and unselect
                isThereSelectedAya = false;
                listener.onHideAyaInfo();
            } else {
                //select and show layout info
                isThereSelectedAya = true;
                /*
                listener.onSaveAndShare(aya);
            */

            }
        });
    }

    @SuppressLint("CheckResult")
    private void displayMushafImage(int pageNumber) {
        String url = riwaya.quran_page_image_url+pageNumber+".png";
        Glide.with(this).load(url).into(binding.imageview);
    }

    private void setObservers() {
        StateViewModel.getData().observe(getViewLifecycleOwner(), isfullModeActive -> isfullModeActiveGlobal = isfullModeActive);
    }
    public int getCurrantPage() {
        return pageNumber;
    }

}

