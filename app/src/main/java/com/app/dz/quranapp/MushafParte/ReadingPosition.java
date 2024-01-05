package com.app.dz.quranapp.MushafParte;

public class ReadingPosition {
    public int sura;
    public int aya;
    public String ayaText;
    public int page;

    public ReadingPosition(int sura, int aya,int page,String ayaText) {
        this.sura = sura;
        this.aya = aya;
        this.page = page;
        this.ayaText = ayaText;
    }

    public ReadingPosition() {

    }
}
