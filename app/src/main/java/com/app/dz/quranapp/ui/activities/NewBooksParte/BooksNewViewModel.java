package com.app.dz.quranapp.ui.activities.NewBooksParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte.BooksRepository;
import com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte.ChaptersRepository;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;

import java.util.List;


public class BooksNewViewModel extends AndroidViewModel {

    private final BooksRepository repository;
    private final ChaptersRepository repositoryChapters;

    public BooksNewViewModel(@NonNull Application application) {
        super(application);
        repository = new BooksRepository(application);
        repositoryChapters = new ChaptersRepository(application);
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


    public LiveData<List<Chapter>> getChaptersList() {
        return repositoryChapters.getChaptersObject();
    }

    public void askForChaptersList(String collectionName, String bookNumber) {
        repositoryChapters.setChaptersObject(collectionName,bookNumber);
    }

    public LiveData<List<BookWithCount>> getBooksWithChaptersList() {
        return repositoryChapters.getBooksList();
    }

    public void askForBooksWithChaptersList(String collectionName) {
        repositoryChapters.setBooksWithChapters(collectionName);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

