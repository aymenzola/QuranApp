package com.app.dz.quranapp.MushafParte.TafsirParte;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.MushafParte.Reader;
import com.app.dz.quranapp.MushafParte.ReadersAdapter;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.databinding.ActivityAudioFilesBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReaderListActivity extends AppCompatActivity {
    private ReadersAdapter adapter;
    private static final String TAG = ReaderListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAudioFilesBinding binding = ActivityAudioFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        QuranInfoManager quranInfoManager = QuranInfoManager.getInstance();

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        List<Reader> readerList  = new ArrayList<>();
        readerList.add(new Reader(1, "مشاري العفاسي", quranInfoManager.getReaderName(0)));
        readerList.add(new Reader(2, "سعود الشريم", quranInfoManager.getReaderName(1)));
        readerList.add(new Reader(3, "عبد الرحمن السديس", quranInfoManager.getReaderName(2)));
        readerList.add(new Reader(4, "محمد الطبلاوي", quranInfoManager.getReaderName(3)));
        readerList.add(new Reader(5, "عبد الباسط عبد الصمد", quranInfoManager.getReaderName(4)));


        PublicMethods p = PublicMethods.getInstance();
        adapter = new ReadersAdapter(readerList, this, new ReadersAdapter.OnAdapterClickListener() {
            @Override
            public void onClick(Reader reader, int position) {
             Intent intent =new Intent(ReaderListActivity.this,AudioFilesActivity.class);
             intent.putExtra("FolderName",p.getReaderTag(reader.readerEnglishName));
             intent.putExtra("readerId",reader.readerId);
             startActivity(intent);
            }

            @Override
            public void onAudioPlayClicked(Reader reader, int position) {

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
}