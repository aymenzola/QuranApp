package com.app.dz.quranapp.MushafParte.warsh_parte;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.Util.CsvReader;
import com.app.dz.quranapp.data.room.MushafDatabase;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

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
        Log.e("trak_page", "we ask for reader  id "+readerId);
        List<ReaderAudio> list = CsvReader.readReaderAudioListFromCsv(application1.getApplicationContext(), "audio.csv");
        for (ReaderAudio readerAudio : list) {
            if (readerAudio.getId() == readerId) {
                Log.e("trak_page", "we ask for reader  id "+readerId);
                selectedReader.setValue(readerAudio);
                break;
            } else {
                Log.e("trak_page", "no equal id "+readerId+"   list reader id "+readerAudio.getId());
            }
        }
        Log.e("trak_page", "reader loop finished");
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
