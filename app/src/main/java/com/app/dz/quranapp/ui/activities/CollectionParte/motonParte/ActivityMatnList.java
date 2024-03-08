package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.CsvReader;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.databinding.DialogDownloadProgressBinding;
import com.app.dz.quranapp.databinding.FragmentChaptersListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class ActivityMatnList extends AppCompatActivity implements LifecycleOwner {


    public final static String TAG = ActivityMatnList.class.getSimpleName();
    public static final int MATN_WRITE_REQUEST_CODE = 13;
    private FragmentChaptersListBinding binding;
    private DialogDownloadProgressBinding binding_dialog;
    private OneTimeWorkRequest downloadRequest;
    private MotonAdapter motonAdapter;
    private Matn globalModelMatn;
    private int globalMatnPosition;
    private AlertDialog dialog_download_matn;
    private Matn matn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentChaptersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }

        matn = (Matn) getIntent().getSerializableExtra("matn");
        //should use matn id to get the list of matn childrens

        binding.includeCategoryAdkarCard.tvFastAdkarTitle.setText(matn.matnTitle);

        initializeMotonAdapter();

        binding.imgBack.setOnClickListener(v->onBackPressed());

    }

    private List<Matn> getListMatn() {
        List<Matn> arrayList = CsvReader.readMotonListFromCsv(this, "moton_items.csv", null);
        List<Matn> newList = new ArrayList<>();
        for (Matn matn1 : arrayList) {
            if (!matn1.isParent() && matn1.parentId == matn.matnId)
                newList.add(matn1);
        }
        return newList;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    private void initializeMotonAdapter() {
        List<Matn> books = new ArrayList<>();
        books = PublicMethods.getInstance().checkBooksExistence(getListMatn());
        motonAdapter = new MotonAdapter(books, ActivityMatnList.this, (model, position) -> {
            if (model.isDownloaded) {
                startActivity(new Intent(ActivityMatnList.this, ActivityMatnViewer.class).putExtra("matn", model));
            } else {
                prepareMatn(model, position);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ActivityMatnList.this,2,LinearLayoutManager.VERTICAL, false);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setHasFixedSize(true);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setLayoutManager(gridLayoutManager);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setAdapter(motonAdapter);

    }

    private void prepareMatn(Matn model, int position) {
        if (havePermissions())
            downloadPreparedMatn(model, position);
        else {
            globalModelMatn = model;
            globalMatnPosition = position;
            askForPermission(MATN_WRITE_REQUEST_CODE);
        }

    }


    private void askForPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        }
    }

    private void downloadPreparedMatn(Matn model, int position) {
        showDownloadProgress();
        // Start the Worker to download the book.
        downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(new Data.Builder()
                        .putString("fileUrl", model.getFileUrl())
                        .putString("fileTitle", model.matnTitle)
                        .putString("fileName", model.fileName).build())
                .build();
        WorkManager.getInstance(ActivityMatnList.this).enqueue(downloadRequest);

        // Observe the LiveData of WorkInfo for the Worker.
        WorkManager.getInstance(ActivityMatnList.this).getWorkInfoByIdLiveData(downloadRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null) {
                        // Update the progress dialog with the current progress.
                        int progress = workInfo.getProgress().getInt("progress", 0);
                        Log.e(TAG, "initializeMotonAdapter: progress " + progress);
                        if (binding_dialog != null) {
                            if (progress == 100) {
                                Log.e(TAG, "progress  equals 100 ");
                                if (dialog_download_matn != null)
                                    dialog_download_matn.dismiss();
                                Toast.makeText(ActivityMatnList.this, "تم التحميل ", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ActivityMatnList.this, ActivityMatnViewer.class).putExtra("matn", model));
                                //should notify or update the adapter
                                model.isDownloaded = true;
                                if (motonAdapter != null)
                                    motonAdapter.notifyItemChanged(position);
                                return;
                            }
                            binding_dialog.progressDownload.setProgress(progress);
                        }
                    }
                });
    }

    private boolean havePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //we have permission
            return EasyPermissions.hasPermissions(ActivityMatnList.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return true;
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
        /*binding_dialog.btnCancel.setOnClickListener(v -> {
            WorkManager.getInstance(ActivityMatnList.this).cancelWorkById(downloadRequest.getId());
            dialog_download_matn.dismiss();
        });*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("checkpermision", "onPermission resule   requestCode " + requestCode);

        if (requestCode == MATN_WRITE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //complete download matn
                downloadPreparedMatn(globalModelMatn, globalMatnPosition);
            } else {
                Toast.makeText(ActivityMatnList.this, "لا يمكن التحميل من غير الادن بالتخزين", Toast.LENGTH_SHORT).show();
                // we do not have access
            }
        }


    }


}



