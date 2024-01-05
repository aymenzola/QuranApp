package com.app.dz.quranapp.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {
   @NonNull
   @PrimaryKey(autoGenerate = true)
   public Integer id;
   public String bookNumber;
   public String bookName;
   public String bookCollection;
   @Ignore
   public int chaptersCount;

   public Book(String bookNumber, String bookName, String bookCollection) {
      this.bookNumber = bookNumber;
      this.bookName = bookName;
      this.bookCollection = bookCollection;
   }

   @Override
   public String toString() {
      return "Book{" +
              "bookNumber='" + bookNumber + '\'' +
              ", bookName='" + bookName + '\'' +
              ", bookCollection='" + bookCollection + '\'' +
              '}';
   }
}
