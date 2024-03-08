package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.BookCollection;
import com.app.dz.quranapp.databinding.ItemCollectionAdapterrBinding;
import com.app.dz.quranapp.databinding.ItemMatnBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MotonAdapter extends RecyclerView.Adapter<MotonAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Matn> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public MotonAdapter(List<Matn> list, Context mCtx, OnAdapterClickListener listener1) {
        this.arrayList = list;
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public Matn getItem(int position) {
        return arrayList.get(position);
    }


    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMatnBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_matn, parent, false);
        return new ViewHolder_(item);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        Matn model = arrayList.get(position);

        holder.binding.tvMatnName.setText(model.matnTitle);
        holder.binding.tvMatnDescription.setText(model.matnDescription);
        Glide.with(mCtx).load(model.matnImage).into(holder.binding.matnImage);

        if (model.isParent()) {
            holder.binding.btnRead.setVisibility(View.GONE);
            holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(model, position));
        } else {
            holder.binding.btnRead.setOnClickListener(v -> listener.onItemClick(model, position));

            if (model.isDownloaded) {
                holder.binding.btnRead.setText("قراءة");
                holder.binding.btnRead.setBackgroundResource(R.drawable.shape_button_selected);
                holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(model,position));
            } else {
                holder.binding.btnRead.setText("تحميل");
                holder.binding.btnRead.setBackgroundResource(R.drawable.shape_button_download);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemMatnBinding binding;

        public ViewHolder_(ItemMatnBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Matn model, int position);
    }

}
