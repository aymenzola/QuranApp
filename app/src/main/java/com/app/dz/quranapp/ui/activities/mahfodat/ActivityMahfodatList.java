package com.app.dz.quranapp.ui.activities.mahfodat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.MotonDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.databinding.FragmentMahfodat1Binding;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarModel;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarSavedAdapter;
import com.app.dz.quranapp.ui.activities.AdkarParte.ChaptersSavedAdapter;
import com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte.BooksUtils;
import com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte.ActivityChapterList;
import com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte.ChapterUtils;
import com.app.dz.quranapp.ui.activities.CollectionParte.motonParte.ActivityMatnViewer;
import com.app.dz.quranapp.ui.activities.CollectionParte.motonParte.SavedMatnPage;

import java.util.List;


public class ActivityMahfodatList extends AppCompatActivity {


    public final static String TAG = ActivityMahfodatList.class.getSimpleName();
    private FragmentMahfodat1Binding binding;
    private MahfodatViewModel viewModel;
    private ChaptersSavedAdapter adapter;
    private MotonDao motonDao;


    public ActivityMahfodatList() {
        // Required empty public constructor
    }


    public static ActivityMahfodatList newInstance() {
        ActivityMahfodatList fragment = new ActivityMahfodatList();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentMahfodat1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }

        AppDatabase db = DatabaseClient.getInstance(ActivityMahfodatList.this).getAppDatabase();
        motonDao = db.getMotonDao();

        viewModel = new ViewModelProvider(this).get(MahfodatViewModel.class);

        setObservers();
        setListenrs();

        initializeChaptersAdapter();

        viewModel.askForSavedAdkar();

        getSavedMotonPages();
        getSavedChapters();
        getSavedBooks();

    }

    private void getSavedChapters() {
        List<Chapter> chapters = ChapterUtils.getSavedChapters(this);
        if (chapters != null && chapters.size() > 0) adapter.addChapters(chapters);
    }

    private void getSavedMotonPages() {
        motonDao.getSavedPagesList().observe(this, savedMatnPageList -> {
            if (savedMatnPageList != null && savedMatnPageList.size() > 0) {
                Log.e(TAG, "we getSavedMotonPages: " + savedMatnPageList.size()+" adapter.getItemCount(); "+adapter.getItemCount());
                boolean isRecyclerviewVisible = binding.recyclerview.getVisibility()==View.VISIBLE;
                boolean isRecyclerviewAdkarVisible = binding.recyclerviewAdkar.getVisibility()==View.VISIBLE;

                Log.e(TAG, "is recyclerview is visible " +isRecyclerviewVisible+" isRecyclerviewAdkarVisible "+isRecyclerviewAdkarVisible);

                adapter.addMoton(savedMatnPageList);
            }
        });
    }

    private void getSavedBooks() {
        List<BookWithCount> savedBook = BooksUtils.getSavedBooksList(this);
        if (savedBook != null && savedBook.size() > 0) adapter.addBooks(savedBook);
    }

    private void setListenrs() {

        binding.imgClose.setOnClickListener(v -> onBackPressed());
        binding.tvBooks.setOnClickListener(v -> bookTabClicked());
        binding.tvAdkar.setOnClickListener(v -> adkarTabClicked());
    }

    private void adkarTabClicked() {

        binding.recyclerview.setVisibility(View.GONE);
        binding.recyclerviewAdkar.setVisibility(View.VISIBLE);

        binding.tvAdkar.setTextColor(getResources().getColor(R.color.white));
        binding.tvAdkar.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_left, null));

        binding.tvBooks.setBackgroundColor(getResources().getColor(R.color.white));
        binding.tvBooks.setTextColor(getResources().getColor(R.color.tv_gri_color));
    }

    private void bookTabClicked() {
        binding.recyclerview.setVisibility(View.VISIBLE);
        binding.recyclerviewAdkar.setVisibility(View.GONE);

        binding.tvBooks.setTextColor(getResources().getColor(R.color.white));
        binding.tvBooks.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_right, null));

        binding.tvAdkar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.tvAdkar.setTextColor(getResources().getColor(R.color.tv_gri_color));
    }


    private void setObservers() {

        viewModel.getSavedAdkarList().observe(ActivityMahfodatList.this, adkarModelList -> {
            if (adkarModelList != null && adkarModelList.size() > 0)
                initializeAdkarAdapter(adkarModelList);
        });

    }

    public void initializeAdkarAdapter(List<AdkarModel> items) {
        AdkarSavedAdapter adapter_adkar = new AdkarSavedAdapter(items, new AdkarSavedAdapter.ClickListener() {
            @Override
            public void onClick(AdkarModel adkarModel, int isSaved) {
                viewModel.updateDikrSaveState(adkarModel.getId(), isSaved);
            }

            @Override
            public void onOpenClicked(AdkarModel adkarModel) {
                Intent intent = new Intent(ActivityMahfodatList.this, MainActivity.class);
                intent.putExtra("adkarModel", adkarModel);
                startActivity(intent);
            }
        });

        binding.recyclerviewAdkar.setLayoutManager(new LinearLayoutManager(ActivityMahfodatList.this, RecyclerView.VERTICAL, false));
        binding.recyclerviewAdkar.setHasFixedSize(true);
        binding.recyclerviewAdkar.setAdapter(adapter_adkar);
    }


    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void initializeChaptersAdapter() {
        adapter = new ChaptersSavedAdapter(new ChaptersSavedAdapter.ClickListener() {

            @Override
            public void onOpenChapterClicked(Chapter chapter) {
                ChapterUtils.moveToChapterDetails(ActivityMahfodatList.this, chapter);
            }

            @Override
            public void onOpenBookClicked(BookWithCount bookWithCount) {
                moveToChapters(bookWithCount);
            }

            @Override
            public void onOpenMatnClicked(SavedMatnPage savedMatnPage) {
                moveToMatnView(savedMatnPage);
            }

            @Override
            public void onChapterSaveClick(Chapter chapter, boolean isSaved, int position) {
                ChapterUtils.updateChapter(ActivityMahfodatList.this, chapter);
                if (!isSaved) adapter.removeItem(position);
            }

            @Override
            public void onBookSaveClick(BookWithCount bookWithCount, boolean isSaved, int position) {
                BooksUtils.updateBook(ActivityMahfodatList.this, bookWithCount);
                if (!isSaved) adapter.removeItem(position);
            }

            @Override
            public void onMatnSaveClick(SavedMatnPage savedMatnPage, int position) {
                new Thread(() -> motonDao.deleteMatnPage(savedMatnPage));
                adapter.removeItem(position);
            }
        });

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(ActivityMahfodatList.this, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }

    private void moveToMatnView(SavedMatnPage savedMatnPage) {
        Intent intent = new Intent(this, ActivityMatnViewer.class);
        intent.putExtra("saved_matn", savedMatnPage);
        startActivity(intent);
    }

    private void moveToChapters(BookWithCount book) {
        Intent intent = new Intent(this, ActivityChapterList.class);
        intent.putExtra("collectionName", book.bookCollection);
        intent.putExtra("bookNumber", book.bookNumber);
        intent.putExtra("bookName", book.bookName);
        startActivity(intent);
    }

}



