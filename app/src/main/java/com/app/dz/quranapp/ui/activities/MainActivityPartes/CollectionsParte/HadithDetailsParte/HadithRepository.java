package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.HadithDetailsParte;

import static com.app.dz.quranapp.data.Api.RetrofitClient.BASE_URL;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.data.Api.Api;
import com.app.dz.quranapp.data.Api.RetrofitClient;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.DatabaseClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HadithRepository {

    private final CompositeDisposable compositeDisposable;
    private final Api api;
    private final MutableLiveData<List<Hadith>> hadithList;
    private final MutableLiveData<List<Hadith>> hadithListForChapter;
    private final BookDao bookdao;

    public HadithRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        bookdao = db.getBookDao();

        api = RetrofitClient.getInstance(BASE_URL).getApi();
        hadithList = new MutableLiveData<>();
        hadithListForChapter = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<Hadith>> getHadithListOfBook() {
        return hadithList;
    }

    public void sethadithObject(String CollectionName, String bookNumber, List<String> chapterIds) {
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.getSpecificHadith(CollectionName,bookNumber,chapterIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithList1 -> {
                    Log.e("checkdata","1 data coming  "+hadithList1.size());
                    hadithList.setValue(hadithList1);
                },e->{
                    Log.e("checkdata","1 data error  1 "+e.getMessage());
                }));
    }

    public void sethadithObject(String CollectionName, String bookNumber) {
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.getSpecificHadith(CollectionName,bookNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithList1 -> {
                    Log.e("checkdata","1 data coming  "+hadithList1.size());
                    hadithList.setValue(hadithList1);
                },e->{
                    Log.e("checkdata","1 data error  1 "+e.getMessage());
                }));
    }

    public void askHadithsListChapter(String CollectionName, String bookNumber, String chapterId) {
        compositeDisposable.add(bookdao.getHadithList(CollectionName,bookNumber,chapterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hadithListForChapter::setValue, e-> Log.e("checkdata","1 data error  1 "+e.getMessage())));
    }

    public MutableLiveData<List<Hadith>> getHadithListForChapter() {
        return hadithListForChapter;
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
