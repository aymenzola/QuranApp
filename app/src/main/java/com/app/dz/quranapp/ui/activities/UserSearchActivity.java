package com.app.dz.quranapp.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dz.quranapp.databinding.ActivityLocationSearchBinding;
import com.app.dz.quranapp.ui.activities.LocationParte.CitySearchService;
import com.app.dz.quranapp.ui.models.City;

import java.util.List;

public class UserSearchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLocationSearchBinding binding = ActivityLocationSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.editSearch.getText().toString();
            Toast.makeText(this, "we are searching for "+ query, Toast.LENGTH_SHORT).show();
            if (!query.isEmpty()) {
                CitySearchService citySearchService = new CitySearchService(UserSearchActivity.this);
                citySearchService.searchCity(query, new CitySearchService.CitySearchCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCitySearchResult(List<City> cities) {
//                        binding.tvResult.setText(jsonObject.toString());
                        if (cities.isEmpty()) {
                            binding.tvResult.setText("No results found");
                        } else {
                            StringBuilder result = new StringBuilder("size is "+cities.get(0));
                            for (City city : cities) {
                                Log.e("logcheck",""+city);
                                result.append(city.getName()).append("\n");
                            }
                            binding.tvResult.setText(result.toString());
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCitySearchError(String error) {
                        binding.tvResult.setText("Error: " + error);

                    }
                });
            }
        });
    }
}