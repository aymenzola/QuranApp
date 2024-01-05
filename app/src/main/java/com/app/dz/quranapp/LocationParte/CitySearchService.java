package com.app.dz.quranapp.LocationParte;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CitySearchService {
    private static final String NOMINATIM_ENDPOINT = "https://nominatim.openstreetmap.org/search";
    private static final String FORMAT_PARAM = "format";
    private static final String QUERY_PARAM = "q";
    private static final String LIMIT_PARAM = "limit";
    private static final int DEFAULT_LIMIT = 10;

    private RequestQueue requestQueue;

    public CitySearchService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void searchCity(String query, final CitySearchCallback callback) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String requestUrl = NOMINATIM_ENDPOINT + "?" +
                    FORMAT_PARAM + "=json&" +
                    QUERY_PARAM + "=" + encodedQuery + "&" +
                    LIMIT_PARAM + "=" + DEFAULT_LIMIT;

            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    requestUrl,
                    null,
                    response -> {
                        List<City> cities = parseCitiesFromResponse(response);
                        callback.onCitySearchResult(cities);
                    },
                    error -> callback.onCitySearchError(error.getMessage())
            );

            requestQueue.add(request);
        } catch (UnsupportedEncodingException e) {
            Log.e("CitySearchService", "Failed to encode search query: " + e.getMessage());
        }
    }

    private List<City> parseCitiesFromResponse(JSONObject response) {
        List<City> cities = new ArrayList<>();
        try {
            JSONArray results = response.getJSONArray("features");
            for (int i = 0; i < results.length(); i++) {
                JSONObject feature = results.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");

                String city = properties.getString("display_name");
                double latitude = feature.getJSONObject("geometry").getDouble("lat");
                double longitude = feature.getJSONObject("geometry").getDouble("lon");

                cities.add(new City(city, latitude, longitude));
            }
        } catch (JSONException e) {
            Log.e("CitySearchService", "Failed to parse search response: " + e.getMessage());
        }
        return cities;
    }

    private List<City> parseCitiesFromResponse(JSONArray array) {
        List<City> cities = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject city = array.getJSONObject(i);
                String cityName = city.getString("display_name");
                double lat = city.getDouble("lat");
                double lon = city.getDouble("lon");
                cities.add(new City(cityName,lat,lon));
            }
        } catch (JSONException e) {
            Log.e("CitySearchService", "Failed to parse search response: " + e.getMessage());
        }
        return cities;
    }

    public interface CitySearchCallback {
        void onCitySearchResult(List<City> cities);
        void onCitySearchError(String errorMessage);
    }
}