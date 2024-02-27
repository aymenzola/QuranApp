package com.app.dz.quranapp.ui.activities.AdkarParte.AdkarDetailsParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarRepository;

import java.util.List;


public class AdkarDetailsViewModel extends AndroidViewModel {

    private final AdkarRepository repository;

    public AdkarDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = new AdkarRepository(application);
    }

    public LiveData<List<Hadith>> getAdkarModel() {
        return repository.getHadithList();
    }

    public void setAdkarByCategory(String categoryName,String chapterTitle) {
        repository.sethadithListWithChaperName(categoryName,chapterTitle);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

