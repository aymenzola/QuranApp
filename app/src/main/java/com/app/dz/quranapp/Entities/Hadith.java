package com.app.dz.quranapp.Entities;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "hadithstable",indices = {@Index(value = {"body"},unique = true)})
public class Hadith {

   @NonNull
   @PrimaryKey(autoGenerate = true)
   public Integer id;
   public String collection;
   public String bookNumber;
   public String chapterId;
   public String hadithNumber;
   public String chapterTitle;
   public String body;
   public String body_no_tachkil;
   public String chapterTitle_no_tachkil;
   public String graded_by;
   public String grade;
   @Ignore
   public int type;

   @Ignore
   public SpannableStringBuilder stringBuilder;

   @Ignore
   public Hadith(String collection, String bookNumber, String chapterId, String hadithNumber, String chapterTitle, String body,String
                 body_no_tachkil,String chapterTitle_no_tachkil,int id) {
      this.collection = collection;
      this.bookNumber = bookNumber;
      this.chapterId = chapterId;
      this.hadithNumber = hadithNumber;
      this.chapterTitle = chapterTitle;
      this.body = body;
      this.body_no_tachkil = body_no_tachkil;
      this.chapterTitle_no_tachkil = chapterTitle_no_tachkil;
      this.id = id;
   }

   public Hadith(String collection, String bookNumber, String chapterId, String hadithNumber, String chapterTitle, String body,String
                 body_no_tachkil,String chapterTitle_no_tachkil,String graded_by,String grade) {
      this.collection = collection;
      this.bookNumber = bookNumber;
      this.chapterId = chapterId;
      this.hadithNumber = hadithNumber;
      this.chapterTitle = chapterTitle;
      this.body = body;
      this.body_no_tachkil = body_no_tachkil;
      this.chapterTitle_no_tachkil = chapterTitle_no_tachkil;
      this.graded_by = graded_by;
      this.grade = grade;
   }

   @Ignore
   public Hadith(String collection, String bookNumber, String chapterId, String hadithNumber, String chapterTitle, String body,String
                 body_no_tachkil,String chapterTitle_no_tachkil) {
      this.collection = collection;
      this.bookNumber = bookNumber;
      this.chapterId = chapterId;
      this.hadithNumber = hadithNumber;
      this.chapterTitle = chapterTitle;
      this.body = body;
      this.body_no_tachkil = body_no_tachkil;
      this.chapterTitle_no_tachkil = chapterTitle_no_tachkil;
   }

   @Override
   public String toString() {
      return "Hadith{" +
              "id=" + id +
              ", collection='" + collection + '\'' +
              ", bookNumber='" + bookNumber + '\'' +
              ", chapterId='" + chapterId + '\'' +
              ", hadithNumber='" + hadithNumber + '\'' +
              ", chapterTitle='" + chapterTitle + '\'' +
              ", body='" + body + '\'' +
              ", body_no_tachkil='" + body_no_tachkil + '\'' +
              ", chapterTitle_no_tachkil='" + chapterTitle_no_tachkil + '\'' +
              ", graded_by='" + graded_by + '\'' +
              ", grade='" + grade + '\'' +
              ", type=" + type +
              ", stringBuilder=" + stringBuilder +
              '}';
   }
}

