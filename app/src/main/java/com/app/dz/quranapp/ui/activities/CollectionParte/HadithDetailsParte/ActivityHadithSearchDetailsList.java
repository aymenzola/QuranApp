package com.app.dz.quranapp.ui.activities.CollectionParte.HadithDetailsParte;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.databinding.ActivitySearchedHadithDetailBinding;
import com.app.dz.quranapp.ui.activities.searchParte.SearchActivity;
import com.google.gson.Gson;

import java.util.List;


public class ActivityHadithSearchDetailsList extends AppCompatActivity {

    private HadithViewModel viewModel;
    private ActivitySearchedHadithDetailBinding binding;
    private Chapter chapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchedHadithDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        viewModel = new ViewModelProvider(this).get(HadithViewModel.class);


        setObservers();

        Intent intent = getIntent();
        if (intent != null) {
            String chapterString = intent.getStringExtra("chapter");
            chapter = new Gson().fromJson(chapterString, Chapter.class);

            viewModel.setBookName(chapter.bookNumber,chapter.collectionName);

        }

        setListeners();

    }


    private void setListeners() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }


    private void setObservers() {

        viewModel.getBookName().observe(this,bookNamee->{
            if (bookNamee!=null){
                chapter.bookName = bookNamee;
                viewModel.askHadithListOfChapter(chapter.collectionName,chapter.bookNumber,chapter.chapterId);
            }
        });

        viewModel.getHadithListOfChapter().observe(ActivityHadithSearchDetailsList.this, hadithList -> {
            if (hadithList != null && hadithList.size() > 0) displayData(hadithList);
        });
    }


    public void displayData(List<Hadith> hadithList) {
        StringBuilder hadithBody = new StringBuilder();

        for (Hadith hadith : hadithList) hadithBody.append(hadith.body);

        String destination = PublicMethods.getInstance().getCollectionArabicName(chapter.collectionName) + " > " + chapter.bookName + " > ";
        binding.includeHadithDetail.tvDestination.setText(destination);
        binding.includeHadithDetail.tvChapter.setText(chapter.chapterTitle_no_tachkil);
        String myData = "<html><body style='text-align:right;'>" + hadithBody + "</body></html>";

        binding.includeHadithDetail.webView.loadData(myData, "text/html", "UTF-8");

    }


}



