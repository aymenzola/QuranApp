package com.app.dz.quranapp.CollectionParte.chaptreParte;


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
import com.app.dz.quranapp.databinding.ItemChapterBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Chapter> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public ChaptersAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void setItems(List<Chapter> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public List<Chapter> getArrayList(){
        return arrayList;
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_chapter, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Chapter model = arrayList.get(position);
        int count = position+1;
        holder.binding.tvCount.setText(""+count);
        if (model.chapterTitle.equals("باب")){
            int p = position+1;
            holder.binding.tvSuraName.setText("الباب "+p);
        }else {
            holder.binding.tvSuraName.setText(Html.fromHtml(model.chapterTitle));

        }

        holder.binding.tvSuraName.setOnClickListener(v-> listener.onItemClick(model,position));
        holder.binding.clickView.setOnClickListener(v-> listener.onItemClick(model,position));


    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemChapterBinding binding;
        public ViewHolder_(ItemChapterBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Chapter model,int position);
    }

}
