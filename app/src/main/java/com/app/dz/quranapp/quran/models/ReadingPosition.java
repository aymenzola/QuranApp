package com.app.dz.quranapp.quran.models;

public class ReadingPosition {
    public int sura;
    public int aya;
    public String ayaText;
    public Integer page;

    public ReadingPosition(int sura, int aya,int page,String ayaText) {
        this.sura = sura;
        this.aya = aya;
        this.page = page;
        this.ayaText = ayaText;
    }

    public ReadingPosition() {

    }
}
