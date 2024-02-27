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
import com.app.dz.quranapp.databinding.ActivityQiblaFinderBinding;


public class QublaFragment2 extends Fragment {

    public final static String TAG = "QublaFragment";
    private ActivityQiblaFinderBinding binding;
    private Compass compass;
    private float currentAzimuth;
    private GPSTracker gps;
    private SharedPreferences prefs;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    public QublaFragment2() {
        // Required empty public constructor
    }


    public static QublaFragment2 newInstance() {
        QublaFragment2 fragment = new QublaFragment2();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityQiblaFinderBinding.inflate(getLayoutInflater(), container, false);
        prefs = requireActivity().getSharedPreferences("qibla", MODE_PRIVATE);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), grantResults -> {
            if (grantResults.containsValue(true)) {
                // Permission is granted
                SaveBoolean("permission_granted", true);
                chekIfGpsOnAndStart();

            } else {
                // Permission is denied
                binding.tvMoaayara.setText("تم رفض الصلاحيات لا يمكنك تحديد اتجاه القبلة");
            }
        });
        return binding.getRoot();
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

        boolean permission_granted = GetBoolean(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        if (permission_granted) {
            binding.tvMoaayara.setText("");
            binding.btnMoaayara.setText("تحديد اتجاه القبلة");
            gps = new GPSTracker(requireActivity(), new GPSTracker.LocationListener() {
                @Override
                public void onNewLocation(double latitude, double longitude) {

                }
            });
            setupCompass();

        } else {
            binding.tvMoaayara.setText("حتى تتمكن من تحديد اتجاه القبلة يجب أن تمنح الصلاحيات للتطبيق");
            binding.btnMoaayara.setText("منح الصلاحيات");
            binding.mainImageQibla.setVisibility(View.GONE);
        }

        setListeners();

    }

    private void setListeners() {

        binding.btnMoaayara.setOnClickListener(v -> {
            boolean permission_granted = GetBoolean(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            if (permission_granted) {
                chekIfGpsOnAndStart();
            } else {
                //ask for permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
                } else {
                    chekIfGpsOnAndStart();
                }
            }
        });

        binding.imgTitle.setOnClickListener(v -> startActivity(new Intent(requireActivity(), QiblaFinder.class)));

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        binding.imgBack.setOnClickListener(v -> navController.navigateUp());

    }

    private void chekIfGpsOnAndStart() {
        gps = new GPSTracker(requireActivity(), new GPSTracker.LocationListener() {
            @Override
            public void onNewLocation(double latitude, double longitude) {

            }
        });
        if (gps.canGetLocation()) {
            setupCompass();
            binding.tvMoaayara.setText("GPS مفعل الان يمكنك تحديد اتجاه القبلة");
            binding.btnMoaayara.setText("تحديد اتجاه القبلة");
        } else {
            showSettigAlert();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        if (compass != null) {
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
        if (compass != null) {
            if (gps.canGetLocation()) {
                binding.tvMoaayara.setText("GPS مفعل الان يمكنك تحديد اتجاه القبلة");
                binding.btnMoaayara.setText("تحديد اتجاه القبلة");
                compass.start();
            } else {

                // GPS is not enabled
                // You can show a message to the user or perform other operations
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        if (compass != null) {
            compass.stop();
        }
    }

    private void setupCompass() {
        boolean permission_granted = GetBoolean(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        if (permission_granted) {
            getBearing();
        } else {
            binding.textUp.setText("");
            binding.textDown.setText(getResources().getString(R.string.permission_not_garanted));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            } else {
                getBearing();
            }
        }
        compass = new Compass(requireActivity());
        Compass.CompassListener cl = new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(float azimuth) {
                QublaFragment2.this.adjustArrowQiblat(azimuth);
            }
        };
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

    @SuppressLint("MissingPermission")
    public void getBearing() {
        // Get the location manager
        binding.btnMoaayara.setVisibility(INVISIBLE);
        binding.tvMoaayara.setText("حرك جهازك بشكل سريع ثم ضعه على شكل مسطح و اضغط معايرة للحصول على أفضل نتيجة.");
        fetch_GPS();
    }

    public void SaveBoolean(String key, Boolean bb) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(key, bb);
        edit.apply();
    }

    public boolean GetBoolean(String[] permissions) {
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
        double result = 0;

        gps = new GPSTracker(requireActivity(), new GPSTracker.LocationListener() {
            @Override
            public void onNewLocation(double latitude, double longitude) {

            }
        });
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            binding.textDown.setText(getResources().getString(R.string.coord) + "\n" + getResources().getString(R.string.latitude) + ": " + latitude + getResources().getString(R.string.longitude) + ": " + longitude);
            Log.e("TAG", "GPS is on");
            double lat_saya = gps.getLatitude();
            double lon_saya = gps.getLongitude();
            Log.e("TAG", "GPS is on  lat_saya " + lat_saya + " lon_saya : " + lon_saya);
            if (lat_saya < 0.001 && lon_saya < 0.001) {
                binding.mainImageQibla.setVisibility(INVISIBLE);
                binding.mainImageQibla.setVisibility(View.GONE);
                binding.textUp.setText("");
                binding.textDown.setText(getResources().getString(R.string.locationunready));
            } else {
                double longitude2 = 39.826209; // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double latitude2 = Math.toRadians(21.422507); // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double latitude1 = Math.toRadians(lat_saya);
                double longDiff = Math.toRadians(longitude2 - lon_saya);
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
        } else {
            showSettigAlert();
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



