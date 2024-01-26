package com.app.dz.quranapp.audios;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.MediaStore;
import android.util.Log;

import com.app.dz.quranapp.riwayat.audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    private static final String TAG = "CsvReader";

    public static List<audio> readAudioListFromCsv(Context context, String fileName) {
        List<audio> audioList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Read the header line (skip it for now)
            String line = reader.readLine();

            // Read the remaining lines
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 9) { // Assuming 9 columns as per your Audio class
                    audio audio = new audio();
                    audio.setId(Integer.parseInt(columns[0]));
                    audio.setName(columns[1]);
                    audio.setName_english(columns[2]);
                    audio.setAudiotype(Integer.parseInt(columns[3]));
                    audio.setUrl(columns[4]);
                    audio.setIs_there_selection(Integer.parseInt(columns[5]));
                    audio.setRiwaya(columns[6]);
                    audio.setReader_tag(columns[7]);
                    audio.setReader_image(columns[8]);

                    audioList.add(audio);
                } else {
                    Log.e(TAG, "Invalid number of columns in CSV file");
                }
            }

            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file", e);
        }

        return audioList;
    }
}
