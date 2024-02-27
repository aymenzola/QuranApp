package com.app.dz.quranapp.ui.activities.AdkarParte.AdkarDetailsParte;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "adkar")
public class Adkar {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "dikr")
    private String dikr;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "source")
    private String source;

    @ColumnInfo(name = "categoryId")
    private int categoryId;

    @ColumnInfo(name = "isSaved")
    private int isSaved;

    @ColumnInfo(name = "dikr_title")
    private String dikrTitle;

    public Adkar(int id, String dikr, String category, String source, int categoryId, int isSaved, String dikrTitle) {
        this.id = id;
        this.dikr = dikr;
        this.category = category;
        this.source = source;
        this.categoryId = categoryId;
        this.isSaved = isSaved;
        this.dikrTitle = dikrTitle;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDikr() {
        return dikr;
    }

    public void setDikr(String dikr) {
        this.dikr = dikr;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(int isSaved) {
        this.isSaved = isSaved;
    }

    public String getDikrTitle() {
        return dikrTitle;
    }

    public void setDikrTitle(String dikrTitle) {
        this.dikrTitle = dikrTitle;
    }
}