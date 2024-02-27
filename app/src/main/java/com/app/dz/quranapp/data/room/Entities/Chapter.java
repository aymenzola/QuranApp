package com.app.dz.quranapp.data.room.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

public class Chapter {
    public String chapterId;
    public String chapterTitle;
    public String bookNumber;
    public String chapterTitle_no_tachkil;
    @Ignore
    public boolean isSaved = false;
    @Ignore
    public String collectionName;
    @Ignore
    public String bookName;
    @Ignore
    public int positionInChaptersList;

    public Chapter(String chapterId, String chapterTitle, String bookNumber, String chapterTitle_no_tachkil) {
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.bookNumber = bookNumber;
        this.chapterTitle_no_tachkil = chapterTitle_no_tachkil;
    }

    @Ignore
    public Chapter(String chapterId, String chapterTitle, String bookNumber, boolean isSaved) {
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.bookNumber = bookNumber;
        this.isSaved = isSaved;
    }

    protected Chapter(Parcel in) {
        chapterId = in.readString();
        chapterTitle = in.readString();
        bookNumber = in.readString();
    }

    public String getCompleteChapterId() {
        //use chapter title prefx to avoid duplicate chapter id
        if (this.chapterTitle.length() < 5) {
            return chapterId + "_" + this.chapterTitle;
        }
        String pfex = this.chapterTitle.substring(0, 5);
        return chapterId + "_" + pfex;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "chapterId='" + chapterId + '\'' +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", bookNumber='" + bookNumber + '\'' +
                ", isSaved=" + isSaved +
                ", collectionName='" + collectionName + '\'' +
                ", bookName='" + bookName + '\'' +
                ", positionInChaptersList=" + positionInChaptersList +
                '}';
    }
}

