package com.app.dz.quranapp.CollectionParte.BooksParte;

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

import com.app.dz.quranapp.CollectionParte.chaptreParte.ActivityChapterList;
import com.app.dz.quranapp.Entities.Book;
import com.app.dz.quranapp.Entities.BookWithCount;
import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentBookListBinding;

import java.util.ArrayList;
import java.util.List;


public class ActivityBooksList extends AppCompatActivity {


    private BooksAdapter adapter;
    private BooksViewModel viewModel;
    private OnListenerInterface listener;
    private FragmentBookListBinding binding;
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
        binding = FragmentBookListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        viewModel = new ViewModelProvider(this).get(BooksViewModel.class);

        Intent intent = getIntent();
        if (intent != null) collectionName = intent.getStringExtra("collectionName");


        initializeArticlesAdapter();
        Log.e("lifecycle", "B onViewCreated");
        getBooks(collectionName);
        setObservers();

        binding.included.imgSearch.setVisibility(View.GONE);
        binding.included.tvTitle.setText(getCollectionArabicName(collectionName));
        binding.included.imgBack.setOnClickListener(v -> {
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
        adapter = new BooksAdapter(ActivityBooksList.this, this::moveToAyatFragment);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(ActivityBooksList.this, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }

    private void moveToAyatFragment(BookWithCount book) {
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
        //lastid = items.get(items.size() - 1).getEtag();
        Log.e("article", "size " + items.size());
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
