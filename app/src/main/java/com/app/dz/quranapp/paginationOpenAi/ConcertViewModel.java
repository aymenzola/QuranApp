package com.app.dz.quranapp.paginationOpenAi;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.app.dz.quranapp.Entities.Sura;
import com.app.dz.quranapp.room.Daos.SuraDao;
import com.app.dz.quranapp.room.MushafDatabase;

public class ConcertViewModel extends AndroidViewModel {
    private SuraDao dao;
    public final LiveData<PagedList<Sura>> concertList;

    public ConcertViewModel(@NonNull Application application) {
        super(application);
        MushafDatabase database = MushafDatabase.getInstance(application);
        dao = database.getSuraDao();

        concertList = new LivePagedListBuilder<>(
                dao.concertsByDate(),10).build();
    }
}
