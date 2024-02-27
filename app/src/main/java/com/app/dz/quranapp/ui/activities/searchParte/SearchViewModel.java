package com.app.dz.quranapp.ui.activities.searchParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.Hadith;

import java.util.List;


public class SearchViewModel extends AndroidViewModel {

   private final SearchRepository repository;

   public SearchViewModel(@NonNull Application application) {
      super(application);
      repository = new SearchRepository(application);
   }


   public LiveData<String> getBookName() {
      return repository.getBookName();
   }
   public LiveData<Integer> getHadithRank() {
      return repository.getHadithRank();
   }
   public LiveData<List<Hadith>> getSearchInHadith() {
      return repository.getSearchInHadith();
   }
   public LiveData<List<Hadith>> getSearchInHadithChapter() {
      return repository.getSearchInHadithChapter();
   }
   public LiveData<List<Book>> getSearchInBooks() {
      return repository.getSearchInBooks();
   }


   public void searchInHadith(String query, int offset) {
      repository.searchInHadith(query,offset);
   }

   public void searchInChapter(String query, int offset) {
      repository.searchInChapter(query,offset);
   }

   public void searchInBook(String query, int offset) {
      repository.searchInBooks(query,offset);
   }
   public void setBookName(String bookNumber,String collectionName) {
      repository.setBookName(bookNumber,collectionName);
   }
   public void setHadithRank(Integer hadithId,String bookNUmber,String collectionName) {
      repository.setHadithRank(bookNUmber,collectionName,hadithId);
   }

   @Override
   protected void onCleared() {
      super.onCleared();
      repository.clearDesposite();
   }

}

