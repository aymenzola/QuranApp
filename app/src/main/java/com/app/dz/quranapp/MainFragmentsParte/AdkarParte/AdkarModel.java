package com.app.dz.quranapp.MainFragmentsParte.AdkarParte;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "adkar")
public
class AdkarModel {

   @NonNull
   @PrimaryKey()
   private int id;
   private String dikr;
   private String category;
   private String source;

   public AdkarModel(String dikr, String category, String source) {
      this.dikr = dikr;
      this.category = category;
      this.source = source;
   }


   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getDikr() {
      return dikr;
   }

   public void setDikr(String dikr) {
      this.dikr = dikr;
   }

   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }
}
