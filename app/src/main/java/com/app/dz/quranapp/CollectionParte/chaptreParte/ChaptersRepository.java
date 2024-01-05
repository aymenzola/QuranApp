package com.app.dz.quranapp.CollectionParte.chaptreParte;

import static com.app.dz.quranapp.Api.RetrofitClient.BASE_URL;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Api.Api;
import com.app.dz.quranapp.Api.RetrofitClient;
import com.app.dz.quranapp.Entities.Chapter;
import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.BookDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ChaptersRepository {

    private final CompositeDisposable compositeDisposable;
    private final Api api;
    private final MutableLiveData<List<Chapter>> chaptersList;
    private final BookDao bookdao;

    public ChaptersRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        bookdao = db.getBookDao();

        api = RetrofitClient.getInstance(BASE_URL).getApi();
        chaptersList = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<Chapter>> getChaptersObject() {
        return chaptersList;
    }

    public void setChaptersObject(String CollectionName, String bookNumber) {
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.getCollectionWithBook(CollectionName,bookNumber)
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
