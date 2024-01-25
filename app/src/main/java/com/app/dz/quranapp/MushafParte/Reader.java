package com.app.dz.quranapp.MushafParte;

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;

public class Reader {
   public int readerId;
   public String readerName;
   public String readerEnglishName;
   public boolean isSelected = false;

   public Reader(int readerId, String readerName, String readerEnglishName) {
      this.readerId = readerId;
      this.readerName = readerName;
      this.readerEnglishName = readerEnglishName;
   }

}
