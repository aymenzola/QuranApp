package com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.Juz;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.data.room.Daos.SuraDao;
import com.app.dz.quranapp.data.room.MushafDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SuraRepository {

    private final SuraDao dao;
    private final MutableLiveData<List<Juz>> juzaList;
    private final MutableLiveData<List<Sura>> suraList;
    private final MutableLiveData<List<Book>> BooksList;
    private final CompositeDisposable compositeDisposable;

    public SuraRepository(Application application) {
        MushafDatabase database = MushafDatabase.getInstance(application);
        dao = database.getSuraDao();
        juzaList = new MutableLiveData<>();
        suraList = new MutableLiveData<>();
        BooksList = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<Sura>> getAllSura() {
        return suraList;
    }
    public LiveData<List<Juz>> getAllJuza() {
        return juzaList;
    }


    public void setSuraList(int startId, int pageSize){
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(dao.getSuraList(startId,pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suraList1 -> {
                    Log.e("checkdata","1 data coming  "+suraList1.size());
                    suraList.setValue(suraList1);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }

    public void setAllSuraList(){
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suraList1 -> {
                    Log.e("checkdata","1 data coming  "+suraList1.size());
                    suraList.setValue(suraList1);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }

    public void setJuzaList(){
        Log.e("checkdata","we are getting data juza ");
        compositeDisposable.add(dao.getAllJuz()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suraList1 -> {
                    Log.e("checkdata","juz data coming  "+suraList1.size());
                    juzaList.setValue(suraList1);
                },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }


    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
