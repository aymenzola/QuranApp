package com.app.dz.quranapp.ui.activities.mahfodat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarModel;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.CollectionRepository;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment.HomeRepository;

import java.util.List;


public class MahfodatViewModel extends AndroidViewModel {

    private final CollectionRepository repository;
    private final HomeRepository repositoryAdkar;


    public MahfodatViewModel(@NonNull Application application) {
        super(application);
        repository = new CollectionRepository(application);
        repositoryAdkar = new HomeRepository(application);
    }

    public LiveData<List<String>> getBooksList() {
        return repository.getBooksList();
    }

    public void setBooksList() {
        repository.setBookAvailable();
    }


    public LiveData<List<AdkarModel>> getSavedAdkarList() {
        return repositoryAdkar.getSavedAdkarListML();
    }

    public void askForSavedAdkar() {
        repositoryAdkar.askForSavedAdkar();
    }

    public void updateDikrSaveState(Integer dikrId,Integer isSaved) {
        repositoryAdkar.updateDikrSaveState(dikrId,isSaved);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

