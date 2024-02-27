package com.app.dz.quranapp.Util;

import static com.app.dz.quranapp.Communs.Constants.QuranHafsIMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.QuranWARCH_TAJWID_IMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.QuranWarchIMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.Quran_ENGLISH_IMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.Quran_FRECH_IMAGE_LINK;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.dz.quranapp.MushafParte.ReadingPosition;
import com.app.dz.quranapp.MushafParte.riwayat_parte.RiwayaType;
import com.app.dz.quranapp.data.room.Entities.Riwaya;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceManager {

    private final SharedPreferences sharedPreferences; // New field
    private final SharedPreferences.Editor editor;
    public static final String SHARED_PREF_NAME = "my_shared_pref";
    private static SharedPreferenceManager mInstance;
    private final Context mCtx;

    private SharedPreferenceManager(Context context) {
        mCtx = context;
        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferenceManager(context);
        }
        return mInstance;
    }


    public void saveLocation(UserLocation userLocation) {
        editor.putLong("longitude", userLocation.longitude);
        editor.putLong("latitude", userLocation.latitude);
        editor.putString("country", userLocation.country);
        editor.putString("Locality", userLocation.locality);
        editor.putString("address", userLocation.address);
        editor.putBoolean("isLocationAvialable", true);
        editor.apply();
    }

    public UserLocation getUserLocation() {
        UserLocation userLocation = new UserLocation();
        userLocation.address = sharedPreferences.getString("address", null);
        userLocation.longitude = sharedPreferences.getLong("longitude", 0L);
        userLocation.latitude = sharedPreferences.getLong("latitude", 0L);
        userLocation.country = sharedPreferences.getString("country", null);
        userLocation.locality = sharedPreferences.getString("Locality", null);

        return userLocation;
    }


    public boolean iSFirstTime() {
        return sharedPreferences.getBoolean("iSFirstTime", true);
    }

    public void changeFirstTimeValue() {
        editor.putBoolean("iSFirstTime", false).apply();
    }

    public boolean iSLocationAvialable() {
        return sharedPreferences.getBoolean("isLocationAvialable", false);
    }

    public void saveReadingPosition(ReadingPosition readingPosition) {
        editor.putInt("aya", readingPosition.aya);
        editor.putInt("sura", readingPosition.sura);
        editor.putInt("page", readingPosition.page);
        editor.putString("ayaText", readingPosition.ayaText);
        editor.putBoolean("isAyaSaved", true);
        editor.apply();
    }


    public ReadingPosition getReadinPosition() {
        ReadingPosition readingPosition = new ReadingPosition();
        readingPosition.aya = sharedPreferences.getInt("aya", -1);
        readingPosition.sura = sharedPreferences.getInt("sura", -1);
        readingPosition.page = sharedPreferences.getInt("page", -1);
        readingPosition.ayaText = sharedPreferences.getString("ayaText", "");
        return readingPosition;
    }


    public void saveSelectedReaderId(int readerId) {
        Log.e("saveSelectedReaderId", "saveSelectedReaderId: " + readerId);
        editor.putInt("readerId", readerId);
        editor.apply();
    }


    public String getSelectedReader() {
        return sharedPreferences.getString("readerName", "Shuraym");
    }

    public int getSelectedReaderId() {
        int id = sharedPreferences.getInt("readerId", 2);
        Log.e("getSelectedReaderId", "getSelectedReaderId: " + id);
        return id;
    }

    public boolean iSThereAyaSaved() {
        return sharedPreferences.getBoolean("isAyaSaved", false);
    }

    public Riwaya getLastRiwaya() {
        String reiwayaString = sharedPreferences.getString("riwaya","no");
        if (reiwayaString.equals("no")) {
            //default riwaya
            return getAllRiwayaList().get(0);
        } else
            return new Gson().fromJson(reiwayaString, Riwaya.class);
    }

    public void saveLastRiwaya(Riwaya riwaya) {
        editor.putString("riwaya",new Gson().toJson(riwaya)).apply();
    }

    public List<Riwaya> getAllRiwayaList(){
        List<Riwaya> list = new ArrayList<>();

        list.add(new Riwaya(1, "القرآن برواية ورش", RiwayaType.WARCH.name(), "", QuranWarchIMAGE_LINK, 604));
        list.add(new Riwaya(2, "القرآن برواية حفص", RiwayaType.HAFS.name(), "", QuranHafsIMAGE_LINK, 604));
        list.add(new Riwaya(3, "مصحف التجويد حفص", RiwayaType.HAFS_TAJWID.name(), "", QuranWARCH_TAJWID_IMAGE_LINK, 604));
        list.add(new Riwaya(4, "المصحف التفاعلي حفص", RiwayaType.HAFS_SMART.name(), "", QuranWarchIMAGE_LINK, 604));
        list.add(new Riwaya(5, "القرآن باللغة الفرنسية", RiwayaType.FRENCH_QURAN.name(), "", Quran_FRECH_IMAGE_LINK, 604));
        list.add(new Riwaya(6, "القرآن بالغة الانجليزية", RiwayaType.ENGLISH_QURAN.name(), "", Quran_ENGLISH_IMAGE_LINK, 604));
        list.add(new Riwaya(7, "المصحف مع التفسير حفص", RiwayaType.TAFSIR_QURAN.name(), "", QuranWarchIMAGE_LINK, 604));

        return list;
    }

}