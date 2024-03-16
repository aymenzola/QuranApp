package com.app.dz.quranapp.data.room;

import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseClient {
 
    private Context mCtx;
    private static DatabaseClient mInstance;
    
    //our app database object
    private AppDatabase appDatabase;
 
    private DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
        
        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx,AppDatabase.class,"PrayerTimes")
                .addMigrations(MIGRATION_1_3)
                .build();
    }
 
    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }
 
    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    Migration MIGRATION_1_3 = new Migration(1,3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // This migration adds new tables for SavedMatnPage and SavedBookPage entities.
            database.execSQL("CREATE TABLE IF NOT EXISTS `saved_book_pages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `book_id` INTEGER NOT NULL, `page_number` INTEGER NOT NULL, `book_title` TEXT, `page_title` TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `saved_pages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `matn_id` INTEGER NOT NULL, `page_number` INTEGER NOT NULL, `book_title` TEXT, `page_title` TEXT)");
        }
    };
}

