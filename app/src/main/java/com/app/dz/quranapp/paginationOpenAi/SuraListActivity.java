package com.app.dz.quranapp.paginationOpenAi;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.dz.quranapp.databinding.FragmentQuranListBinding;

public class SuraListActivity extends AppCompatActivity {
    private FragmentQuranListBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentQuranListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ConcertViewModel viewModel = new ViewModelProvider(this).get(ConcertViewModel.class);
        SuraAdapter2 adapter = new SuraAdapter2();
        viewModel.concertList.observe(this,adapter::submitList);
        binding.recyclerview.setAdapter(adapter);
    }

}