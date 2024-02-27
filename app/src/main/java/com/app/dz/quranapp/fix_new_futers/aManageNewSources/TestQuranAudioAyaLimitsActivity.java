package com.app.dz.quranapp.fix_new_futers.aManageNewSources;

import static com.app.dz.quranapp.fix_new_futers.ai_commands.WitAiRequest.BEARER_TOKEN;
import static com.app.dz.quranapp.fix_new_futers.ai_commands.WitAiRequest.WIT_AI_API_URL;
import static com.app.dz.quranapp.fix_new_futers.ai_commands.WitAiRequest.WIT_AI_API_URL1;
import static com.app.dz.quranapp.fix_new_futers.ai_commands.WitAiRequest.formatCurrentDate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import com.app.dz.quranapp.fix_new_futers.ai_commands.MyHttpURLConnection;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public class TestQuranAudioAyaLimitsActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private TestActivityBinding binding;
    private List<Sura> suraList = new ArrayList<>();

    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;

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

        binding.btnSpeech.setOnClickListener(v -> {

            String formattedCurrentDate = formatCurrentDate();
            String apiUrl = WIT_AI_API_URL + "?v=" + formattedCurrentDate + "&q=" + binding.editText.getText().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String response = MyHttpURLConnection.sendGetRequest(apiUrl);
                    Log.e("steptag", " self response 1 "+decodeArabicCharacters(response));


                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        // Access values in the JSONObject
                        String text = jsonObject.optString("text", "");
                        JSONArray intents = jsonObject.optJSONArray("intents");
                        JSONObject entities = jsonObject.optJSONObject("entities");
                        JSONObject traits = jsonObject.optJSONObject("traits");

                        // Access specific values within entities if they exist
                        JSONArray suraNameEntities = entities != null ? entities.optJSONArray("sura_name:sura_name") : null;
                        if (suraNameEntities != null && suraNameEntities.length() > 0) {
                            JSONObject suraNameEntity = suraNameEntities.getJSONObject(0);
                            String body = suraNameEntity.optString("body", "");
                            double confidence = suraNameEntity.optDouble("confidence", 0.0);
                            Log.e("steptag", "json self response " + body + " " + confidence);
                        } else {
                            Log.e("steptag", "No sura_name:sura_name object in entities");
                        }

                        JSONArray readerNameEntities = entities != null ? entities.optJSONArray("readerName:readerName") : null;
                        if (readerNameEntities != null && readerNameEntities.length() > 0) {
                            JSONObject readerNameEntity = readerNameEntities.getJSONObject(0);
                            String body_reader = readerNameEntity.optString("body", "");
                            double confidence_reader = readerNameEntity.optDouble("confidence", 0.0);
                            Log.e("steptag", "json self response " + body_reader + " " + confidence_reader);
                        } else {
                            Log.e("steptag", "No readerName:readerName object in entities");
                        }




                    } catch (JSONException e) {
                        Log.e("steptag", "error self response " + e.getMessage());
                        throw new RuntimeException(e);
                    }

                }
            }).start();

            // Handle the response

            /*
            WitAiRequest witAiRequest = new WitAiRequest();
            witAiRequest.makeWitAiRequest(TestQuranAudioAyaLimitsActivity.this, binding.editText.getText().toString(), new WitAiRequest.ListenersAudio() {
                @Override
                public void prepareCall() {
                    binding.tvResult.setText("making call ... البرج");
                }

                @Override
                public void getResult(String suraName,String readerName) {
                    // Convert Unicode escaped string to Arabic text

                    ;

                    String val = convertUnicodeToArabic(suraName);
                    Log.e("steptag","result value : "+val);
                    binding.tvResult.setText(""+val);


                    //binding.tvResult.setText(""+suraName);
                }

                @Override
                public void getError(String errorBody) {
                    binding.tvResult.setText("" + errorBody);
                }
            });
            */
        });



/*        binding.btnSpeech.setOnClickListener(v -> {
            //startVoiceRecognition();
            makeRetrofitCall(binding.editText.getText().toString());
        });*/

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new MyRecognitionListener());

        // Initialize RecognizerIntent
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar");

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
        speechRecognizer.startListening(recognizerIntent);
    }

    private class MyRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            Log.e("steptag", "Error in speech recognition " + error);
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                String command = matches.get(0).toLowerCase();
                processVoiceCommand(command);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    }

    private void makeRetrofitCall(String inputWord) {

        String formattedCurrentDate = formatCurrentDate();


        // Create an OkHttpClient with an Interceptor to add the Authorization header
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", BEARER_TOKEN)
                            .build();
                    return chain.proceed(newRequest);
                }).addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        // Create a Retrofit instance with the customized OkHttpClient
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WIT_AI_API_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        // Create an instance of the ApiService interface
        ApiService apiService = retrofit.create(ApiService.class);

        // Make the network request
        Call<Object> call = apiService.fetchData(BEARER_TOKEN, inputWord, formattedCurrentDate);

        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response
                    Object responseString = response.body();
                    if (responseString != null) {
                        Log.e("steptag", "retrofit response " + responseString.toString());
                        String val1 = responseString.toString().replace(":", "_");
                        String val2 = responseString.toString().replace("=", ":");

                        binding.tvResult.setText("" + val2);
                    } else {
                        Log.e("steptag", "retrofit Error null");
                    }
                    // Process the response as needed

                } else {
                    Log.e("steptag", "retrofit unsuccessful response ");
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                // Handle network failure
            }
        });
    }

    public interface ApiService {
        @GET("message/")
        Call<Object> fetchData(@Header("Authorization") String authorizationHeader, @Query("q") String query, @Query("v") String date);
    }

    private void processVoiceCommand(String command) {
        Log.e("steptag", " voice command is " + command);
        binding.tvResult.setText("searching ...");
        binding.editText.setText("" + command);
        makeRetrofitCall(command);
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

    private static String convertUnicodeToArabic(String mixedString) {
        StringBuilder result = new StringBuilder();
        String[] tokens = mixedString.split("(?<=\\\\u)|(?=\\\\u)");
        for (String token : tokens) {
            if (token.startsWith("\\u")) {
                try {
                    int codePoint = Integer.parseInt(token.substring(2), 16);
                    result.appendCodePoint(codePoint);
                } catch (NumberFormatException e) {
                    // Handle invalid Unicode token or log the error
                    System.err.println("Invalid Unicode token: " + token);
                    result.append(token); // Keep the original token if it's invalid
                }
            } else {
                result.append(token); // Keep non-Unicode parts as they are
            }
        }
        return result.toString();
    }
}
