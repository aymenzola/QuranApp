package com.app.dz.quranapp.MushafParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarRepository;
import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.MushafParte.warsh_parte.ReadersRepository;

import java.util.List;

public class MyViewModel extends AndroidViewModel {
    private final ReadersRepository readersRepository;

    private MutableLiveData<Boolean> isFullModeActivated = new MutableLiveData<>();
    private MutableLiveData<String> value = new MutableLiveData<>();


    public MyViewModel(@NonNull Application application) {
        super(application);
        readersRepository = new ReadersRepository(application);
    }

    public void setData(boolean isfullModeActive) {
        isFullModeActivated.setValue(isfullModeActive);
    }

    public void setReaderWithId(int readerId) {
        readersRepository.setReaderAudio(readerId);
    }

    public void setReadersList() {
        readersRepository.setReaderAudioList();
    }

    public LiveData<Boolean> getData() {
        return isFullModeActivated;
    }

    public LiveData<ReaderAudio> getReader() {
        return readersRepository.getReaderAudio();
    }

    public LiveData<List<ReaderAudio>> getReadersList() {
        return readersRepository.getReaderAudioList();
    }


    public MutableLiveData<String> getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value.postValue(value);
    }
}