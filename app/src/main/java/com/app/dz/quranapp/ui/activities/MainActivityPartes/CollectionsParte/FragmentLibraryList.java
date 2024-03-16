package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte;

import static android.content.Context.RECEIVER_NOT_EXPORTED;
import static com.app.dz.quranapp.Communs.Constants.books_file_name;
import static com.app.dz.quranapp.Communs.Constants.moton_file_name;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_abudawaed;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_bukhari;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_hisn_muslim;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_ibn_majah;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_nisai;
import static com.app.dz.quranapp.Communs.Statics.countItemsCount_sahih_muslim;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;

import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.ExtractBooksService;
import com.app.dz.quranapp.Util.CsvReader;
import com.app.dz.quranapp.Util.PublicMethods;
import com.app.dz.quranapp.data.room.Entities.BookCollection;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.databinding.DialogDownloadProgressBinding;
import com.app.dz.quranapp.databinding.FragmentLibraryBinding;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.BooksParte.ActivityBooksList;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.BooksParte.BooksUtils;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.chaptreParte.ActivityChapterList;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte.ActivityBookViewer;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte.Book;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.ActivityMatnViewer;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.DownloadWorker;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.Matn;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte.MotonAdapter;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment.HomeFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;


public class FragmentLibraryList extends Fragment {

    public static final String MOTON_DOWNLOAD = "moton_download";
    public static final String BOOK_DOWNLOAD = "book_download";
    public static final String COLLECTION_DOWNLOAD = "collection_download";

    public final static String TAG = FragmentLibraryList.class.getSimpleName();
    private static Dialog dialog;
    private CollectionsAdapter adapter;
    private FragmentLibraryBinding binding;
    private BroadcastReceiver downloadCollectionReceiver;
    private String ClickedCollectionName;
    private CollectionViewModel viewModel;
    private int LastClickedPosition = 0;
    private int type = HomeFragment.BOOKS_TYPE;
    private boolean isBooksDownloadType = true;
    private Book bookDownloaded;
    private int bookDownloadedPosition;
    private DialogDownloadProgressBinding binding_dialog;
    private AlertDialog dialog_download_matn;
    private OneTimeWorkRequest downloadRequest;
    private BroadcastReceiver downloadBookReceiver;
    private Matn matnDownloaded;
    private int matnDownloadedPosition;
    private MotonAdapter motonAdapter;
    private BroadcastReceiver downloadMatnReceiver;

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
        binding = FragmentLibraryBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.blan));
        }

        viewModel = new ViewModelProvider(this).get(CollectionViewModel.class);

        viewModel.setBooksList();

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


        initializeMotonAdapter();

        downloadCollectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) return;
                if (action.equals(COLLECTION_DOWNLOAD)) {
                    String actionType = intent.getStringExtra("action");
                    if (actionType == null) return;

                    switch (actionType) {
                        case DOWNLOAD_CANCEL_ACTION ->
                                Toast.makeText(requireActivity(), "تم الغاء التحميل", Toast.LENGTH_LONG).show();
                        case DOWNLOAD_ERROR_ACTION -> {
                            if (dialog != null) dialog.dismiss();
                            String error = intent.getStringExtra("error");
                            Log.e(TAG, "error " + error);
                            Toast.makeText(requireActivity(), "خطأ", Toast.LENGTH_LONG).show();
                        }
                        case DOWNLOAD_COMPLETE_ACTION -> {
                            if (isBooksDownloadType) {
                                adapter.updateItem(LastClickedPosition);
                                if (dialog != null) dialog.dismiss();
                                moveToAyatFragment(ClickedCollectionName);
                            }
                        }
                    }
                }
            }
        };


        downloadBookReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) return;
                if (action.equals(BOOK_DOWNLOAD)) {
                    String actionType = intent.getStringExtra("type");
                    if (actionType == null) return;

                    switch (actionType) {
                        case DOWNLOAD_CANCEL_ACTION -> handleError("تم الغاء التحميل");
                        case DOWNLOAD_ERROR_ACTION -> handleError(intent.getStringExtra("message"));
                        case DOWNLOAD_COMPLETE_ACTION ->
                                downloadCompleted(bookDownloaded, bookDownloadedPosition);
                        case PROGRESS_ACTION ->
                                updateDialogProgress(intent.getIntExtra("progress", 0));
                    }
                }
            }
        };


        downloadMatnReceiver = new BroadcastReceiver() {
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
                                downloadMatnCompleted(matnDownloaded,matnDownloadedPosition);
                        case PROGRESS_ACTION ->
                                updateDialogProgress(intent.getIntExtra("progress",0));
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
            binding.includeSavedBook.tvMoveLibrary.setOnClickListener(v -> moveToBookChapters(lastBook));
        } else {
            binding.includeSavedBook.getRoot().setVisibility(View.GONE);
        }
    }

    private void moveToBookChapters(BookWithCount book) {
        Intent intent = new Intent(requireActivity(), ActivityChapterList.class);
        intent.putExtra("collectionName", book.bookCollection);
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


        viewModel.getBooksList().observe(getViewLifecycleOwner(), booksAvailableList -> {
            List<BookCollection> booksList = new ArrayList<>();
            booksList.add(new BookCollection("bukhari", booksAvailableList.contains("bukhari"), " أبو عبد الله محمد بن إسماعيل الجعفي البخاري", "صحيح البخاري", countItemsCount_bukhari));
            booksList.add(new BookCollection("muslim", booksAvailableList.contains("muslim"), "هو أبو الحسين مسلم بن الحجاج بن مسلم", "صحيح مسلم", countItemsCount_sahih_muslim));
            booksList.add(new BookCollection("nasai", booksAvailableList.contains("nasai"), "احمد بن شعيب النسائي", "سنن النسائي", countItemsCount_nisai));
            booksList.add(new BookCollection("ibnmajah", booksAvailableList.contains("ibnmajah"), "ابن ماجة", "سنن ابن ماجة", countItemsCount_ibn_majah));
            booksList.add(new BookCollection("hisn", booksAvailableList.contains("hisn"), " سعيد بن علي بن وهف القحطاني", "حصن المسلم", countItemsCount_hisn_muslim));
            booksList.add(new BookCollection("abudawud", booksAvailableList.contains("abudawud"), "أبي داود", "سنن أبي داود", countItemsCount_abudawaed));
            initializeArticlesAdapter(booksList);
            getAndAddBooksToAdapter();
        });
    }

    private void getAndAddBooksToAdapter() {
        adapter.addBookList(PublicMethods.getInstance().checkBookExistence(getbooksList(), requireActivity()));
    }

    private List<Book> getbooksList() {
        List<Book> allList = CsvReader.readBooksListFromCsv(requireActivity(), books_file_name, null);
        List<Book> parentList = new ArrayList<>();
        for (Book matn : allList) if (!matn.isParent()) parentList.add(matn);
        return parentList;
    }

    private List<Matn> getListMatn() {
        List<Matn> arrayList = CsvReader.readMotonListFromCsv(requireActivity(), moton_file_name, null);
        List<Matn> newList = new ArrayList<>();
        for (Matn matn1 : arrayList) if (!matn1.isParent()) newList.add(matn1);
        return newList;
    }

    public void initializeArticlesAdapter(List<BookCollection> collectionList) {
        adapter = new CollectionsAdapter(requireActivity(), new CollectionsAdapter.OnAdapterClickListener() {
            @Override
            public void onItemClick(BookCollection model, int position) {
                if (model.isDownloaded) {
                    //Open The book
                    moveToAyatFragment(model.CollectionName);
                } else {
                    ClickedCollectionName = model.CollectionName;
                    LastClickedPosition = position;
                    isBooksDownloadType = true;
                    prepareDownload(model);
                }
            }

            @Override
            public void onItemBookClick(Book model, int position) {
                if (model.isDownloaded) {
                    startActivity(new Intent(requireActivity(), ActivityBookViewer.class).putExtra("book", model));
                } else {
                    downloadPreparedBook(model,position);
                }
            }
        });
        binding.recyclerview.setLayoutManager(new GridLayoutManager(requireActivity(), 2, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);

        adapter.addCollectionList(collectionList);
    }

    private void prepareDownload(BookCollection model) {
        final int serviceState = ExtractBooksService.getState();
        if (serviceState == Statics.STATE_SERVICE.NOT_INIT)
            startDownload(model);
        else if (serviceState == Statics.STATE_SERVICE.PREPARE || serviceState == Statics.STATE_SERVICE.PLAY)
            PauseDownload();
    }

    private void startDownload(BookCollection bookCollection) {
        if (dialog != null) dialog.show();
        else showDialog(requireActivity());

        Intent startIntent = new Intent(requireActivity(), ExtractBooksService.class);
        startIntent.setAction(Statics.ACTION.START_ACTION);
        startIntent.putExtra("BookCollection", bookCollection);
        ContextCompat.startForegroundService(requireActivity(),startIntent);


    }

    private void PauseDownload() {
        Intent lPauseIntent = new Intent(requireActivity(), ExtractBooksService.class);
        lPauseIntent.setAction(Statics.ACTION.STOP_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(requireActivity(), 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            Log.e(TAG, "lPendingPauseIntent.send()");
            lPendingPauseIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void moveToAyatFragment(String collectionName) {
        Intent intent = new Intent(requireActivity(), ActivityBooksList.class);
        intent.putExtra("collectionName", collectionName);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        manageSavedBook();
        IntentFilter filter_download = new IntentFilter(COLLECTION_DOWNLOAD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(downloadCollectionReceiver, filter_download,RECEIVER_NOT_EXPORTED);
        }else
            requireActivity().registerReceiver(downloadCollectionReceiver, filter_download);

        IntentFilter filter_book_download = new IntentFilter(BOOK_DOWNLOAD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(downloadBookReceiver, filter_book_download,RECEIVER_NOT_EXPORTED);
        } else
            requireActivity().registerReceiver(downloadBookReceiver, filter_book_download);


        IntentFilter filter_matn_download = new IntentFilter(MOTON_DOWNLOAD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(downloadMatnReceiver,filter_matn_download,RECEIVER_NOT_EXPORTED);
        } else
            requireActivity().registerReceiver(downloadMatnReceiver,filter_matn_download);

    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(downloadCollectionReceiver);
        requireActivity().unregisterReceiver(downloadBookReceiver);
        requireActivity().unregisterReceiver(downloadMatnReceiver);
    }

    public static void showDialog(@NonNull Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.dialog_loading, null);
        CircularProgressIndicator circularProgress = view.findViewById(R.id.circular_progress);
        circularProgress.setIndeterminate(true);

        dialog.setContentView(view);
        dialog.show();
    }

    private void initializeMotonAdapter() {
        List<Matn> books = PublicMethods.getInstance().checkMatnExistence(getListMatn(),requireActivity());
        motonAdapter = new MotonAdapter(books, requireActivity(), (model, position) -> {
            if (model.isDownloaded) {
                startActivity(new Intent(requireActivity(),ActivityMatnViewer.class).putExtra("matn",model));
            } else {
                downloadPreparedMatn(model,position);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 2, LinearLayoutManager.VERTICAL, false);
        binding.recyclerviewAdkar.setHasFixedSize(true);
        binding.recyclerviewAdkar.setLayoutManager(gridLayoutManager);
        binding.recyclerviewAdkar.setAdapter(motonAdapter);
    }

    private void downloadPreparedBook(Book model,int position) {
        bookDownloaded = model;
        bookDownloadedPosition = position;
        showDownloadProgress();
        // Start the Worker to download the book.
        downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(new Data.Builder()
                        .putString("fileUrl", model.getFileUrl())
                        .putString("fileTitle", model.bookTitle)
                        .putString("action", BOOK_DOWNLOAD)
                        .putInt("notifyId", model.bookId)
                        .putString("fileName", model.fileName).build())
                .build();
        WorkManager.getInstance(requireActivity()).enqueue(downloadRequest);
    }

    private void downloadPreparedMatn(Matn model,int position) {
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
        WorkManager.getInstance(requireActivity()).enqueue(downloadRequest);
    }

    public void showDownloadProgress() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());

        binding_dialog = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()),
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
            WorkManager.getInstance(requireActivity()).cancelWorkById(downloadRequest.getId());
            dialog_download_matn.dismiss();
        });

        binding_dialog.btnCancel.setVisibility(View.GONE);
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
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("SetTextI18n")
    private void updateDialogProgress(int progress) {
        if (binding_dialog == null) return;
        binding_dialog.tvMessage.setText("جاري التحميل " + progress + "%");
        binding_dialog.progressDownload.setProgress(progress);
    }

    private void downloadCompleted(Book model, int position) {
        Toast.makeText(requireActivity(), "تم التحميل ", Toast.LENGTH_SHORT).show();

        if (model == null) return;
        if (dialog_download_matn != null && dialog_download_matn.isShowing())
            dialog_download_matn.dismiss();

        startActivity(new Intent(requireActivity(), ActivityBookViewer.class).putExtra("book", model));
        model.isDownloaded = true;
        if (adapter != null) adapter.notifyItemChanged(position);
    }

    private void downloadMatnCompleted(Matn model, int position) {
        Toast.makeText(requireActivity(), "تم التحميل ", Toast.LENGTH_SHORT).show();

        if (model == null) return;
        if (dialog_download_matn != null && dialog_download_matn.isShowing())
            dialog_download_matn.dismiss();

        startActivity(new Intent(requireActivity(),ActivityMatnViewer.class).putExtra("matn",model));
        model.isDownloaded = true;
        if (motonAdapter != null) motonAdapter.notifyItemChanged(position);
    }


}



