package com.app.dz.quranapp.MushafParte.TafsirParte;


import static com.app.dz.quranapp.Services.ForegroundDownloadAudioService.AppfolderName;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.FilesParte.FilesAdapter;
import com.app.dz.quranapp.MushafParte.AudioFile;
import com.app.dz.quranapp.databinding.ActivityAudioFilesBinding;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AudioFilesActivity extends AppCompatActivity {
    DecimalFormat decimalFormat =new DecimalFormat("0.0");
    private List<AudioFile> audioFileNames;
    private FilesAdapter adapter;
    private String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyAudioFolder/";
    private static final String TAG = AudioFilesActivity.class.getSimpleName();
    private AudioFile model;
    private String FolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAudioFilesBinding binding = ActivityAudioFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        FolderName = intent.getStringExtra("FolderName");
        int readerId = intent.getIntExtra("readerId",1);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);

        audioFileNames = new ArrayList<>();

        // Get all mp3 files in the folder
        File directory = new File(getFolderPath());
        Log.e(TAG," dirctory exists "+directory.exists()+" folder "+FolderName+" path "+directory.getPath());
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith(".mp3")) {
                    long fileSizeBytes = file.length();
                    double fileSizeMB = (double) fileSizeBytes / (1024 * 1024);
                    audioFileNames.add(new AudioFile(file, fileName,decimalFormat.format(fileSizeMB)));
                }
            }
        }

        adapter = new FilesAdapter(readerId,this,audioFileNames, (model, position) -> {
            //remove file
            if (deleteFile(model.file)) {
                //file deleted
                adapter.fileDeleted(position);

            } else {

            }
        });
        binding.recyclerview.setAdapter(adapter);
    }


    public String getFolderPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +"/"+AppfolderName+ "/"+FolderName+"/";
    }

    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.delete();
    }
}