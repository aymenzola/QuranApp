package com.app.dz.quranapp.ui.models.adhan;

import androidx.annotation.NonNull;

public class PrayerConfig {
    public String name;
    public String soundType;
    public boolean isNotifyOnSilentMode;


    public PrayerConfig(String name, String soundType, boolean isNotifyOnSilentMode) {
        this.name = name;
        this.soundType = soundType;
        this.isNotifyOnSilentMode = isNotifyOnSilentMode;
    }

    @Override
    public String toString() {
        return "PrayerConfig{" +
                "name='" + name + '\'' +
                ", soundType='" + soundType + '\'' +
                ", isNotifyOnSilentMode=" + isNotifyOnSilentMode +
                '}';
    }
}
