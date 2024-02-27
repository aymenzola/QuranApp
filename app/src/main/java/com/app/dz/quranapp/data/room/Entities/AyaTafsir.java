package com.app.dz.quranapp.data.room.Entities;

import java.io.Serializable;

public class AyaTafsir implements Serializable {

    private boolean iscustomview = false;
    private boolean isSuraStar = false;
    private String suraTitle;
    private Aya aya;
    private int page;


    public AyaTafsir(boolean iscustomview,boolean isSuraStar,String suraTitle) {
        this.iscustomview = iscustomview;
        this.isSuraStar = isSuraStar;
        this.suraTitle = suraTitle;
    }

    public AyaTafsir(boolean iscustomview, boolean isSuraStar, String suraTitle, Aya aya,int page) {
        this.iscustomview = iscustomview;
        this.isSuraStar = isSuraStar;
        this.suraTitle = suraTitle;
        this.aya = aya;
        this.page = page;
    }

    public boolean isSuraStar() {
        return isSuraStar;
    }

    public void setSuraStar(boolean suraStar) {
        isSuraStar = suraStar;
    }

    public boolean isIscustomview() {
        return iscustomview;
    }

    public void setIscustomview(boolean iscustomview) {
        this.iscustomview = iscustomview;
    }

    public String getSuraTitle() {
        return suraTitle;
    }

    public void setSuraTitle(String suraTitle) {
        this.suraTitle = suraTitle;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Aya getAya() {
        return aya;
    }

    public void setAya(Aya aya) {
        this.aya = aya;
    }
}
