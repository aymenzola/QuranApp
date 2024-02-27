package com.app.dz.quranapp.MushafParte.multipleRiwayatParte;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "audio")
public class ReaderAudio implements Serializable {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "name_english")
    private String nameEnglish;

    @ColumnInfo(name = "audiotype")
    private int audioType;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "is_there_selection")
    private int isThereSelection;

    @ColumnInfo(name = "riwaya")
    private String riwaya;

    @ColumnInfo(name = "reader_tag")
    private String readerTag;

    @ColumnInfo(name = "reader_image", defaultValue = "https://www.assabile.com/media/photo/full_size/abdul-rahman-al-sudais-81.jpg")
    private String readerImage;

    @Ignore
    private boolean isSelected = false;

    // Getter and Setter methods for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public int getAudioType() {
        return audioType;
    }

    public void setAudioType(int audioType) {
        this.audioType = audioType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsThereSelection() {
        return isThereSelection;
    }

    public boolean isThereSelection() {
        return isThereSelection==1;
    }

    public void setIsThereSelection(int isThereSelection) {
        this.isThereSelection = isThereSelection;
    }

    public String getRiwaya() {
        return riwaya;
    }

    public void setRiwaya(String riwaya) {
        this.riwaya = riwaya;
    }

    public String getReaderTag() {
        return readerTag;
    }

    public void setReaderTag(String readerTag) {
        this.readerTag = readerTag;
    }

    public String getReaderImage() {
        return readerImage;
    }

    public void setReaderImage(String readerImage) {
        this.readerImage = readerImage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "ReaderAudio{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nameEnglish='" + nameEnglish + '\'' +
                ", audioType=" + audioType +
                ", isThereSelection=" + isThereSelection +
                ", riwaya='" + riwaya + '\'' +
                ", readerTag='" + readerTag + '\'' +
                ", readerImage='" + readerImage + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
