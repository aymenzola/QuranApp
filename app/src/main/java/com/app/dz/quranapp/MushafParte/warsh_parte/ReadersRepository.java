package com.app.dz.quranapp.MushafParte.warsh_parte;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarCategoryModel;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarModel;
import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.AdkarDao;
import com.app.dz.quranapp.room.Daos.BookDao;
import com.app.dz.quranapp.room.Daos.ReaderAudioDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ReadersRepository {

    private final ReaderAudioDao dao;
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<List<ReaderAudio>> readersList;
    private final MutableLiveData<ReaderAudio> selectedReader;


    public ReadersRepository(Application application) {
        MushafDatabase database = MushafDatabase.getInstance(application);
        readersList = new MutableLiveData<>();
        selectedReader = new MutableLiveData<>();

        dao = database.getReaderAudioDao();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<ReaderAudio>> getReaderAudioList() {
        return readersList;
    }
    public LiveData<ReaderAudio> getReaderAudio() {
        return selectedReader;
    }

    public void setReaderAudioList() {
        compositeDisposable.add(dao.getAvailableReaders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(readersList::setValue, e->{
                    Log.e("checkdata","audios data error   "+e.getMessage());
                }));
    }

    public void setReaderAudio(int readerId) {
        compositeDisposable.add(dao.getReaderWithId(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(selectedReader::setValue, e->{
                    Log.e("checkdata","audioes data error   "+e.getMessage());
                }));
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
