package com.app.dz.quranapp.MushafParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.MushafParte.hafs_parte.AyatPageRepository;
import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.MushafParte.warsh_parte.ReadersRepository;
import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment.SuraRepository;

import java.util.List;

public class MyViewModel extends AndroidViewModel {
    private final ReadersRepository readersRepository;
    private final SuraRepository repository;
    private final AyatPageRepository repositoryAyat;

    private final MutableLiveData<Boolean> isFullModeActivated = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFragmentClicked = new MutableLiveData<>();
    private MutableLiveData<Boolean> isOnBackClicked = new MutableLiveData<>();
    private MutableLiveData<String> value = new MutableLiveData<>();


    public MyViewModel(@NonNull Application application) {
        super(application);
        readersRepository = new ReadersRepository(application);
        repository = new SuraRepository(application);
        repositoryAyat = new AyatPageRepository(application);
    }

    public LiveData<List<Aya>> getPageAyatList() {
        return repositoryAyat.getAllAyat();
    }
    public void askPageAyaList(int page_number) {
        repositoryAyat.setAyatList(page_number);
    }

    public void setData(boolean isfullModeActive) {
        isFullModeActivated.setValue(isfullModeActive);
    }

    public void setReaderWithId(int readerId) {
        readersRepository.setReaderAudio(readerId);
    }

    public LiveData<List<Sura>> getAllSura() {
        return repository.getAllSura();
    }

    public void setAllSuraList() {
        repository.setAllSuraList();
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

    public MutableLiveData<Boolean> getIsFragmentClicked() {
        return isFragmentClicked;
    }

    public void setIsFragmentClicked(boolean isFragmentClicked) {
        this.isFragmentClicked.postValue(isFragmentClicked);
    }

    public MutableLiveData<Boolean> getIsOnBackClicked() {
        return isOnBackClicked;
    }

    public void setIsOnBackClicked(boolean isOnBackClicked) {
        this.isOnBackClicked.postValue(isOnBackClicked);
    }

    public MutableLiveData<String> getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value.postValue(value);
    }
}