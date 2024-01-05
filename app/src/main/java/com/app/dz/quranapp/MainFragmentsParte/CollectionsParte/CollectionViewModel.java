package com.app.dz.quranapp.MainFragmentsParte.CollectionsParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.Entities.BookAvailable;
import com.app.dz.quranapp.Entities.Chapter;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarCategoryModel;

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

    public LiveData<List<Chapter>> getchaptersObject() {
        return repository.getChaptersObject();
    }

    public void setChaptersObject(String collectionName) {
        repository.setChaptersObject(collectionName);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

