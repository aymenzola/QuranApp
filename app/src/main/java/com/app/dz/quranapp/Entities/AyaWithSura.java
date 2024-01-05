package com.app.dz.quranapp.Entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class AyaWithSura {
    @Embedded public Aya aya;
    @Relation(parentColumn = "sura",entityColumn = "id")
    public Sura sura;

    public AyaWithSura(Aya aya, Sura sura) {
        this.aya = aya;
        this.sura = sura;
    }

    @Override
    public String toString() {
        return "AyaWithSura{" +
                "aya=" + aya +
                ", sura=" + sura +
                '}';
    }
}
