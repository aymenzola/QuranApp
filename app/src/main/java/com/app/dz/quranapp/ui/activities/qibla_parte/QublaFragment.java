package com.app.dz.quranapp.ui.activities.qibla_parte;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.INVISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.Util.UserLocation;
import com.app.dz.quranapp.databinding.ActivityQiblaFinderBinding;


public class QublaFragment extends Fragment {

    public final static String TAG = "QublaFragment";
    private ActivityQiblaFinderBinding binding;
    private Compass compass;
    private float currentAzimuth;
    private GPSTracker gps;
    private SharedPreferences prefs;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private boolean workingOffLine = false;

    public QublaFragment() {
        // Required empty public constructor
    }


    public static QublaFragment newInstance() {
        QublaFragment fragment = new QublaFragment();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityQiblaFinderBinding.inflate(getLayoutInflater(), container, false);
        prefs = requireActivity().getSharedPreferences("qibla", MODE_PRIVATE);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), grantResults -> {
            if (grantResults.containsValue(true)) {
                weHavePermissionStartGps();
            } else {
                // Permission is denied
                binding.tvMoaayara.setText("تم رفض الصلاحيات لا يمكنك تحديد اتجاه القبلة");
            }
        });
        return binding.getRoot();
    }

    private void weHavePermissionStartGps() {
        if (PublicMethods.getInstance().checkNetworkConnection(requireActivity()))
            fetch_GPS();
        else {
            Toast.makeText(requireActivity(), "تحقق من الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.blan));
        }


        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (SharedPreferenceManager.getInstance(requireActivity()).iSLocationAvialable()) {
            setupOfflineCompass();
        } else {
            //should use internet
            if (PublicMethods.getInstance().checkNetworkConnection(requireActivity())) {
                // no check for permission here because we will request it in fetch_GPS

                boolean permission_granted = hasPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
                if (permission_granted) {
                    weHavePermissionStartGps();
                } else {
                    //wait for button clicked to ask permission
                    binding.tvMoaayara.setText("حتى تتمكن من تحديد اتجاه القبلة يجب أن تمنح الصلاحيات للتطبيق");
                    binding.btnMoaayara.setText("منح الصلاحيات");
                    binding.mainImageQibla.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(requireActivity(), "تحقق من الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
            }

        }


        setListeners();

    }

    private void setListeners() {

        binding.btnMoaayara.setOnClickListener(v -> {
            if (PublicMethods.getInstance().checkNetworkConnection(requireActivity())) {
                boolean permission_granted = hasPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
                if (permission_granted) {
                    Log.e(TAG, "button clicked we have permission");
                    weHavePermissionStartGps();
                } else {
                    //user need to grant permission so we request it
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
                }
            } else {
                Toast.makeText(requireActivity(), "تحقق من الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
            }

        });

        binding.imgTitle.setOnClickListener(v -> startActivity(new Intent(requireActivity(), QiblaFinder.class)));

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        binding.imgBack.setOnClickListener(v -> navController.navigateUp());

    }

    private void setupOfflineCompass() {
        UserLocation userLocation = SharedPreferenceManager.getInstance(requireActivity()).getUserLocation();
        Log.d(TAG, "setupOfflineCompass with latitude " + userLocation.latitude + " longitude " + userLocation.longitude);

        workingOffLine = true;
        binding.mainImageQibla.setVisibility(View.VISIBLE);
        binding.btnMoaayara.setVisibility(INVISIBLE);
        binding.tvMoaayara.setText("حرك جهازك بشكل سريع ثم ضعه على شكل مسطح و اضغط معايرة للحصول على أفضل نتيجة.");

        getQublaDegree(userLocation.latitude, userLocation.longitude);
        compass = new Compass(requireActivity());
        Compass.CompassListener cl = QublaFragment.this::adjustArrowQiblat;
        compass.setListener(cl);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (compass != null) {
            setFinalView();
            Log.d(TAG, "start compass");
            compass.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (compass != null) {
            compass.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (workingOffLine) {
            Log.e(TAG, "onResume workingOffLine true so compass start");
            setFinalView();
            compass.start();
            return;
        }

        if (compass != null) compass.start();

    }

    private void setFinalView() {
        if (binding.mainImageQibla.getVisibility() == View.GONE) {
            binding.mainImageQibla.setVisibility(View.VISIBLE);
        }
        binding.tvMoaayara.setText("حرك جهازك بشكل سريع ثم ضعه على شكل مسطح و اضغط معايرة للحصول على أفضل نتيجة.");
        binding.btnMoaayara.setText("تحديد اتجاه القبلة");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        if (compass != null) {
            compass.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gps != null) {
            Log.d(TAG, "onDestroy: stopUsingGPS");
            gps.stopUsingGPS();
        }
    }

    private void setupCompass() {
        compass = new Compass(requireActivity());
        Compass.CompassListener cl = QublaFragment.this::adjustArrowQiblat;
        compass.setListener(cl);
    }


    public void adjustArrowQiblat(float azimuth) {
        float QiblaDegree = GetFloat("QiblaDegree");
        Animation an = new RotateAnimation(-(currentAzimuth) + QiblaDegree, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        binding.mainImageQibla.startAnimation(an);

        if (QiblaDegree > 0) {
            binding.mainImageQibla.setVisibility(View.VISIBLE);
        } else {
            binding.mainImageQibla.setVisibility(INVISIBLE);
            binding.mainImageQibla.setVisibility(View.GONE);
        }
    }


    public void SaveBoolean(String key, Boolean bb) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(key, bb);
        edit.apply();
    }

    public boolean hasPermissions(String[] permissions) {

        //add check for android level
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        boolean allPermissionsGranted = true;
        SharedPreferences.Editor edit = prefs.edit();

        for (String permission : permissions) {
            // Check if the permission is granted
            int permissionState = ContextCompat.checkSelfPermission(requireActivity(), permission);
            boolean isPermissionGranted = permissionState == PackageManager.PERMISSION_GRANTED;

            // Save the permission state in shared preferences
            edit.putBoolean(permission, isPermissionGranted);

            // If any permission is not granted, set allPermissionsGranted to false
            if (!isPermissionGranted) {
                allPermissionsGranted = false;
            }
        }

        edit.apply();

        return allPermissionsGranted;
    }

    public void SaveFloat(String key, Float ff) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putFloat(key, ff);
        edit.apply();
    }

    public Float GetFloat(String key) {
        return prefs.getFloat(key, 0);
    }

    @SuppressLint("SetTextI18n")
    public void fetch_GPS() {
        Log.e(TAG, "fetch gps");

        if (gps == null) gps = new GPSTracker(requireActivity(), (latitude, longitude) -> {
            // do something with the location
            Log.e(TAG, "location listener receive " + latitude + " " + longitude);
            getQublaDegree(latitude, longitude);
            if (compass == null) {
                setupCompass();
                compass.start();
            } else compass.start();
        });


        if (gps.canGetLocation()) {
            Log.e(TAG, "fetch_GPS canGetLocation true so gps getLocation");
            gps.getLocation();
        } else {
            Log.e(TAG, "fetch_GPS canGetLocation false");
            binding.btnMoaayara.setVisibility(View.VISIBLE);
            binding.btnMoaayara.setText("البحث على اتجاه القبلة");


            showSettigAlert();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getQublaDegree(double latitude, double longitude) {
        double result = 0;
        binding.textDown.setText(getResources().getString(R.string.coord) + "\n" + getResources().getString(R.string.latitude) + ": " + latitude + getResources().getString(R.string.longitude) + ": " + longitude);
        if (latitude < 0.001 && longitude < 0.001) {
            binding.mainImageQibla.setVisibility(INVISIBLE);
            binding.mainImageQibla.setVisibility(View.GONE);
            binding.tvMoaayara.setVisibility(View.VISIBLE);
            binding.tvMoaayara.setText(getResources().getString(R.string.locationunready));
        } else {
            double longitude2 = 39.826209; // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
            double latitude2 = Math.toRadians(21.422507); // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
            double latitude1 = Math.toRadians(latitude);
            double longDiff = Math.toRadians(longitude2 - longitude);
            double y = Math.sin(longDiff) * Math.cos(latitude2);
            double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
            result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
            float result2 = (float) result;
            Log.e("logstep", "dsaveDegree " + result2 + " and show the arrow");
            SaveFloat("QiblaDegree", result2);
            binding.textUp.setText(getResources().getString(R.string.qibladirection) + " " + result2 + " " + getResources().getString(R.string.fromnorth));
            binding.mainImageQibla.setVisibility(View.VISIBLE);
            binding.tvMoaayara.setText("للحصول على أفضل نتيجة حرك جهازك بشكل سريع ثم ضعه على شكل مسطح");
        }
    }

    private void showSettigAlert() {
        gps.showSettingsAlert();
        binding.mainImageQibla.setVisibility(INVISIBLE);
        binding.mainImageQibla.setVisibility(View.GONE);
        binding.textUp.setText("");
        binding.tvMoaayara.setText(getResources().getString(R.string.gpsplz));
    }

    public void QiblaTips() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
        alertDialog.setTitle(requireActivity().getResources().getString(R.string.consignes));
        alertDialog.setMessage(requireActivity().getResources().getString(R.string.qiblatips));
        alertDialog.setPositiveButton(requireActivity().getResources().getString(R.string.ok), (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

}



