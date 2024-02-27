package com.app.dz.quranapp.fix_new_futers.aManageNewSources;

import java.io.Serializable;

public class AyaAudioLimitsFirebaseJson  implements Serializable {
    public int ayah;
    public long start_time;
    public long end_time;

    public AyaAudioLimitsFirebaseJson(int ayah, long start_time, long end_time) {
        this.ayah = ayah;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public int getAyah() {
        return ayah;
    }

    public void setAyah(int ayah) {
        this.ayah = ayah;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
}