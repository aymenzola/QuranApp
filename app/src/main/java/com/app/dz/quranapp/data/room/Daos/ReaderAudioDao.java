package com.app.dz.quranapp.data.room.Daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.app.dz.quranapp.quran.models.ReaderAudio;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface ReaderAudioDao {
    @Query("SELECT * FROM audio where audiotype == 1 OR audiotype == 3")
    Observable<List<ReaderAudio>> getAvailableReaders();

    @Query("SELECT * FROM audio where id == :readerId")
    Observable<ReaderAudio> getReaderWithId(int readerId);

    @Query("SELECT reader_tag FROM audio WHERE id = :id")
    Observable<String> getReaderTag(int id);

    @Query("SELECT reader_image FROM audio WHERE id = :id")
    Observable<String> getReaderImage(int id);

    @Query("SELECT is_there_selection FROM audio WHERE id = :id")
    Observable<Integer> getIsThereSelection(int id);

}

