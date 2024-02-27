package com.app.dz.quranapp.fix_new_futers.ai_commands;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WitAiRequest {

    public static final String WIT_AI_API_URL1 = "https://api.wit.ai/";
    public static final String WIT_AI_API_URL = "https://api.wit.ai/message/";
    public static final String ACCESS_TOKEN = "PVJO5VRG7OPQK5A6YJOS436NO5DPILOA";

    private static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_TOKEN = "Bearer " + ACCESS_TOKEN;

    public static final String BEARER_TOKEN_Call = "Bearer I45PX7FI2U7BM5CVQ5RJ5FOUXSRM7CCU";

    public void makeWitAiRequest(Context context,String inputWord, ListenersAudio listenersAudio) {
        listenersAudio.prepareCall();
        String formattedCurrentDate = formatCurrentDate();
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Build the API URL with dynamic values
        String apiUrl = WIT_AI_API_URL + "?v=" + formattedCurrentDate + "&q=" + inputWord;

        Log.e("steptag",inputWord);

        // Create a StringRequest with the dynamic API URL
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,apiUrl,

                response -> {
                    // Handle the successful response here
                    Log.e("steptag","Response : ");
                    listenersAudio.getResult(getSuraName(response),getReaderName(response),getEntitiesWithKey(response,"read_speed:read_speed"));
                },
                error -> {
                    // Handle errors here
                    Log.e("steptag","Error : "+error.getMessage());
                    listenersAudio.getError(error.getMessage());

                }
        ) {
            // Override the getHeaders method to add the Authorization header
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(AUTH_HEADER, BEARER_TOKEN);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                // Specify UTF-8 encoding in the request headers
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    // Specify UTF-8 encoding for the request body
                    return getParams().toString().getBytes(StandardCharsets.UTF_8);
                } catch (AuthFailureError e) {
                    return null;
                }
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }


    public void makeWitAiCallRequest(Context context, String inputWord, ListenersCaller listenersCaller) {
        listenersCaller.prepareCall();
        String formattedCurrentDate = formatCurrentDate();
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Build the API URL with dynamic values
        String apiUrl = WIT_AI_API_URL + "?v=" + formattedCurrentDate + "&q=" + inputWord;

        Log.e("steptag","Formatted Current Date: " + formattedCurrentDate+" apiUrl "+apiUrl);

        // Create a StringRequest with the dynamic API URL
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                apiUrl,
                response -> {
                    // Handle the successful response here
                    Log.e("steptag","Response : ");
                    listenersCaller.getResultContactName(getContactName(response));
                },
                error -> {
                    // Handle errors here
                    Log.e("steptag","Error : "+error.getMessage());
                    listenersCaller.getError(error.getMessage());

                }
        ) {
            // Override the getHeaders method to add the Authorization header
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(AUTH_HEADER, BEARER_TOKEN_Call);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                // Specify UTF-8 encoding in the request headers
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    // Specify UTF-8 encoding for the request body
                    return getParams().toString().getBytes(StandardCharsets.UTF_8);
                } catch (AuthFailureError e) {
                    return null;
                }
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    // Method to format the current date
    public static String formatCurrentDate() {
        Date currentDate = new Date();
        return formatDate(currentDate);
    }
    // Method to format a specific date
    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    public interface ListenersAudio {
        void prepareCall();
        void getResult(String suraName,String readerName,int readSpeed);
        void getError(String errorBody);
    }


    public interface ListenersCaller{
        void prepareCall();
        void getResultContactName(String contactName);
        void getError(String errorBody);
    }
    private static String getReaderName(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // Access values in the JSONObject
            JSONObject entities = jsonObject.optJSONObject("entities");


            JSONArray readerNameEntities = entities != null ? entities.optJSONArray("readerName:readerName") : null;
            if (readerNameEntities != null && readerNameEntities.length() > 0) {
                JSONObject readerNameEntity = readerNameEntities.getJSONObject(0);
                String body_reader = readerNameEntity.optString("value", "");
                double confidence_reader = readerNameEntity.optDouble("confidence", 0.0);
                Log.e("steptag", "json self response " + body_reader + " " + confidence_reader);
                return body_reader;

            } else {
                Log.e("steptag", "No readerName:readerName object in entities");
                return "No";
            }




        } catch (JSONException e) {
            return "No";
        }
    }

    private static String getSuraName(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // Access values in the JSONObject
            JSONObject entities = jsonObject.optJSONObject("entities");

            // Access specific values within entities if they exist
            JSONArray suraNameEntities = entities != null ? entities.optJSONArray("sura_name:sura_name") : null;
            if (suraNameEntities != null && suraNameEntities.length() > 0) {
                JSONObject suraNameEntity = suraNameEntities.getJSONObject(0);
                String body = suraNameEntity.optString("value", "");
                double confidence = suraNameEntity.optDouble("confidence", 0.0);
                Log.e("steptag", "json self response " + body + " " + confidence);
                return body;
            } else {
                Log.e("steptag", "c:sura_name object in entities");
                return "No";
            }

        } catch (JSONException e) {
            Log.e("steptag", "error self response " + e.getMessage());
            return "No";
        }
    }


    private static int getEntitiesWithKey(String response,String key) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject entities = jsonObject.optJSONObject("entities");

            JSONArray suraNameEntities = entities != null ? entities.optJSONArray(key) : null;
            if (suraNameEntities != null && suraNameEntities.length() > 0) {
                JSONObject suraNameEntity = suraNameEntities.getJSONObject(0);
                int body = suraNameEntity.optInt("value",1);
                double confidence = suraNameEntity.optDouble("confidence", 0.0);
                return body;
            } else {
                Log.e("steptag", key+" it is not in entities");
                return -1;
            }

        } catch (JSONException e) {
            Log.e("steptag", "error self response " + e.getMessage());
            return -1;
        }
    }


    private static String getContactName(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // Access values in the JSONObject
            JSONObject entities = jsonObject.optJSONObject("entities");

            // Access specific values within entities if they exist
            JSONArray suraNameEntities = entities != null ? entities.optJSONArray("wit$contact:contact") : null;
            if (suraNameEntities != null && suraNameEntities.length() > 0) {
                JSONObject suraNameEntity = suraNameEntities.getJSONObject(0);
                String body = suraNameEntity.optString("value", "");
                double confidence = suraNameEntity.optDouble("confidence", 0.0);
                Log.e("steptag", "json self response " + body + " " + confidence);
                return body;
            } else {
                Log.e("steptag", "c:sura_name object in entities");
                return "No";
            }

        } catch (JSONException e) {
            Log.e("steptag", "error self response " + e.getMessage());
            return "No";
        }
    }
}
