package com.app.dz.quranapp.quran.listeners;

import com.app.dz.quranapp.data.room.Entities.Aya;

public interface OnFragmentListeners {
    void onAyaClick(Aya aya);
    void onHideAyaInfo();
    void onSaveAndShare(Aya aya);
    void onAyaTouch();
    void onScreenClick();

    void onPageChanged(int page);
}
