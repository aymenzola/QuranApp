package com.app.dz.quranapp.MushafParte;

import com.app.dz.quranapp.Entities.Aya;

public interface OnFragmentListeners {
    void onAyaClick(Aya aya);
    void onHideAyaInfo();
    void onSaveAndShare(Aya aya);
    void onAyaTouch();
    void onScreenClick();
}
