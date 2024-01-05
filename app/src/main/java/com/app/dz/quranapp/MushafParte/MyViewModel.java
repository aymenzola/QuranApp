package com.app.dz.quranapp.MushafParte;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData<Boolean> isFullModeActivated = new MutableLiveData<>();
    private MutableLiveData<String> value = new MutableLiveData<>();

    public void setData(boolean isfullModeActive) {
        isFullModeActivated.setValue(isfullModeActive);
    }

    public LiveData<Boolean> getData() {
        return isFullModeActivated;
    }


    public MutableLiveData<String> getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value.postValue(value);
    }
}