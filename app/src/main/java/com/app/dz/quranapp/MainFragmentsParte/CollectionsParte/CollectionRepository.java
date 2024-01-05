package com.app.dz.quranapp.MainFragmentsParte.CollectionsParte;

import static com.app.dz.quranapp.Api.RetrofitClient.BASE_URL;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Api.Api;
import com.app.dz.quranapp.Api.RetrofitClient;
import com.app.dz.quranapp.Entities.BookAvailable;
import com.app.dz.quranapp.Entities.Chapter;
import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarCategoryModel;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarModel;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.AdkarDao;
import com.app.dz.quranapp.room.Daos.BookDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CollectionRepository {


    private final AdkarDao dao;
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<List<AdkarCategoryModel>> categoriesList;
    private final MutableLiveData<List<AdkarModel>> adkarList;
    private final MutableLiveData<List<String>> booksList;

    private final BookDao bookdao;
    private final MutableLiveData<List<Chapter>> chaptersList;

    public CollectionRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        MushafDatabase database = MushafDatabase.getInstance(application);
        bookdao = db.getBookDao();
        booksList = new MutableLiveData<>();

        categoriesList = new MutableLiveData<>();
        adkarList = new MutableLiveData<>();

        dao = database.getAdkarDao();
        chaptersList = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<String>> getBooksList() {
        return booksList;
    }

    public void setBookAvailable() {
        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(bookdao.getAvailableBooks().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringList -> {
                    for (String s : stringList) {
                        Log.e("checkdata", "book " + s + " Available ");
                    }
                    booksList.postValue(stringList);
                }, e ->
                {
                    booksList.postValue(new ArrayList<>());
                    Log.e("checkdata", "Available books error " + e.getMessage());
                }));
    }


    public LiveData<List<AdkarCategoryModel>> getCategories() {
        return categoriesList;
    }

    public LiveData<List<AdkarModel>> getAdkarCategory() {
        return adkarList;
    }

    public void setAllCategories() {
        compositeDisposable.add(dao.getAdkar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoriesList::setValue, e -> {
                    Log.e("checkdata", "adkar data error   " + e.getMessage());
                }));
    }

    public void setAdkarByCategory(String categoryName) {
        compositeDisposable.add(dao.getCategoryAdkar(categoryName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adkarList::setValue, e -> {
                    Log.e("checkdata", "adkar data error   " + e.getMessage());
                }));
    }

    public LiveData<List<Chapter>> getChaptersObject() {
        return chaptersList;
    }

    public void setChaptersObject(String CollectionName) {
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.getHisnAdkar(CollectionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithList -> {
                    Log.e("checkdata","1 data coming  "+hadithList.size());

                    List<Chapter> chapterList = new ArrayList<>();

                    for (Hadith hadith:hadithList) chapterList.add(new Chapter(hadith.chapterId,hadith.chapterTitle,hadith.bookNumber));

                    chaptersList.setValue(chapterList);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }


    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
