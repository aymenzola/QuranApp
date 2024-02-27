package com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.BookWithCount;

import java.util.List;


public class BooksViewModel extends AndroidViewModel {

    private final BooksRepository repository;

    public BooksViewModel(@NonNull Application application) {
        super(application);
        repository = new BooksRepository(application);
    }

    public LiveData<List<BookWithCount>> getDBooks() {
        return repository.getBooks();
    }

    public LiveData<Object> getDBookObject() {
        return repository.getBooksObject();
    }

    public void setBooks(String collectionName) {
        repository.setBooks(collectionName);
    }

    public void setBooksObject(String collectionName) {
        repository.setBooksFromApi(collectionName);
    }

    public LiveData<Object> getHadithObject() {
        return repository.getHadithObject();
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

