package com.app.dz.quranapp.CollectionParte.HadithDetailsParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Book;
import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemHadithBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HadithsAdapter extends RecyclerView.Adapter<HadithsAdapter.ViewHolder_> {

    private String destination;
    private Context mCtx;
    private List<Hadith> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public HadithsAdapter(String destination,Context mCtx, OnAdapterClickListener listener1) {
        this.destination = destination;
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void setItems(List<Hadith> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public Hadith getItem(int position) {
        return arrayList.get(position);
    }


    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHadithBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_hadith, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Hadith hadith = arrayList.get(position);
        holder.binding.tvHadith.setText(Html.fromHtml(hadith.body));

        holder.binding.tvDestination.setText(destination);
        holder.binding.tvChapter.setText(hadith.chapterTitle);
        holder.binding.grade.setText("صحة الحديث "+hadith.grade);
        holder.binding.tvGradedBy.setText(hadith.graded_by);

    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void addItems(List<Hadith> hadithList) {
    arrayList.addAll(hadithList);
    notifyDataSetChanged();
    }
    public void addItemsAtFirst(List<Hadith> hadithList) {
    arrayList.addAll(0,hadithList);
    notifyDataSetChanged();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemHadithBinding binding;
        public ViewHolder_(ItemHadithBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Book model);
    }

}
