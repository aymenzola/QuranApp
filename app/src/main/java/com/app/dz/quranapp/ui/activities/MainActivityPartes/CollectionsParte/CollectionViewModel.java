package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


public class CollectionViewModel extends AndroidViewModel {

    private final CollectionRepository repository;

    public CollectionViewModel(@NonNull Application application) {
        super(application);
        repository = new CollectionRepository(application);
    }

    public LiveData<List<String>> getBooksList() {
        return repository.getBooksList();
    }

    public void setBooksList() {
        repository.setBookAvailable();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

