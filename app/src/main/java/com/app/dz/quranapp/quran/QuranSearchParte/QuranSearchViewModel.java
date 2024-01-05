package com.app.dz.quranapp.quran.QuranSearchParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.Entities.AyaWithSura;

import java.util.List;


public class QuranSearchViewModel extends AndroidViewModel {

   private final QuranSearchRepository repository;

   public QuranSearchViewModel(@NonNull Application application) {
      super(application);
      repository = new QuranSearchRepository(application);
   }


   public LiveData<List<AyaWithSura>> getSearchAyat() {
      return repository.getSearchAyatList();
   }
   public LiveData<Integer> getSearchSize() {
      return repository.getSearchSize();
   }

   public void searchInaAyat(String query, int offset) {
      repository.searchForAyat(query,offset);
   }

   @Override
   protected void onCleared() {
      super.onCleared();
      repository.clearDesposite();
   }

}

