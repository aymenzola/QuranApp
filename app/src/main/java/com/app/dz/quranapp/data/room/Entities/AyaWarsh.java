package com.app.dz.quranapp.data.room.Entities;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "AyaWarsh")
public class AyaWarsh implements Serializable {

    @NonNull
    @PrimaryKey()
    private int id;

    private int jozz;
    private int page;
    private int sura_no;
    @NonNull
    private String sura_name_en;
    @NonNull
    private String sura_name_ar;
    private int line_start;
    private int line_end;
    private int aya_no;
    @NonNull
    private String aya_text;

    @Ignore
    private boolean iselected = false;
    @Ignore
    private SpannableStringBuilder stringBuilder;
    @Ignore
    private boolean iscustomview = false;
    @Ignore
    private String suraTitle;


    @Ignore
    public AyaWarsh(boolean iscustomview, String suraTitle) {
        this.iscustomview = iscustomview;
        this.suraTitle = suraTitle;
    }

    public AyaWarsh(int id, int jozz, int page, int sura_no, String sura_name_en, String sura_name_ar, int line_start, int line_end, int aya_no, String aya_text) {
        this.id = id;
        this.jozz = jozz;
        this.page = page;
        this.sura_no = sura_no;
        this.sura_name_en = sura_name_en;
        this.sura_name_ar = sura_name_ar;
        this.line_start = line_start;
        this.line_end = line_end;
        this.aya_no = aya_no;
        this.aya_text = aya_text;
    }

    public int getJozz() {
        return jozz;
    }

    public void setJozz(int jozz) {
        this.jozz = jozz;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSura_no() {
        return sura_no;
    }

    public void setSura_no(int sura_no) {
        this.sura_no = sura_no;
    }

    public String getSura_name_en() {
        return sura_name_en;
    }

    public void setSura_name_en(String sura_name_en) {
        this.sura_name_en = sura_name_en;
    }

    public String getSura_name_ar() {
        return sura_name_ar;
    }

    public void setSura_name_ar(String sura_name_ar) {
        this.sura_name_ar = sura_name_ar;
    }

    public int getLine_start() {
        return line_start;
    }

    public void setLine_start(int line_start) {
        this.line_start = line_start;
    }

    public int getLine_end() {
        return line_end;
    }

    public void setLine_end(int line_end) {
        this.line_end = line_end;
    }

    public int getAya_no() {
        return aya_no;
    }

    public void setAya_no(int aya_no) {
        this.aya_no = aya_no;
    }

    public String getAya_text() {
        return aya_text;
    }

    public void setAya_text(String aya_text) {
        this.aya_text = aya_text;
    }

    public String getSuraTitle() {
        return suraTitle;
    }

    public void setSuraTitle(String suraTitle) {
        this.suraTitle = suraTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpannableStringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public void setStringBuilder(SpannableStringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    public boolean isIselected() {
        return iselected;
    }

    public void setIselected(boolean iselected) {
        this.iselected = iselected;
    }

    public boolean isIscustomview() {
        return iscustomview;
    }

    public void setIscustomview(boolean iscustomview) {
        this.iscustomview = iscustomview;
    }
}
