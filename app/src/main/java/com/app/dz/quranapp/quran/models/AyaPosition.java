package com.app.dz.quranapp.quran.models;

public class AyaPosition {
    public int ayaId;
    public int adapterPosition;
    public int start;
    public int end;

    public AyaPosition(int ayaId, int start, int end) {
        this.ayaId = ayaId;
        this.start = start;
        this.end = end;
    }

    public AyaPosition() {
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

