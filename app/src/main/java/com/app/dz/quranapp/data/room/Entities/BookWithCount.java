package com.app.dz.quranapp.data.room.Entities;

import androidx.room.Ignore;

import java.util.List;

public class BookWithCount {
   public Integer id;
   public String bookNumber;
   public String bookName;
   public String bookCollection;
   public int chaptersCount;
    @Ignore
   public List<Chapter> chaptersList;
   @Ignore
   public boolean isSaved = false;
   @Ignore
   public String firstChapterTitle;
   public BookWithCount(Integer id, String bookNumber, String bookName, String bookCollection, int chaptersCount) {
      this.id = id;
      this.bookNumber = bookNumber;
      this.bookName = bookName;
      this.bookCollection = bookCollection;
      this.chaptersCount = chaptersCount;
   }

   @Ignore
   public BookWithCount(Integer id, String bookNumber, String bookName, String bookCollection, int chaptersCount,List<Chapter> chaptersList) {
      this.id = id;
      this.bookNumber = bookNumber;
      this.bookName = bookName;
      this.bookCollection = bookCollection;
      this.chaptersCount = chaptersCount;
      this.chaptersList = chaptersList;
   }


   @Override
   public String toString() {
      return "BookWithCount{" +
              "id=" + id +
              ", bookNumber='" + bookNumber + '\'' +
              ", bookName='" + bookName + '\'' +
              ", bookCollection='" + bookCollection + '\'' +
              ", chaptersCount=" + chaptersCount +
              '}';
   }

   public String getCompleteBookId(){
      return bookCollection + "_" + bookNumber;
   }

}
