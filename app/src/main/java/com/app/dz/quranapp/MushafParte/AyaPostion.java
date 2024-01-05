package com.app.dz.quranapp.MushafParte;

public class AyaPostion {
    public int ayaId;
    public int adapterPosition;
    public int start;
    public int end;

    public AyaPostion(int ayaId, int start, int end) {
        this.ayaId = ayaId;
        this.start = start;
        this.end = end;
    }

    public AyaPostion() {
    }


    @Override
    public String toString() {
        return "AyaPostion{" +
                "ayaId=" + ayaId +
                ", adapterPosition=" + adapterPosition +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}

