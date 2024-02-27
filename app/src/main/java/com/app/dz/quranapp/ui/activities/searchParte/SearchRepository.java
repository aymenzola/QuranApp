package com.app.dz.quranapp.ui.activities.searchParte;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.DatabaseClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchRepository {

    private final BookDao bookdao;
    private final MutableLiveData<String> bookName;
    private final MutableLiveData<Integer> hadtithRank;
    private final MutableLiveData<List<Hadith>> hadithList;
    private final MutableLiveData<List<Hadith>> hadithChapterList;
    private final MutableLiveData<List<Book>> booksList;
    private final CompositeDisposable compositeDisposable;

    public SearchRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        bookdao = db.getBookDao();

        hadtithRank = new MutableLiveData<>();
        bookName = new MutableLiveData<>();
        hadithList = new MutableLiveData<>();
        hadithChapterList = new MutableLiveData<>();
        booksList = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<String> getBookName() {
        return bookName;
    }
    public LiveData<Integer> getHadithRank() {
        return hadtithRank;
    }
    public LiveData<List<Hadith>> getSearchInHadith() {
        return hadithList;
    }
    public LiveData<List<Hadith>> getSearchInHadithChapter() {
        return hadithChapterList;
    }
    public LiveData<List<Book>> getSearchInBooks() {
        return booksList;
    }


    public void searchInHadith(String query, int offset){
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.searchInHadith(query, SearchActivity.searchPageSize,offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithList1 -> {
                    Log.e("checkdata","1 data coming  "+hadithList1.size());
                    hadithList.setValue(hadithList1);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }

    public void searchInBooks(String query, int offset){
        Log.e("checkdata","we are getting data query"+query);
        compositeDisposable.add(bookdao.searchInBooks(query, SearchActivity.searchPageSize,offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookList1 -> {
                    Log.e("checkdata","1 data coming  "+bookList1.size());
                    booksList.setValue(bookList1);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }

    public void setBookName(String bookNumber,String collectionName){
        Log.e("checkdata","we are getting data query"+bookNumber);
        compositeDisposable.add(bookdao.getBookName(bookNumber,collectionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookNamee -> {
                    Log.e("checkdata","1 data coming  "+bookNamee);
                    bookName.setValue(bookNamee);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }

    public void searchInChapter(String query, int offset){
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.searchInChapter(query, SearchActivity.searchPageSize,offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithList2 -> {
                    Log.e("checkdata","1 data coming  "+hadithList2.size());
                    hadithChapterList.setValue(hadithList2);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }


    public void clearDesposite() {
        compositeDisposable.clear();
    }

    public void setHadithRank(String bookNUmber,String collectionName,Integer hadithId) {
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.getHadithRank(bookNUmber,collectionName,hadithId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rank -> {
                    Log.e("checkdata","1 data coming  rank "+rank);
                    hadtithRank.setValue(rank);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));

    }
}
