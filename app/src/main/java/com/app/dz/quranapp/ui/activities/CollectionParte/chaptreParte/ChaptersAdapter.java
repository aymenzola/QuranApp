package com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte;


import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.databinding.ItemChaptersBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ViewHolder_> {

    private final List<Chapter> arrayList = new ArrayList<>();
    private final OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public ChaptersAdapter(OnAdapterClickListener listener1) {
        this.listener = listener1;
    }

    public void setItems(List<Chapter> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public List<Chapter> getArrayList() {
        return arrayList;
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChaptersBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_chapters, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Chapter model = arrayList.get(position);
        if (model.chapterTitle.equals("باب")) {
            int p = position + 1;
            holder.binding.tvTitle.setText("الباب " + p);
        } else {
            holder.binding.tvTitle.setText(Html.fromHtml(model.chapterTitle));
        }

        holder.binding.tvTitle.setOnClickListener(v -> listener.onItemClick(model, position));

        if (model.isSaved) {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
        } else {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
        }

        holder.binding.imgSave.setOnClickListener(v -> {
            if (!model.isSaved) {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                model.isSaved = true;
                listener.onItemSaveClick(model,true,position);
            } else {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                model.isSaved = false;
                listener.onItemSaveClick(model,false,position);
            }
        });


    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemChaptersBinding binding;
        public ViewHolder_(ItemChaptersBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Chapter model,int position);
        void onItemSaveClick(Chapter model, boolean isSaved,int position);
    }

}
