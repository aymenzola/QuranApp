package com.app.dz.quranapp.Util;

import static com.app.dz.quranapp.Communs.Constants.QuranHafsIMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.QuranWARCH_TAJWID_IMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.QuranWarchIMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.Quran_ENGLISH_IMAGE_LINK;
import static com.app.dz.quranapp.Communs.Constants.Quran_FRECH_IMAGE_LINK;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.dz.quranapp.quran.models.ReadingPosition;
import com.app.dz.quranapp.quran.models.RiwayaType;
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
        saveLastQublaLocation(userLocation);
        editor.putString("userLocation", new Gson().toJson(userLocation, UserLocation.class)).apply();
        editor.putBoolean("isLocationAvialable", true).apply();
    }

    public UserLocation getUserLocation() {
        String stringLocation = sharedPreferences.getString("userLocation", "no");
        if (stringLocation.equals("no")) {
            return new UserLocation();
        } else {
            return new Gson().fromJson(stringLocation, UserLocation.class);
        }
    }


    public void saveLastQublaLocation(UserLocation userLocation) {
        editor.putString("LastQublaLocation", new Gson().toJson(userLocation, UserLocation.class)).apply();
    }

    public UserLocation getLastQublaLocation() {
        String stringLocation = sharedPreferences.getString("LastQublaLocation", "no");
        if (stringLocation.equals("no")) {
            return null;
        } else {
            return new Gson().fromJson(stringLocation, UserLocation.class);
        }
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
        Log.e("checkSaveTag","saving "+readingPosition.toString());
        editor.putString("readingPosition", new Gson().toJson(readingPosition,ReadingPosition.class));
        editor.putBoolean("isAyaSaved", true);
        editor.apply();
    }


    public ReadingPosition getReadinPosition() {
        String reiwayaString = sharedPreferences.getString("readingPosition","no");
        if (!reiwayaString.equals("no")) {
            return new Gson().fromJson(reiwayaString,ReadingPosition.class);
        } else
            return new ReadingPosition(-1, -1, -1, "", RiwayaType.HAFS.name());
    }

    //clrear reading position values
    public void clearReadingPosition() {
        saveReadingPosition(new ReadingPosition(-1, -1, -1, "", ""));
        editor.putBoolean("isAyaSaved",false);
        editor.apply();
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
        String reiwayaString = sharedPreferences.getString("riwaya", "no");
        if (reiwayaString.equals("no")) {
            //default riwaya
            return getAllRiwayaList().get(0);
        } else
            return new Gson().fromJson(reiwayaString, Riwaya.class);
    }

    public void saveLastRiwaya(Riwaya riwaya) {
        editor.putString("riwaya", new Gson().toJson(riwaya)).apply();
    }

    public void saveAsLastRiwayaWithName(String riwayaName) {
        List<Riwaya> riwayaList = getAllRiwayaList();
        for (Riwaya riwaya : riwayaList) {
            if (riwaya.tag.equals(riwayaName)) {
                saveLastRiwaya(riwaya);
                break;
            }
        }
    }

    public List<Riwaya> getAllRiwayaList() {
        List<Riwaya> list = new ArrayList<>();

        list.add(new Riwaya(1, "القرآن برواية ورش", RiwayaType.WARCH.name(), "", QuranWarchIMAGE_LINK, 604));
        list.add(new Riwaya(2, "القرآن برواية حفص", RiwayaType.HAFS.name(), "", QuranHafsIMAGE_LINK, 604));
        list.add(new Riwaya(3, "مصحف التجويد حفص", RiwayaType.HAFS_TAJWID.name(), "", QuranWARCH_TAJWID_IMAGE_LINK, 604));
        list.add(new Riwaya(4, "المصحف التفاعلي حفص", RiwayaType.HAFS_SMART.name(), "", QuranWarchIMAGE_LINK, 604));
        list.add(new Riwaya(5, "القرآن بالترجمة الفرنسية", RiwayaType.FRENCH_QURAN.name(), "", Quran_FRECH_IMAGE_LINK, 604));
        list.add(new Riwaya(6, "القرآن بالترجمة الانجليزية", RiwayaType.ENGLISH_QURAN.name(), "", Quran_ENGLISH_IMAGE_LINK, 604));
        list.add(new Riwaya(7, "المصحف مع التفسير حفص", RiwayaType.TAFSIR_QURAN.name(), "", QuranWarchIMAGE_LINK, 604));

        list.get(4).fileName = "FrenchQuran.pdf";
        list.get(5).fileName = "EnglishQuran.pdf";

        list.get(4).setFileUrl("https://www.dropbox.com/scl/fi/susr5ju1ut9r4rl17qu19/FrenchQuran.pdf?rlkey=ssn775mix8jze0czy1wcwjbhj&dl=1");
        list.get(5).setFileUrl("https://www.dropbox.com/scl/fi/rvhcl9t5fu9hmwm9p7evz/EnglishQuran.pdf?rlkey=ws1w55gp36722r6rspxdcebza&dl=1");

        return list;
    }

}