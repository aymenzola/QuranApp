package com.app.dz.quranapp.room;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ClearDatabaseMigration extends Migration {

    public ClearDatabaseMigration(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Drop all tables
        database.execSQL("DROP TABLE IF EXISTS sura");
        database.execSQL("DROP TABLE IF EXISTS aya");
        database.execSQL("DROP TABLE IF EXISTS juz");
        database.execSQL("DROP TABLE IF EXISTS adkar");

        // Recreate tables with new schema
        /*
        database.execSQL("CREATE TABLE IF NOT EXISTS sura (...)");
        database.execSQL("CREATE TABLE IF NOT EXISTS aya (...)");
        database.execSQL("CREATE TABLE IF NOT EXISTS juz (...)");
        database.execSQL("CREATE TABLE IF NOT EXISTS adkar (...)");*/
    }
}