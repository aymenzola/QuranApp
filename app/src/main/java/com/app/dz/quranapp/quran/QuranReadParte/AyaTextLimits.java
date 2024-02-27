package com.app.dz.quranapp.quran.QuranReadParte;

import com.app.dz.quranapp.data.room.Entities.Aya;

public class AyaTextLimits {
    public int id;
    public int starindexInSura;
    public int endindexInSura;
    public Aya aya;

    public AyaTextLimits(int id,int starindexInSura, int endindexInSura) {
        this.id = id;
        this.starindexInSura = starindexInSura;
        this.endindexInSura = endindexInSura;
    }
    public AyaTextLimits(int id,int starindexInSura, int endindexInSura,Aya aya) {
        this.id = id;
        this.aya = aya;
        this.starindexInSura = starindexInSura;
        this.endindexInSura = endindexInSura;
    }
    public AyaTextLimits() {
    }
}
