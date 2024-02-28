package com.app.dz.quranapp.Util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.ui.activities.CollectionParte.motonParte.Matn;

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

    public static List<Matn> readMotonListFromCsv(Context context, String fileName,Integer matnId) {
        List<Matn> matnList = new ArrayList<>();
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
                if (columns.length == 9) { // Assuming 9 columns as per your Matn class
                    Matn matn = new Matn();
                    matn.matnId = Integer.parseInt(columns[0]);
                    matn.parentId = Integer.parseInt(columns[1]);
                    matn.matnTitle = columns[2];
                    matn.fileName = columns[3];
                    matn.matnDescription = columns[4];
                    matn.setFileUrl(columns[5]);
                    matn.pagesCount = Integer.parseInt(columns[6]);
                    matn.matnImage = columns[7];
                    matn.fileKbSize = Integer.parseInt(columns[8]);

                    Log.e(TAG, "matn : "+matn.toString());

                    if (matnId != null) {
                        if (matn.matnId == matnId) {
                            matnList.add(matn);
                            break;
                        }
                    } else {
                        matnList.add(matn);
                    }
                } else {
                    Log.e(TAG, "Invalid number of columns in CSV file");
                }
            }

            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file", e);
        }

        return matnList;
    }
}
