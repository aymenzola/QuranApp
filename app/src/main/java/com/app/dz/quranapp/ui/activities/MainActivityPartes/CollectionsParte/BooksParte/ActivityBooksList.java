package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.BooksParte;

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
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.databinding.FragmentChaptersListBinding;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.chaptreParte.ActivityChapterList;

import java.util.List;


public class ActivityBooksList extends AppCompatActivity {

    private BooksAdapter adapter;
    private BooksViewModel viewModel;
    private FragmentChaptersListBinding binding;
    private String collectionName = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentChaptersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.background_color));
        }

        viewModel = new ViewModelProvider(this).get(BooksViewModel.class);

        Intent intent = getIntent();
        if (intent != null) collectionName = intent.getStringExtra("collectionName");


        initializeArticlesAdapter();
        Log.e("lifecycle", "B onViewCreated");
        getBooks(collectionName);
        setObservers();

        binding.includeBooksLayout.tvFastAdkarTitle.setText(PublicMethods.getInstance().getCollectionArabicName(collectionName));
        binding.imgBack.setOnClickListener(v -> onBackPressed());

    }


    private void setObservers() {
        viewModel.getDBooks().observe(ActivityBooksList.this, bookList -> {
            if (bookList != null && bookList.size() > 0) displayData(bookList);
        });

        /*viewModel.getDBookObject().observe(getViewLifecycleOwner(),booksObject -> {
            if (booksObject != null) manageBooksObject(booksObject);
        });*/
    }

    public void initializeArticlesAdapter() {
        adapter = new BooksAdapter(ActivityBooksList.this, new BooksAdapter.OnAdapterClickListener() {
            @Override
            public void onItemClick(BookWithCount model) {
                moveToChapters(model);
            }
            @Override
            public void onItemSaveClick(BookWithCount model,boolean isSaved) {
                BooksUtils.updateBook(ActivityBooksList.this,model);
            }
        });

        binding.includeBooksLayout.recyclerViewFastAdkar.setLayoutManager(new LinearLayoutManager(ActivityBooksList.this, RecyclerView.VERTICAL, false));
        binding.includeBooksLayout.recyclerViewFastAdkar.setHasFixedSize(true);
        binding.includeBooksLayout.recyclerViewFastAdkar.setAdapter(adapter);
    }

    private void moveToChapters(BookWithCount book) {
        Intent intent = new Intent(this, ActivityChapterList.class);
        intent.putExtra("collectionName", collectionName);
        intent.putExtra("bookNumber", book.bookNumber);
        intent.putExtra("bookName", book.bookName);
        startActivity(intent);
    }

    public void getBooks(String collectionName) {
        viewModel.setBooks(collectionName);
    }

    private void displayData(List<BookWithCount> items) {
        List<BookWithCount> savedBooks = BooksUtils.getSavedBooksList(ActivityBooksList.this);
        for (BookWithCount book : items) {
            for (BookWithCount savedBook : savedBooks) {
                if (book.bookNumber.equals(savedBook.bookNumber) && book.bookCollection.equals(savedBook.bookCollection)) {
                    book.isSaved = true;
                    break;
                }
            }
        }
        adapter.setItems(items);
    }

}
