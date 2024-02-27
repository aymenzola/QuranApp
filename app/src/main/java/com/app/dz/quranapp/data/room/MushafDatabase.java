package com.app.dz.quranapp.data.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaWarsh;
import com.app.dz.quranapp.data.room.Entities.Juz;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarModel;
import com.app.dz.quranapp.data.room.Daos.AyaDao;
import com.app.dz.quranapp.data.room.Daos.JuzDao;
import com.app.dz.quranapp.data.room.Daos.AdkarDao;
import com.app.dz.quranapp.data.room.Daos.AyaWarshDao;
import com.app.dz.quranapp.data.room.Daos.SuraDao;


@Database(entities = {Sura.class,Aya.class,Juz.class,AdkarModel.class, AyaWarsh.class}, version = 2, exportSchema = false)
public abstract class MushafDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "mushaf_metadata.db";
    public static final int ASSET_DB_VERSION = 2;

    private static volatile MushafDatabase instance;

    public static MushafDatabase getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (MushafDatabase.class) {
                if (instance == null) {
                    instance = RoomAsset.databaseBuilder(context.getApplicationContext(),
                            MushafDatabase.class, DATABASE_NAME, ASSET_DB_VERSION)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract AyaDao getAyaDao();
    public abstract AyaWarshDao getAyaDaoWarsh();

    public abstract SuraDao getSuraDao();

    public abstract JuzDao getJuzDao();
    public abstract AdkarDao getAdkarDao();


}
