package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.BooksParte;

import static com.app.dz.quranapp.data.Api.RetrofitClient.BASE_URL;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.data.Api.Api;
import com.app.dz.quranapp.data.Api.RetrofitClient;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.Daos.SuraDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.data.room.MushafDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BooksRepository {

    private final SuraDao dao;
    private final CompositeDisposable compositeDisposable;
    private final Api api;
    private final MutableLiveData<List<BookWithCount>> booksList;
    private final MutableLiveData<Object> booksListObject;
    private final MutableLiveData<Object> hadithObject;
    private final BookDao bookdao;

    public BooksRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        bookdao = db.getBookDao();
        MushafDatabase database = MushafDatabase.getInstance(application);
        api = RetrofitClient.getInstance(BASE_URL).getApi();
        booksList = new MutableLiveData<>();
        hadithObject = new MutableLiveData<>();
        booksListObject = new MutableLiveData<>();

        dao = database.getSuraDao();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<BookWithCount>> getBooks() {
        return booksList;
    }
    public LiveData<Object> getBooksObject() {
        return booksListObject;
    }

    public void setBooks(String CollectionName) {
        Log.e("checkdata", "we are getting data "+CollectionName);
        compositeDisposable.add(bookdao.getBooksWithCollection4(CollectionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookList -> {
                    for (BookWithCount book:bookList) {
                        Log.e("checkdata", "size "+bookList.size() +" books data coming  " + book.bookName+" "+book.chaptersCount);
                    }
                    booksList.setValue(bookList);
                }, e -> {
                    Log.e("checkdata", "1 data error   " + e.getMessage());
                }));
    }

    public void setBooksFromApi(String CollectionName) {
        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(api.getBooks(CollectionName, 500)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookList -> {
                  Log.e("checkdata", "1 data coming");
                    booksListObject.setValue(bookList);
                }, e -> {
                    Log.e("checkdata", "1 data error   " + e.getMessage());
                }));
    }

    public LiveData<Object> getHadithObject() {
        return hadithObject;
    }

    public void sethadithObject(String CollectionName, String bookNumber) {
        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(api.getHadithsOfBook(CollectionName, bookNumber, 200)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadith -> {
                    Log.e("checkdata", "1 data coming  " + hadith);
                    hadithObject.setValue(hadith);
                }, e -> {
                    Log.e("checkdata", "1 data error   " + e.getMessage());
                }));
    }


    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
