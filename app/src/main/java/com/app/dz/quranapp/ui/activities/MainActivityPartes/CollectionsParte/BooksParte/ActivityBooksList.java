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

import com.app.dz.quranapp.databinding.FragmentChaptersListBinding;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.chaptreParte.ActivityChapterList;
import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.R;

import java.util.ArrayList;
import java.util.List;


public class ActivityBooksList extends AppCompatActivity {


    private BooksAdapter adapter;
    private BooksViewModel viewModel;
    private OnListenerInterface listener;
    private FragmentChaptersListBinding binding;
    private Integer lastid = null;
    private String collectionName = "";
    private List<Hadith> HadithsGloablList = new ArrayList<>();
    private List<Book> bookList = new ArrayList<>();


    //CSV
    private int Count = 0;
    private String CurrantFileName = "muslim";
    private int max = 2;


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

        binding.includeCategoryAdkarCard.tvFastAdkarTitle.setText(getCollectionArabicName(collectionName));
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

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

        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setLayoutManager(new LinearLayoutManager(ActivityBooksList.this, RecyclerView.VERTICAL, false));
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setHasFixedSize(true);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setAdapter(adapter);
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

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

}
