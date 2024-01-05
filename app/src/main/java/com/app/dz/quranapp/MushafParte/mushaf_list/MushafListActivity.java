package com.app.dz.quranapp.MushafParte.mushaf_list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.databinding.FragmentMyBinding;

import java.util.ArrayList;
import java.util.List;

public class MushafListActivity extends AppCompatActivity {

    private MyAdapter mAdapter;

    private FragmentMyBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentMyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAdapter = new MyAdapter(getItems());
        LinearLayoutManager manager = new LinearLayoutManager(MushafListActivity.this, RecyclerView.VERTICAL,false);
        binding.recyclerview.setLayoutManager(manager);
        binding.recyclerview.setAdapter(mAdapter);
        Log.e("tagadapter","in activity ");
        // Register the broadcast receiver
        LocalBroadcastManager.getInstance(MushafListActivity.this).registerReceiver(mProgressReceiver, new IntentFilter("download_progress"));

    }


    private List<MushafItem> getItems() {
        List<MushafItem> mushafItemList = new ArrayList<>();
        mushafItemList.add(new MushafItem(1,"رواية ورش",0,false));
        mushafItemList.add(new MushafItem(2,"رواية حفص",0,false));
        mushafItemList.add(new MushafItem(2,"رواية ورش مع احام التجويد",0,false));
        return mushafItemList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the broadcast receiver
        LocalBroadcastManager.getInstance(MushafListActivity.this).unregisterReceiver(mProgressReceiver);
    }

    private BroadcastReceiver mProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long itemId = intent.getLongExtra("item_id", -1);
            int progress = intent.getIntExtra("progress", -1);

            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                MushafItem item = mAdapter.getItem(i);
                if (item.getId() == itemId) {
                    if (progress==-1) item.setDownloading(false);
                    item.setProgress(progress);
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    };
}