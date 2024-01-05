package com.app.dz.quranapp.Util;

public class UserLocation{
      public String address;
      public long latitude;
      public long longitude;
      public String country;
      public String locality;

      public UserLocation(String adresse, long latitude, long longitude, String country, String locality) {
         this.address= adresse;
         this.latitude = latitude;
         this.longitude = longitude;
         this.country = country;
         this.locality = locality;
      }

      public UserLocation() {
      }
   }