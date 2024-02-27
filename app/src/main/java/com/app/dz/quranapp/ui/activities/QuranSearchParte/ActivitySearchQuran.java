package com.app.dz.quranapp.ui.activities.QuranSearchParte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.data.room.Entities.AyaWithSura;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentQuranSearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class ActivitySearchQuran extends AppCompatActivity {
    private final BlockingQueue<List<AyaWithSura>> queue = new LinkedBlockingQueue<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String TAG = ActivitySearchQuran.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static final int searchPageSize = 10; // Maximum number of items to return in each page
    public int currentPage = 0; // Index of the current page


    private FragmentQuranSearchBinding binding;
    private QuranSearchAdapter adapter;
    private QuranSearchViewModel searchViewModel;

    private OnFragmentInteractionListener mListener;
    private List<String> suggestionList = new ArrayList<>();
    private Handler handler = new Handler();
    private String searchedText;
    private boolean isLastData = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentQuranSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }

        searchViewModel = new ViewModelProvider(this).get(QuranSearchViewModel.class);

        initializeArticlesAdapter();

        setObservers();

        setListeners();

        executor.execute(() -> {
            while (true) {
                try {
                    List<AyaWithSura> myObjects = queue.take(); // This will block until a new item is available
                    List<AyaWithSura> ayaWithSuraList = new ArrayList<>();
                    Log.e(TAG, "0 search class " + Thread.currentThread().getName());
                    for (AyaWithSura aya_sura : myObjects) {

                        String fasila = "﴿" + aya_sura.aya.getSuraAya() + "﴾";
                        String aya_with_fasila = aya_sura.aya.getPureText() + fasila;
                        SpannableStringBuilder builder = new SpannableStringBuilder(aya_with_fasila);
                        int start = aya_sura.aya.getPureText().indexOf(searchedText);
                        int end = start + searchedText.length();

                        int fasila_lenght = fasila.length();

                        BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.YELLOW);
                        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.parseColor("#B07A1A"));
                        builder.setSpan(backgroundSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builder.setSpan(foregroundSpan, aya_with_fasila.length() - fasila_lenght, aya_with_fasila.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        aya_sura.aya.setStringBuilder(builder);
                        ayaWithSuraList.add(new AyaWithSura(aya_sura.aya, aya_sura.sura));
                    }
                        runOnUiThread(() -> {
                            manageStateView(View.VISIBLE, View.GONE, View.GONE);
//                            TODO binding.tvItemsCount.setText("عدد النتائج : " + ayaWithSuraList.size());
                            Log.e(TAG, "1 search calss " + Thread.currentThread().getName());
                            if (ayaWithSuraList.size() < 10) isLastData = true;
                            adapter.addAyat(ayaWithSuraList);
                        });


                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });


    }


    private final Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            initilizeForNewSearch();
            searchViewModel.searchInaAyat(searchedText, 0);
        }
    };

    private void setListeners() {
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged ");
                if (binding.editSearch.length() > 0) {
                    if (binding.itemClearClickParent.getVisibility() != View.VISIBLE) {
                        binding.itemClearClickParent.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.itemClearClickParent.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 3) prepareSearch(s.toString());
            }
        });

        binding.editSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.editSearch.getText().toString().length() >= 3) {
                    // search icon clicked
                    binding.editSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(binding.editSearch.getWindowToken(), 0);

                    manageStateView(View.GONE, View.VISIBLE, View.GONE);
                    searchedText = binding.editSearch.getText().toString();
                    initilizeForNewSearch();
                    searchViewModel.searchInaAyat(binding.editSearch.getText().toString(), 0);
                    Log.e(TAG, "search icon clicked " + binding.editSearch.getText().toString());

                    return true;
                } else {
                    Toast.makeText(ActivitySearchQuran.this, "النص قصير", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });

        binding.itemClearClickParent.setOnClickListener(v -> {
            binding.editSearch.getText().clear();
            initilizeForNewSearch();
        });

    }

    private void initilizeForNewSearch() {
        currentPage = 0;
        isLastData = false;
        adapter.clear();
    }

    private void prepareSearch(String s) {
        manageStateView(View.GONE, View.VISIBLE, View.GONE);
        handler.removeCallbacks(searchRunnable);
        handler.postDelayed(searchRunnable, 2000);
        searchedText = s;
    }


    @SuppressLint("SetTextI18n")
    private void setObservers() {
        searchViewModel.getSearchAyat().observe(ActivitySearchQuran.this, ayaWithSuras -> {

            if (ayaWithSuras.size() > 0 && binding.editSearch.getText().toString().length() >= 3)
                queue.offer(ayaWithSuras);
            else manageStateView(View.GONE, View.GONE, View.VISIBLE);

        });

        searchViewModel.getSearchSize().observe(ActivitySearchQuran.this, integer -> {
            if (integer != null && integer > 0) {
                binding.tvItemsCount.setText("عدد النتائج : " + integer);
            }
        });
    }

    private void manageStateView(int value_recyclerview, int value_progressBar, int value_tvNoResult) {
        binding.tvItemsCount.setVisibility(value_recyclerview);
        binding.recyclerview.setVisibility(value_recyclerview);
        binding.progressBar.setVisibility(value_progressBar);
        binding.tvNoResult.setVisibility(value_tvNoResult);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSearchBarClicked(String word, boolean isSuggestion);
    }


    public void initializeArticlesAdapter() {

        adapter = new QuranSearchAdapter(ActivitySearchQuran.this, ayaWithSura -> {
            //Move to quran page
            moveToAyatMushaf(ayaWithSura.aya.getPage());
        });


        binding.recyclerview.setLayoutManager(new LinearLayoutManager(ActivitySearchQuran.this, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLastData && !binding.recyclerview.canScrollVertically(1)) {
                    Log.e(TAG, "load more data");
                    searchViewModel.searchInaAyat(binding.editSearch.getText().toString(), ++currentPage * searchPageSize);
                }
            }
        });


    }

    private void moveToAyatMushaf(int page) {
        Intent intent = new Intent(ActivitySearchQuran.this,MainActivity.class);
        intent.putExtra("page",page);
        startActivity(intent);

    }

}
