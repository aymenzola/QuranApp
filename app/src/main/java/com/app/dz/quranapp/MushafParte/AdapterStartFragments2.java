package com.app.dz.quranapp.MushafParte;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.dz.quranapp.MushafParte.hafs_parte.QuranPageFragment;

import java.util.ArrayList;

public class AdapterStartFragments2 extends FragmentStatePagerAdapter {

    int pagesCount;
    ArrayList<ModuleFragments> list = new ArrayList<>();

    public AdapterStartFragments2(@NonNull FragmentManager fm, int page_count) {
        super(fm);
        pagesCount = page_count;
    }


  /*  public void addlist(List<ModuleFragments> moduleTab){
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
*/


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return QuranPageFragment.newInstance(position+1);
    }



    @Override
    public int getCount() {
        return pagesCount;
    }
}
