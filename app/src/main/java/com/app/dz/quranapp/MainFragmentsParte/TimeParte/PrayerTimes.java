package com.app.dz.quranapp.MainFragmentsParte.TimeParte;

 public class PrayerTimes {
   public long fajr;
   public long sunrise;
   public long duhr;
   public long assr;
   public long maghrib;
   public long ishaa;

   public PrayerTimes(long fajr,long sunrise,long duhr, long assr, long maghrib, long ishaa) {
      this.fajr = fajr;
      this.sunrise = sunrise;
      this.duhr = duhr;
      this.assr = assr;
      this.maghrib = maghrib;
      this.ishaa = ishaa;
   }

   public PrayerTimes() {
   }
}
