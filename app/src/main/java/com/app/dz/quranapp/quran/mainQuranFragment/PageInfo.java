package com.app.dz.quranapp.quran.mainQuranFragment;

import androidx.annotation.NonNull;

public class PageInfo {
        public String suraName;
        public int page;
        public String justName;

        public PageInfo(String suraName, int page, String justName) {
            this.suraName = suraName;
            this.page = page;
            this.justName = justName;
        }

    @Override
    public String toString() {
        return "PageInfo{" +
                "suraName='" + suraName + '\'' +
                ", page=" + page +
                ", justName='" + justName + '\'' +
                '}';
    }
}