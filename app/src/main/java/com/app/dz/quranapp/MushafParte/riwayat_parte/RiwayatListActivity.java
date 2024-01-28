package com.app.dz.quranapp.MushafParte.riwayat_parte;

import static com.app.dz.quranapp.Constants.QuranHafsIMAGE_LINK;
import static com.app.dz.quranapp.Constants.QuranWARCH_TAJWID_IMAGE_LINK;
import static com.app.dz.quranapp.Constants.QuranWarchIMAGE_LINK;
import static com.app.dz.quranapp.Constants.Quran_ENGLISH_IMAGE_LINK;
import static com.app.dz.quranapp.Constants.Quran_FRECH_IMAGE_LINK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Riwaya;
import com.app.dz.quranapp.MushafParte.QuranActivityDev;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ActivityRiwayatListBinding;
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
        list.add(new Riwaya(1,"المصحف التفاعلي حفص",RiwayaType.HAFS_SMART.name(),"",QuranWarchIMAGE_LINK,604));
        list.add(new Riwaya(2,"المصحف مع التفسير حفص",RiwayaType.TAFSIR_QURAN.name(),"",QuranWarchIMAGE_LINK,604));
        list.add(new Riwaya(3,"القرآن برواية ورش",RiwayaType.WARCH.name(),"",QuranWarchIMAGE_LINK,604));
        list.add(new Riwaya(4,"القرآن برواية حفص",RiwayaType.HAFS.name(),"",QuranHafsIMAGE_LINK,604));
        list.add(new Riwaya(5,"مصحف التجويد حفص",RiwayaType.HAFS_TAJWID.name(),"",QuranWARCH_TAJWID_IMAGE_LINK,604));
        list.add(new Riwaya(6,"القرآن باللغة الفرنسية",RiwayaType.FRENCH_QURAN.name(),"",Quran_FRECH_IMAGE_LINK,604));
        list.add(new Riwaya(7,"القرآن بالغة الانجليزية",RiwayaType.ENGLISH_QURAN.name(),"",Quran_ENGLISH_IMAGE_LINK,604));

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



