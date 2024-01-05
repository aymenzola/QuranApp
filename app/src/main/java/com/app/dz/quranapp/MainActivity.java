package com.app.dz.quranapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.app.dz.quranapp.MainFragmentsParte.TimeParte.FragmentPrayer;
import com.app.dz.quranapp.databinding.ActivityMainBinding;
import com.app.dz.quranapp.quran.QuranSearchParte.ActivitySearchQuran;
import com.app.dz.quranapp.quran.searchParte.SearchActivity;

public class MainActivity extends AppCompatActivity implements SearchActivity.OnFragmentInteractionListener, ActivitySearchQuran.OnFragmentInteractionListener {

    private ActivityMainBinding binding;
    private final static String TAG = MainActivity.class.getSimpleName();
    private NavController navController;
    private int backCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main3);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }


    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    @Override
    public void onSearchBarClicked(String word, boolean isSuggestion) {

    }

    @Override
    public void onBackPressed() {


        //handel back clickes
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count == 0 && binding.navView.getSelectedItemId() == R.id.navigation_Prayer) {
            navController.navigate(R.id.action_navigation_Prayer_to_fragment_home);
        } else if (count == 0 && binding.navView.getSelectedItemId() == R.id.fragments_collection) {
            navController.navigate(R.id.action_fragments_collection_to_fragment_home);
        } else if (binding.navView.getSelectedItemId() == R.id.fragment_home) {
            if (backCount != 0) finish();
            backCount++;
        } else {
            super.onBackPressed();
        }
    }

}