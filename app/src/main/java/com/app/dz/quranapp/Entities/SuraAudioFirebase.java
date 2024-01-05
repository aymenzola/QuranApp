package com.app.dz.quranapp.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SuraAudioFirebase implements Serializable {
    public String readerName;
    public int SuraNumber;
    public List<AyaAudioLimitsFirebase> ayaAudioList = new ArrayList<>();

    public SuraAudioFirebase(String readerName, int suraNumber, List<AyaAudioLimitsFirebase> ayaAudioList) {
        this.readerName = readerName;
        SuraNumber = suraNumber;
        this.ayaAudioList = ayaAudioList;
    }

    public SuraAudioFirebase() {
    }
}
/*
public class SuraAudioFirebase {
    public String readerName;
    public int SuraNumber;
    public List<AyaAudioLimitsFirebase> ayaAudioList = new ArrayList<>();

}

public class AyaAudioLimitsFirebase {
    public int suraAya;
    public long startAyaTime;
    public long endAyaTime;

}








 */