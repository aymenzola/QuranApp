package com.app.dz.quranapp.ui.activities.searchParte;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.ui.activities.CollectionParte.HadithDetailsParte.ActivityHadithDetailsListDev;
import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentSearchBinding;
import com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte.ActivityChapterList;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    public static final int SEARCH_TYPE_HADITH = 0;
    public static final int SEARCH_TYPE_BOOK = 1;
    public static final int SEARCH_TYPE_CHAPTER = 2;

    public static final int searchPageSize = 10; // Maximum number of items to return in each page
    public int currentPage = 0; // Index of the current page

    private boolean areWeAskForBookName = false;
    private com.app.dz.quranapp.databinding.FragmentSearchBinding binding;
    private SearchAdapter adapter;
    private SearchViewModel searchViewModel;
    private int selectedType = SEARCH_TYPE_BOOK;


    private OnFragmentInteractionListener mListener;
    private List<String> suggestionList = new ArrayList<>();
    private Handler handler = new Handler();
    private String searchedText;
    private boolean isLastData = false;
    private String clickedCollection;
    private Chapter clickedChapter;
    private Hadith clickedHadith;
    private String clickedBookName;
    private OnBackPressedDispatcher dispatcher;

    @Override
    public void onStart() {
        super.onStart();
        switch (selectedType) {
            case SEARCH_TYPE_BOOK:
                selecteBookType();
                break;
            case SEARCH_TYPE_HADITH:
                selecteHadithType();
                break;
            case SEARCH_TYPE_CHAPTER:
                selecteChapterType();
                break;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editSearch, InputMethodManager.SHOW_IMPLICIT);

        initializeArticlesAdapter();

        setObservers();

        setListeners();
        dispatcher = this.getOnBackPressedDispatcher();

    }

    private final Runnable searchRunnable = this::runTheSearch;

    private void setListeners() {



        binding.imgBack.setOnClickListener(v->dispatcher.onBackPressed());
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("searchtag", "onTextChanged ");
                if (binding.editSearch.length() > 0) {
                    if (binding.itemClearClickParent.getVisibility() != View.VISIBLE) {
                        binding.itemClearClickParent.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.itemClearClickParent.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 3) prepareSearch(s.toString());
            }
        });

        binding.editSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.editSearch.getText().toString().length() >= 3) {
                    // search icon clicked
                    binding.editSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(binding.editSearch.getWindowToken(), 0);

                    binding.recyclerview.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.tvNoResult.setVisibility(View.GONE);
                    searchedText = binding.editSearch.getText().toString();
                    runTheSearch();
                    Log.e("searchtag", "search icon clicked " + binding.editSearch.getText().toString());

                    return true;
                } else {
                    Toast.makeText(SearchActivity.this, "النص قصير", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });

        binding.itemClearClickParent.setOnClickListener(v -> binding.editSearch.getText().clear());

        binding.btnBooks.setOnClickListener(v -> {
            adapter.clear();
            isLastData = true;
            currentPage = 0;
            selecteBookType();
            selectedType = SEARCH_TYPE_BOOK;
            if (binding.editSearch.getText().toString().length() >= 3)
                prepareSearch(binding.editSearch.getText().toString());
        });
        binding.btnHadiths.setOnClickListener(v -> {
            currentPage = 0;
            adapter.clear();
            isLastData = true;
            selecteHadithType();
            selectedType = SEARCH_TYPE_HADITH;
            if (binding.editSearch.getText().toString().length() >= 3)
                prepareSearch(binding.editSearch.getText().toString());
        });
        binding.btnChapters.setOnClickListener(v -> {
            currentPage = 0;
            adapter.clear();
            isLastData = true;
            selecteChapterType();
            selectedType = SEARCH_TYPE_CHAPTER;
            if (binding.editSearch.getText().toString().length() >= 3)
                prepareSearch(binding.editSearch.getText().toString());
        });
    }

    private void runTheSearch() {
        switch (selectedType) {
            case SEARCH_TYPE_BOOK:
                if (currentPage == 0) {
                    Log.e("search", "currant page 0");
                    currentPage++;
                    searchViewModel.searchInBook(binding.editSearch.getText().toString(), 0);
                } else {
                    int i = ++currentPage * searchPageSize;
                    Log.e("search", "books currant page " + i + " query : " + binding.editSearch.getText().toString());
                    searchViewModel.searchInBook(binding.editSearch.getText().toString(), i);
                }
                break;
            case SEARCH_TYPE_HADITH:
                if (currentPage == 0) {
                    currentPage++;
                    searchViewModel.searchInHadith(binding.editSearch.getText().toString(), 0);
                } else {
                    int i = ++currentPage * searchPageSize;
                    Log.e("search", "hadith currant page " + i + " query : " + binding.editSearch.getText().toString());
                    searchViewModel.searchInHadith(binding.editSearch.getText().toString(), i);
                }
                break;
            case SEARCH_TYPE_CHAPTER:

                if (currentPage == 0) {
                    currentPage++;
                    searchViewModel.searchInChapter(binding.editSearch.getText().toString(), 0);
                } else {
                    int i = ++currentPage * searchPageSize;
                    Log.e("search", "chapter currant page " + i + " query : " + binding.editSearch.getText().toString());
                    searchViewModel.searchInChapter(binding.editSearch.getText().toString(), i);

                }
                break;
        }
    }

    private void selecteChapterType() {
        binding.btnBooks.setTextColor(getResources().getColor(R.color.white));
        binding.btnHadiths.setTextColor(getResources().getColor(R.color.white));
        binding.btnChapters.setTextColor(getResources().getColor(R.color.purple_700));

        binding.btnBooks.setBackgroundResource(R.drawable.rounde_button);
        binding.btnHadiths.setBackgroundResource(R.drawable.rounde_button);
        binding.btnChapters.setBackgroundResource(R.drawable.rounde_button_selected);
    }

    private void selecteHadithType() {
        binding.btnBooks.setTextColor(getResources().getColor(R.color.white));
        binding.btnHadiths.setTextColor(getResources().getColor(R.color.purple_700));
        binding.btnChapters.setTextColor(getResources().getColor(R.color.white));

        binding.btnBooks.setBackgroundResource(R.drawable.rounde_button);
        binding.btnHadiths.setBackgroundResource(R.drawable.rounde_button_selected);
        binding.btnChapters.setBackgroundResource(R.drawable.rounde_button);
    }

    private void selecteBookType() {
        binding.btnBooks.setTextColor(getResources().getColor(R.color.purple_700));
        binding.btnHadiths.setTextColor(getResources().getColor(R.color.white));
        binding.btnChapters.setTextColor(getResources().getColor(R.color.white));

        binding.btnBooks.setBackgroundResource(R.drawable.rounde_button_selected);
        binding.btnHadiths.setBackgroundResource(R.drawable.rounde_button);
        binding.btnChapters.setBackgroundResource(R.drawable.rounde_button);
    }

    private void prepareSearch(String s) {
        manageStateView(View.GONE, View.VISIBLE, View.GONE);
        handler.removeCallbacks(searchRunnable);
        handler.postDelayed(searchRunnable, 2000);
        searchedText = s;
    }


    private void setObservers() {


        searchViewModel.getBookName().observe(SearchActivity.this,bookName->{
            if (bookName!=null){
                if (areWeAskForBookName){
                    clickedBookName = bookName;
                    searchViewModel.setHadithRank(clickedHadith.id,clickedHadith.bookNumber,clickedHadith.collection);
                }
            }
        });

        searchViewModel.getHadithRank().observe(SearchActivity.this,rank->{
            if (areWeAskForBookName) {
                areWeAskForBookName = false;
                moveToHadithFragment(clickedChapter, clickedCollection, clickedBookName,rank);
            }
            });
        searchViewModel.getSearchInHadith().observe(SearchActivity.this, hadithList -> {

            if (hadithList.size() > 0 & binding.editSearch.getText().toString().length() >= 3)
                manageStateView(View.VISIBLE, View.GONE, View.GONE);
            else manageStateView(View.GONE, View.GONE, View.VISIBLE);

            for (Hadith hadith : hadithList) {
                hadith.body_no_tachkil = hadith.body_no_tachkil.replaceAll("[a-zA-Z]", "");

                int start = hadith.body_no_tachkil.indexOf(searchedText);
                int end = start + searchedText.length();


                /*String html = "<span style='background-color: yellow'>"+searchedText+"</span>";
                hadith.body_no_tachkil = hadith.body_no_tachkil.replaceFirst(searchedText,html);

                int start = hadith.body_no_tachkil.indexOf(html);
                int end  = start+html.length();
*/
                int subStart;
                int subEnd;
                if ((start) >= 120) subStart = start - 120;
                else subStart = 0;
                if ((hadith.body_no_tachkil.length() - end) >= 120) subEnd = end + 120;
                else subEnd = hadith.body_no_tachkil.length();

                String newString = hadith.body_no_tachkil.substring(subStart, subEnd);


                int newStart = newString.indexOf(searchedText);
                int newEnd = newStart + searchedText.length();


                SpannableStringBuilder builder = new SpannableStringBuilder(newString);

                BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.YELLOW);
                builder.setSpan(backgroundSpan, newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                hadith.stringBuilder = builder;

                //hadith.body_no_tachkil=hadith.body_no_tachkil.substring(subStart,subEnd);
                hadith.type = SEARCH_TYPE_HADITH;
            }
            Log.e("search", "hadiths size " + hadithList.size());
            if (hadithList.size() < searchPageSize) isLastData = true;
            adapter.addHadiths(hadithList);
            //
        });
        searchViewModel.getSearchInHadithChapter().observe(SearchActivity.this, hadithList -> {
            if (hadithList.size() > 0 & binding.editSearch.getText().toString().length() >= 3)
                manageStateView(View.VISIBLE, View.GONE, View.GONE);
            else manageStateView(View.GONE, View.GONE, View.VISIBLE);

            for (Hadith hadith : hadithList) hadith.type = SEARCH_TYPE_CHAPTER;

            if (hadithList.size() < searchPageSize) isLastData = true;
            Log.e("search", "chapters size " + hadithList.size());
            adapter.addHadiths(hadithList);
            //
        });
        searchViewModel.getSearchInBooks().observe(SearchActivity.this, bookList -> {
            if (bookList.size() > 0 & binding.editSearch.getText().toString().length() >= 3)
                manageStateView(View.VISIBLE, View.GONE, View.GONE);
            else manageStateView(View.GONE, View.GONE, View.VISIBLE);

            if (bookList.size() < searchPageSize) isLastData = true;
            Log.e("search", "books size " + bookList.size());
            adapter.addBooks(bookList);
        });
    }

    private void manageStateView(int value_recyclerview, int value_progressBar, int value_tvNoResult) {
        binding.recyclerview.setVisibility(value_recyclerview);
        binding.progressBar.setVisibility(value_progressBar);
        binding.tvNoResult.setVisibility(value_tvNoResult);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSearchBarClicked(String word, boolean isSuggestion);
    }


    public void initializeArticlesAdapter() {
        adapter = new SearchAdapter(SearchActivity.this, new SearchAdapter.OnAdapterClickListener() {
            @Override
            public void onHadithClick(Hadith hadith) {
                clickedHadith =hadith;
                clickedCollection = hadith.collection;
                clickedChapter =new Chapter(hadith.chapterId, hadith.chapterTitle,hadith.bookNumber,hadith.chapterTitle_no_tachkil);
                getBookNameToMove(clickedChapter,clickedCollection);
            }

            @Override
            public void onChapterClick(Hadith hadith) {
                clickedHadith =hadith;
                clickedCollection = hadith.collection;
                clickedChapter =new Chapter(hadith.chapterId, hadith.chapterTitle,hadith.bookNumber,hadith.chapterTitle_no_tachkil);
                getBookNameToMove(clickedChapter,clickedCollection);
            }

            @Override
            public void onBookClick(Book book) {
                moveToBookFragment(book);
            }
        });

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);


        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLastData && !binding.recyclerview.canScrollVertically(1)) {
                    Log.e("search", "load more data");
                    runTheSearch();
                }
            }
        });

    }

    private void moveToBookFragment(Book book) {
        Intent intent = new Intent(SearchActivity.this, ActivityChapterList.class);
        intent.putExtra("collectionName", book.bookCollection);
        intent.putExtra("bookNumber", book.bookNumber);
        intent.putExtra("bookName", book.bookName);
        startActivity(intent);
    }

    private void getBookNameToMove(Chapter chapter, String collectionName) {
        areWeAskForBookName = true;
        searchViewModel.setBookName(chapter.bookNumber,collectionName);
    }

    private void moveToHadithFragment(Chapter chapter, String collectionName, String bookName, Integer rank) {
        Log.e("lifecycle", "moveToHadithFragment data recieved data b number :"+chapter.bookNumber+" collection "+collectionName+ ""+bookName);

        Bundle bundle = new Bundle();
        Intent intent = new Intent(SearchActivity.this, ActivityHadithDetailsListDev.class);
        bundle.putString("collectionName", collectionName);
        bundle.putString("bookNumber", chapter.bookNumber);
        bundle.putString("bookName",bookName);
        bundle.putInt("position",rank);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }


}
