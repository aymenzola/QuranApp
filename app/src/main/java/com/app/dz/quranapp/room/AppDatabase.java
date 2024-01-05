package com.app.dz.quranapp.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.dz.quranapp.Entities.AyaAudioLimits;
import com.app.dz.quranapp.Entities.Book;
import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.room.Daos.AyaAudioLimitDao;
import com.app.dz.quranapp.room.Daos.BookDao;
import com.app.dz.quranapp.room.Daos.DayPrayerTimesDao;

@Database(entities = {DayPrayerTimes.class,AyaAudioLimits.class,Book.class,Hadith.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DayPrayerTimesDao getDayPrayerTimesDao();
    public abstract AyaAudioLimitDao getAyaAudioLimitsDao();
    public abstract BookDao getBookDao();
}
