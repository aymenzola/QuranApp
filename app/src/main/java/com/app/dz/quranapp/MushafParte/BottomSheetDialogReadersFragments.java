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
import com.app.dz.quranapp.MushafParte.riwayat_parte.RiwayaType;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.data.room.Entities.Riwaya;
import com.app.dz.quranapp.databinding.BottomSheetLayoutLogoutBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BottomSheetDialogReadersFragments extends BottomSheetDialogFragment implements
        ReadersAdapter.OnAdapterClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private BottomSheetListener mListener;
    private BottomSheetLayoutLogoutBinding mBinding;
    private MediaPlayer mediaPlayer;
    private int PlayingReaderId = -1;
    private PublicMethods publicMethods;
    private SharedPreferenceManager sharedPreferenceManager;
    private int DefaultSelectedReader;
    private ReaderAudio CurrantSelectedReader;
    private final Riwaya selectedRiwaya;
    private static List<ReaderAudio> readersList = new ArrayList<>();

    public BottomSheetDialogReadersFragments(Riwaya selectedRiwaya, BottomSheetListener listener) {
        this.selectedRiwaya = selectedRiwaya;
        this.mListener = listener;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        publicMethods = PublicMethods.getInstance();
        getReaderAudioList(getActivity());
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        DefaultSelectedReader = sharedPreferenceManager.getSelectedReaderId();
        int selectedReaderPosition = getReaderAudioPosition(DefaultSelectedReader);
        CurrantSelectedReader = getReaderAudioWithId(DefaultSelectedReader);
        manageTitleName();
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
        if (selectedReaderPosition != -1) adapter.selectItem(selectedReaderPosition);


        mBinding.tvSave.setOnClickListener(v -> {
            if (!Objects.equals(DefaultSelectedReader, CurrantSelectedReader.getId())) {
                sharedPreferenceManager.saveSelectedReaderId(CurrantSelectedReader.getId());
                mListener.onReaderChanger(CurrantSelectedReader);
                Log.e("bottomsheet", "we send to activity " + CurrantSelectedReader);
            }
            dismiss();
        });


    }

    private void manageTitleName() {
        if (CurrantSelectedReader.getRiwaya().contains(RiwayaType.HAFS.name()))
            mBinding.tvTitle.setText("قراء حفص");
        else mBinding.tvTitle.setText("قراء ورش");
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
        CurrantSelectedReader = reader;
    }

    @Override
    public void onAudioPlayClicked(ReaderAudio reader, int position) {
        Toast.makeText(getActivity(), "audio " + reader.getName(), Toast.LENGTH_SHORT).show();
        CurrantSelectedReader = reader;

        if (PlayingReaderId == -1) {
            //no playing audio
            new Thread(() -> {
                try {
                    PlayingReaderId = reader.getId();
                    String url = publicMethods.getSreamLink(CurrantSelectedReader, 112);
                    Log.e("bottomsheet", "read url : " + url);
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getActivity(), Uri.parse(url));
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
                        String url = publicMethods.getSreamLink(CurrantSelectedReader, 112);
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
        void onReaderChanger(ReaderAudio reader);
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mediaPlayer.release();
    }

    public void getReaderAudioList(Context context) {

        if (!selectedRiwaya.tag.equals(RiwayaType.HAFS.name())) {
            readersList = PublicMethods.getReadersAudiosListWithRiwaya(context,selectedRiwaya.tag);
        }else {
            Log.e("taglog", "we get default rewaya reader which warch");
            readersList = PublicMethods.getReadersAudiosListWithRiwaya(context,RiwayaType.WARCH.name());
        }
        Log.e("taglog", "readerList size " + readersList.size() + " selected riwaya " + selectedRiwaya.tag);
    }

    public int getReaderAudioPosition(int readerId) {
        for (int i = 0; i < readersList.size(); i++) {
            if (readersList.get(i).getId() == readerId) return i;
        }
        return -1;
    }

    public ReaderAudio getReaderAudioWithId(int readerId) {
        for (int i = 0; i < readersList.size(); i++) {
            Log.e("taglog", "list id " + readersList.get(i).getId() + " selected one : " + readerId);
            if (readersList.get(i).getId() == readerId) {
                Log.e("taglog", "audio position " + i);
                return readersList.get(i);
            }
        }
        Log.e("taglog", "return empty audio" + readersList.size());
        return readersList.get(0);
    }

}