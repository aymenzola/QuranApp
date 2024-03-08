package com.app.dz.quranapp.Util;

import android.content.Context;
import android.util.Log;

import com.app.dz.quranapp.Services.ForegroundPlayAudioService2;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.AyaDao;
import com.app.dz.quranapp.data.room.Daos.SuraDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.data.room.MushafDatabase;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuraRepository {

    private static SuraRepository instance = null;
    private final AyaDao dao;

    private SuraRepository(Context context) {
        MushafDatabase database = MushafDatabase.getInstance(context);
        dao = database.getAyaDao();
    }

    public static SuraRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SuraRepository(context);
        }
        return instance;
    }


    public List<Sura> getSuraListInPage(int pageNum) {
        final List<Sura> suras = new ArrayList<>();

        Thread thread = new Thread(() -> {
            List<Aya> ayas = dao.getAyaListInPage(pageNum);
            Set<Integer> suraIds = new HashSet<>();

            for (Aya aya : ayas) suraIds.add(aya.getSura());
            for (Integer suraId : suraIds) suras.add(dao.findSuraById(suraId));

        });
        thread.start();

        try {
            thread.join(); // Wait for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return suras;
    }


}
