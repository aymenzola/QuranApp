package com.app.dz.quranapp.CollectionParte.HadithDetailsParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.Entities.Hadith;

import java.util.List;


public class HadithViewModel extends AndroidViewModel {

    private HadithRepository repository;

    public HadithViewModel(@NonNull Application application) {
        super(application);
        repository = new HadithRepository(application);
    }

    public LiveData<List<Hadith>> getHadithObject() {
        return repository.getHadithObject();
    }

    public void setHadith(String collectionName, String bookNumber, List<String> chapterIds) {
        repository.sethadithObject(collectionName,bookNumber,chapterIds);
    }
    public void setHadith(String collectionName, String bookNumber) {
        repository.sethadithObject(collectionName,bookNumber);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

