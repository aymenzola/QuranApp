package com.app.dz.quranapp.MushafParte.riwayat_parte;


import static com.app.dz.quranapp.MainFragmentsParte.HomeFragment.HomeFragment.QURAN_TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Juz;
import com.app.dz.quranapp.Entities.Riwaya;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemJuzaBinding;
import com.app.dz.quranapp.databinding.ItemRiwayaBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Riwaya> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public RiwayatAdapter(Context mCtx,List<Riwaya> riwayaList, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
        this.arrayList = riwayaList;
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRiwayaBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_riwaya, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Riwaya model = arrayList.get(position);
        holder.binding.tvRiwayaName.setText("" + model.name);
        Glide.with(mCtx).load(model.image).placeholder(R.drawable.app_icon).into(holder.binding.tvImage);

        holder.binding.clickView.setOnClickListener(v -> listener.onItemClick(model));
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemRiwayaBinding binding;

        public ViewHolder_(ItemRiwayaBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Riwaya riwaya);
    }

}
