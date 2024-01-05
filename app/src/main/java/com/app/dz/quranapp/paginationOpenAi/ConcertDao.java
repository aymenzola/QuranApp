package com.app.dz.quranapp.paginationOpenAi;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.app.dz.quranapp.Entities.Sura;

@Dao
public interface ConcertDao {
    // The Integer type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM sura ORDER BY ayas DESC")
    DataSource.Factory<Integer,Sura> concertsByDate();
}


