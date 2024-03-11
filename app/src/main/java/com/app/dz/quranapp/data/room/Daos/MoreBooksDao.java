package com.app.dz.quranapp.data.room.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte.SavedBookPage;

import java.util.List;

@Dao
public interface MoreBooksDao {
    @Query("SELECT page_number FROM saved_book_pages WHERE book_id = :bookId")
    LiveData<List<Integer>> getSavedPagesNumbersByBookId(int bookId);

    @Query("SELECT * FROM saved_book_pages")
    LiveData<List<SavedBookPage>> getSavedPagesList();

    @Query("SELECT * FROM saved_book_pages WHERE book_id = :bookId")
    List<SavedBookPage> getSavedPagesListByBookId(int bookId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveBookPage(SavedBookPage savedPage);


    @Query("DELETE FROM saved_book_pages WHERE book_id = :bookId AND page_number = :pageNumber")
    void deleteBookByBookId(int bookId,Integer pageNumber);

    @Delete(entity = SavedBookPage.class)
    void deleteBookPage(SavedBookPage savedPage);

}

