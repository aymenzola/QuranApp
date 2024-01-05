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


public class ActivityHadithDetailsList extends AppCompatActivity {


    private static final Integer FIRST_TYPE = 0;
    private static final Integer NEXT_TYPE = 1;
    private boolean islastData = false;
    private HadithViewModel viewModel;
    private OnListenerInterface listener;
    private FragmentHadithDetailBinding binding;
    private Integer lastid = null;
    private List<Chapter> chaptersList;
    private int CurrantPosition = 0;
    private ViewPager2.OnPageChangeCallback onPageChangeCallback;
    private HadithsAdapter adapter;
    String collectionName = "";
    String bookNumber = "";
    private String chapterIdCurrent = "";
    private int lastItemInlist;
    private int viewpagerStartPosition = 0;
    private int FirstItemInlist = 0;
    private int addtype = NEXT_TYPE;
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
            chapterIdCurrent = bundle.getString("chapterId");
            CurrantPosition = bundle.getInt("position");
            chaptersList = bundle.getParcelableArrayList("chaptersList");
        }

        Log.e("lifecycle", "B onViewCreated " + CurrantPosition);


        binding.included.imgBack.setOnClickListener(v -> onBackPressed());

        binding.included.imgSearch.setOnClickListener(v -> {
            Intent intent1 = new Intent(ActivityHadithDetailsList.this, SearchActivity.class);
            startActivity(intent1);
        });

        onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.e("lifecycle", "onPageSelected " + position);
                if (position == 0) {
                    // Notify fragment to load data at the beginning
                    Log.e("lifecycle", "positio is 0 we need call load back");
                    loadBackItems(collectionName,bookNumber);
                } else if (position == adapter.getItemCount() - 1) {
                    // Notify fragment to load data at the end
                    Log.e("lifecycle", "we call load next at " + position);
                    loadNextItems(collectionName, bookNumber);
                }
            }
        };

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
            Gson gson = new Gson();
            String json_chaptersList = gson.toJson(chaptersList);
            editor.putString("json_chaptersList", json_chaptersList);
            editor.putString("collectionName", collectionName);
            editor.putString("bookName", bookName);
            editor.putString("chapterIdCurrent", chapterIdCurrent);
            editor.putString("bookNumber", bookNumber);
            editor.putString("chapterName", hadith.chapterTitle_no_tachkil);
            editor.putString("destination", destination);
            editor.putInt("CurrantPosition", binding.viewpager.getCurrentItem());

            String jsonHadith = gson.toJson(hadith);
            editor.putString("jsonHadith", jsonHadith);

            editor.apply();


            Toast.makeText(ActivityHadithDetailsList.this, "تم حفظ الحديث", Toast.LENGTH_SHORT).show();
            /*

            // Retrieve the JSON string from SharedPreferences
String json = sharedPreferences.getString("myObject", null);

// Convert the JSON string back to a MyObject instance
Gson gson = new Gson();
MyObject myObject = gson.fromJson(json, MyObject.class);

            String json2 = sharedPreferences.getString("myObjects", null);

            Gson gson2 = new Gson();
            Type type = new TypeToken<ArrayList<Chapter>>() {
            }.getType();
            ArrayList<Chapter> myObjects = gson.fromJson(json, type);
*/
        });
        binding.cardNext.setOnClickListener(v -> {
            int i = binding.viewpager.getCurrentItem();
            if (i == chaptersList.size() - 1) return;
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
        List<String> chaptersIds = new ArrayList<>();
        int max = Math.min(CurrantPosition + 5, chaptersList.size());
//        viewpagerStartPosition = max < chaptersList.size() ? (max - 5) : (chaptersList.size() - CurrantPosition);
        int min = Math.max(CurrantPosition - 5, 0);
        lastItemInlist = max;
        FirstItemInlist = min;
        viewpagerStartPosition = CurrantPosition-min;

        Log.e("lifecycle", "load items max " + max + " min " + min+" viewpagerStartPosition "+ viewpagerStartPosition);
        for (int i = min; i < max; i++) {
            chaptersIds.add(chaptersList.get(i).chapterId);
        }
        getHadiths(collectionName, bookNumber, chaptersIds);
    }

    private void loadNextItems(String collectionName, String bookNumber) {
        List<String> chaptersIds = new ArrayList<>();
        int max = Math.min(lastItemInlist + 5, chaptersList.size());
        for (int i = lastItemInlist; i < max; i++) {
            chaptersIds.add(chaptersList.get(i).chapterId);
        }
        lastItemInlist = max;
        Log.e("lifecycle", "next max " + max);
        addtype = NEXT_TYPE;
        getHadiths(collectionName, bookNumber, chaptersIds);
    }

    private void loadBackItems(String collectionName, String bookNumber) {
        List<String> chaptersIds = new ArrayList<>();
        int min = Math.max(FirstItemInlist - 5, 0);
        for (int i = FirstItemInlist; i > min; i--) {
            chaptersIds.add(chaptersList.get(i).chapterId);
        }
        FirstItemInlist = min;
        Log.e("lifecycle", "back min " + min);
        addtype = FIRST_TYPE;
        getHadiths(collectionName, bookNumber, chaptersIds);
    }

    private void setObservers() {
        viewModel.getHadithObject().observe(ActivityHadithDetailsList.this, hadithList -> {
            if (hadithList != null && hadithList.size() > 0) displayData(hadithList);
        });
    }

    public void getHadiths(String collectionName, String bookNumber, List<String> chapterIds) {
        viewModel.setHadith(collectionName, bookNumber, chapterIds);
    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    public void displayData(List<Hadith> hadithList) {
        Log.e("lifecycle", "we recieve hadiths  size " + hadithList.size());
        boolean isStart = adapter.getItemCount() == 0;
        if (addtype == NEXT_TYPE) adapter.addItems(hadithList);
        else if (addtype == FIRST_TYPE) {
            adapter.addItemsAtFirst(hadithList);
            /*int p = CurrantPosition - FirstItemInlist;
            if (p >= 0 && p < adapter.getItemCount()) {
                Log.e("lifecycle", "move adapter to "+p);
                binding.viewpager.setCurrentItem(p);
            }*/
        }
        if (isStart) binding.viewpager.setCurrentItem(viewpagerStartPosition);

    }

    private void initilizeAdapter() {
        destination = getCollectionArabicName(collectionName) + " > " + bookName + " > ";
        adapter = new HadithsAdapter(destination, ActivityHadithDetailsList.this, model -> {
        });
        binding.viewpager.setOrientation(ORIENTATION_HORIZONTAL);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.viewpager.unregisterOnPageChangeCallback(onPageChangeCallback);
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



