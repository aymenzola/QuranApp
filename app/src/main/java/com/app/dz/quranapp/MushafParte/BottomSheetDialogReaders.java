package com.app.dz.quranapp.MushafParte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.databinding.BottomSheetLayoutLogoutBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BottomSheetDialogReaders extends BottomSheetDialogFragment implements ReadersAdapter.OnAdapterClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private BottomSheetListener mListener;
    private BottomSheetLayoutLogoutBinding mBinding;
    private MediaPlayer mediaPlayer;
    private int PlayingReaderId = -1;
    private PublicMethods publicMethods;
    private SharedPreferenceManager sharedPreferenceManager;
    private QuranInfoManager quranInfoManager;
    private String DefaultSelectedReader;
    private String CurrantSelectedReader;

    public BottomSheetDialogReaders() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        publicMethods = PublicMethods.getInstance();
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        DefaultSelectedReader = sharedPreferenceManager.getSelectedReader();
        quranInfoManager = QuranInfoManager.getInstance();
        CurrantSelectedReader = DefaultSelectedReader;


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        ReadersAdapter adapter;
        // Create some example data
        List<Reader> readersList = new ArrayList<>();
        readersList.add(new Reader(1, "مشاري العفاسي", quranInfoManager.getReaderName(0)));
        readersList.add(new Reader(2, "سعود الشريم", quranInfoManager.getReaderName(1)));
        readersList.add(new Reader(3, "عبد الرحمن السديس", quranInfoManager.getReaderName(2)));
        readersList.add(new Reader(4, "خليل الحصري", quranInfoManager.getReaderName(3)));
        readersList.add(new Reader(5, "عبد الباسط عبد الصمد", quranInfoManager.getReaderName(4)));
        adapter = new ReadersAdapter(readersList, getActivity(), this);

        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerview.setHasFixedSize(true);
        mBinding.recyclerview.setAdapter(adapter);


        switch (DefaultSelectedReader) {
            case ("Alafasy"):
                adapter.selectItem(0);
                break;
            case ("Shuraym"):
                adapter.selectItem(1);
                break;
            case ("Sudais"):
                adapter.selectItem(2);
                break;
            case ("Mohammad_al_Tablaway_128kbps"):
                adapter.selectItem(3);
                break;
            case ("AbdulBaset/Murattal"):
                adapter.selectItem(4);
                break;
        }

        mBinding.tvSave.setOnClickListener(v -> {
            if (!Objects.equals(DefaultSelectedReader, CurrantSelectedReader)) {
                sharedPreferenceManager.saveSelectedReader(CurrantSelectedReader);
                mListener.onReaderChanger(CurrantSelectedReader);
                Log.e("bottomsheet", "we send to activity "+CurrantSelectedReader);
            }
            dismiss();
        });


    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_layout_logout, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(Reader reader, int position) {
        CurrantSelectedReader = reader.readerEnglishName;
    }

    @Override
    public void onAudioPlayClicked(Reader reader, int position) {
        Toast.makeText(getActivity(), "audio " + reader.readerName, Toast.LENGTH_SHORT).show();
        CurrantSelectedReader = reader.readerEnglishName;

        if (PlayingReaderId == -1) {
            //no playing audio
            new Thread(() -> {
                try {
                    PlayingReaderId = reader.readerId;
                    String url = publicMethods.getCorrectUrlWithReaderRank(reader.readerId, 112);
                    Log.e("bottomsheet", "read url : "+url);
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getActivity(),Uri.parse(url));
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            mediaPlayer.reset();
            if (PlayingReaderId != reader.readerId) {
                //  audio is playing and user click on another one
                new Thread(() -> {
                    try {
                        PlayingReaderId = reader.readerId;
                        String url = publicMethods.getCorrectUrlWithReaderRank(reader.readerId, 112);
                        mediaPlayer.setDataSource(getActivity(), Uri.parse(url));
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            } else {
                PlayingReaderId = -1;
            }

        }

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        PlayingReaderId = -1;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }


    public interface BottomSheetListener {
        void onReaderChanger(String selctedReader);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mediaPlayer.release();

    }

}