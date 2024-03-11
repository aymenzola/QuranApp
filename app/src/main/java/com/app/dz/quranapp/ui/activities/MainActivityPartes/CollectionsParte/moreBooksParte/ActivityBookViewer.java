package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.Communs.Constants;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.CsvReader;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.MoreBooksDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.databinding.ActivityPdfBinding;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.DrawerMatnParentAdapter;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.SavedMatnPage;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.util.ArrayList;
import java.util.List;


public class ActivityBookViewer extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    private static final String TAG = ActivityBookViewer.class.getSimpleName();
    private ActivityPdfBinding binding;
    Integer pageNumber = 0;
    private Book book;
    private MoreBooksDao bookDao;
    private List<Integer> savedPagesNumbers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.background_color));
        }

        AppDatabase db = DatabaseClient.getInstance(ActivityBookViewer.this).getAppDatabase();
        bookDao = db.getMoreBookDao();
        book = (Book) getIntent().getSerializableExtra("book");


        if (book != null) {
            Log.e(TAG, "onCreate: " + book.getFileUrl());
            showPdfFile(0);
        } else {
            //it is a saved book
            SavedMatnPage savedMatnPage = (SavedMatnPage) getIntent().getSerializableExtra("saved_book");
            if (savedMatnPage != null) {
                book = getBookById(savedMatnPage.matnId);
                showPdfFile(savedMatnPage.pageNumber);
            } else {
                Log.e(TAG, "onCreate: matn is null");
            }
        }

        //should change drawer header text
        binding.includeDrawer.tvTitle.setText("المحتوى");
        binding.tvTitle.setText(book.bookTitle);
        binding.tvTitle.setSelected(true);


        setListeners();
        setObservers();

    }

    private Book getBookById(int bookId) {
        return CsvReader.readBooksListFromCsv(this, Constants.books_file_name, bookId).get(0);
    }

    private void showPdfFile(int pageNumber) {
        binding.pdfView.fromFile(PublicMethods.getInstance().getFile(book.fileName, this))
                .onPageChange(this)
                .onLoad(this)
                .pageSnap(true)
                .defaultPage(pageNumber)
                .onError(t -> Log.e(TAG, "onError: " + t.getMessage()))
                .scrollHandle(new DefaultScrollHandle(this)).load();
    }

    private void setObservers() {
        bookDao.getSavedPagesNumbersByBookId(book.bookId).observe(this, pageNumbers -> savedPagesNumbers = pageNumbers);
    }

    private void setListeners() {

        binding.imgSave.setOnClickListener(v -> {
            if (savedPagesNumbers.contains(pageNumber)) {
                new Thread(() -> bookDao.deleteBookByBookId(book.bookId, pageNumber));
                binding.imgSave.setImageResource(R.drawable.ic_unsaved_new);
            } else {
                binding.imgSave.setImageResource(R.drawable.ic_saved_new);
                SavedBookPage savedMatnPage = new SavedBookPage(book.bookId, pageNumber, book.bookTitle, getPageTitle(pageNumber));
                new Thread(() -> bookDao.saveBookPage(savedMatnPage)).start();
            }

        });

        binding.imgMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.END));

    }

    private String getPageTitle(Integer pageNumber) {
        PdfDocument.Bookmark bookmark = PublicMethods.getInstance().findBookmarkByPage(binding.pdfView.getTableOfContents(), pageNumber);
        return bookmark != null ? bookmark.getTitle() : "page " + pageNumber + " " + book.bookTitle;
    }


    @Override
    public void loadComplete(int nbPages) {
        Log.e(TAG, "loadComplete: " + nbPages);

        if (binding.pdfView.getTableOfContents() == null || binding.pdfView.getTableOfContents().size() == 0) {
            binding.imgMenu.setVisibility(View.INVISIBLE);
            return;
        }
        if (binding.pdfView.getTableOfContents().get(0).getChildren().size() == 0) {
            binding.imgMenu.setVisibility(View.INVISIBLE);
            return;
        }

        Log.e(TAG, "loadComplete: loading bookmarks");
        DrawerMatnParentAdapter adapterDrawer = new DrawerMatnParentAdapter(binding.pdfView.getTableOfContents().get(0).getChildren(), (bookmark, position) -> {
            binding.pdfView.jumpTo((int) bookmark.getPageIdx(), true);
            binding.drawerLayout.closeDrawer(GravityCompat.END);
        });

        binding.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.nestedRecyclerView.setAdapter(adapterDrawer);

    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        if (savedPagesNumbers.contains(page)) {
            binding.imgSave.setImageResource(R.drawable.ic_saved_new);
        } else {
            binding.imgSave.setImageResource(R.drawable.ic_unsaved_new);
        }
    }
}



