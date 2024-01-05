package com.app.dz.quranapp.CollectionParte.HadithDetailsParte;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.app.dz.quranapp.Entities.Chapter;
import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentHadithDetailBinding;
import com.app.dz.quranapp.quran.searchParte.SearchActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class ActivityHadithDetailsListDev extends AppCompatActivity {


    private HadithViewModel viewModel;
    private FragmentHadithDetailBinding binding;
    private int CurrantPosition = 0;
    private HadithsAdapter adapter;
    String collectionName = "";
    String bookNumber = "";
    private String bookName = "";
    private String destination = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHadithDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        viewModel = new ViewModelProvider(this).get(HadithViewModel.class);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            bookName = bundle.getString("bookName");
            collectionName = bundle.getString("collectionName");
            bookNumber = bundle.getString("bookNumber");
            CurrantPosition = bundle.getInt("position");
        }

        Log.e("lifecycle", " data recieved data " + CurrantPosition+" b number :"+bookNumber+" collection "+collectionName);

        binding.included.imgBack.setOnClickListener(v -> onBackPressed());

        binding.included.imgSearch.setVisibility(View.GONE);
        /*binding.included.imgSearch.setOnClickListener(v -> {
            Intent intent1 = new Intent(ActivityHadithDetailsListDev.this, SearchActivity.class);
            startActivity(intent1);
        });*/

        initilizeAdapter();
        loadItems(collectionName, bookNumber);
        setObservers();

        setListeners();

    }


    private void setListeners() {
        binding.cardSaveHadith.setOnClickListener(v -> {
            Hadith hadith = adapter.getItem(binding.viewpager.getCurrentItem());
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("collectionName", collectionName);
            editor.putString("bookName", bookName);
            editor.putString("bookNumber", bookNumber);
            editor.putString("chapterName", hadith.chapterTitle_no_tachkil);
            editor.putString("destination", destination);
            editor.putInt("CurrantPosition", binding.viewpager.getCurrentItem());

            editor.apply();

            Toast.makeText(ActivityHadithDetailsListDev.this, "تم حفظ الحديث", Toast.LENGTH_SHORT).show();

        });
        binding.cardNext.setOnClickListener(v -> {
            int i = binding.viewpager.getCurrentItem();
            if (i == adapter.getItemCount() - 1) return;
            binding.viewpager.setCurrentItem(i + 1);
        });
        binding.cardBack.setOnClickListener(v -> {
            int i = binding.viewpager.getCurrentItem();
            if (i == 0) return;
            binding.viewpager.setCurrentItem(i - 1);
        });
        binding.included.tvTitle.setText("الاحاديث");
        binding.included.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadItems(String collectionName, String bookNumber) {
        getHadiths(collectionName, bookNumber);
    }

    private void setObservers() {
        viewModel.getHadithObject().observe(ActivityHadithDetailsListDev.this, hadithList -> {
            if (hadithList != null && hadithList.size() > 0) displayData(hadithList);
        });
    }

    public void getHadiths(String collectionName, String bookNumber) {
        viewModel.setHadith(collectionName, bookNumber);
    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    public void displayData(List<Hadith> hadithList) {
        Log.e("lifecycle", "we recieve hadiths  size " + hadithList.size());
        adapter.addItemsAtFirst(hadithList);
        binding.viewpager.setCurrentItem(CurrantPosition);

    }

    private void initilizeAdapter() {
        destination = getCollectionArabicName(collectionName) + " > " + bookName + " > ";
        adapter = new HadithsAdapter(destination, ActivityHadithDetailsListDev.this, model -> {
        });
        binding.viewpager.setOrientation(ORIENTATION_HORIZONTAL);
        binding.viewpager.setAdapter(adapter);
    }


    public String getCollectionArabicName(String collectionName) {
        switch (collectionName) {
            case "bukhari":
                return "صحيح البخاري";
            case "muslim":
                return "صحيح مسلم";
            case "nasai":
                return "سنن النسائي";
            case "ibnmajah":
                return "سنن ابن ماجة";
            case "hisn":
                return "حصن المسلم";
            default:
                return "سنن أبي داود";
        }
    }

}



