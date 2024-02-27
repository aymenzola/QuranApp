package com.app.dz.quranapp.data.room.Entities;

import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.NORMAL_ACTION;

import java.io.Serializable;

public class BookCollection implements Serializable {

   public String CollectionName;
   public boolean isDownloaded;
   public String CollectionWriter;
   public String arabicName;
   public int itemsCount;
   public int progress = 0;
   public String progress_text= "جاري تحميل الكتاب ... ";
   public String state = NORMAL_ACTION;


   public BookCollection(String collectionName, boolean isDownloaded, String collectionWriter, String arabicName,int itemsCount) {
      CollectionName = collectionName;
      this.isDownloaded = isDownloaded;
      CollectionWriter = collectionWriter;
      this.arabicName = arabicName;
      this.itemsCount = itemsCount;
   }


   @Override
   public String toString() {
      return "BookCollection{" +
              "CollectionName='" + CollectionName + '\'' +
              ", isDownloaded=" + isDownloaded +
              ", CollectionWriter='" + CollectionWriter + '\'' +
              ", arabicName='" + arabicName + '\'' +
              '}';
   }
}
