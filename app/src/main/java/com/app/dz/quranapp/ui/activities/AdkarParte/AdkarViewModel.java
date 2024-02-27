package com.app.dz.quranapp.ui.activities.AdkarParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


public class AdkarViewModel extends AndroidViewModel {

    private final AdkarRepository repository;

    public AdkarViewModel(@NonNull Application application) {
        super(application);
        repository = new AdkarRepository(application);
    }

    public LiveData<List<AdkarCategoryModel>> getCategories() {
        return repository.getCategories();
    }

    public void setAdkarModel() {
        repository.setAllCategories();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

