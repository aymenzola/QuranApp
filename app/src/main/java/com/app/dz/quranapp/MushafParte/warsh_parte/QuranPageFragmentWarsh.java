package com.app.dz.quranapp.MushafParte.warsh_parte;

import static com.app.dz.quranapp.Services.ForegroundDownloadAudioService.AppfolderName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.MushafParte.AyaPostion;
import com.app.dz.quranapp.MushafParte.MushafPageAdapter;
import com.app.dz.quranapp.MushafParte.MyViewModel;
import com.app.dz.quranapp.MushafParte.OnFragmentListeners;
import com.app.dz.quranapp.databinding.QuranPageFragmentWarshBinding;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class QuranPageFragmentWarsh extends Fragment {

//    public static final String QuranWarchFolderName = "WarchImages";
    public static final String QuranWarchFolderName = "small size";
    private static final String ARG_PAGE_NUMBER = "page_number";
    DecimalFormat decimalFormat = new DecimalFormat("000");

    private String imageId5 = "1-ZpzlDZIDZt0aYFfmBcd6RHroSZLxHKs";
    private String imageId3 = "1Jd4AlM01tcYf6VAy9l3lxCYyU6funr5I";
    private String imageId7 = "1V__HvMM1CsPNdIOBvuKvgTubqRfu43HL";
    private String imageId4 = "1ZDDlyBtsBsgJWJNajyCI69Zftc-i4Rec";
    private String imageId2 = "1_-rGGtXEUtZoVaJJEjQc6jchaMVoEfiJ";
    private String imageId6 = "1cQhfpjOMzfFUIHr_cHJHKp4TNe5fHuSS";
    private String imageId8 = "1gnJCBNvSXRdkidHfo-vQk4Z3wutJ-rGK";
    private String imageId1 = "1z4pwpFOWFlGu3khBY2JITjdYMsBtX-VJ";

    public String DOWNLOAD_LINK = "https://drive.google.com/uc?export=download&id=";

    //example
    //private String image1 = "https://drive.google.com/uc?export=download&id=1V__HvMM1CsPNdIOBvuKvgTubqRfu43HL";

    private Map<Integer, AyaPostion> stringAyaPostionHashMap = new HashMap<>();
    private final static String TAG = QuranPageFragmentWarsh.class.getSimpleName();
    private OnFragmentListeners listener;
    private int pageNumber = 1;
    private QuranPageFragmentWarshBinding binding;
    private AyatPageViewModelWarsh viewModel;
    private MushafPageAdapter adapter;
    private Aya lastAyaInPrivouisPage;
    private boolean isThereSelectedAya = false;
    private boolean isTvClicked = true;
    private int lastSelectedItem = -1; //default -1
    private boolean isTitlereated = false;

    private Aya CurrantAya; //default the first aya in sura
    private Aya DefaulAya; //default the first aya in sura
    private MyViewModel StateViewModel;
    private Boolean isfullModeActiveGlobal = false;


    public QuranPageFragmentWarsh() {
        // Required empty public constructor
    }


    public static QuranPageFragmentWarsh newInstance(int pageNumber) {
        QuranPageFragmentWarsh fragment = new QuranPageFragmentWarsh();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = QuranPageFragmentWarshBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AyatPageViewModelWarsh.class);
        StateViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

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

        //https://raw.githubusercontent.com/BetimShala/quran-images-api/master/quran-images/1.png
        String url = "https://raw.githubusercontent.com/BetimShala/quran-images-api/master/quran-images/"+pageNumber+".png";
        Glide.with(this).load(url).into(binding.imageview);

        /*
        if (getImageDesitination().exists()){
            Log.e("tag","image exist for "+pageNumber);
            Bitmap bitmap = BitmapFactory.decodeFile(getImageDesitination().getPath());
            Glide.with(this).load(bitmap).into(binding.imageview);
        }else {
            Log.e("tag","image does not exist for "+pageNumber);
            Toast.makeText(getActivity(), "image does not exist", Toast.LENGTH_SHORT).show();
        } */
        /*
        String url = "";
        switch (pageNumber) {
            case 1:
                url = DOWNLOAD_LINK + imageId1;
                break;
            case 2:
                url = DOWNLOAD_LINK + imageId2;
                break;
            case 3:
                url = DOWNLOAD_LINK + imageId3;
                break;
            case 4:
                url = DOWNLOAD_LINK + imageId4;
                break;
            case 5:
                url = DOWNLOAD_LINK + imageId5;
                break;
            case 6:
                url = DOWNLOAD_LINK + imageId6;
                break;
            case 7:
                url = DOWNLOAD_LINK + imageId7;
                break;
            case 8:
                url = DOWNLOAD_LINK + imageId8;
                break;
        }

        Glide.with(this).load(url).into(binding.imageview);


        // Check if image file exists in local storage
        /*File file = new File(getActivity().getFilesDir(), "sura" + pageNumber + ".jpg");
        if (file.exists()) {
            // Load image from local storage
            Glide.with(this).load(file).into(binding.imageview);
        } else {
            // Download image from URL and save to local storage

            Glide.with(this).asBitmap().load(DOWNLOAD_LINK + imageId1).downloadOnly(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
// File has been downloaded, now store it for later use
                    String filePath = getActivity().getFilesDir().getPath() + "/image.jpg";
                    File newFile = new File(filePath);
                    try {
                        copyFile(resource, newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

        }*/
    }

    public File getImageDesitination() {
        String pageN = decimalFormat.format(pageNumber);
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName +"/"+QuranWarchFolderName+ "/p "+pageN+".cst");
    }

    private void setObservers() {
        StateViewModel.getData().observe(getViewLifecycleOwner(), isfullModeActive -> {
            isfullModeActiveGlobal = isfullModeActive;
        });

    }

    public int getCurrantPage() {
        return pageNumber;
    }



    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

}

