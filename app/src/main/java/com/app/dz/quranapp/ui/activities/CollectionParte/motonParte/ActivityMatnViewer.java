package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.CsvReader;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.MotonDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.databinding.ActivityPdfBinding;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.util.ArrayList;
import java.util.List;


public class ActivityMatnViewer extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {


    private static final String TAG = ActivityMatnViewer.class.getSimpleName();
    private ActivityPdfBinding binding;
    Integer pageNumber = 0;
    private Matn matn;
    private MotonDao motonDao;
    private List<Integer> savedPagesNumbers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        AppDatabase db = DatabaseClient.getInstance(ActivityMatnViewer.this).getAppDatabase();
        motonDao = db.getMotonDao();
        matn = (Matn) getIntent().getSerializableExtra("matn");

        binding.imgSave.setOnLongClickListener(v->{
            startActivity(new Intent(ActivityMatnViewer.this,ActivityMatnOnlineViewer.class));
            return true;
        });

        if (matn != null) {
            Log.e(TAG, "onCreate: " + matn.getFileUrl());
            showPdfFile(0);
        } else {
            //it is a saved book

            SavedMatnPage savedMatnPage = (SavedMatnPage) getIntent().getSerializableExtra("saved_matn");
            if (savedMatnPage != null) {
                matn = getMatnById(savedMatnPage.matnId);
                showPdfFile(savedMatnPage.pageNumber);
            } else {
                Log.e(TAG, "onCreate: matn is null");
            }
        }

        setListeners();
        setObservers();

    }

    private Matn getMatnById(int matnId) {
        return CsvReader.readMotonListFromCsv(this, "moton_items.csv", matnId).get(0);
    }

    private void showPdfFile(int pageNumber) {
        binding.pdfView.fromFile(PublicMethods.getInstance().getFile(matn.fileName))
                .onPageChange(this)
                .onLoad(this)
                .pageSnap(true)
                .defaultPage(pageNumber)
                .onError(t -> Log.e(TAG, "onError: " + t.getMessage()))
                .scrollHandle(new DefaultScrollHandle(this)).load();
    }

    private void setObservers() {
        motonDao.getSavedPagesNumbersByMatnId(matn.matnId).observe(this, pageNumbers -> savedPagesNumbers = pageNumbers);
    }

    private void setListeners() {

        binding.imgSave.setOnClickListener(v -> {
            if (savedPagesNumbers.contains(pageNumber)) {
                new Thread(() -> motonDao.deleteMatnByMatnId(matn.matnId, pageNumber));
                binding.imgSave.setImageResource(R.drawable.ic_unsaved_new);
            } else {
                binding.imgSave.setImageResource(R.drawable.ic_saved_new);
                SavedMatnPage savedMatnPage = new SavedMatnPage(matn.matnId, pageNumber, matn.matnTitle, getPageTitle(pageNumber));
                new Thread(() -> motonDao.saveMatnPage(savedMatnPage)).start();
            }

        });

        binding.imgMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.END));

    }

    private String getPageTitle(Integer pageNumber) {
        PdfDocument.Bookmark bookmark = PublicMethods.getInstance().findBookmarkByPage(binding.pdfView.getTableOfContents(), pageNumber);
        return bookmark != null ? bookmark.getTitle() : "page " + pageNumber + " " + matn.matnTitle;
    }


    @Override
    public void loadComplete(int nbPages) {
        Log.e(TAG, "loadComplete: " + nbPages);

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



