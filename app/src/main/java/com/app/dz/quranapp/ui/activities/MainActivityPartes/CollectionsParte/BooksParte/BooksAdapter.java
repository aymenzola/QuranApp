package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.BooksParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemBooksBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder_> {

    private Context mCtx;
    private List<BookWithCount> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public BooksAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void setItems(List<BookWithCount> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBooksBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_books, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        BookWithCount model = arrayList.get(position);
        holder.binding.tvBookName.setText(model.bookName);
        holder.binding.tvChapterCount.setText(" عدد الابواب "+model.chaptersCount);

        holder.binding.tvBookName.setOnClickListener(v-> listener.onItemClick(model));
        holder.binding.getRoot().setOnClickListener(v-> listener.onItemClick(model));

        if (model.isSaved) {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
        } else {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
        }

        holder.binding.imgSave.setOnClickListener(v -> {
            if (!model.isSaved) {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                model.isSaved = true;
                listener.onItemSaveClick(model, true);
            } else {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                model.isSaved = false;
                listener.onItemSaveClick(model, false);
            }
        });

    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemBooksBinding binding;
        public ViewHolder_(ItemBooksBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(BookWithCount model);
        void onItemSaveClick(BookWithCount model, boolean isSaved);

    }

}
