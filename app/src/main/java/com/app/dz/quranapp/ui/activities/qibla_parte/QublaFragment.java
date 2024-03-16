package com.app.dz.quranapp.ui.activities.qibla_parte;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.Util.UserLocation;
import com.app.dz.quranapp.databinding.ActivityQiblaFinderBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class QublaFragment extends Fragment {

    public final static String TAG = "QublaFragment";
    private ActivityQiblaFinderBinding binding;
    private Compass compass;
    private float currentAzimuth;
    private GPSTracker gps;
    private SharedPreferences prefs;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private boolean workingOffLine = false;
    private boolean doesWeSavedTheNewLocation = false;
    private String globalAdresseName = null;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requestPermissionLauncher != null) requestPermissionLauncher = null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (SharedPreferenceManager.getInstance(requireActivity()).getLastQublaLocation() != null) {
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
                }

            } else {
                binding.tvMoaayara.setText("هده اول مرة تحتاج الى الاتصال بالانترنت لتحديد اتجاه القبلة");
                binding.btnMoaayara.setText("الحصول على اتجاه القبلة");

            }

        }


        setListeners();

    }

    private void setListeners() {

        binding.btnMoaayara.setOnClickListener(v -> buttonClicked());

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        binding.imgBack.setOnClickListener(v -> navController.navigateUp());

    }

    private void buttonClicked() {
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
    }

    private void setupOfflineCompass() {
        UserLocation userLocation = SharedPreferenceManager.getInstance(requireActivity()).getLastQublaLocation();
        Log.d(TAG, "setupOfflineCompass with latitude " + userLocation.latitude + " longitude " + userLocation.longitude);
        if (userLocation.locality != null)
            globalAdresseName = userLocation.locality;
        else
            globalAdresseName = userLocation.address;

        binding.tvMoaayara.setText("يتم تحديد اتجاه القبلة على  اخر موقع تم تحديده\n  " + globalAdresseName);
        binding.btnMoaayara.setVisibility(INVISIBLE);
        binding.btnUpdateLocation.setVisibility(VISIBLE);
        binding.btnUpdateLocation.setOnClickListener(v -> {
            workingOffLine = false;
            buttonClicked();
        });
        workingOffLine = true;
        binding.mainImageQibla.setVisibility(VISIBLE);

        getQublaDegree(userLocation.latitude, userLocation.longitude);
        compass = new Compass(requireActivity());
        Compass.CompassListener cl = QublaFragment.this::adjustArrowQiblat;
        compass.setListener(cl);

        if (!compass.areSensorsAvailable()) {
            binding.tvMoaayara.setText("جهازك لا يدعم البوصلة");
            binding.btnMoaayara.setVisibility(INVISIBLE);
            binding.btnUpdateLocation.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (compass != null) {
            //setFinalView();
            Log.d(TAG, "start compass");

            startCompass();

        }
    }

    private void startCompass() {
        if (compass == null) return;
        if (!compass.areSensorsAvailable()) {
            binding.tvMoaayara.setText("جهازك لا يدعم البوصلة");
            binding.btnMoaayara.setVisibility(INVISIBLE);
            binding.btnUpdateLocation.setVisibility(View.INVISIBLE);
            binding.mainImageQibla.setVisibility(View.INVISIBLE);
        } else
            compass.start();
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
            startCompass();
            return;
        }
        startCompass();

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
            //should make variables null for memory leak
            gps.stopUsingGPS();
            gps = null;
            binding = null;
            compass = null;
            requestPermissionLauncher = null;
        }
    }

    private void setupCompass() {
        compass = new Compass(requireActivity());
        Compass.CompassListener cl = QublaFragment.this::adjustArrowQiblat;
        compass.setListener(cl);
    }


    public void adjustArrowQiblat(float azimuth) {
        if (!workingOffLine)
            binding.tvMoaayara.setText("حرك جهازك بشكل سريع ثم ضعه على شكل مسطح و اضغط معايرة للحصول على أفضل نتيجة.");
        float QiblaDegree = GetFloat("QiblaDegree");
        Animation an = new RotateAnimation(-(currentAzimuth) + QiblaDegree, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        binding.mainImageQibla.startAnimation(an);

        if (QiblaDegree > 0) {
            binding.mainImageQibla.setVisibility(VISIBLE);
            binding.btnMoaayara.setVisibility(INVISIBLE);
        } else {
            binding.mainImageQibla.setVisibility(INVISIBLE);
        }
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

        if (gps == null)
            gps = new GPSTracker(requireActivity(), (latitude, longitude, location) -> {
                // do something with the location
                Log.e(TAG, "location listener receive " + latitude + " " + longitude);
                getQublaDegree(latitude, longitude);
                saveLastQublaLocation(location);
                if (compass == null) {
                    setupCompass();
                    startCompass();
                } else
                    startCompass();
            });


        if (gps.canGetLocation())
            gps.getLocation();
        else
            showSettigAlert();

    }

    @SuppressLint("SetTextI18n")
    private void getQublaDegree(double latitude, double longitude) {
        double result = 0;
        if (latitude < 0.001 && longitude < 0.001) {
            binding.mainImageQibla.setVisibility(View.GONE);
            binding.tvMoaayara.setText(getResources().getString(R.string.locationunready));
        } else {
            double longitude2 = 39.826209; // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
            double latitude2 = Math.toRadians(21.422507); // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
            double latitude1 = Math.toRadians(latitude);
            double longDiff = Math.toRadians(longitude2 - longitude);
            double y = Math.sin(longDiff) * Math.cos(latitude2);
            double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
            result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
            float qublaDegree = (float) result;
            SaveFloat("QiblaDegree", qublaDegree);

            if (globalAdresseName != null) {
                String formattedQublaDegree = String.format(Locale.US, "%.1f", qublaDegree);
                binding.textUp.setText(getResources().getString(R.string.qibladirection) + " " + formattedQublaDegree + " " + getResources().getString(R.string.fromnorth) + " \n " + globalAdresseName);
            }
            binding.mainImageQibla.setVisibility(VISIBLE);
        }
    }

    private void showSettigAlert() {
        gps.showSettingsAlert();
        binding.mainImageQibla.setVisibility(INVISIBLE);
        binding.mainImageQibla.setVisibility(View.GONE);
        binding.textUp.setText("");
        binding.tvMoaayara.setText(getResources().getString(R.string.gpsplz));
    }

    private void saveLastQublaLocation(Location location) {
        if (doesWeSavedTheNewLocation) return;
        Geocoder geocoder = new Geocoder(requireActivity(), new Locale("ar"));
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Log.e(TAG, "adress object " + addresses.get(0).toString());

            UserLocation newQublaLocation = new UserLocation();
            newQublaLocation.address = addresses.get(0).getAddressLine(0);
            newQublaLocation.longitude = (long) addresses.get(0).getLongitude();
            newQublaLocation.latitude = (long) addresses.get(0).getLatitude();
            newQublaLocation.country = addresses.get(0).getCountryName();
            newQublaLocation.locality = addresses.get(0).getLocality();

            if (newQublaLocation.locality != null) globalAdresseName = newQublaLocation.locality;
            else globalAdresseName = newQublaLocation.address;


            SharedPreferenceManager.getInstance(requireActivity()).saveLastQublaLocation(newQublaLocation);
            doesWeSavedTheNewLocation = true;
        } catch (IOException e) {
            e.printStackTrace();
            doesWeSavedTheNewLocation = false;
        }
    }


}



