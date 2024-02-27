package com.app.dz.quranapp.ui.models.adhan;

import java.util.UUID;

public class DayPrayersConfig {
    public PrayerConfig FajrConfig;
    public PrayerConfig ShourokConfig;
    public PrayerConfig DuhrConfig;
    public PrayerConfig AsrConfig;
    public PrayerConfig MaghribConfig;
    public PrayerConfig IchaaConfig;
    public boolean isThereOnSchedule;
    public UUID newWorkId;

    public DayPrayersConfig(PrayerConfig fajrConfig,PrayerConfig ShourokConfig,PrayerConfig duhrConfig, PrayerConfig asrConfig, PrayerConfig maghribConfig, PrayerConfig ichaaConfig, boolean isThereOnSchedule) {
        this.FajrConfig = fajrConfig;
        this.ShourokConfig = ShourokConfig;
        this.DuhrConfig = duhrConfig;
        this.AsrConfig = asrConfig;
        this.MaghribConfig = maghribConfig;
        this.IchaaConfig = ichaaConfig;
        this.isThereOnSchedule = isThereOnSchedule;
    }

    public DayPrayersConfig(PrayerConfig fajrConfig, PrayerConfig duhrConfig, PrayerConfig asrConfig, PrayerConfig maghribConfig, PrayerConfig ichaaConfig, boolean isThereOnSchedule,UUID newWorkId){
        this.FajrConfig = fajrConfig;
        this.DuhrConfig = duhrConfig;
        this.AsrConfig = asrConfig;
        this.MaghribConfig = maghribConfig;
        this.IchaaConfig = ichaaConfig;
        this.isThereOnSchedule = isThereOnSchedule;
        this.newWorkId = newWorkId;
    }

    public void updatePrayerConfigAtPosition(PrayerConfig updatedConfig, int position) {
        switch (position) {
            case 0 -> this.FajrConfig = updatedConfig;
            case 1 -> this.ShourokConfig = updatedConfig;
            case 2 -> this.DuhrConfig = updatedConfig;
            case 3 -> this.AsrConfig = updatedConfig;
            case 4 -> this.MaghribConfig = updatedConfig;
            case 5 -> this.IchaaConfig = updatedConfig;
        }
    }

    @Override
    public String toString() {
        return "DayPrayersConfig{" +
                "FajrConfig=" + FajrConfig +
                ", ShourokConfig=" + ShourokConfig +
                ", DuhrConfig=" + DuhrConfig +
                ", AsrConfig=" + AsrConfig +
                ", MaghribConfig=" + MaghribConfig +
                ", IchaaConfig=" + IchaaConfig +
                ", isThereOnSchedule=" + isThereOnSchedule +
                ", newWorkId=" + newWorkId +
                '}';
    }
}
