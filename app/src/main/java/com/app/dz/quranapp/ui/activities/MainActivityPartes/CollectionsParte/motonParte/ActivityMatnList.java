package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte;

import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;

import com.app.dz.quranapp.Communs.Constants;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.CsvReader;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.databinding.DialogDownloadProgressBinding;
import com.app.dz.quranapp.databinding.FragmentChaptersListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;


public class ActivityMatnList extends AppCompatActivity implements LifecycleOwner {


    public final static String TAG = ActivityMatnList.class.getSimpleName();
    public static final String MOTON_DOWNLOAD = "moton_download";
    private FragmentChaptersListBinding binding;
    private DialogDownloadProgressBinding binding_dialog;
    private OneTimeWorkRequest downloadRequest;
    private MotonAdapter motonAdapter;
    private AlertDialog dialog_download_matn;
    private Matn parentMatn;
    private Matn matnDownloaded;
    private int matnDownloadedPosition;
    private BroadcastReceiver downloadReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentChaptersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.background_color));
        }

        parentMatn = (Matn) getIntent().getSerializableExtra("matn");
        //should use matn id to get the list of matn childrens

        binding.includeCategoryAdkarCard.tvFastAdkarTitle.setText(parentMatn.matnTitle);

        initializeMotonAdapter();

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) return;
                if (action.equals(MOTON_DOWNLOAD)) {
                    String actionType = intent.getStringExtra("type");
                    if (actionType == null) return;

                    switch (actionType) {
                        case DOWNLOAD_CANCEL_ACTION -> handleError("تم الغاء التحميل");
                        case DOWNLOAD_ERROR_ACTION -> handleError(intent.getStringExtra("message"));
                        case DOWNLOAD_COMPLETE_ACTION ->
                                downloadCompleted(matnDownloaded, matnDownloadedPosition);
                        case PROGRESS_ACTION ->
                                updateDialogProgress(intent.getIntExtra("progress", 0));
                    }
                }
            }
        };


    }

    private List<Matn> getListMatn() {
        List<Matn> arrayList = CsvReader.readMotonListFromCsv(this, Constants.moton_file_name, null);
        List<Matn> newList = new ArrayList<>();
        for (Matn matn1 : arrayList)
            if (!matn1.isParent() && matn1.parentId.equals(parentMatn.matnId)) newList.add(matn1);
        return newList;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    private void initializeMotonAdapter() {
        List<Matn> books;
        books = PublicMethods.getInstance().checkMatnExistence(getListMatn(), this);
        motonAdapter = new MotonAdapter(books, ActivityMatnList.this, (model, position) -> {
            if (model.isDownloaded) {
                startActivity(new Intent(ActivityMatnList.this,ActivityMatnViewer.class).putExtra("matn",model));
            } else {
                downloadPreparedMatn(model,position);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ActivityMatnList.this, 2, LinearLayoutManager.VERTICAL, false);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setHasFixedSize(true);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setLayoutManager(gridLayoutManager);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setAdapter(motonAdapter);

    }

    private void downloadPreparedMatn(Matn model, int position) {
        matnDownloaded = model;
        matnDownloadedPosition = position;
        showDownloadProgress();
        // Start the Worker to download the book.
        downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(new Data.Builder()
                        .putString("fileUrl", model.getFileUrl())
                        .putString("fileTitle", model.matnTitle)
                        .putString("action",MOTON_DOWNLOAD)
                        .putInt("notifyId",model.matnId)
                        .putString("fileName", model.fileName).build())
                .build();
        WorkManager.getInstance(ActivityMatnList.this).enqueue(downloadRequest);
    }

    private void handleError(String error) {
        String message = PublicMethods.getInstance().getUserFriendlyErrorMessage(error);
        if (dialog_download_matn != null) {
            binding_dialog.progressDownload.setVisibility(View.GONE);
            binding_dialog.btnCancel.setVisibility(View.GONE);
            binding_dialog.tvTitle.setText("تنبيه");
            binding_dialog.btnDone.setText("حسنا");
            binding_dialog.tvMessage.setText(message);
        } else {
            Toast.makeText(ActivityMatnList.this, message, Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("SetTextI18n")
    private void updateDialogProgress(int progress) {
        if (binding_dialog == null) return;
        binding_dialog.tvMessage.setText("جاري التحميل " + progress + "%");
        binding_dialog.progressDownload.setProgress(progress);
    }

    private void downloadCompleted(Matn model, int position) {
        Toast.makeText(ActivityMatnList.this, "تم التحميل ", Toast.LENGTH_SHORT).show();

        if (model == null) return;
        if (dialog_download_matn != null && dialog_download_matn.isShowing())
            dialog_download_matn.dismiss();

        startActivity(new Intent(ActivityMatnList.this, ActivityMatnViewer.class).putExtra("matn", model));
        model.isDownloaded = true;
        if (motonAdapter != null) motonAdapter.notifyItemChanged(position);
    }

    public void showDownloadProgress() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(ActivityMatnList.this);

        binding_dialog = DataBindingUtil.inflate(LayoutInflater.from(ActivityMatnList.this),
                R.layout.dialog_download_progress, null, false);
        dialogBuilder.setView(binding_dialog.getRoot());
        dialog_download_matn = dialogBuilder.create();

        if (dialog_download_matn.getWindow() != null)
            dialog_download_matn.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog_download_matn.setCancelable(false);
        dialog_download_matn.show();

        binding_dialog.progressDownload.setProgress(0);

        binding_dialog.btnDone.setText("الغاء التحميل");
        binding_dialog.btnDone.setOnClickListener(v -> {
            WorkManager.getInstance(ActivityMatnList.this).cancelWorkById(downloadRequest.getId());
            dialog_download_matn.dismiss();
        });

        binding_dialog.btnCancel.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter_download = new IntentFilter(MOTON_DOWNLOAD);
        registerReceiver(downloadReceiver, filter_download);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }


}



