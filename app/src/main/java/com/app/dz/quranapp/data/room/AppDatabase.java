package com.app.dz.quranapp.data.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.dz.quranapp.data.room.Daos.MoreBooksDao;
import com.app.dz.quranapp.data.room.Daos.MotonDao;
import com.app.dz.quranapp.data.room.Entities.AyaAudioLimits;
import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.data.room.Daos.AyaAudioLimitDao;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte.SavedBookPage;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.SavedMatnPage;

@Database(entities = {DayPrayerTimes.class,AyaAudioLimits.class,Book.class,Hadith.class,SavedMatnPage.class,SavedBookPage.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DayPrayerTimesDao getDayPrayerTimesDao();
    public abstract AyaAudioLimitDao getAyaAudioLimitsDao();
    public abstract BookDao getBookDao();
    public abstract MotonDao getMotonDao();
    public abstract MoreBooksDao getMoreBookDao();

}
