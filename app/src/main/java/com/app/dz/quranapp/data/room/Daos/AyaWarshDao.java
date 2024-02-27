package com.app.dz.quranapp.data.room.Daos;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.app.dz.quranapp.data.room.Entities.AyaWarsh;

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

