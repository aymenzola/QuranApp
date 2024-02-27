package com.app.dz.quranapp.quran.QuranReadParte;

import android.text.SpannableStringBuilder;

import com.app.dz.quranapp.data.room.Entities.Aya;

import java.util.List;

public class PageModel {
    public String suraName;
    public int page;
    public List<Aya> AyaList;
    public List<AyaTextLimits> ayaLimitsList;
    public SpannableStringBuilder ayatText;

    public PageModel(int page, List<Aya> ayaList, List<AyaTextLimits> ayaLimitsList, SpannableStringBuilder ayatText,String suraName) {
        this.page = page;
        AyaList = ayaList;
        this.ayaLimitsList = ayaLimitsList;
        this.ayatText = ayatText;
        this.suraName = suraName;
    }

    public PageModel() {
    }
}
