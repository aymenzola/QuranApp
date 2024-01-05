package com.app.dz.quranapp.LocationParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class TimeViewModel extends AndroidViewModel {

    private MutableLiveData<String> result;

    public TimeViewModel(@NonNull Application application) {
        super(application);
        result = new MutableLiveData<>();
    }

    public LiveData<String> getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result.postValue(result);
    }

}

