package com.app.dz.quranapp.room.Daos;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.app.dz.quranapp.Entities.Juz;
import com.app.dz.quranapp.Entities.Sura;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface SuraDao {

    @Query("SELECT * FROM Sura")
    Observable<List<Sura>> getAll();

    @Query("SELECT * FROM Sura where id>:startId ORDER BY id ASC LIMIT :pageSize")
    Observable<List<Sura>> getSuraList(int startId, int pageSize);

    @Query("SELECT * FROM Sura WHERE id=:suraId")
    Sura findById(int suraId);


    @Query("SELECT * FROM Juz")
    Observable<List<Juz>> getAllJuz();

    // The Integer type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM sura ORDER BY id DESC")
    DataSource.Factory<Integer,Sura> concertsByDate();

/*
    @Query("SELECT juz, sura from aya where page=:currentPage LIMIT 1")
    Single<QuranPageInfo> getQuranPageInfo(int currentPage);

    @Query("select sura.id, sura.ayas, sura.type, aya.juz, aya.page, aya.sura from sura join aya on aya.sura=sura.id and aya.sura_aya=1")
    Single<List<SuraIndexModel>> getSuraIndexInfo();

    @Query("select id, ayas from sura")
    Single<List<SuraVersesNumber>> getSuraVersesNumber();*/
}
