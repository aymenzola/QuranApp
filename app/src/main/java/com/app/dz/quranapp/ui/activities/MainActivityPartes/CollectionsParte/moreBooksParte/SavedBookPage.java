package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "saved_book_pages")
public class SavedBookPage implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "book_id")
    public int bookId;

    @ColumnInfo(name = "page_number")
    public int pageNumber;

    @ColumnInfo(name = "book_title")
    public String bookTitle;

    @ColumnInfo(name = "page_title")
    public String pageTitle;

    public SavedBookPage(int bookId, int pageNumber, String bookTitle, String pageTitle) {
        this.bookId = bookId;
        this.pageNumber = pageNumber;
        this.bookTitle = bookTitle;
        this.pageTitle = pageTitle;
    }

    // getters and setters
}
