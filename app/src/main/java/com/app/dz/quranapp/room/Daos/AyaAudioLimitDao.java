package com.app.dz.quranapp.room.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.dz.quranapp.Entities.AyaAudioLimits;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface AyaAudioLimitDao {

    @Query("SELECT * FROM AyaAudioLimits WHERE sura_number=:suraNumb and reader_name=:readerName")
    List<AyaAudioLimits> getSuraAyatLimitsWithId(int suraNumb, String readerName);


    @Query("SELECT COUNT(ayalimitid) FROM AyaAudioLimits WHERE sura_number=:suraNumb and reader_name=:readerName")
    Integer getSuraAyatLimitsCount(int suraNumb, String readerName);

    @Query("SELECT COUNT(sura_number) FROM AyaAudioLimits WHERE sura_number=:suraNumb and reader_name=:readerName")
    Observable<Integer> getSuraAyatLimitsCountObsrvable(int suraNumb, String readerName);

    @Insert
    void insert(AyaAudioLimits ayaAudioLimits);

}

