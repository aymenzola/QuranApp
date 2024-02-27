package com.app.dz.quranapp.ui.activities.OnBoardingParte;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class AdapterStartFragments extends FragmentStatePagerAdapter {

    ArrayList<ModuleFragments> list = new ArrayList<>();

    public AdapterStartFragments(@NonNull FragmentManager fm, int behavior, ArrayList<ModuleFragments> list) {
        super(fm, behavior);
        this.list = list;
    }

    public AdapterStartFragments(@NonNull FragmentManager fm,ArrayList<ModuleFragments> list) {
        super(fm);
        this.list = list;
    }


    public void addlist(ModuleFragments moduleTab){
     list.add(moduleTab);
    }

    public void norifyadapter(ModuleFragments moduleTab,int p){
    Log.e("logrefrech","position "+p);
     list.set(p,moduleTab);
     notifyDataSetChanged();
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position).getFragment();
    }



    @Override
    public int getCount() {
        return list.size();
    }
}
