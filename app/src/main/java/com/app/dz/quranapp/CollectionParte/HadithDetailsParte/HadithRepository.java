package com.app.dz.quranapp.CollectionParte.HadithDetailsParte;

import static com.app.dz.quranapp.Api.RetrofitClient.BASE_URL;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Api.Api;
import com.app.dz.quranapp.Api.RetrofitClient;
import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.BookDao;
import com.app.dz.quranapp.room.DatabaseClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HadithRepository {

    private final CompositeDisposable compositeDisposable;
    private final Api api;
    private final MutableLiveData<List<Hadith>> hadithList;
    private final BookDao bookdao;

    public HadithRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        bookdao = db.getBookDao();

        api = RetrofitClient.getInstance(BASE_URL).getApi();
        hadithList = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<Hadith>> getHadithObject() {
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


    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
