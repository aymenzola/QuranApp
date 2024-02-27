package com.app.dz.quranapp.data.room.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaWithSura;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.ui.models.SearchModel;
import com.app.dz.quranapp.ui.models.TafseerModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface AyaDao {


    @Query("SELECT * FROM Sura WHERE name LIKE '%' || :suraName || '%'")
    Observable<Sura> getSuraName(String suraName);

    @Transaction
    @Query("SELECT * FROM Aya where pure_text LIKE '%' || :query || '%' LIMIT :searchPageSize OFFSET :offset")
    Observable<List<AyaWithSura>> searchAyatWithSura(String query, int searchPageSize, int offset);

    @Transaction
    @Query("SELECT count(*) FROM Aya where pure_text LIKE '%' || :query || '%'")
    Observable<Integer> searchSize(String query);

    @Query("SELECT * FROM Aya where pure_text LIKE '%' || :query || '%'")
    Observable<List<Aya>> searchInAyat(String query);

    @Query("SELECT * FROM Sura where id =:suraId")
    Observable<Sura> getSuraWithId(int suraId);

    @Query("SELECT * FROM Sura where id =:suraId")
    Sura getSuraWithIdNoObserver(int suraId);

    @Query("SELECT * FROM Aya")
    List<Aya> getAll();

    @Query("SELECT * FROM Aya WHERE sura=:suraNumb")
    Observable<List<Aya>> getAllWithSuraId(int suraNumb);

    @Query("SELECT * FROM Aya WHERE sura=:suraNumb")
    List<Aya> getAyatWithSuraId(int suraNumb);

    @Query("SELECT * FROM Aya WHERE juz=:JuzNumb")
    List<Aya> getAyatWithJuzaId(int JuzNumb);

    @Query("SELECT * FROM Aya WHERE id IN (:ayaIds)")
    List<Aya> getAllByIds(int... ayaIds);

    @Query("SELECT * FROM Aya WHERE id=:ayaId")
    Single<Aya> findById(int ayaId);

    @Query("SELECT * FROM Aya WHERE id=:ayaId")
    Aya findAyaById(int ayaId);

    @Query("SELECT * FROM Aya WHERE page=:pageNum")
    Observable<List<Aya>> getAllInPage(int pageNum);

    @Query("SELECT * FROM Aya WHERE page=:page AND id=:ayaId LIMIT 1")
    Aya getPageAya(int page, int ayaId);

    @Query("select text, tafseer, pure_text from aya WHERE sura=:suraNumber")
    LiveData<List<TafseerModel>> getPageTafseers(int suraNumber);

    @Query("SELECT * FROM Aya WHERE page=:pageNum LIMIT 1")
    Observable<Aya> getFirstAyaInPage(int pageNum);

    @Query("SELECT * FROM Aya WHERE page=:pageNum ORDER BY id DESC LIMIT 1")
    Observable<List<Aya>> getLastAyaInPage(int pageNum);

    @Query("SELECT * FROM Aya where id=(SELECT MIN(id) FROM Aya WHERE sura=:sura)")
    Aya getFirstAyaInSura(int sura);

    @Query("SELECT * FROM Aya where id=(SELECT MIN(id) FROM Aya WHERE sura=:sura)")
    Observable<Aya> getFirstAyaInSuraObservable(int sura);

    @Query("SELECT * FROM Aya where sura=:sura and sura_aya=:ayaInSura")
    Aya getJuzaStartAya(int sura,int ayaInSura);



/*
    @Query("SELECT * FROM Aya WHERE page = :pageNum OR (page = :previousPageNumber AND id = (SELECT MAX(id) FROM Aya WHERE page = :previousPageNumber)) ORDER BY id ASC")
    Observable<List<Aya>> getLastAyaInPa(int pageNum,int previousPageNumber);*/




  /*  @Query("SELECT id, sura, pure_text, text, page, sura_aya, juz FROM Aya WHERE id IN (select aya from AyaQuranSubject where subject=:categoryId)")
    Single<List<SearchModel>> getCategoryAyas(int categoryId);
*/
    @Query("SELECT id, sura, pure_text, page, sura_aya, juz FROM Aya WHERE pure_text like '%' || :input || '%'")
    Single<List<SearchModel>> getSimpleSearchResult(String input);

    @Query("SELECT id, sura, pure_text, page, sura_aya, juz FROM Aya WHERE pure_text like '%' || :input || '%' and sura=:suraNumber")
    Single<List<SearchModel>> getSuraSearchResult(String input, int suraNumber);

    @Query("SELECT id, sura, pure_text, page, sura_aya, juz FROM Aya WHERE pure_text like '%' || :input || '%' and juz=:juzNumber")
    Single<List<SearchModel>> getJuzSearchResult(String input, int juzNumber);


    @Query("SELECT distinct sura FROM Aya where juz=:juz ")
    LiveData<List<Integer>> getSurasInChapter(int juz);

    @Query("SELECT id, sura, pure_text, text, page, sura_aya, juz FROM Aya WHERE pure_text like '%' || :inputSearch || '%' and juz=:selectedJuz and sura=:selectedSura")
    Single<List<SearchModel>> getSuraJuzSearchResult(String inputSearch, int selectedSura, int selectedJuz);

/*
    @Query("SELECT id, sura, pure_text, text, page, sura_aya, juz FROM Aya WHERE pure_text like '%' || :inputSearch || '%' " +
            "and juz=:selectedJuz and " +
            "id between (select aya_from from hizbquarter where id=:startHezbInterval) AND (select aya_to from hizbquarter where id=:endHezbInterval)")
    Single<List<SearchModel>> getJuzHezbSearchResult(String inputSearch, int selectedJuz, int startHezbInterval, int endHezbInterval);

    @Query("SELECT id, sura, pure_text, text, page, sura_aya, juz FROM Aya WHERE pure_text like '%' || :inputSearch || '%' " +
            "and juz=:selectedJuz and sura=:selectedSura and " +
            "id between (select aya_from from hizbquarter where id=:startHezbInterval) AND (select aya_to from hizbquarter where id=:endHezbInterval)")
    Single<List<SearchModel>> getSuraJuzHezbSearchResult(String inputSearch, int selectedSura, int selectedJuz, int startHezbInterval, int endHezbInterval);

    @Query("select sura, sura_aya, pure_text,text, page from aya where id IN(:ayaIds)")
    Single<List<MyNoteModel>> getNoteData(List<Integer> ayaIds);

    @Query("select DISTINCT (page), sura from aya ")
    Single<List<PageSuras>> getSuraPage();

    @Query("select page from aya where id=:ayaId")
    Single<Integer> getAyaPage(int ayaId);

    @Query("SELECT * FROM Aya where id=(SELECT MIN(id) FROM Aya WHERE sura=:sura)")
    Aya getFirstAyaInSura(int sura);

    @Query("SELECT * FROM Aya where id=(SELECT MAX(id) FROM Aya WHERE sura=:sura)")
    Aya getLastAyaInSura(int sura);
*/
}

