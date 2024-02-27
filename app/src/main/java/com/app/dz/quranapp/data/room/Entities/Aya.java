package com.app.dz.quranapp.data.room.Entities;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(foreignKeys = {
        @ForeignKey(entity = Sura.class, parentColumns = "id", childColumns = "sura"),
        @ForeignKey(entity = Juz.class, parentColumns = "id", childColumns = "juz")})
public class Aya implements Serializable {

    @PrimaryKey
    private int id;

    // sura number in Quran
    private int sura;

    // num of this aya in its sura
    @ColumnInfo(name = "sura_aya")
    private int suraAya;

    @NonNull
    private String text;

    @NonNull
    @ColumnInfo(name = "pure_text")
    private String pureText;

    private int page;

    private double amount;

    private int juz;

    private String x;

    private String y;

    private int xw;

    private int yw;

    @NonNull
    private String tafseer;

    @Ignore
    private boolean iselected = false;
    @Ignore
    private SpannableStringBuilder stringBuilder;
    @Ignore
    private boolean iscustomview = false;
    @Ignore
    private String suraTitle;


    public Aya(int id, int sura, int suraAya, @NonNull String text, @NonNull String pureText
            , int page, double amount, int juz, String x, String y, int xw, int yw
            , @NonNull String tafseer) {
        this.id = id;
        this.sura = sura;
        this.suraAya = suraAya;
        this.text = text;
        this.pureText = pureText;
        this.page = page;
        this.amount = amount;
        this.juz = juz;
        this.x = x;
        this.y = y;
        this.xw = xw;
        this.yw = yw;
        this.tafseer = tafseer;
    }


    @Ignore
    public Aya(int id, int sura, int suraAya, @NonNull String text, int page, int juz, @NonNull String tafseer) {
        this.id = id;
        this.sura = sura;
        this.suraAya = suraAya;
        this.text = text;
        this.page = page;
        this.juz = juz;
        this.tafseer = tafseer;
    }

    @Ignore
    public Aya(boolean iscustomview, String suraTitle) {
        this.iscustomview = iscustomview;
        this.suraTitle = suraTitle;
    }

    @Ignore
    public Aya(int sura,int suraAya) {
        this.sura = sura;
        this.suraAya = suraAya;
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

    public int getSura() {
        return sura;
    }

    public void setSura(int sura) {
        this.sura = sura;
    }

    public int getSuraAya() {
        return suraAya;
    }

    public void setSuraAya(int suraAya) {
        this.suraAya = suraAya;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    @NonNull
    public String getPureText() {
        return pureText;
    }

    public void setPureText(@NonNull String pureText) {
        this.pureText = pureText;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getJuz() {
        return juz;
    }

    public void setJuz(int juz) {
        this.juz = juz;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public int getXw() {
        return xw;
    }

    public void setXw(int xw) {
        this.xw = xw;
    }

    public int getYw() {
        return yw;
    }

    public void setYw(int yw) {
        this.yw = yw;
    }

    @NonNull
    public String getTafseer() {
        return tafseer;
    }

    public void setTafseer(@NonNull String tafseer) {
        this.tafseer = tafseer;
    }

    @NonNull
    @Override
    public String toString() {
        return "Aya{" +
                "id=" + id +
                ", sura=" + sura +
                ", suraAya=" + suraAya +
                ", text='" + text + '\'' +
                ", pureText='" + pureText + '\'' +
                ", page=" + page +
                ", amount=" + amount +
                ", juz=" + juz +
                ", x=" + x +
                ", y=" + y +
                ", xw=" + xw +
                ", yw=" + yw +
                ", tafseer='" + tafseer + '\'' +
                '}';
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
