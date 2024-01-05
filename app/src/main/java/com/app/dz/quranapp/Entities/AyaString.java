package com.app.dz.quranapp.Entities;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.app.dz.quranapp.quran.QuranReadParte.AyaTextLimits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AyaString implements Serializable {

    private SpannableStringBuilder stringBuilder;
    private boolean iscustomview = false;
    private boolean isSuraStar = false;
    private String suraTitle;
    private List<Aya> AyaList = new ArrayList<>();
    private List<AyaTextLimits> ayaLimitsList = new ArrayList<>();
    private int page;


    public AyaString(SpannableStringBuilder stringBuilder, boolean iscustomview,String suraTitle,boolean isSuraStar) {
        this.stringBuilder = stringBuilder;
        this.iscustomview = iscustomview;
        this.isSuraStar = isSuraStar;
        this.suraTitle = suraTitle;
    }
    public AyaString(SpannableStringBuilder stringBuilder, boolean iscustomview,String suraTitle) {
        this.stringBuilder = stringBuilder;
        this.iscustomview = iscustomview;
        this.suraTitle = suraTitle;
    }

    public AyaString(SpannableStringBuilder stringBuilder, boolean iscustomview, boolean isSuraStar, String suraTitle, List<Aya> ayaList, List<AyaTextLimits> ayaLimitsList, int page) {
        this.stringBuilder = stringBuilder;
        this.iscustomview = iscustomview;
        this.isSuraStar = isSuraStar;
        this.suraTitle = suraTitle;
        AyaList = ayaList;
        this.ayaLimitsList = ayaLimitsList;
        this.page = page;
    }

    public boolean isSuraStar() {
        return isSuraStar;
    }

    public void setSuraStar(boolean suraStar) {
        isSuraStar = suraStar;
    }

    public SpannableStringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public void setStringBuilder(SpannableStringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
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

    public List<Aya> getAyaList() {
        return AyaList;
    }

    public void setAyaList(List<Aya> ayaList) {
        AyaList.addAll(ayaList);
    }

    public List<AyaTextLimits> getAyaLimitsList() {
        return ayaLimitsList;
    }

    public void setAyaLimitsList(List<AyaTextLimits> ayaLimitsList) {
        this.ayaLimitsList.addAll(ayaLimitsList);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
