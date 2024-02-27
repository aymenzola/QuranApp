package com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarModel;

import java.util.List;


public class AdkarViewModel extends AndroidViewModel {

    private final HomeRepository repository;

    public AdkarViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeRepository(application);
    }

    public LiveData<List<AdkarModel>> getFastDikr() {
        return repository.getFastAdkarListMutableLiveData();
    }

    public void setAdkarList() {
        repository.setAdkarList();
    }

    public LiveData<List<AdkarModel>> getAdkarList() {
        return repository.getAdkarListMutableLiveData();
    }

    public void setFastDick() {
        repository.setFastDikr();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

    public LiveData<List<AdkarModel>> getAdkarListByCategory() {
        return repository.getAdkarListByCategoryML();
    }

    public void setAdkarListByCategory(Integer categoryId) {
        repository.setAdkarListByCategory(categoryId);
    }



    public LiveData<Boolean> getDikrUpdateResult() {
        return repository.getDikrSaveResultML();
    }

    public void updateDikrSaveState(Integer dikrId,Integer isSaved) {
        repository.updateDikrSaveState(dikrId,isSaved);
    }
}

