package com.app.dz.quranapp.MushafParte.riwayat_parte;

import static com.app.dz.quranapp.Constants.QuranHafsIMAGE_LINK;
import static com.app.dz.quranapp.Constants.QuranWARCH_TAJWID_IMAGE_LINK;
import static com.app.dz.quranapp.Constants.QuranWarchIMAGE_LINK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.Entities.Juz;
import com.app.dz.quranapp.Entities.Riwaya;
import com.app.dz.quranapp.Entities.Sura;
import com.app.dz.quranapp.MainFragmentsParte.HomeFragment.JuzaAdapter;
import com.app.dz.quranapp.MainFragmentsParte.HomeFragment.SuraAdapter;
import com.app.dz.quranapp.MainFragmentsParte.HomeFragment.SuraViewModel;
import com.app.dz.quranapp.MushafParte.QuranActivity;
import com.app.dz.quranapp.MushafParte.QuranActivityDev;
import com.app.dz.quranapp.MushafParte.ReadingPosition;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.databinding.ActivityRiwayatListBinding;
import com.app.dz.quranapp.databinding.FragmentQuranListBinding;
import com.app.dz.quranapp.databinding.QuranActivityBinding;
import com.app.dz.quranapp.quran.QuranSearchParte.ActivitySearchQuran;
import com.app.dz.quranapp.room.Daos.AyaDao;
import com.app.dz.quranapp.room.MushafDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class RiwayatListActivity extends AppCompatActivity {

    public final static String TAG = "RiwayatListActivity";
    private ActivityRiwayatListBinding binding;
    private int startPage;

    private RiwayatAdapter adapterJuza;


    public RiwayatListActivity() {
        // Required empty public constructor
    }


    public static RiwayatListActivity newInstance() {
        RiwayatListActivity fragment = new RiwayatListActivity();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiwayatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }

        Intent intent = getIntent();
        startPage = intent.getIntExtra("page", 1);

        initializeJuzAdapter(getRiwayatList());

    }




    public interface OnListenerInterface {
        void onitemclick(int position);
    }



    private void OpenMushaf(int startPage) {
        Intent intent = new Intent(RiwayatListActivity.this, QuranActivityDev.class);
        intent.putExtra("page",startPage);
        startActivity(intent);
    }

    public List<Riwaya> getRiwayatList() {
        List<Riwaya> list = new ArrayList<>();
        list.add(new Riwaya(1,"المصحف التفاعلي",RiwayaType.HAFS_SMART.name(),"",QuranWarchIMAGE_LINK,604));
        list.add(new Riwaya(2,"المصحف مع التفسير",RiwayaType.TAFSIR_QURAN.name(),"",QuranWarchIMAGE_LINK,604));
        list.add(new Riwaya(3,"القران برواية ورش",RiwayaType.WARCH.name(),"",QuranWarchIMAGE_LINK,604));
        list.add(new Riwaya(4,"القران برواية حفص",RiwayaType.HAFS.name(),"",QuranHafsIMAGE_LINK,604));
        list.add(new Riwaya(5,"مصحف التجويد",RiwayaType.WARCH_TAJWID.name(),"",QuranWARCH_TAJWID_IMAGE_LINK,604));
        list.add(new Riwaya(6,"القران باللغة الفرنسية",RiwayaType.FRENCH_QURAN.name(),"",QuranWarchIMAGE_LINK,604));
        list.add(new Riwaya(7,"القران بالغة الانجليزية",RiwayaType.ENGLISH_QURAN.name(),"",QuranWarchIMAGE_LINK,604));

        return list;
    }

    public void initializeJuzAdapter(List<Riwaya> list) {
        adapterJuza = new RiwayatAdapter(this,list,this::moveToMushaf);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapterJuza);
    }

    private void moveToMushaf(Riwaya riwaya) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        prefs.edit().putString("riwaya",new Gson().toJson(riwaya,Riwaya.class)).apply();
        OpenMushaf(startPage);
    }

}



