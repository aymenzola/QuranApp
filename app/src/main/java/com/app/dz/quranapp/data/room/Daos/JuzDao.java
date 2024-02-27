package com.app.dz.quranapp.data.room.Daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.app.dz.quranapp.data.room.Entities.Juz;

import java.util.List;


@Dao
public interface JuzDao {

    @Query("SELECT * FROM Juz")
    List<Juz> getAll();

    @Query("SELECT * FROM Juz WHERE id=:id")
    Juz getById(int id);

}
