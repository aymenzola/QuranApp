package com.app.dz.quranapp.Entities;

import java.io.Serializable;

public class SuraDownload implements Serializable {
    public String readerName;
    public int endAya;
    public int SuraNumber;
    public boolean isThereSelection = false;


    public SuraDownload(String readerName,int endAya, int suraNumber,boolean isThereSelection) {
        this.readerName = readerName;
        this.endAya = endAya;
        this.SuraNumber = suraNumber;
        this.isThereSelection = isThereSelection;
    }
}
