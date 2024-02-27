package com.app.dz.quranapp.ui.activities.AdkarParte.AdkarDetailsParte;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentBookListBinding;

import java.util.List;


public class ActivityDikrDetailsList extends AppCompatActivity {

    private AdkarDetailsAdapter adapter;
    private AdkarDetailsViewModel viewModel;
    private FragmentBookListBinding binding;
    private String categoryName;

    public ActivityDikrDetailsList() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentBookListBinding.inflate(getLayoutInflater());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AdkarDetailsViewModel.class);

        Intent intent = getIntent();
        if (intent != null) categoryName = intent.getStringExtra("categoryName");

        initializeArticlesAdapter();
        getAdkar("hisn",categoryName);
        setObservers();

        binding.tvSize.setOnClickListener(v -> {
        });

        binding.included.imgBack.setOnClickListener(v->onBackPressed());
        binding.included.imgSearch.setVisibility(View.GONE);
        binding.included.tvTitle.setText("اذكار المسلم");
    }


    private void setObservers() {
        viewModel.getAdkarModel().observe(ActivityDikrDetailsList.this, hadithList -> {
            if (hadithList != null && hadithList.size() > 0) displayData(hadithList);
        });
    }

    public void initializeArticlesAdapter() {

        adapter = new AdkarDetailsAdapter(ActivityDikrDetailsList.this);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(ActivityDikrDetailsList.this, RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);

    }


    public void getAdkar(String categoryName,String chapterTitle) {
        viewModel.setAdkarByCategory(categoryName,chapterTitle);
    }

    private void displayData(List<Hadith> items) {
        adapter.setItems(items);
    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }


}
