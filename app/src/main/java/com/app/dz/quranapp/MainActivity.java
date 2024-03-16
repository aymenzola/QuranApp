package com.app.dz.quranapp;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.app.dz.quranapp.databinding.ActivityMainBinding;
import com.app.dz.quranapp.quran.viewmodels.MyViewModel;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final static String TAG = MainActivity.class.getSimpleName();
    private NavController navController;
    private int backCount = 0;
    private MyViewModel viewModel;
    private Boolean isFullModeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.background_color));
        }
        viewModel = new ViewModelProvider(MainActivity.this).get(MyViewModel.class);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main3);
        NavigationUI.setupWithNavController(binding.navView, navController);

        viewModel.getData().observe(this, isFullModeActivee -> {
            isFullModeActive = isFullModeActivee;
            if (isFullModeActive) hideBottomBar();
        });

        // Get the data from the Intent
        AdkarModel adkarModel = (AdkarModel) getIntent().getSerializableExtra("adkarModel");
        int page = getIntent().getIntExtra("page", -1);


        getOnBackPressedDispatcher().addCallback(MainActivity.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleOnBack();
            }
        });


        if (adkarModel != null) {
            // If the data is not null, navigate to the desired Fragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main3);
            Bundle bundle = new Bundle();
            bundle.putSerializable("adkarModel", adkarModel);
            navController.navigate(R.id.action_fragment_home_to_adkarDetailsFragment, bundle);
        }

        if (page != -1) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main3);
            Bundle bundle = new Bundle();
            bundle.putInt("page", page);
            navController.navigate(R.id.action_fragment_home_to_quranFragmentDev, bundle);
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    public void hideBottomBar() {
        binding.navView.setVisibility(View.GONE);
    }

    public void showBottomBar() {
        binding.navView.setVisibility(View.VISIBLE);
    }

    private void handleOnBack() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        int currentDestinationId = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : -1;

        if (count==0)
        handleHomeBackPress(currentDestinationId);
        else {
            //how to handle back press
            super.onBackPressed();
        }
    }

    private void handleHomeBackPress(int currentDestinationId) {
        if (currentDestinationId == R.id.fragment_home) {
            if (backCount != 0) finish();
            backCount++;
        } else if (currentDestinationId == R.id.adkarDetailsFragment) {
            backCount = 0;
            navController.navigate(R.id.action_adkarDetailsFragment_to_adkarFragment);
        } else if (currentDestinationId == R.id.adkarFragment) {
            backCount = 0;
            navController.navigate(R.id.action_adkarFragment_to_fragment_home);
        } else if (currentDestinationId == R.id.qublaFragment) {
            backCount = 0;
            navController.navigate(R.id.action_qublaFragment_to_fragment_home);
        } else if (currentDestinationId == R.id.navigation_Prayer) {
            navController.navigate(R.id.action_navigation_Prayer_to_fragment_home);

        } else if (currentDestinationId == R.id.fragments_collection) {
            navController.navigate(R.id.action_fragments_collection_to_fragment_home);
        } else if (currentDestinationId == R.id.quranFragmentDev) {
            if (binding.navView.getVisibility() == View.GONE) {
                viewModel.setIsOnBackClicked(true);
                viewModel.setData(false);
                showBottomBar();
            } else {
                backCount = 0;
                navController.navigate(R.id.action_quranFragmentDev_to_fragment_home);
            }
        }
    }

}