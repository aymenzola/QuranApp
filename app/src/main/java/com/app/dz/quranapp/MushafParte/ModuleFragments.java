package com.app.dz.quranapp.MushafParte;

import androidx.fragment.app.Fragment;

public class ModuleFragments {

    String tabname;
    Fragment fragment;


    public ModuleFragments(String tabname, Fragment fragment) {
        this.tabname = tabname;
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
