package com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.Chapter;

import java.util.List;


public class ChaptersViewModel extends AndroidViewModel {

    private final ChaptersRepository repository;

    public ChaptersViewModel(@NonNull Application application) {
        super(application);
        repository = new ChaptersRepository(application);
    }

    public LiveData<List<Chapter>> getchaptersObject() {
        return repository.getChaptersObject();
    }

    public void setChaptersObject(String collectionName, String bookNumber) {
        repository.setChaptersObject(collectionName,bookNumber);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

