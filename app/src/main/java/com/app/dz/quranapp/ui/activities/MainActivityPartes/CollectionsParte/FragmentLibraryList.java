package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte;

import static android.content.Context.MODE_PRIVATE;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_abudawaed;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_bukhari;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_hisn_muslim;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_ibn_majah;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_nisai;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_sahih_muslim;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.databinding.FragmentLibrary1Binding;
import com.app.dz.quranapp.databinding.FragmentLibraryBinding;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarDetailsParte.ActivityDikrDetailsList;
import com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte.ActivityBooksList;
import com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte.BooksUtils;
import com.app.dz.quranapp.ui.activities.CollectionParte.HadithDetailsParte.ActivityHadithDetailsListDev;
import com.app.dz.quranapp.data.room.Entities.BookCollection;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarCategoryAdapter;
import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.ForegroundDownloadBookService;
import com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte.ActivityChapterList;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment.HomeFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class FragmentLibraryList extends Fragment implements EasyPermissions.PermissionCallbacks {


    public final static String TAG = FragmentLibraryList.class.getSimpleName();
    private static final int WRITE_REQUEST_CODE = 12;
    private static Dialog dialog;
    private CollectionsAdapter adapter;
    private FragmentLibrary1Binding binding;
    private BroadcastReceiver DownloadReceiver;
    private String ClickedCollectionName;
    private CollectionViewModel viewModel;
    private int LastClickedPosition = 0;
    private BookCollection globalModel;
    private int type = HomeFragment.BOOKS_TYPE;
    private boolean isDkarAvilaible = false;
    private boolean isBooksDownloadType = true;


    public FragmentLibraryList() {
        // Required empty public constructor
    }


    public static FragmentLibraryList newInstance() {
        FragmentLibraryList fragment = new FragmentLibraryList();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLibrary1Binding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.blan));
        }

        viewModel = new ViewModelProvider(this).get(CollectionViewModel.class);

        Log.e("lifecycle", "B onViewCreated");
        viewModel.setBooksList();
        viewModel.setChaptersObject("hisn");


        setObservers();
        setListenrs();
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type", HomeFragment.BOOKS_TYPE);
            if (type == HomeFragment.BOOKS_TYPE) {
                bookTabClicked();
            } else {
                adkarTabClicked();
            }
        }


        DownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("COLLECTION_DOWNLOAD")) {
                    switch (intent.getStringExtra("action")) {
                        case DOWNLOAD_CANCEL_ACTION:
                            Toast.makeText(getActivity(), "تم الغاء التحميل", Toast.LENGTH_LONG).show();
                            break;
                        case DOWNLOAD_ERROR_ACTION:
                            //DownloadTheBook();
                            if (dialog != null) dialog.dismiss();
                            String error = intent.getStringExtra("error");
                            Log.e(TAG, "error " + error);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    //we have permission
                                    Log.e(TAG, "we have permission");
                                } else {
                                    //we ask FOr permission
                                    Log.e(TAG, "we ask For permission");
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                                }
                            } else {
                                Log.e(TAG, "we dont need permission");
                                //we do not need permission
                            }
                            Toast.makeText(getActivity(), "خطأ", Toast.LENGTH_LONG).show();
                            break;
                        case DOWNLOAD_COMPLETE_ACTION:
                            if (isBooksDownloadType) {
                                adapter.updateItem(LastClickedPosition);
                                if (dialog != null) dialog.dismiss();
                                moveToAyatFragment(ClickedCollectionName);
                            } else {
                                //todo dirk type
                                isDkarAvilaible = true;

                            }

                            break;
                    }
                }
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void manageSavedBook() {
        List<BookWithCount> list = BooksUtils.getSavedBooksList(requireActivity());
        if (list.size() > 0) {
            //show last book details
            BookWithCount lastBook = list.get(list.size() - 1);
            binding.includeSavedBook.tvBookTitle.setText(lastBook.bookName);
            binding.includeSavedBook.tvChaptersNumber.setText(lastBook.firstChapterTitle);
            binding.includeSavedBook.imgSave.setVisibility(View.GONE);
            binding.includeSavedBook.tvMoveLibrary.setOnClickListener(v-> moveToBookChapters(lastBook));
        } else {
            binding.includeSavedBook.getRoot().setVisibility(View.GONE);
        }
    }

    private void moveToBookChapters(BookWithCount book) {
        Intent intent = new Intent(requireActivity(),ActivityChapterList.class);
        intent.putExtra("collectionName",book.bookCollection);
        intent.putExtra("bookNumber", book.bookNumber);
        intent.putExtra("bookName", book.bookName);
        startActivity(intent);
    }

    private void setListenrs() {

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

        if (!isDkarAvilaible) {
            BookCollection bookCollection = new BookCollection("hisn", isDkarAvilaible, " سعيد بن علي بن وهف القحطاني", "حصن المسلم", countItemsCount_hisn_muslim);
            isBooksDownloadType = false;
            DownloadTheBook(bookCollection);
        }

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

        viewModel.getchaptersObject().observe(getViewLifecycleOwner(), chapterList -> {
            if (chapterList != null && chapterList.size() > 0) initializeAdkarAdapter(chapterList);
        });

        viewModel.getBooksList().observe(getViewLifecycleOwner(), booksAvailableList -> {
            List<BookCollection> booksList = new ArrayList<>();
            booksList.add(new BookCollection("bukhari", booksAvailableList.contains("bukhari"), " أبو عبد الله محمد بن إسماعيل الجعفي البخاري", "صحيح البخاري", countItemsCount_bukhari));
            booksList.add(new BookCollection("muslim", booksAvailableList.contains("muslim"), "هو أبو الحسين مسلم بن الحجاج بن مسلم", "صحيح مسلم", countItemsCount_sahih_muslim));
            booksList.add(new BookCollection("nasai", booksAvailableList.contains("nasai"), "احمد بن شعيب النسائي", "سنن النسائي", countItemsCount_nisai));
            booksList.add(new BookCollection("ibnmajah", booksAvailableList.contains("ibnmajah"), "ابن ماجة", "سنن ابن ماجة", countItemsCount_ibn_majah));
            booksList.add(new BookCollection("hisn", booksAvailableList.contains("hisn"), " سعيد بن علي بن وهف القحطاني", "حصن المسلم", countItemsCount_hisn_muslim));
            booksList.add(new BookCollection("abudawud", booksAvailableList.contains("abudawud"), "أبي داود", "سنن أبي داود", countItemsCount_abudawaed));
            isDkarAvilaible = booksAvailableList.contains("hisn");
            initializeArticlesAdapter(booksList);
        });
    }

    private void moveToAdkarDetails(Chapter categoryName) {
        Intent intent = new Intent(getActivity(), ActivityDikrDetailsList.class);
        intent.putExtra("categoryName", categoryName.chapterTitle);
        startActivity(intent);
    }

    public void initializeArticlesAdapter(List<BookCollection> booksList) {
        adapter = new CollectionsAdapter(booksList, getActivity(), (model, position) -> {
            if (model.isDownloaded) {
                //Open The book
                moveToAyatFragment(model.CollectionName);
            } else {
                ClickedCollectionName = model.CollectionName;
                LastClickedPosition = position;
                isBooksDownloadType = true;
                DownloadTheBook(model);
            }
        });
        binding.recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }

    public void initializeAdkarAdapter(List<Chapter> items) {
        AdkarCategoryAdapter adapter_adkar = new AdkarCategoryAdapter(items, getActivity(), this::moveToAdkarDetails);
        binding.recyclerviewAdkar.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerviewAdkar.setHasFixedSize(true);
        binding.recyclerviewAdkar.setAdapter(adapter_adkar);
    }

    private void DownloadTheBook(BookCollection model) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //we have permission
                prepareDownload(model);
            } else {
                globalModel = model;
                //we ask FOr permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
            }
        } else {
            prepareDownload(model);
            //we do not need permission
        }

    }

    private void prepareDownload(BookCollection model) {
        final int serviceState = ForegroundDownloadBookService.getState();
        if (serviceState == Statics.STATE_SERVICE.NOT_INIT)
            startDownload(model);
        else if (serviceState == Statics.STATE_SERVICE.PREPARE || serviceState == Statics.STATE_SERVICE.PLAY)
            PauseDownload();
    }

    private void startDownload(BookCollection bookCollection) {
        if (dialog != null) dialog.show();
        else showDialog(getActivity());

        Intent startIntent = new Intent(getActivity(), ForegroundDownloadBookService.class);
        startIntent.setAction(Statics.ACTION.START_ACTION);
        startIntent.putExtra("BookCollection", bookCollection);
        ContextCompat.startForegroundService(getActivity(), startIntent);

    }

    private void PauseDownload() {
        Intent lPauseIntent = new Intent(getActivity(), ForegroundDownloadBookService.class);
        lPauseIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(getActivity(), 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            Log.e(TAG, "lPendingPauseIntent.send()");
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void prepareData(BookCollection bookCollection) {
        if (bookCollection.isDownloaded) {
            moveToAyatFragment(bookCollection.CollectionName);
        } else {
            Toast.makeText(getActivity(), "عليك تحميل الكتاب اولا", Toast.LENGTH_SHORT).show();
        }
        //ClickedCollectionName = bookCollection.CollectionName;
        //new ExampleAsyncTask().execute(bookCollection.CollectionName+".csv");


    }

    private void moveToAyatFragment(String collectionName) {
        Intent intent = new Intent(getActivity(), ActivityBooksList.class);
        intent.putExtra("collectionName", collectionName);
        startActivity(intent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("checkpermision", "onPermission resule   requestCode " + requestCode);

        if (requestCode == WRITE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have access
                if (globalModel != null)
                    prepareDownload(globalModel);
            } else {
                // we do not have access

            }


        }


    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayCardHadith();
        manageSavedBook();
        IntentFilter filter_download = new IntentFilter("COLLECTION_DOWNLOAD");
        getActivity().registerReceiver(DownloadReceiver, filter_download);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(DownloadReceiver);
    }

    public static void showDialog(@NonNull Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.dialog_loading, null);
        TextView loadingTextView = view.findViewById(R.id.loading_text_view);
        CircularProgressIndicator circularProgress = view.findViewById(R.id.circular_progress);
        circularProgress.setIndeterminate(true);

        dialog.setContentView(view);
        dialog.show();
    }

    private void MoveToHadithDetails() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String bookName = sharedPreferences.getString("bookName", "");

        if (bookName.isEmpty()) return;
        String collectionName = sharedPreferences.getString("collectionName", "");
        String bookNumber = sharedPreferences.getString("bookNumber", "");
        int CurrantPosition = sharedPreferences.getInt("CurrantPosition", 0);

        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), ActivityHadithDetailsListDev.class);
        bundle.putString("collectionName", collectionName);
        bundle.putString("bookNumber", bookNumber);
        bundle.putString("bookName", bookName);
        bundle.putInt("position", CurrantPosition);

        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    public void DisplayCardHadith() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String destination = sharedPreferences.getString("destination", "");
        String chapterName = sharedPreferences.getString("chapterName", "");

        /*
        if (chapterName.isEmpty()) {
            binding.tvDestination.setVisibility(View.GONE);
            return;
        }
        binding.tvDestination.setVisibility(View.VISIBLE);
        binding.tvDestination.setText("" + destination);
        binding.tvChapter.setText("" + Html.fromHtml(chapterName));

        */
    }

}



