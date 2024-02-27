package com.app.dz.quranapp.data.room.Daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarCategoryModel;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarModel;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface AdkarDao {

    @Query("SELECT category,COUNT(*) as count FROM adkar GROUP BY category")
    Observable<List<AdkarCategoryModel>> getAdkar();

    @Query("SELECT * FROM adkar where category =:categoryName")
    Observable<List<AdkarModel>> getCategoryAdkar(String categoryName);

    @Query("SELECT * FROM adkar where categoryId =:categoryId")
    Observable<List<AdkarModel>> getAdkarByCategoryId(Integer categoryId);

    @Query("SELECT * FROM adkar where id =:Id")
    Observable<AdkarModel> getdikrWithId(int Id);

    @Query("SELECT * FROM adkar GROUP BY categoryId")
    Observable<List<AdkarModel>> getAdkarGroupedByCategoryId();

    @Query("UPDATE adkar SET isSaved = :isSaved WHERE id = :dikrId")
    int updateIsSaved(int dikrId,int isSaved);

    @Query("SELECT * FROM adkar where isSaved = 1")
    Observable<List<AdkarModel>> getSavedAskar();



}

