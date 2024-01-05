package com.app.dz.quranapp.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SuraAudio implements Serializable {
    public String readerName;
    public int startAya;
    public int endAya;
    public int SuraNumber;
    public long SuraDuration;
    public boolean isFromLocal;
    public boolean isThereSelection = true;
    public List<AyaAudioLimits> ayaAudioList = new ArrayList<>();

    public SuraAudio(String readerName,int startAya, int endAya, int suraNumber, boolean isFromLocal, List<AyaAudioLimits> ayaAudioList,boolean isThereSelection) {
        this.readerName = readerName;
        this.startAya = startAya;
        this.endAya = endAya;
        SuraNumber = suraNumber;
        this.isFromLocal = isFromLocal;
        this.ayaAudioList = ayaAudioList;
        this.isThereSelection = isThereSelection;
    }

    public SuraAudio() {
    }
}
