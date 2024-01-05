package com.app.dz.quranapp.MushafParte.mushaf_list;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<MushafItem> mItems;

    public MyAdapter(List<MushafItem> items) {
        mItems = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mushaf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MushafItem item = mItems.get(position);

        holder.itemName.setText(item.getName());

        Log.e("tagadapter","in adapter "+position);
        if (item.getProgress() == -1) {
            //download finished
            holder.progressBar.setVisibility(View.GONE);
            holder.downloadButton.setVisibility(View.GONE);
        }else
            if (item.isDownloading()) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.downloadButton.setVisibility(View.GONE);
            holder.progressBar.setProgress(item.getProgress());
            Log.e("tagadapter","progress "+item.getProgress());
        } else {
            Log.e("tagadapter","4 in adapter "+position);
            holder.progressBar.setVisibility(View.GONE);
            holder.downloadButton.setVisibility(View.VISIBLE);
        }

        holder.downloadButton.setOnClickListener(v -> {
            item.setDownloading(true);
            notifyItemChanged(position);

            // Start the download service and pass in the item ID
            Intent intent = new Intent(v.getContext(),DownloadService.class);
            intent.putExtra("item_id", item.getId());
            v.getContext().startService(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public MushafItem getItem(int i) {
    return mItems.get(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName;
        public Button downloadButton;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.item_name);
            downloadButton = (Button) itemView.findViewById(R.id.download_button);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}