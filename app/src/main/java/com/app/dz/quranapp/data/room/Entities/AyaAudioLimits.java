package com.app.dz.quranapp.data.room.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "AyaAudioLimits",indices = {@Index(value = {"ayalimitid"},unique = true)})
public class AyaAudioLimits implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String ayalimitid;
    public int sura_number;
    @ColumnInfo(name = "sura_aya")
    public int suraAya;
    @ColumnInfo(name = "aya_start")
    public long startAyaTime;
    @ColumnInfo(name = "aya_end")
    public long endAyaTime;
    @ColumnInfo(name = "reader_name")
    public String readerName;

    public AyaAudioLimits(String ayalimitid, int sura_number, int suraAya, long startAyaTime, long endAyaTime, String readerName) {
        this.ayalimitid = ayalimitid;
        this.sura_number = sura_number;
        this.suraAya = suraAya;
        this.startAyaTime = startAyaTime;
        this.endAyaTime = endAyaTime;
        this.readerName = readerName;
    }

    public AyaAudioLimits() {
    }


    /*
    *  class Item {
    private String item_name;
    private List<Limits> limits;
    }

     class Limits {
    private int rank;
    private long start;
    private long end;
    }
    * *
    * */
}
