package com.app.dz.quranapp.MushafParte;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterStartFragments extends FragmentStatePagerAdapter {

    ArrayList<ModuleFragments> list = new ArrayList<>();

    public AdapterStartFragments(@NonNull FragmentManager fm) {
        super(fm);
    }


    public void addlist(List<ModuleFragments> moduleTab){
        list.addAll(moduleTab);
    }

    public void addNewItems(List<ModuleFragments> list1){
     list.addAll(list1);
     notifyDataSetChanged();
    }

    public void addNewItemsAfirst(List<ModuleFragments> list1){
     list.addAll(0,list1);
     notifyDataSetChanged();
    }

    public void clearAdapter(){
     list.clear();
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
