package com.app.dz.quranapp.MainFragmentsParte.AdkarParte;


public class AdkarCategoryModel {

   private String category;
   private int count;

   public AdkarCategoryModel(String category,int count) {
      this.category = category;
      this.count = count;
   }


   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }
}
