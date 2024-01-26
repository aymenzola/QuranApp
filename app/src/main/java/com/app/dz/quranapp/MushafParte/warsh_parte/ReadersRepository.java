package com.app.dz.quranapp.MushafParte.warsh_parte;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.riwayat.CsvReader;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ReadersRepository {

    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<List<ReaderAudio>> readersList;
    private final MutableLiveData<ReaderAudio> selectedReader;


    Application application1;

    public ReadersRepository(Application application) {
        application1 = application;
        MushafDatabase database = MushafDatabase.getInstance(application);
        readersList = new MutableLiveData<>();
        selectedReader = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<ReaderAudio>> getReaderAudioList() {
        return readersList;
    }

    public LiveData<ReaderAudio> getReaderAudio() {
        return selectedReader;
    }

    public void setReaderAudioList() {
        readersList.setValue(CsvReader.readReaderAudioListFromCsv(application1.getApplicationContext(), "audio.csv"));
    }

    public void setReaderAudio(int readerId) {
        List<ReaderAudio> list = CsvReader.readReaderAudioListFromCsv(application1.getApplicationContext(), "audio.csv");
        for (ReaderAudio readerAudio : list) {
            if (readerAudio.getId() == readerId) {
                selectedReader.setValue(readerAudio);
                break;
            }
        }
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
