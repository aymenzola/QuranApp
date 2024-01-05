package com.app.dz.quranapp.MainFragmentsParte.AdkarParte;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.AdkarDao;
import com.app.dz.quranapp.room.Daos.BookDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AdkarRepository {

    private final AdkarDao dao;
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<List<AdkarCategoryModel>> categoriesList;
    private final MutableLiveData<List<AdkarModel>> adkarList;
    private final MutableLiveData<List<Hadith>> hadithList;
    private final BookDao bookdao;


    public AdkarRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        bookdao = db.getBookDao();

        MushafDatabase database = MushafDatabase.getInstance(application);
        categoriesList = new MutableLiveData<>();
        adkarList = new MutableLiveData<>();
        hadithList = new MutableLiveData<>();

        dao = database.getAdkarDao();
        compositeDisposable = new CompositeDisposable();
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
                .subscribe(categoriesList::setValue, e->{
                    Log.e("checkdata","adkar data error   "+e.getMessage());
                }));
    }

    public void setAdkarByCategory(String categoryName) {
        compositeDisposable.add(dao.getCategoryAdkar(categoryName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adkarList::setValue, e->{
                    Log.e("checkdata","adkar data error   "+e.getMessage());
                }));
    }


    public LiveData<List<Hadith>> getHadithList() {
        return hadithList;
    }

    public void sethadithListWithChaperName(String CollectionName,String chapterTitle) {
        Log.e("checkdata","we are getting data");
        compositeDisposable.add(bookdao.getHadithWithChapterName(CollectionName,chapterTitle)
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
