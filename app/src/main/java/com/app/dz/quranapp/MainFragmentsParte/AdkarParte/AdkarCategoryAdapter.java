package com.app.dz.quranapp.MainFragmentsParte.AdkarParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Chapter;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAdkarBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdkarCategoryAdapter extends RecyclerView.Adapter<AdkarCategoryAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Chapter> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public AdkarCategoryAdapter(List<Chapter> items, Context mCtx, OnAdapterClickListener listener1) {
        this.arrayList = items;
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void setItems(List<Chapter> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdkarBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_adkar, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Chapter model = arrayList.get(position);
        holder.binding.tvSuraName.setText(Html.fromHtml(model.chapterTitle));
//        holder.binding.tvDescription.setText(model.getCount()+" أذكار ");

        int p = position+1;
        holder.binding.tvCount.setText(""+p);

        holder.binding.tvSuraName.setOnClickListener(v-> listener.onItemClick(model));
        holder.binding.clickView.setOnClickListener(v-> listener.onItemClick(model));


    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemAdkarBinding binding;
        public ViewHolder_(ItemAdkarBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Chapter model);
    }

}
