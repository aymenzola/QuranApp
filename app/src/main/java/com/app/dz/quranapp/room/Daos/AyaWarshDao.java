package com.app.dz.quranapp.room.Daos;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.Entities.AyaWarsh;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface AyaWarshDao {

    @Transaction
    @Query("SELECT * FROM AyaWarsh")
    Observable<List<AyaWarsh>> getAyatWarsh();

    @Query("SELECT * FROM AyaWarsh WHERE page=:pageNum")
    Observable<List<AyaWarsh>> getAllInPage(int pageNum);

    @Query("SELECT * FROM AyaWarsh WHERE page=:pageNum ORDER BY id DESC LIMIT 1")
    Observable<AyaWarsh> getLastAyaInPage(int pageNum);

}

