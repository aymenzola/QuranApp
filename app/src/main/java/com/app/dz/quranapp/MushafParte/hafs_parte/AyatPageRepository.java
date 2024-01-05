package com.app.dz.quranapp.MushafParte.hafs_parte;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.room.Daos.AyaDao;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AyatPageRepository {

    private final AyaDao dao;
    private final MutableLiveData<List<Aya>> ayatList;
    private final MutableLiveData<Aya> PrevAyatList;
    private final List<Aya> ayatListlocal = new ArrayList<>();
    private final CompositeDisposable compositeDisposable;

    public AyatPageRepository(Application application) {
        MushafDatabase database = MushafDatabase.getInstance(application);
        dao = database.getAyaDao();
        ayatList = new MutableLiveData<>();
        PrevAyatList = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<Aya>> getAllAyat() {
        return ayatList;
    }
    public LiveData<Aya> getPrevAya() {
        return PrevAyatList;
    }

    public void setAyatList(int page_number) {
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(dao.getAllInPage(page_number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suraList1 -> {
                    Log.e("checkdata","1 data coming  "+suraList1.size()+" "+suraList1.get(0).getText());
                    //ayatListlocal.addAll(suraList1);
                    ayatList.setValue(suraList1);
                    },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }

    public void setLastAyaInPage(int page_number) {

        Log.e("checkdata","we are getting data");
        compositeDisposable.add(dao.getLastAyaInPage(page_number-1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suraList1 -> {
                    Log.e("checkdata","1 data coming  "+suraList1.size()+" "+suraList1.get(0).getText());
                    PrevAyatList.setValue(suraList1.get(0));
                    },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }


    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
