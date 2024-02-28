package com.app.dz.quranapp.data.room.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.dz.quranapp.ui.activities.CollectionParte.motonParte.SavedMatnPage;

import java.util.List;

@Dao
public interface MotonDao {
    @Query("SELECT page_number FROM saved_pages WHERE matn_id = :matnId")
    LiveData<List<Integer>> getSavedPagesNumbersByMatnId(int matnId);

    @Query("SELECT * FROM saved_pages")
    LiveData<List<SavedMatnPage>> getSavedPagesList();

    @Query("SELECT * FROM saved_pages WHERE matn_id = :matnId")
    List<SavedMatnPage> getSavedPagesListByMatnId(int matnId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMatnPage(SavedMatnPage savedPage);


    @Query("DELETE FROM saved_pages WHERE matn_id = :matnId AND page_number = :pageNumber")
    void deleteMatnByMatnId(int matnId,Integer pageNumber);

    @Delete(entity = SavedMatnPage.class)
    void deleteMatnPage(SavedMatnPage savedPage);

}

