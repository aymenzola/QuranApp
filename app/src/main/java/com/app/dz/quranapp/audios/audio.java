package com.app.dz.quranapp.audios;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "audio")
public class audio {

    @PrimaryKey
    private int id;
    private String name;
    private String name_english;
    private int audiotype;
    private String url;
    private int is_there_selection;
    private String riwaya;
    private String reader_tag;
    private String reader_image;

    // Constructors, getters, and setters


    public audio() {
    }

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

    public String getName_english() {
        return name_english;
    }

    public void setName_english(String name_english) {
        this.name_english = name_english;
    }

    public int getAudiotype() {
        return audiotype;
    }

    public void setAudiotype(int audiotype) {
        this.audiotype = audiotype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIs_there_selection() {
        return is_there_selection;
    }

    public void setIs_there_selection(int is_there_selection) {
        this.is_there_selection = is_there_selection;
    }

    public String getRiwaya() {
        return riwaya;
    }

    public void setRiwaya(String riwaya) {
        this.riwaya = riwaya;
    }

    public String getReader_tag() {
        return reader_tag;
    }

    public void setReader_tag(String reader_tag) {
        this.reader_tag = reader_tag;
    }

    public String getReader_image() {
        return reader_image;
    }

    public void setReader_image(String reader_image) {
        this.reader_image = reader_image;
    }
}
