package com.app.dz.quranapp.ui.activities.LocationParte;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.Util.UserLocation;
import com.app.dz.quranapp.Communs.PrayerTimesHelper;
import com.app.dz.quranapp.databinding.ActivityLocationBinding;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class LocationActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 50;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private boolean mLocationPermissionGranted = false;
    private static final int ERROR_DIALOG_REQUEST = 3;
    private final static String TAG = LocationActivity.class.getSimpleName();
    private ActivityLocationBinding binding;
    private PublicMethods publicMethods;
    private boolean isNewLocationReadyToSave = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private UserLocation newUserLocation;
    private boolean alreadyExist = false;
    private boolean alreadyAnimted = false;
    private UserLocation prevUserLocation;
    private TimeViewModel viewModel;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        publicMethods = PublicMethods.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        binding.imgCloseLocation.setOnClickListener(v -> moveToMainAtivty());


        alreadyExist = SharedPreferenceManager.getInstance(this).iSLocationAvialable();

        if (alreadyExist) {
            binding.btnGetLocation.setText("تحديد موقع جديد تلقائيا");
            binding.tvMessage.setVisibility(View.INVISIBLE);
            binding.tvLocationResult.setVisibility(View.VISIBLE);
            prevUserLocation = SharedPreferenceManager.getInstance(this).getUserLocation();
            binding.tvLocationResult.setText(prevUserLocation.address);
            binding.tvPrevious.setVisibility(View.VISIBLE);
        }

        binding.tvMessage.setOnClickListener(v -> {
            // startActivity(new Intent(LocationActivity1.this, UserSearchActivity.class));
        });


        setObservers();
        binding.btnGetLocation.setOnClickListener(v -> {
            //anim.setDuration(1000); // 1 second
            //binding.imageview.startAnimation(anim);


            if (isNewLocationReadyToSave) {
                preparePrayerTimes(newUserLocation.longitude, newUserLocation.latitude, newUserLocation);
                return;
            }
            if (publicMethods.checkNetworkConnection(this)) {
                if (checkMapServices()) {
                    if (mLocationPermissionGranted) {
                        // do what you want
                        checkPermissionsAndGetUserLoaction();
                    } else {
                        getLocationPermission();
                    }
                }
            } else {
                publicMethods.showNoInternetDialog(LocationActivity.this, getString(R.string.check_internet));
            }

        });
    }


    private void handleErrorCase() {
        binding.tvSavingLocationMessage.setVisibility(View.VISIBLE);
        binding.tvSavingLocationMessage.setText("حدث خطأ ما");
        binding.btnGetLocation.setText("اعادة المحاولة");
        binding.progressBarSaving.setVisibility(View.GONE);
        binding.btnGetLocation.setEnabled(true);
    }


    private void setNextAlarm() {
        PrayerTimesHelper.scheduleNextPrayerJob(LocationActivity.this);
        Toast.makeText(LocationActivity.this, "next prayer adan scheduled", Toast.LENGTH_SHORT).show();
    }
    private void setObservers() {
        viewModel.getResult().observe(LocationActivity.this, s -> {
            Log.e(TAG, "result : " + s);
            if (s.equals("Finished")) {
                setNextAlarm();
                // Save data to shared preferences
                SharedPreferenceManager.getInstance(LocationActivity.this).saveLocation(newUserLocation);
                moveToMainAtivty();

            } else if (s.equals("error")) {
                handleErrorCase();
            }

        });
    }

    private void moveToMainAtivty() {
        startActivity(new Intent(LocationActivity.this, MainActivity.class));
    }

    private void AnimationAction() {
        Animation animation = new ScaleAnimation(1f, 0.75f, 1f, 0.75f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setFillAfter(true);

        binding.tvMessage.animate()
                .alpha(0f)
                .translationY(-binding.tvMessage.getHeight())
                .setDuration(500)
                .withEndAction(() -> {
                    // hide the view after animation
                    binding.tvMessage.setVisibility(View.INVISIBLE);
                })
                .start();


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.tvLocationResult.setVisibility(View.VISIBLE);
                binding.tvPrevious.setVisibility(View.VISIBLE);
                requestNewLocation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        binding.imageview.startAnimation(animation);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("gpstag", "addresse 1 " + requestCode);

        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (mLocationPermissionGranted) {
                checkPermissionsAndGetUserLoaction();
            } else {
                getLocationPermission();
            }
        }

    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
        builder.setMessage("هذا التطبيق يحتاج الى تفعيل خدمة تحديد المواقع لحساب مواقيت الصلاة هل توافق ؟")
                .setCancelable(false)
                .setPositiveButton("حسنا", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.e(TAG, "getLocationPermission");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e(TAG, "we have permission");
                mLocationPermissionGranted = true;
                checkPermissionsAndGetUserLoaction();

            } else {
                Log.e(TAG, "we ask For permission");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            Log.e(TAG, "we have permission");
            mLocationPermissionGranted = true;
            checkPermissionsAndGetUserLoaction();
        }

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LocationActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LocationActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "لا يمكن طلب الخريطة", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void requestNewLocation() {
        Log.e(TAG, "requestNewLocation ");
        binding.tvLocationResult.setVisibility(View.INVISIBLE);
        binding.tvMessage.setVisibility(View.INVISIBLE);
        isNewLocationReadyToSave = false;
        binding.btnGetLocation.setText("تحديد الموقع تلقائيا");
        binding.btnGetLocation.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.e(TAG, "location result failure");
                    binding.btnGetLocation.setText("اعادة المحاولة");
                    binding.btnGetLocation.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);

                    // Handle failure
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.e(TAG, "we recieve new location");
                        binding.progressBar.setVisibility(View.GONE);
                        ManageTheRecievedLocation(location);
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        // Use the location
                        return;
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "request new location");
            if (fusedLocationProviderClient == null)
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
        } else {
            //No permission
            Log.e(TAG, "no location permission");
        }

    }


    private void checkPermissionsAndGetUserLoaction() {
        if (publicMethods.checkNetworkConnection(this)) {
            if (!alreadyExist && !alreadyAnimted) {
                Log.e(TAG, "start Animation");
                AnimationAction();
                return;
            }
            requestNewLocation();

        } else {
            publicMethods.showNoInternetDialog(LocationActivity.this, getString(R.string.check_internet));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void ManageTheRecievedLocation(Location location) {
        Geocoder geocoder = new Geocoder(LocationActivity.this, new Locale("ar"));
        List<Address> addresses = null;
        binding.btnGetLocation.setEnabled(true);
        binding.tvLocationResult.setVisibility(View.VISIBLE);
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Log.e(TAG, "latitude " + addresses.get(0).getLatitude());
            Log.e(TAG, "longitude " + addresses.get(0).getLongitude());
            Log.e(TAG, "latitude adresse" + addresses.get(0).getAddressLine(0));
            Log.e(TAG, "latitude locality " + addresses.get(0).getLocality());
            Log.e(TAG, "latitude counry " + addresses.get(0).getCountryName());

            newUserLocation = new UserLocation();

            newUserLocation.address = addresses.get(0).getAddressLine(0);
            newUserLocation.longitude = (long) addresses.get(0).getLongitude();
            newUserLocation.latitude = (long) addresses.get(0).getLatitude();
            newUserLocation.country = addresses.get(0).getCountryName();
            newUserLocation.locality = addresses.get(0).getLocality();

            if (prevUserLocation != null) {
                if (!newUserLocation.address.equals(prevUserLocation.address)) {
                    isNewLocationReadyToSave = true;
                    binding.tvLocationResult.setVisibility(View.VISIBLE);
                    binding.btnGetLocation.setText("حفظ الموقع الجديد");
                    binding.btnGetLocation.setVisibility(View.VISIBLE);
                    binding.tvLocationResult.setText(newUserLocation.address);
                }
            } else {
                isNewLocationReadyToSave = true;
                binding.tvLocationResult.setVisibility(View.VISIBLE);
                binding.btnGetLocation.setText("حفظ الموقع الجديد");
                binding.btnGetLocation.setVisibility(View.VISIBLE);
                binding.tvLocationResult.setText(newUserLocation.address);
            }
        } catch (IOException e) {
            e.printStackTrace();
            binding.btnGetLocation.setText("اعادة المحاولة ");

        }
    }


    public void preparePrayerTimes(long longitude, long latitude, UserLocation userLocation) {

        Calendar ca = Calendar.getInstance();
        int month = ca.get(Calendar.MONTH) + 1;
        int year = ca.get(Calendar.YEAR);
        String date = ca.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + year;

        Log.e(TAG, "date : " + date);
        viewModel.setResult("We start");
        binding.tvSavingLocationMessage.setVisibility(View.VISIBLE);
        binding.progressBarSaving.setVisibility(View.VISIBLE);
        binding.btnGetLocation.setEnabled(false);

        new Thread(() -> getPrayerTimesFromServer(month
                , year, latitude, longitude, userLocation)).start();

    }

    public void getPrayerTimesFromServer(int month, int year, double latitude, double longitude, UserLocation userLocation) {

        //adding to database
        AppDatabase db = DatabaseClient.getInstance(LocationActivity.this).getAppDatabase();
        DayPrayerTimesDao dao = db.getDayPrayerTimesDao();

        String urll = "http://api.aladhan.com/v1/calendar/" + year + "?latitude=" + latitude + "&" + "longitude=" + longitude + "&method=" + 3;

        RequestQueue queue = Volley.newRequestQueue(LocationActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urll, null, response -> {
            try {

                viewModel.setResult("Started saving data");

                List<DayPrayerTimes> prayerTimesList = new ArrayList<>();
                JSONObject data = response.getJSONObject("data");

                for (int j = 1; j <= 12; j++) {
                    JSONArray jsonArrayMonthe = data.getJSONArray(String.valueOf(j));
                    for (int i = 0; i < jsonArrayMonthe.length(); i++) {
                        JSONObject timings = jsonArrayMonthe.getJSONObject(i).getJSONObject("timings");

                        String fajr = timings.getString("Fajr");
                        String Sunrise = timings.getString("Sunrise");
                        String Dhuhr = timings.getString("Dhuhr");
                        String Asr = timings.getString("Asr");
                        String Maghrib = timings.getString("Maghrib");
                        String Isha = timings.getString("Isha");
                        String Imsak = timings.getString("Imsak");


                        String date = jsonArrayMonthe.getJSONObject(i).getJSONObject("date").getJSONObject("gregorian").getString("date");

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = dateFormat.parse(date);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date1);


                        int mont = cal.get(Calendar.MONTH) + 1;
                        Log.e("timing", "cal month : " + mont + "date foramt " + date);

                        DayPrayerTimes dayPrayerTimes = new DayPrayerTimes(
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.YEAR),
                                fajr.substring(0, 5),
                                Sunrise.substring(0, 5),
                                Dhuhr.substring(0, 5),
                                Asr.substring(0, 5),
                                Maghrib.substring(0, 5),
                                Isha.substring(0, 5),
                                Imsak.substring(0, 5),
                                date
                        );
                        prayerTimesList.add(dayPrayerTimes);
                    }
                }

                new Thread(() -> {
                    try {
                        if (prayerTimesList.size() > 0) dao.deleteAll();
                        dao.insert(prayerTimesList);
                        viewModel.setResult("Finished");
                    } catch (Exception e) {
                        viewModel.setResult("error");
                        Log.e(TAG, "error while inserting time item " + e.getMessage());
                    }
                }).start();

            } catch (JSONException | ParseException e) {
                viewModel.setResult("error");
                e.printStackTrace();
            }
        }, error -> {
            viewModel.setResult("error");
            Log.e(TAG, "error " + error.getMessage());
        });
        queue.add(jsonObjectRequest);


    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsGranted");

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) getLocationPermission();

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsDenied");

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("checkpermision", "onPermission resule   requestCode " + requestCode);

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have access
                getLocationPermission();
                Log.e(TAG, "onResult Permissions accepted");

            } else {
                // we do not have access
                Log.e(TAG, "onResult Permissions refused");
            }
        }
    }


}
