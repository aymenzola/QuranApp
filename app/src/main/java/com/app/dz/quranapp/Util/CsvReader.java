package com.app.dz.quranapp.Util;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.MediaStore;
import android.util.Log;

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    private static final String TAG = "CsvReader";

    public static List<ReaderAudio> readReaderAudioListFromCsv(Context context, String fileName) {
        List<ReaderAudio> ReaderAudioList = new ArrayList<>();
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
                if (columns.length == 9) { // Assuming 9 columns as per your ReaderAudio class
                    ReaderAudio ReaderAudio = new ReaderAudio();
                    ReaderAudio.setId(Integer.parseInt(columns[0]));
                    ReaderAudio.setName(columns[1]);
                    ReaderAudio.setNameEnglish(columns[2]);
                    ReaderAudio.setAudioType(Integer.parseInt(columns[3]));
                    ReaderAudio.setUrl(columns[4]);
                    ReaderAudio.setIsThereSelection(Integer.parseInt(columns[5]));
                    ReaderAudio.setRiwaya(columns[6]);
                    ReaderAudio.setReaderTag(columns[7]);
                    ReaderAudio.setReaderImage(columns[8]);

                    ReaderAudioList.add(ReaderAudio);
                } else {
                    Log.e(TAG, "Invalid number of columns in CSV file");
                }
            }

            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file", e);
        }

        return ReaderAudioList;
    }
}
