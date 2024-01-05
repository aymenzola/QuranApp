package com.app.dz.quranapp.quran.QuranSearchParte;

import static com.app.dz.quranapp.quran.QuranSearchParte.ActivitySearchQuran.searchPageSize;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Entities.AyaWithSura;
import com.app.dz.quranapp.room.Daos.AyaDao;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class QuranSearchRepository {

    private final AyaDao ayaDao;
    private final MutableLiveData<List<AyaWithSura>> ayat_SuraList;
    private final MutableLiveData<Integer> ayat_SuraList_size;
    private final CompositeDisposable compositeDisposable;

    public QuranSearchRepository(Application application) {
        MushafDatabase database = MushafDatabase.getInstance(application);
        ayaDao = database.getAyaDao();
        ayat_SuraList = new MutableLiveData<>();
        ayat_SuraList_size = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<AyaWithSura>> getSearchAyatList() {
        return ayat_SuraList;
    }
    public LiveData<Integer> getSearchSize() {
        return ayat_SuraList_size;
    }

    public void searchForAyat(String query, int offset) {
        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(ayaDao.searchAyatWithSura(query, searchPageSize, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ayaWithSuraList -> {
                    setResultSize(query, ayaWithSuraList);
                }, e -> {
                    Log.e("checkdata", "1 data error   " + e.getMessage());
                }));
    }

    public void setResultSize(String query, List<AyaWithSura> ayaWithSuraList) {
        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(ayaDao.searchSize(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(size -> {
                    ayat_SuraList.setValue(ayaWithSuraList);
                    ayat_SuraList_size.setValue(size);
                }, e -> {
                    Log.e("checkdata", "1 data error   " + e.getMessage());
                }));
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
