package com.app.dz.quranapp.room.Daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarCategoryModel;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarModel;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface AdkarDao {

    @Query("SELECT category,COUNT(*) as count FROM adkar GROUP BY category")
    Observable<List<AdkarCategoryModel>> getAdkar();

    @Query("SELECT * FROM adkar where category =:categoryName")
    Observable<List<AdkarModel>> getCategoryAdkar(String categoryName);

    @Query("SELECT * FROM adkar where id =:Id")
    Observable<AdkarModel> getdikrWithId(int Id);


}

