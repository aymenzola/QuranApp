package com.app.dz.quranapp.quran.QuranSearchParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.AyaWithSura;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemSearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuranSearchAdapter extends RecyclerView.Adapter<QuranSearchAdapter.ViewHolder_> {

    private Context mCtx;
    private List<AyaWithSura> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public QuranSearchAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void addAyat(List<AyaWithSura> items) {
        Log.e("threadcheck","addAyat methode "+Thread.currentThread().getName());
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public void clear(){
        arrayList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemSearchBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_search, parent, false);
        return new ViewHolder_(item);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {
        int p = position + 1;
        holder.binding.tvCount.setText("" + p);

        AyaWithSura model = arrayList.get(position);
        holder.binding.tvSuraName.setText(model.aya.getStringBuilder());
        holder.binding.tvDescription.setText("سورة " + model.sura.getName());
        holder.binding.clickView.setOnClickListener(v -> listener.onAyaClick(model));
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemSearchBinding binding;

        public ViewHolder_(ItemSearchBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onAyaClick(AyaWithSura ayaWithSura);
    }

}
