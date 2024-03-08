package com.app.dz.quranapp.data.room.Entities;

import com.app.dz.quranapp.Communs.Constants;

import java.io.Serializable;
import java.util.Objects;

public class Riwaya implements Serializable {
    public int id;
    public String name;
    public String image="";
    public int pages;
    public String tag;
    public String quran_page_image_url;
    private String fileUrl;
    public String fileName;

    public Riwaya(int id, String name,String tag, String image,String quran_page_image_url, int pages) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.pages = pages;
        this.tag = tag;
        this.quran_page_image_url = quran_page_image_url;
    }

    public Riwaya() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Riwaya riwaya = (Riwaya) o;
        return id == riwaya.id && pages == riwaya.pages && Objects.equals(name, riwaya.name) && Objects.equals(image, riwaya.image) && Objects.equals(tag, riwaya.tag) && Objects.equals(quran_page_image_url, riwaya.quran_page_image_url);
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
