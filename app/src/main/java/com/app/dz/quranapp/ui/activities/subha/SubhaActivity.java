package com.app.dz.quranapp.ui.activities.subha;


import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentSubhaBinding;

import java.util.List;

public class SubhaActivity extends AppCompatActivity {

    public final static String TAG = "SubhaActivity";
    private FragmentSubhaBinding binding;

    private int count = 0;
    private AdkarListAdapter adapter;

    public SubhaActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSubhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }

        setListeners();

    }


    private void setListeners() {


        binding.includeFastAdkarCard.tvDikrText.setText(AdkarSubhaUtils.getLastDikr(this));
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.includeFastAdkarCard.tvDikrText.setSelected(true);

        binding.includeFastAdkarCard.btnSubhaClicked.setOnClickListener(v -> {
            count++;
            binding.includeFastAdkarCard.btnSubhaClicked.setText(String.valueOf(count));
        });

        binding.includeFastAdkarCard.btnReset.setOnClickListener(v -> {
            count = 0;
            binding.includeFastAdkarCard.btnSubhaClicked.setText(String.valueOf(count));
        });
        binding.tvChooseText.setOnClickListener(v->{
            binding.relativeChooseDikr.setVisibility(View.VISIBLE);
            binding.includeFastAdkarCard.getRoot().setVisibility(View.GONE);
            if (adapter == null) {
                showsAdkarStringAdapter(getAdkarList());
            }
        });


        binding.btnCancelNewDikr.setOnClickListener(v->{
            binding.btnAddNewDikr.setTag("add");
            binding.btnAddNewDikr.setText("اضافة ذكر جديد");
            binding.btnCancelNewDikr.setVisibility(View.GONE);
            adapter.cancelNewItem();
        });

        binding.btnAddNewDikr.setOnClickListener(v->{
           if (v.getTag().equals("add")){
                binding.btnCancelNewDikr.setVisibility(View.VISIBLE);
               binding.btnAddNewDikr.setTag("save");
                binding.btnAddNewDikr.setText("حفظ");
                adapter.addNewItem();
                //should scroll to the top
                binding.recyclerView.smoothScrollToPosition(0);
         } else {
               binding.btnCancelNewDikr.setVisibility(View.GONE);
               binding.btnAddNewDikr.setTag("add");
               binding.btnAddNewDikr.setText("اضافة ذكر جديد");
               adapter.saveNewItem();
           }

        });

    }

    private void showsAdkarStringAdapter(List<AdkarListAdapter.DikrItem> adkarList) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        adapter = new AdkarListAdapter(adkarList,new AdkarListAdapter.OnAdapterClickListener() {
            @Override
            public void onItemClick(AdkarListAdapter.DikrItem dikrItem) {
                binding.relativeChooseDikr.setVisibility(View.GONE);
                binding.includeFastAdkarCard.getRoot().setVisibility(View.VISIBLE);

                count = 0;
                binding.includeFastAdkarCard.btnSubhaClicked.setText(String.valueOf(count));
                binding.includeFastAdkarCard.tvDikrText.setText(dikrItem.dikr);
            }

            @Override
            public void onDikrAdded(AdkarListAdapter.DikrItem model) {
                AdkarSubhaUtils.addDikrItem(SubhaActivity.this,model);
                adapter.updateList(AdkarSubhaUtils.getAdkarList(SubhaActivity.this));
            }

            @Override
            public void onDikrCancel(AdkarListAdapter.DikrItem model) {
                binding.btnCancelNewDikr.setVisibility(View.GONE);
            }
        });
        binding.recyclerView.setAdapter(adapter);
        adapter.setItems(getAdkarList());
    }

    private List<AdkarListAdapter.DikrItem> getAdkarList() {
        return AdkarSubhaUtils.getAdkarList(this);
    }

    @Override
    public void onBackPressed() {
        //if the choose dikr is visible, hide it
        if (binding.relativeChooseDikr.getVisibility() == View.VISIBLE) {
            binding.relativeChooseDikr.setVisibility(View.GONE);
            binding.includeFastAdkarCard.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }
}



