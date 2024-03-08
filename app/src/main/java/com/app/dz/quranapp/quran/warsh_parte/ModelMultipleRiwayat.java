package com.app.dz.quranapp.quran.warsh_parte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.Aya;

import java.util.List;


public class ModelMultipleRiwayat extends AndroidViewModel {

    private final RepositoryMultipleRiwayat repository;

    public ModelMultipleRiwayat(@NonNull Application application) {
        super(application);
        repository = new RepositoryMultipleRiwayat(application);
    }

    public LiveData<List<Aya>> getAllAyat() {
        return repository.getAllAyat();
    }
    public LiveData<Aya> getPevAya() {
        return repository.getPrevAya();
    }

    public void setAyatList(int page_number) {
        repository.setAyatList(page_number);
    }
    public void setlastAya(int page_number) {
        repository.setLastAyaInPage(page_number);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

