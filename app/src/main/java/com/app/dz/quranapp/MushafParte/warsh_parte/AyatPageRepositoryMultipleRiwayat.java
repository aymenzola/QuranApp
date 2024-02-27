package com.app.dz.quranapp.MushafParte.warsh_parte;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaWarsh;
import com.app.dz.quranapp.data.room.Daos.AyaWarshDao;
import com.app.dz.quranapp.data.room.MushafDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AyatPageRepositoryMultipleRiwayat {

    private final AyaWarshDao dao;
    private final MutableLiveData<List<Aya>> ayatList;
    private final MutableLiveData<Aya> PrevAyatList;
    private final List<Aya> ayatListlocal = new ArrayList<>();
    private final CompositeDisposable compositeDisposable;

    public AyatPageRepositoryMultipleRiwayat(Application application) {
        MushafDatabase database = MushafDatabase.getInstance(application);
        dao = database.getAyaDaoWarsh();
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
                .subscribe(ayatWarchList -> {
                    Log.e("checkdata","1 data coming  "+ayatWarchList.size());

                    List<Aya> ayaList = new ArrayList<>();

                    for (AyaWarsh ayaWarsh:ayatWarchList) {
                        ayaList.add(new Aya(ayaWarsh.getId(),
                                ayaWarsh.getSura_no(),
                                ayaWarsh.getAya_no(),
                                ayaWarsh.getAya_text(),
                                ayaWarsh.getPage(),
                                ayaWarsh.getJozz(),
                                "no"));
                    }

                    ayatList.setValue(ayaList);
                    },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }

    public void setLastAyaInPage(int page_number) {

        Log.e("checkdata","we are getting data");
        compositeDisposable.add(dao.getLastAyaInPage(page_number-1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ayaWarsh -> {
                    Log.e("checkdata","1 data coming  "+ayaWarsh);

                        Aya aya = new Aya(ayaWarsh.getId(),
                                ayaWarsh.getSura_no(),
                                ayaWarsh.getAya_no(),
                                ayaWarsh.getAya_text(),
                                ayaWarsh.getPage(),
                                ayaWarsh.getJozz(),
                                "no");


                    PrevAyatList.setValue(aya);
                    },e->{
                    Log.e("checkdata","1 data error   "+e.getMessage());
                }));
    }


    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
