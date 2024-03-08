package com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.databinding.FragmentChaptersListBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityChapterList extends AppCompatActivity {


    private ChaptersAdapter adapter;
    private ChaptersViewModel viewModel;
    private FragmentChaptersListBinding binding;
    private String collectionName;
    private String bookName = "";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentChaptersListBinding.inflate(getLayoutInflater());

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

        Log.e("quran_tag","collection_name "+collectionName+" booknumber "+bookNumber+" bookname "+bookName);

        binding.includeCategoryAdkarCard.tvFastAdkarTitle.setText("" + bookName);
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        initializeArticlesAdapter();
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
        adapter = new ChaptersAdapter(new ChaptersAdapter.OnAdapterClickListener() {
            @Override
            public void onItemClick(Chapter model, int position) {
                model.bookName = bookName;
                model.collectionName = collectionName;
                model.positionInChaptersList = position;
                moveToAyatFragment(model, position);
            }

            @Override
            public void onItemSaveClick(Chapter model, boolean isSaved,int position) {
                model.bookName = bookName;
                model.collectionName = collectionName;
                model.positionInChaptersList = position;
                ChapterUtils.updateChapter(ActivityChapterList.this,model);
                ChapterUtils.saveLastChapter(ActivityChapterList.this,model);
            }
        });

        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setLayoutManager(new LinearLayoutManager(ActivityChapterList.this, RecyclerView.VERTICAL, false));
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setHasFixedSize(true);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setAdapter(adapter);
    }

    private void moveToAyatFragment(Chapter chapter, int position) {
        chapter.positionInChaptersList = position;
        chapter.collectionName = collectionName;
        chapter.bookName = bookName;
        ChapterUtils.moveToChapterDetails(ActivityChapterList.this, chapter);
    }

    public void getChapters(String collectionName, String bookNumber) {
        viewModel.setChaptersObject(collectionName, bookNumber);
    }

    private void displayData(List<Chapter> items) {
        List<Chapter> savedChapters = ChapterUtils.getSavedChapters(this);
        for (Chapter item : items) {
            for (Chapter savedChapter : savedChapters) {
                if (item.getCompleteChapterId().equals(savedChapter.getCompleteChapterId())) {
                    item.isSaved = savedChapter.isSaved;
                    break;
                }
            }
        }
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

//            chapterList.add(new Chapter(chapterId, chapterTitle, bookNumber));

            Log.e("books", "" + bookNumber + " name " + chapterTitle);

        }
        Log.e("books", "chapterList.size() = " + chapterList.size());
        displayData(chapterList);

    }
}



