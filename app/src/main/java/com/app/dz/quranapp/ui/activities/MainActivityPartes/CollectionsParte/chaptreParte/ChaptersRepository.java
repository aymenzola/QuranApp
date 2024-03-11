package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.chaptreParte;

import static com.app.dz.quranapp.data.Api.RetrofitClient.BASE_URL;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.data.Api.Api;
import com.app.dz.quranapp.data.Api.RetrofitClient;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChaptersRepository {

    private final CompositeDisposable compositeDisposable;
    private final Api api;
    private final MutableLiveData<List<Chapter>> chaptersList;
    private final MutableLiveData<List<BookWithCount>> booksList;
    private final BookDao bookdao;

    public ChaptersRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        bookdao = db.getBookDao();

        api = RetrofitClient.getInstance(BASE_URL).getApi();
        chaptersList = new MutableLiveData<>();
        booksList = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<Chapter>> getChaptersObject() {
        return chaptersList;
    }

    public MutableLiveData<List<BookWithCount>> getBooksList() {
        return booksList;
    }

    public void setChaptersObject(String CollectionName, String bookNumber) {
        Log.e("quran_tag", "we are getting data collection name "+CollectionName+" bookNumber "+bookNumber);
        compositeDisposable.add(bookdao.getHadithsBookGroupedChapterId(CollectionName, bookNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithList -> {
                    Log.e("quran_tag", "1 data coming  " + hadithList.size());

                    List<Chapter> chapterList = new ArrayList<>();

                    for (Hadith hadith : hadithList)
                        chapterList.add(new Chapter(hadith.chapterId, hadith.chapterTitle, hadith.bookNumber,hadith.chapterTitle_no_tachkil));

                    chaptersList.setValue(chapterList);
                }, e -> {
                    Log.e("quran_tag", "1 data error   " + e.getMessage());
                }));
    }

    public void setBooksWithChapters(String collectionName) {
        Log.e("quran_position_tag1", "collectionName "+collectionName);

        Disposable disposable = Observable.zip(
                        bookdao.getBooksWithCollection4(collectionName),
                        bookdao.getChaptersForCollection(collectionName),
                        (bookWithCountList,chapterList) -> {
                            Log.e("quran_position_tag1","1 we receive list "+bookWithCountList.size() + " chapterList "+chapterList.size());

                            for (BookWithCount book : bookWithCountList) {
                                List<Chapter> bookChapters = new ArrayList<>();
                                int position = 0;
                                for (Chapter chapter : chapterList) {
                                    if (chapter.bookNumber.equals(book.bookNumber)) {
                                        chapter.positionInChaptersList = position++;
                                        bookChapters.add(chapter);
                                    }
                                }
                                book.chaptersList = bookChapters;
                            }
                            return bookWithCountList;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookWithCountList -> {
                    Log.e("quran_tag","we receive list "+bookWithCountList.size());
                    booksList.postValue(bookWithCountList);
                    // Handle the result
                }, e -> {
                    Log.e("quran_tag","error "+e.getMessage());
                    // Handle the error
                });

        compositeDisposable.add(disposable);


    }













    private void getChaptersAndMergeTheme(String CollectionName, List<BookWithCount> bookWithCountList) {
        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(bookdao.getChaptersWithCollectionName(CollectionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithList -> {
                    Log.e("checkdata", "1 data coming  " + hadithList.size());

                    List<Chapter> chapterList = new ArrayList<>();
                    for (Hadith hadith : hadithList)
                        chapterList.add(new Chapter(hadith.chapterId, hadith.chapterTitle, hadith.bookNumber,hadith.chapterTitle_no_tachkil));

                    mergeBookAndChapter(bookWithCountList, chapterList);
                }, e -> {
                    Log.e("checkdata", "1 data error   " + e.getMessage());
                }));
    }

    private void mergeBookAndChapter(List<BookWithCount> bookList, List<Chapter> chapterList) {

        for (BookWithCount book : bookList) {
            List<Chapter> bookChapters = new ArrayList<>();
            for (Chapter chapter : chapterList) {
                if (chapter.bookNumber.equals(book.bookNumber)) {
                    bookChapters.add(chapter);
                }
            }
            book.chaptersList = bookChapters;
        }
        booksList.postValue(bookList);
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }

}
