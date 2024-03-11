package com.app.dz.quranapp.quran.models;

public class ReadingPosition {
    public int sura;
    public int aya;
    public String ayaText;
    public Integer page;
    public String riwaya;

    public ReadingPosition(int sura, int aya,int page,String ayaText,String riwaya) {
        this.sura = sura;
        this.aya = aya;
        this.page = page;
        this.ayaText = ayaText;
        this.riwaya = riwaya;
    }

    public ReadingPosition() {

    }

    @Override
    public String toString() {
        return "ReadingPosition{" +
                "sura=" + sura +
                ", aya=" + aya +
                ", ayaText='" + ayaText + '\'' +
                ", page=" + page +
                ", riwaya='" + riwaya + '\'' +
                '}';
    }
}
