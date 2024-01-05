package com.app.dz.quranapp.Entities;

public class BookWithCount {
   public Integer id;
   public String bookNumber;
   public String bookName;
   public String bookCollection;
   public int chaptersCount;

   public BookWithCount(Integer id, String bookNumber, String bookName, String bookCollection, int chaptersCount) {
      this.id = id;
      this.bookNumber = bookNumber;
      this.bookName = bookName;
      this.bookCollection = bookCollection;
      this.chaptersCount = chaptersCount;
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
}
