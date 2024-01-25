package com.app.dz.quranapp.MushafParte.TafsirParte;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.MushafParte.Reader;
import com.app.dz.quranapp.MushafParte.ReadersAdapter;
import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.databinding.ActivityAudioFilesBinding;
import com.app.dz.quranapp.room.Daos.ReaderAudioDao;
import com.app.dz.quranapp.room.MushafDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ReaderListActivity extends AppCompatActivity {
    private ReadersAdapter adapter;
    private static List<ReaderAudio> readersList = new ArrayList<>();
    private static final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final String TAG = ReaderListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAudioFilesBinding binding = ActivityAudioFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getReaderAudioList(this);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        adapter = new ReadersAdapter(readersList, this, new ReadersAdapter.OnAdapterClickListener() {
            @Override
            public void onClick(ReaderAudio reader, int position) {
             Intent intent =new Intent(ReaderListActivity.this,AudioFilesActivity.class);
             intent.putExtra("FolderName",reader.getReaderTag());
             intent.putExtra("readerId",reader.getId());
             startActivity(intent);
            }

            @Override
            public void onAudioPlayClicked(ReaderAudio reader, int position) {

            }
        });
        binding.recyclerview.setAdapter(adapter);
    }


    public String getFolderPath(String folderName) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + folderName + "/";
    }

    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.delete();
    }

    public static void getReaderAudioList(Context context) {
        MushafDatabase database = MushafDatabase.getInstance(context);
        ReaderAudioDao dao = database.getReaderAudioDao();

        compositeDisposable.add(dao.getAvailableReaders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(readerList->{
                    readersList = readerList;
                }, e->{
                    Log.e("checkdata","audios data error   "+e.getMessage());
                }));
    }

}