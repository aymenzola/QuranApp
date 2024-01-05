package com.app.dz.quranapp.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Chapter implements Parcelable {
   public String chapterId;
   public String chapterTitle;
   public String bookNumber;

   public Chapter(String chapterId, String chapterTitle,String bookNumber) {
      this.chapterId = chapterId;
      this.chapterTitle = chapterTitle;
      this.bookNumber = bookNumber;
   }

   protected Chapter(Parcel in) {
      chapterId = in.readString();
      chapterTitle = in.readString();
      bookNumber = in.readString();
   }

   public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
      @Override
      public Chapter createFromParcel(Parcel in) {
         return new Chapter(in);
      }

      @Override
      public Chapter[] newArray(int size) {
         return new Chapter[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeString(chapterId);
      parcel.writeString(chapterTitle);
      parcel.writeString(bookNumber);
   }
}

