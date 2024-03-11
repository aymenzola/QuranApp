package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "saved_pages")
public class SavedMatnPage implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "matn_id")
    public int matnId;

    @ColumnInfo(name = "page_number")
    public int pageNumber;

    @ColumnInfo(name = "book_title")
    public String bookTitle;

    @ColumnInfo(name = "page_title")
    public String pageTitle;

    public SavedMatnPage(int matnId, int pageNumber, String bookTitle, String pageTitle) {
        this.matnId = matnId;
        this.pageNumber = pageNumber;
        this.bookTitle = bookTitle;
        this.pageTitle = pageTitle;
    }

    // getters and setters
}
