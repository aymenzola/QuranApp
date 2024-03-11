package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.HadithDetailsParte;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentHadithDetailBinding;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.chaptreParte.ChapterUtils;
import com.app.dz.quranapp.ui.activities.searchParte.SearchActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityHadithDetailsList extends AppCompatActivity {


    private List<BookWithCount> bookWithCountList = new ArrayList<>();
    private HadithViewModel viewModel;
    private FragmentHadithDetailBinding binding;
    private int CurrantPosition = 0;
    private HadithsAdapter adapter;
    String collectionName = "";
    String bookNumber = "";
    private String bookName = "";
    private DrawerBookAdapter adapterDrawer;
    private List<Chapter> chaptersList = new ArrayList<>();
    private ArrayList<BookWithCount> originalBooksList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHadithDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.background_color));
        }

        viewModel = new ViewModelProvider(this).get(HadithViewModel.class);


        setObservers();

        Intent intent = getIntent();
        if (intent != null) {
            String chapterString = intent.getStringExtra("chapter");
            Chapter chapter = new Gson().fromJson(chapterString, Chapter.class);

            bookName = chapter.bookName;
            collectionName = chapter.collectionName;
            bookNumber = chapter.bookNumber;
            CurrantPosition = chapter.positionInChaptersList;


            initializeAdapter();
            viewModel.askForChaptersList(collectionName, bookNumber);
            //}

        }

        binding.includeDrawer.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 3) {
                    filter(s.toString());
                    binding.includeDrawer.imgClose.setVisibility(View.VISIBLE);
                    adapterDrawer.setSearchMode(true);
                } else if (s.length() == 0){
                    adapterDrawer.setSearchMode(false);
                    binding.includeDrawer.imgClose.setVisibility(View.GONE);
                    resetList();
                } else {
                    adapterDrawer.setSearchMode(false);
                    binding.includeDrawer.imgClose.setVisibility(View.GONE);
                }
            }
        });

        /**this book used for drawer **/
        viewModel.askForBooksWithChaptersList(collectionName);

        setListeners();

    }


    private void setListeners() {

        binding.imgSearch.setOnClickListener(v->{
            startActivity(new Intent(ActivityHadithDetailsList.this,SearchActivity.class));
        });

        binding.includeDrawer.imgClose.setOnClickListener(v->{
            binding.includeDrawer.searchEditText.setText("");
            resetList();
        });


        binding.imgMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.END); // Use GravityCompat.START for left-to-right locales
        });

        binding.cardSaveHadith.setOnClickListener(v -> {

            Chapter chapter = chaptersList.get(binding.viewpager.getCurrentItem());

            Log.e("quran_position_tag", "saving " + chapter.chapterId + " " + binding.viewpager.getCurrentItem() + " booknumber " + chapter.bookNumber);

            //we have to provide the saved chapter with this data
            //chapter.positionInChaptersList = binding.viewpager.getCurrentItem();
            chapter.bookName = bookName;
            chapter.collectionName = collectionName;

            if (ChapterUtils.isChapterSaved(ActivityHadithDetailsList.this, chapter)) {
                //here we should remove the chapter from the saved chapters with ChapterUtils
                Glide.with(this).load(R.drawable.ic_unsaved_new).into(binding.imgSave);
                chapter.isSaved = false;
                ChapterUtils.updateChapter(ActivityHadithDetailsList.this, chapter);
                Toast.makeText(ActivityHadithDetailsList.this, "تم الغاء حفظ الحديث", Toast.LENGTH_SHORT).show();

            } else {
                Glide.with(this).load(R.drawable.ic_saved_new).into(binding.imgSave);
                chapter.isSaved = true;
                ChapterUtils.updateChapter(ActivityHadithDetailsList.this, chapter);
                ChapterUtils.saveLastChapter(ActivityHadithDetailsList.this, chapter);
                Toast.makeText(ActivityHadithDetailsList.this, "تم حفظ الحديث", Toast.LENGTH_SHORT).show();
            }




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

        binding.viewpager.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Chapter chapter = chaptersList.get(position);
                if (ChapterUtils.isChapterSaved(ActivityHadithDetailsList.this, chapter)) {
                    Glide.with(ActivityHadithDetailsList.this).load(R.drawable.ic_saved_new).into(binding.imgSave);
                } else {
                    Glide.with(ActivityHadithDetailsList.this).load(R.drawable.ic_unsaved_new).into(binding.imgSave);
                }
            }
        });
    }

    private void getHadithList(String collectionName, String bookNumber) {
        Log.e("quran_tag", "asking for collectionName : " + collectionName + " book number " + bookNumber);
        getHadiths(collectionName, bookNumber);
    }

    private void setObservers() {

        /**here we will receive chapters list for first Activity open , list used to rank hadiths as chapters**/
        viewModel.getChaptersList().observe(ActivityHadithDetailsList.this, chapterList -> {
            if (chapterList != null && chapterList.size() > 0) {
                chaptersList = chapterList;
                for (Chapter chapter : chapterList) {
                    Log.e("quran_position_tag", "chapter " + chapter.chapterId);
                }
                //now after we prepare the chapters list we can ask for the hadiths
                if (adapter.getItemCount() == 0) getHadithList(collectionName, bookNumber);
            }
        });
        /**before receive this we should alreay have or received chapters list **/
        viewModel.getHadithListOfBook().observe(ActivityHadithDetailsList.this, hadithList -> {
            if (hadithList != null && hadithList.size() > 0) displayData(hadithList);
        });

        /***this is Books items contains chpaters as child items used for the drawer **/
        viewModel.getBooksWithChaptersList().observe(this, bookList -> {
            if (bookList != null && bookList.size() > 0) {
                bookWithCountList = bookList;
                this.originalBooksList = new ArrayList<>(bookList);

                adapterDrawer = new DrawerBookAdapter(bookList,(chapter,position,chapterListCurrent) -> {

                    //hide drawer
                    binding.drawerLayout.closeDrawer(GravityCompat.END);

                    bookName = chapter.bookName;
                    collectionName = chapter.collectionName;
                    bookNumber = chapter.bookNumber;
                    CurrantPosition = position;

                    chaptersList = chapterListCurrent;

                    initializeAdapter();
                    Log.e("quran_tag", "asking for hadith for bookName : " + bookName);
                    getHadiths(collectionName, bookNumber);


                });
                binding.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.nestedRecyclerView.setAdapter(adapterDrawer);
            }
        });
    }

    public void getHadiths(String collectionName, String bookNumber) {
        viewModel.setHadith(collectionName, bookNumber);
    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    /**
     * this is used to group hadiths body's by chapterId, so each Hadith will contains all hadiths of its chapter chapter, as string
     * then display each Hadith in one item
     **/
    public void displayData(List<Hadith> hadithList) {
        Map<String, Hadith> groupedHadiths = new HashMap<>();

        for (Hadith hadith : hadithList) {
            if (groupedHadiths.containsKey(hadith.chapterId)) {
                Hadith existingHadith = groupedHadiths.get(hadith.chapterId);
                if (existingHadith != null) {
                    existingHadith.body += "\n" + hadith.body;
                }
            } else {
                groupedHadiths.put(hadith.chapterId, hadith);
            }
        }

        for (Hadith hadith : groupedHadiths.values()) {
            Log.e("quran_position_tag", "hadiths before rank " + hadith.chapterId);
        }

        List<Hadith> newRankedList = rankHadiths(chaptersList, new ArrayList<>(groupedHadiths.values()));

        for (Hadith hadith : newRankedList) {
            Log.e("quran_position_tag", "hadiths after rank " + hadith.chapterId);
        }

        adapter.setItems(newRankedList);

        Log.e("quran_position_tag", "hadiths set current item  " + CurrantPosition + " " + adapter.getHadithAtPosition(CurrantPosition).chapterId
                + " " + adapter.getHadithAtPosition(CurrantPosition).chapterTitle_no_tachkil + " book name ");


        binding.viewpager.setCurrentItem(CurrantPosition);

        Log.e("lifecycle", "we recieve hadiths  size " + hadithList.size());
    }


    /**
     * this is used to rank hadiths as chapters , using chapters list
     **/
    public List<Hadith> rankHadiths(List<Chapter> chaptersList, List<Hadith> hadithList) {
        Map<String, Hadith> hadithMap = new HashMap<>();
        for (Hadith hadith : hadithList) {
            hadithMap.put(hadith.chapterId, hadith);
        }

        List<Hadith> rankedHadiths = new ArrayList<>();
        for (Chapter chapter : chaptersList) {
            Hadith hadith = hadithMap.get(chapter.chapterId);
            if (hadith != null) {
                rankedHadiths.add(hadith);
            }
        }

        return rankedHadiths;
    }


    private void initializeAdapter() {
        String destination = PublicMethods.getInstance().getCollectionArabicName(collectionName) + " > " + bookName + " > ";
        Log.e("quran_tag", "destination " + destination);

        adapter = new HadithsAdapter(destination, ActivityHadithDetailsList.this, model -> {

        });

        binding.viewpager.setOrientation(ORIENTATION_HORIZONTAL);
        binding.viewpager.setAdapter(adapter);
    }




    /**
     * used to filter chapters and books by name
     **/
    private void filter(String query) {
        List<BookWithCount> booksList = new ArrayList<>();
        for (BookWithCount parentBook : bookWithCountList) {
            List<Chapter> filteredChaptersList = new ArrayList<>();
            for (Chapter childItem : parentBook.chaptersList) {
                if (childItem.chapterTitle_no_tachkil.toLowerCase().contains(query.toLowerCase())) {
                    filteredChaptersList.add(childItem);
                }
            }
            if (!filteredChaptersList.isEmpty() || parentBook.bookName.toLowerCase().contains(query.toLowerCase())) {
                parentBook.chaptersList = filteredChaptersList;
                booksList.add(parentBook);
            }
        }
        adapterDrawer.filterList(booksList);
    }

    public void resetList() {
        Log.e("quran_tag", "resetList size " + originalBooksList.size());
        adapterDrawer.setList(originalBooksList);
    }

}



