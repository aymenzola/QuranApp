package com.app.dz.quranapp.ui.activities.CollectionParte.HadithDetailsParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte.BooksRepository;
import com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte.ChaptersRepository;

import java.util.List;


public class HadithViewModel extends AndroidViewModel {

    private HadithRepository repository;
    private final BooksRepository repositoryBooks;
    private final ChaptersRepository repositoryChapters;

    public HadithViewModel(@NonNull Application application) {
        super(application);
        repository = new HadithRepository(application);
        repositoryBooks = new BooksRepository(application);
        repositoryChapters = new ChaptersRepository(application);
    }

    public LiveData<List<Hadith>> getHadithListOfBook() {
        return repository.getHadithListOfBook();
    }

    public void setHadith(String collectionName, String bookNumber, List<String> chapterIds) {
        repository.sethadithObject(collectionName,bookNumber,chapterIds);
    }
    public void setHadith(String collectionName, String bookNumber) {
        repository.sethadithObject(collectionName,bookNumber);
    }

    public LiveData<List<BookWithCount>> getBooksWithChaptersList() {
        return repositoryChapters.getBooksList();
    }

    public void askForBooksWithChaptersList(String collectionName) {
        repositoryChapters.setBooksWithChapters(collectionName);
    }

    public LiveData<List<Chapter>> getChaptersList() {
        return repositoryChapters.getChaptersObject();
    }

    public void askForChaptersList(String collectionName, String bookNumber) {
        repositoryChapters.setChaptersObject(collectionName,bookNumber);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

