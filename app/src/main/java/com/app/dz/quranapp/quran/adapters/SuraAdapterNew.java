package com.app.dz.quranapp.quran.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.Sura;
import com.app.dz.quranapp.databinding.ItemSuraBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SuraAdapterNew extends RecyclerView.Adapter<SuraAdapterNew.ViewHolder_> {

    private final Context mCtx;
    private List<Sura> arrayList = new ArrayList<>();
    private final OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public SuraAdapterNew(List<Sura> items, Context mCtx, OnAdapterClickListener listener1) {
        this.arrayList = items;
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public Sura getSuraItem(int suraId) {
        if (suraId > 0 && suraId <= arrayList.size()){
            return arrayList.get(suraId - 1);
        } else {
            return null;
        }
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSuraBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_sura, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Sura model = arrayList.get(position);
        holder.binding.tvSuraName.setText(model.getName());
        Log.e("logdata", "sura data " + model);

        int p = position + 1;
        holder.binding.tvSuraNumber.setText(String.valueOf(model.getId()));
        holder.binding.tvAyatNumber.setText(" اياتها " + model.getAyas());
        holder.binding.tvSuraName.setOnClickListener(v -> listener.onItemClick(model));
        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(model));

        if (model.getType().equals("Medinan")) {
            Glide.with(mCtx).load(R.drawable.ic_madaniya).into(holder.binding.imgPlace);
        } else {
            Glide.with(mCtx).load(R.drawable.ic_makia).into(holder.binding.imgPlace);
        }

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemSuraBinding binding;

        public ViewHolder_(ItemSuraBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Sura model);
    }

}
