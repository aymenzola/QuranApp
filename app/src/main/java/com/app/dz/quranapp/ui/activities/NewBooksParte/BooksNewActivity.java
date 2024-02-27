package com.app.dz.quranapp.ui.activities.NewBooksParte;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ActivityBooksNewBinding;

import java.util.ArrayList;
import java.util.List;

public class BooksNewActivity extends AppCompatActivity {

    private DrawerBookAdapter adapter;
    private BooksNewViewModel viewModel;
    private String collectionName = "bukhari";
    private ActivityBooksNewBinding binding;
    private List<BookWithCount> bookWithCountList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_new);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_books_new);

        viewModel = new ViewModelProvider(this).get(BooksNewViewModel.class);

        /*
        Intent intent = getIntent();
        if (intent != null) collectionName = intent.getStringExtra("collectionName");*/

        setObservers();
        viewModel.askForBooksWithChaptersList(collectionName);


        EditText searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
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
                    adapter.setSearchMode(true);
                } else {
                    adapter.setSearchMode(false);
                }
                //adapter.setSearchMode(!s.toString().isEmpty());
            }
        });

    }


    private void setObservers() {
        viewModel.getBooksWithChaptersList().observe(this, bookList -> {
            if (bookList != null && bookList.size() > 0) {
                bookWithCountList = bookList;
                adapter = new DrawerBookAdapter(bookList, (chapter, position, itemList) -> {
                    //Toast.makeText(this, "Chapter: " + chapter.chapterTitle, Toast.LENGTH_SHORT).show();
                });
                binding.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.nestedRecyclerView.setAdapter(adapter);
            }
        });
    }


    private void filter(String query) {
        List<BookWithCount> booksList = new ArrayList<>();
        for (BookWithCount parentBook : bookWithCountList) {
            List<Chapter> filteredChaptersList = new ArrayList<>();
            for (Chapter childItem : parentBook.chaptersList) {
                if (childItem.chapterTitle.toLowerCase().contains(query.toLowerCase())) {
                    filteredChaptersList.add(childItem);
                }
            }
            if (!filteredChaptersList.isEmpty() || parentBook.bookName.toLowerCase().contains(query.toLowerCase())) {
                parentBook.chaptersList = filteredChaptersList;
                booksList.add(parentBook);
            }
        }
        adapter.filterList(booksList);
    }

}
