package com.app.dz.quranapp.data.room.Daos;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.Hadith;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface BookDao {

    @Query("SELECT * FROM books LIMIT 5")
    Observable<List<Book>> getAll();



 @Query("SELECT books.id,books.bookNumber,books.bookCollection,books.bookName,COUNT(DISTINCT hadithstable.chapterId) AS chaptersCount FROM books LEFT JOIN hadithstable ON books.bookNumber = hadithstable.bookNumber WHERE bookCollection =:collectionName GROUP BY books.bookNumber")
 Observable<List<Book>> getBooksWithCollection2(String collectionName);

    @Query("SELECT * FROM books where bookCollection=:collectionName")
    Observable<List<Book>> getBooksWithCollection(String collectionName);



    @Query("SELECT books.*, COUNT(hadithstable.id) as chaptersCount " +
            "FROM books " +
            "LEFT JOIN hadithstable ON books.bookNumber = hadithstable.bookNumber " +
            "WHERE books.bookCollection = :collectionName " +
            "GROUP BY books.id, hadithstable.chapterId")
    Observable<List<BookWithCount>> getBooksWithCollection3(String collectionName);



    @Query("SELECT b.*, COUNT(h.bookNumber) as chaptersCount " +
            "FROM books b " +
            "LEFT JOIN hadithstable h ON b.bookNumber = h.bookNumber AND b.bookCollection = h.collection " +
            "WHERE b.bookCollection = :collectionName " +
            "GROUP BY b.bookNumber " +
            "ORDER BY b.id ASC")
    Observable<List<BookWithCount>> getBooksWithCollection4(String collectionName);


    @Query("SELECT * FROM hadithstable where collection=:collectionName and bookNumber=:bookNumber")
    Observable<List<Hadith>> getCollectionWithBook(String collectionName,String bookNumber);

    @Query("SELECT * FROM hadithstable where collection=:collectionName and bookNumber=:bookNumber GROUP BY chapterId ORDER BY chapterId ASC")
    Observable<List<Hadith>> getHadithsBookGroupedChapterId(String collectionName,String bookNumber);

    @Query("SELECT chapterTitle FROM hadithstable where collection=:collectionName and bookNumber=:bookNumber GROUP BY chapterId ORDER BY chapterId ASC LIMIT 1")
    String getFirstChapterInBook(String collectionName,String bookNumber);


    @Query("SELECT * FROM hadithstable where collection=:collectionName")
    Observable<List<Hadith>> getChaptersWithCollectionName(String collectionName);

    @Query("SELECT * FROM hadithstable where collection=:collectionName GROUP BY chapterTitle")
    Observable<List<Hadith>> getHisnAdkar(String collectionName);


    //get book name
   @Query("SELECT bookName FROM books where bookNumber =:booknumber and bookCollection =:collection")
   Observable<String> getBookName(String booknumber,String collection);

    @Query("SELECT COUNT(*) AS position FROM hadithstable WHERE bookNumber = :bookNumber AND collection =:collectionName AND id < :hadith_id")
    Observable<Integer> getHadithRank(String bookNumber,String collectionName ,int hadith_id);


   //Search in hadith

    @Query("SELECT * FROM hadithstable where body_no_tachkil LIKE '%' || :query || '%' LIMIT :searchPageSize OFFSET :offset")
     Observable<List<Hadith>> searchInHadith(String query, int searchPageSize, int offset);

    //search in books
    @Query("SELECT * FROM books  where bookName LIKE '%' || :query || '%' LIMIT :searchPageSize OFFSET :offset")
    Observable<List<Book>> searchInBooks(String query, int searchPageSize, int offset);

    //search in chapters
    @Query("SELECT * FROM hadithstable  where chapterTitle_no_tachkil LIKE '%' || :query || '%' GROUP BY chapterTitle_no_tachkil LIMIT :searchPageSize  OFFSET :offset")
    Observable<List<Hadith>> searchInChapter(String query, int searchPageSize, int offset);


    @Query("SELECT * FROM hadithstable where collection=:collectionName and bookNumber=:bookNumber and chapterId IN (:chapterIds) ORDER BY chapterId ASC")
    Observable<List<Hadith>> getSpecificHadith(String collectionName, String bookNumber, List<String> chapterIds);

    @Query("SELECT * FROM hadithstable where collection=:collectionName and bookNumber=:bookNumber")
    Observable<List<Hadith>> getSpecificHadith(String collectionName, String bookNumber);

    @Query("SELECT * FROM hadithstable where collection=:collectionName and bookNumber=:bookNumber and chapterId=:chapterId ORDER BY chapterId ASC")
    Observable<List<Hadith>> getHadithList(String collectionName, String bookNumber,String chapterId);

    @Query("SELECT * FROM hadithstable where collection=:collectionName and chapterTitle=:chapterName")
    Observable<List<Hadith>> getHadithWithChapterName(String collectionName, String chapterName);


    @Query("SELECT * FROM hadithstable")
    List<Hadith> getAllHadiths();

    @Query("SELECT * FROM hadithstable where id=:id")
    Hadith getHadithWithid(int id);

    @Insert(onConflict = IGNORE)
    void insertHadith(Hadith hadith);

    @Insert(onConflict = IGNORE)
    void insertHadithList(List<Hadith> hadithList);

    @Insert(onConflict = IGNORE)
    void insertBookList(List<Book> bookList);

    @Insert()
    void insertBook(Book book);

    @Query("SELECT COUNT(*) from hadithstable")
    int countHadiths();

    @Query("SELECT COUNT(*) from hadithstable where collection=:collectionName")
    int countCollectionHadiths(String collectionName);


    @Query("SELECT COUNT(*) from books where bookCollection=:collectionName")
    Observable<Integer> countCollectionHadithsObserve(String collectionName);

    @Query("SELECT distinct bookCollection from books")
    Observable<List<String>> getAvailableBooks();


    @Query("SELECT COUNT(*) from books where bookCollection=:collectionName")
    int countCollectionBooks(String collectionName);

    @Query("SELECT COUNT(*) from books where bookCollection =:collectionName")
    int countBooksTable(String collectionName);



    @Query("SELECT * FROM hadithstable WHERE collection = :collectionName  GROUP BY chapterTitle ORDER BY chapterId ASC")
    Observable<List<Chapter>> getChaptersForCollection(String collectionName);

    @Query("UPDATE hadithstable SET chapterTitle = 'باب ' || chapterId || ' كتاب ' || bookNumber WHERE chapterTitle = 'باب'")
    void updateChapterTitle();

}

