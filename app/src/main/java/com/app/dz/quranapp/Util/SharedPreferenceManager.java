package com.app.dz.quranapp.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.dz.quranapp.MushafParte.ReadingPosition;

public class SharedPreferenceManager {
   private static final String SHARED_PREF_NAME = "my_shared_pref";
   private static SharedPreferenceManager mInstance;
   private Context mCtx;
   
   private SharedPreferenceManager(Context context) {
      mCtx = context;
   }
   
   public static synchronized SharedPreferenceManager getInstance(Context context) {
      if (mInstance == null) {
         mInstance = new SharedPreferenceManager(context);
      }
      return mInstance;
   }

   public void saveLocation(UserLocation userLocation) {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putLong("longitude", userLocation.longitude);
      editor.putLong("latitude", userLocation.latitude);
      editor.putString("country", userLocation.country);
      editor.putString("Locality", userLocation.locality);
      editor.putString("address", userLocation.address);
      editor.putBoolean("isLocationAvialable",true);
      editor.apply();
   }
   
   public UserLocation getUserLocation() {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      UserLocation userLocation = new UserLocation();
      userLocation.address = sharedPreferences.getString("address", null);
      userLocation.longitude = sharedPreferences.getLong("longitude", 0L);
      userLocation.latitude = sharedPreferences.getLong("latitude", 0L);
      userLocation.country = sharedPreferences.getString("country", null);
      userLocation.locality = sharedPreferences.getString("Locality", null);

      return userLocation;
   }


   public boolean iSFirstTime() {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      return sharedPreferences.getBoolean("iSFirstTime",true);
   }

   public void changeFirstTimeValue() {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean("iSFirstTime",false).apply();
   }

   public boolean iSLocationAvialable() {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      return sharedPreferences.getBoolean("isLocationAvialable",false);
   }

   public void saveReadingPosition(ReadingPosition readingPosition) {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putInt("aya", readingPosition.aya);
      editor.putInt("sura", readingPosition.sura);
      editor.putInt("page", readingPosition.page);
      editor.putString("ayaText", readingPosition.ayaText);
      editor.putBoolean("isAyaSaved",true);
      editor.apply();
   }


   public ReadingPosition getReadinPosition() {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      ReadingPosition readingPosition = new ReadingPosition();
      readingPosition.aya = sharedPreferences.getInt("aya", -1);
      readingPosition.sura = sharedPreferences.getInt("sura", -1);
      readingPosition.page = sharedPreferences.getInt("page", -1);
      readingPosition.ayaText = sharedPreferences.getString("ayaText","");
      return readingPosition;
   }

   public void saveSelectedReader(String readerName) {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString("readerName",readerName);
      editor.apply();
   }


   public String getSelectedReader() {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      return sharedPreferences.getString("readerName","Shuraym");
   }

   public boolean iSThereAyaSaved() {
      SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
      return sharedPreferences.getBoolean("isAyaSaved",false);
   }


}