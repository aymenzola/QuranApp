package com.app.dz.quranapp.fix_new_futers.aManageNewSources;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.AyaAudioLimitsFirebase;
import com.app.dz.quranapp.data.room.Entities.SuraAudioFirebase;
import com.app.dz.quranapp.databinding.TestActivityBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class TestQuranAudioAyaLimitsActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private TestActivityBinding binding;
    private List<Sura> suraList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TestActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        makeStutsBarColored();

        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(mp -> {
            int millis = Integer.valueOf(binding.editMillis.getText().toString());
            mPlayer.seekTo(millis);
            mPlayer.start();
        });
        String url = "https://server7.mp3quran.net/basit/Almusshaf-Al-Mojawwad/002.mp3";

        try {
            mPlayer.setDataSource(this, Uri.parse(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        binding.btnStart.setOnClickListener(v -> {
            mPlayer.prepareAsync();
        });

        binding.btnRecord.setOnClickListener(v -> {

        });


        /**to add new read limits change readerTag and readeId
         * first check if the are avialble on the server with the read id
         * "https://mp3quran.net/api/v3/ayat_timing?surah=" + suraNumber + "&read="+readeId"
         * if the exist use bellow function to get limits and add them to firebase
         * then you have to add reader item to asset audio.csv manually
         * **/
        binding.btnAddReaderLimits.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String reader = "AbdulBaset";
            String readeId = "51";

            new Thread(() -> {

                for (int suraNumber = 1; suraNumber < 115; suraNumber++) {
                    addReaderLimitsFromJson(suraNumber, reader, readeId);
                }

                Log.e("limitsTag", "list is full start uploading" + suraList.size());


                for (Sura sura : suraList) {
                    sura.suraAudioFirebase.ayaAudioList.remove(0);
                    DocumentReference docRef = db.collection("suraaudios").document(reader)
                            .collection(reader + "Audio").document(String.valueOf(sura.suraNumber));

                    docRef.set(sura.suraAudioFirebase).addOnSuccessListener(s ->
                            Log.e("limitsTag", "upload success " + sura.suraNumber));
                }


            }).start();
        });



/*        binding.btnSpeech.setOnClickListener(v -> {
            //startVoiceRecognition();
            makeRetrofitCall(binding.editText.getText().toString());
        });*/

        // Initialize SpeechRecognizer

        // Initialize RecognizerIntent

    }



    public static String decodeArabicCharacters(String encodedJson) {
        try {
            // Decode the Arabic characters
            return URLDecoder.decode(encodedJson, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void makeStutsBarColored() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }
    }

    private void addReaderLimitsFromJson(int suraNumber, String reader, String readeId) {

//        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.e("limitsTag", "we start " + reader + " sura " + suraNumber);

        try {

            SuraAudioFirebase suraAudioFirebase = new SuraAudioFirebase();
            suraAudioFirebase.readerName = reader;
            suraAudioFirebase.SuraNumber = suraNumber;
//            suraAudioFirebase.ayaAudioList = readJsonFromAssets("data.json");
            suraAudioFirebase.ayaAudioList = readJsonFromUrl("https://mp3quran.net/api/v3/ayat_timing?surah=" + suraNumber + "&read=" + readeId);

            suraList.add(new Sura(suraNumber, suraAudioFirebase));

            /*
            DocumentReference docRef = db.collection("suraaudios").document(reader).collection(reader + "Audio").document(String.valueOf(suraNumber));

            docRef.set(suraAudioFirebase)
                    .addOnSuccessListener(aVoid
                            -> Log.d("limitsTag", "Sura " + suraNumber + " successfully written!"))
                    .addOnFailureListener(e
                            -> Log.w("limitsTag", "Error writing document", e));*/


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private List<AyaAudioLimitsFirebase> readJsonFromAssets(String fileName) throws IOException {
        // Open the input stream for the JSON file in the assets folder
        InputStream inputStream = getClassLoader().getResourceAsStream("assets/" + fileName);
        if (inputStream == null) {
            throw new IOException("File not found: " + fileName);
        }
        // Use Gson to parse the JSON into a List<AyaAudioLimitsFirebaseJson>
        Type listType = new TypeToken<List<AyaAudioLimitsFirebaseJson>>() {
        }.getType();

        List<AyaAudioLimitsFirebaseJson> ayaAudioLimitsJsonList = new Gson().fromJson(new InputStreamReader(inputStream), listType);

        // Convert to List<AyaAudioLimitsFirebase>

        return convertToAyaAudioLimitsList(ayaAudioLimitsJsonList);
    }
    private static List<AyaAudioLimitsFirebase> readJsonFromUrl(String url) throws IOException {
        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        // Use Gson to parse the JSON into a List<AyaAudioLimitsFirebaseJson>
        Type listType = new TypeToken<List<AyaAudioLimitsFirebaseJson>>() {
        }.getType();
        List<AyaAudioLimitsFirebaseJson> jsonList = new Gson().fromJson(
                new InputStreamReader(connection.getInputStream()), listType
        );

        // Convert to List<AyaAudioLimitsFirebase>
        List<AyaAudioLimitsFirebase> result = convertToAyaAudioLimitsList(jsonList);

        // Close the connection
        connection.disconnect();

        return result;
    }

    private static List<AyaAudioLimitsFirebase> convertToAyaAudioLimitsList(List<AyaAudioLimitsFirebaseJson> jsonList) {
        // Manually map values to AyaAudioLimitsFirebase
        List<AyaAudioLimitsFirebase> result = new ArrayList<>();
        for (AyaAudioLimitsFirebaseJson jsonItem : jsonList) {
            AyaAudioLimitsFirebase item = new AyaAudioLimitsFirebase();
            item.suraAya = jsonItem.ayah;
            item.startAyaTime = jsonItem.start_time;
            item.endAyaTime = jsonItem.end_time;
            // You can continue mapping other fields as needed
            result.add(item);
        }
        return result;
    }

    class Sura {
        public int suraNumber;
        public SuraAudioFirebase suraAudioFirebase;

        public Sura(int suraNumber, SuraAudioFirebase suraAudioFirebase) {
            this.suraNumber = suraNumber;
            this.suraAudioFirebase = suraAudioFirebase;
        }

        public int getSuraNumber() {
            return suraNumber;
        }

        public void setSuraNumber(int suraNumber) {
            this.suraNumber = suraNumber;
        }

        public SuraAudioFirebase getSuraAudioFirebase() {
            return suraAudioFirebase;
        }

        public void setSuraAudioFirebase(SuraAudioFirebase suraAudioFirebase) {
            this.suraAudioFirebase = suraAudioFirebase;
        }
    }


    private void startVoiceRecognition() {
        binding.tvResult.setText("speak now ...");
    }



    private void checkRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            // Permission already granted, start listening
            startVoiceRecognition();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start listening
                startVoiceRecognition();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
