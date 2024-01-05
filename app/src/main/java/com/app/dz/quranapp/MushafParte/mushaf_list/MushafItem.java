package com.app.dz.quranapp.MushafParte.mushaf_list;

class MushafItem {
   private int id;
   private String name;
   private int progress;
   private boolean isDownloading;

   public MushafItem(int id, String name, int progress, boolean isDownloading) {
      this.id = id;
      this.name = name;
      this.progress = progress;
      this.isDownloading = isDownloading;
   }

   public boolean isDownloading() {
      return isDownloading;
   }

   public void setDownloading(boolean downloading) {
      isDownloading = downloading;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getProgress() {
      return progress;
   }

   public void setProgress(int progress) {
      this.progress = progress;
   }
}
