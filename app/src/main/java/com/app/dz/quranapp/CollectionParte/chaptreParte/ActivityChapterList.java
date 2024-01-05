package com.app.dz.quranapp.CollectionParte.chaptreParte;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.CollectionParte.HadithDetailsParte.ActivityHadithDetailsListDev;
import com.app.dz.quranapp.Entities.Chapter;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentBookListBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityChapterList extends AppCompatActivity {


    private boolean islastData = false;
    private ChaptersAdapter adapter;
    private ChaptersViewModel viewModel;
    private OnListenerInterface listener;
    private FragmentBookListBinding binding;
    private Integer lastid = null;
    private String collectionName;
    private String bookName="";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentBookListBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ChaptersViewModel.class);

        Intent intent = getIntent();
        collectionName = "";
        String bookNumber = "";
        if (intent != null) {
            collectionName = intent.getStringExtra("collectionName");
            bookNumber = intent.getStringExtra("bookNumber");
            bookName = intent.getStringExtra("bookName");
        }


        binding.included.imgSearch.setVisibility(View.GONE);
        binding.included.tvTitle.setText(""+bookName);
        binding.included.imgBack.setOnClickListener(v -> onBackPressed());
        initializeArticlesAdapter();
        islastData = false;
        Log.e("lifecycle", "B onViewCreated");
        getChapters(collectionName, bookNumber);
        setObservers();

    }



    private void setObservers() {
        viewModel.getchaptersObject().observe(ActivityChapterList.this, chapterList -> {
            if (chapterList != null && chapterList.size() > 0) displayData(chapterList);
        });
    }

    public void initializeArticlesAdapter() {
        adapter = new ChaptersAdapter(ActivityChapterList.this,this::moveToAyatFragment);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(ActivityChapterList.this, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }

    private void moveToAyatFragment(Chapter chapter, int position) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, ActivityHadithDetailsListDev.class);
        bundle.putString("collectionName",collectionName);
        bundle.putString("bookNumber",chapter.bookNumber);
        bundle.putString("chapterId",chapter.chapterId);
        bundle.putString("bookName",bookName);
        bundle.putInt("position", position);
        bundle.putParcelableArrayList("chaptersList", (ArrayList<? extends Parcelable>) adapter.getArrayList());

        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }

    public void getChapters(String collectionName, String bookNumber) {
        viewModel.setChaptersObject(collectionName, bookNumber);
    }

    private void displayData(List<Chapter> items) {
        adapter.setItems(items);
    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    private void manageBooksObject(Object chapterObject) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.toJsonTree(chapterObject).getAsJsonObject();
        JsonArray jsonChapter = jsonObject.getAsJsonArray("data");

        Log.e("books", "" + jsonChapter);

        List<Chapter> chapterList = new ArrayList<>();

        for (int i = 0; i < jsonChapter.size(); i++) {
            JsonObject chapter = jsonChapter.get(i).getAsJsonObject();
            String bookNumber = chapter.get("bookNumber").getAsString();
            String chapterId = chapter.get("chapterId").getAsString();

            JsonArray chapterArray = chapter.getAsJsonArray("chapter");

            String chapterTitle = chapterArray.get(1).getAsJsonObject().get("chapterTitle").getAsString();

            chapterList.add(new Chapter(chapterId, chapterTitle, bookNumber));

            Log.e("books", "" + bookNumber + " name " + chapterTitle);

        }
        Log.e("books", "chapterList.size() = " + chapterList.size());
        displayData(chapterList);

    }
}



