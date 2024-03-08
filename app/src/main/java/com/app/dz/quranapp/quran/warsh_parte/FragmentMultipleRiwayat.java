package com.app.dz.quranapp.quran.warsh_parte;

import static com.app.dz.quranapp.Util.QuranInfoManager.getPageSurasNames;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.Riwaya;
import com.app.dz.quranapp.quran.viewmodels.MyViewModel;
import com.app.dz.quranapp.quran.listeners.OnFragmentListeners;
import com.app.dz.quranapp.databinding.QuranPageFragmentWarshBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.text.DecimalFormat;
import java.util.List;

public class FragmentMultipleRiwayat extends Fragment {


    public static final String QuranWarchFolderName = "small size";
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_RIWAYA = "riwaya";
    private final static String TAG = FragmentMultipleRiwayat.class.getSimpleName();
    private OnFragmentListeners listener;
    private int pageNumber = 1;
    private Riwaya riwaya;
    private QuranPageFragmentWarshBinding binding;
    private boolean isThereSelectedAya = false;
    private MyViewModel StateViewModel;
    private Boolean isfullModeActiveGlobal = false;
    private List<Aya> globalAyatList;
    private boolean isImageLoadFailed = false;

    public FragmentMultipleRiwayat() {
        // Required empty public constructor
    }


    public static FragmentMultipleRiwayat newInstance(int pageNumber, Riwaya riwaya) {
        FragmentMultipleRiwayat fragment = new FragmentMultipleRiwayat();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        args.putSerializable(ARG_RIWAYA, riwaya);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentMultipleRiwayat newInstance(int pageNumber) {
        FragmentMultipleRiwayat fragment = new FragmentMultipleRiwayat();
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
        if (getActivity() == null) return;
        StateViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);

        setListeners();
        setObservers();
        StateViewModel.askPageAyaList(pageNumber);

        displayMushafImage(pageNumber);



    }

    private void displayPageInfo(int pageNumber) {
        binding.tvPageNumber.setText(String.valueOf(pageNumber));
        binding.tvJuzNumber.setText(getJuzaName());
        binding.tvSuraName.setText(getSuraName(pageNumber));
    }

    private void setListeners() {
        binding.imageview.setOnClickListener(v -> {

            if (isImageLoadFailed){
                isImageLoadFailed = false;
                displayMushafImage(pageNumber);
                return;
            }

            StateViewModel.setIsFragmentClicked(true);
            if (isfullModeActiveGlobal) {
                StateViewModel.setData(false);

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
        listener.onPageChanged(pageNumber);
        DecimalFormat format = new DecimalFormat("000");
        String url;


        if (riwaya.quran_page_image_url.contains("qurancomplex.gov.sa/issues/hafs")) {
            int pageNumberCorrectInUrl = pageNumber + 3;
            url = riwaya.quran_page_image_url + pageNumberCorrectInUrl + ".jpg";
        } else if (riwaya.quran_page_image_url.contains("qurancomplex") || riwaya.quran_page_image_url.contains("QuranHub") || riwaya.quran_page_image_url.contains("hafs-tajweed"))

            url = riwaya.quran_page_image_url + pageNumber + ".jpg";

        else if (riwaya.quran_page_image_url.contains("GovarJabbar"))

            url = riwaya.quran_page_image_url + format.format(pageNumber) + ".png";
        else
            url = riwaya.quran_page_image_url + pageNumber + ".png";

        Log.e("checkdata", "url " + url);
        // For additional configurations, you can use RequestOptions
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_kounoz)
                .override(Target.SIZE_ORIGINAL); // Load the original size for max quality

        Glide.with(this)
                .load(url)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // This will be called when the load fails
                        // You can show a message here
                        isImageLoadFailed = true;
                        Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false; // return false if you want Glide to handle the error
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // This will be called when the image is ready to be displayed
                        return false;
                    }
                })
                .error(R.drawable.svg2) // This is a fallback drawable in case of error
                .into(binding.imageview);
    }

    private void setObservers() {
        StateViewModel.getData().observe(getViewLifecycleOwner(),isfullModeActive -> {
            isfullModeActiveGlobal = isfullModeActive;
        });

        StateViewModel.getPageAyatList().observe(getViewLifecycleOwner(),ayatList -> {
            if (ayatList != null && ayatList.size() > 0) {
                globalAyatList = ayatList;
                displayPageInfo(pageNumber);
            }
        });
    }

    public int getCurrantPage() {
        return pageNumber;
    }

    /**
     * check if the page contains more then sura
     **/

    public String getJuzaName() {
        if (globalAyatList == null) return "";
        return QuranInfoManager.getInstance().getJuzaNameNumber(globalAyatList.get(0).getJuz());
    }

    public String getSuraName(int pageNumber) {
        String suranName = getPageSurasNames(pageNumber);
        Log.e("juza_tag", "asking fro page ayat list " + pageNumber + " sura name " + suranName);
        return suranName;
    }

}

