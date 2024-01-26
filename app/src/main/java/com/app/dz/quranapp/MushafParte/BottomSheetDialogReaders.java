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

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.databinding.BottomSheetLayoutLogoutBinding;
import com.app.dz.quranapp.riwayat.CsvReader;
import com.app.dz.quranapp.room.MushafDatabase;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;


public class BottomSheetDialogReaders extends BottomSheetDialogFragment implements ReadersAdapter.OnAdapterClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private BottomSheetListener mListener;
    private static final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private BottomSheetLayoutLogoutBinding mBinding;
    private MediaPlayer mediaPlayer;
    private int PlayingReaderId = -1;
    private PublicMethods publicMethods;
    private SharedPreferenceManager sharedPreferenceManager;
    private QuranInfoManager quranInfoManager;
    private int DefaultSelectedReader;
    private int CurrantSelectedReader;
    private static List<ReaderAudio> readersList = new ArrayList<>();
    public BottomSheetDialogReaders() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        publicMethods = PublicMethods.getInstance();
        getReaderAudioList(getActivity());
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        DefaultSelectedReader = sharedPreferenceManager.getSelectedReaderId();
        quranInfoManager = QuranInfoManager.getInstance();
        CurrantSelectedReader = DefaultSelectedReader;



        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        ReadersAdapter adapter;
        // Create some example data
        //todo receive the correct list

        adapter = new ReadersAdapter(readersList, getActivity(), this);

        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerview.setHasFixedSize(true);
        mBinding.recyclerview.setAdapter(adapter);
        adapter.selectItem(DefaultSelectedReader-1);


        mBinding.tvSave.setOnClickListener(v -> {
            if (!Objects.equals(DefaultSelectedReader, CurrantSelectedReader)) {
                sharedPreferenceManager.saveSelectedReaderId(CurrantSelectedReader);
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
    public void onClick(ReaderAudio reader, int position) {
        CurrantSelectedReader = reader.getId();
    }

    @Override
    public void onAudioPlayClicked(ReaderAudio reader, int position) {
        Toast.makeText(getActivity(), "audio " + reader.getName(), Toast.LENGTH_SHORT).show();
        CurrantSelectedReader = reader.getId();

        if (PlayingReaderId == -1) {
            //no playing audio
            new Thread(() -> {
                try {
                    PlayingReaderId = reader.getId();
                    String url = publicMethods.getSreamLink(publicMethods.getReaderFromList(reader.getId(),readersList), 112);
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
            if (PlayingReaderId != reader.getId()) {
                //  audio is playing and user click on another one
                new Thread(() -> {
                    try {
                        PlayingReaderId = reader.getId();
                        String url = publicMethods.getSreamLink(publicMethods.getReaderFromList(reader.getId(),readersList), 112);
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
        void onReaderChanger(int readerId);
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
    public static void getReaderAudioList(Context context) {
        MushafDatabase database = MushafDatabase.getInstance(context);
        readersList = CsvReader.readReaderAudioListFromCsv(context, "audio.csv");
    }

}