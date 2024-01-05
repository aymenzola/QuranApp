package com.app.dz.quranapp.quran.BooksManager;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Book;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemBookBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HadithStringAdapter extends RecyclerView.Adapter<HadithStringAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Book> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public HadithStringAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void setItems(List<Book> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_book, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Book model = arrayList.get(position);
        holder.binding.tvSuraName.setText(model.bookCollection);
        holder.binding.tvDescription.setText(Html.fromHtml(model.bookName));
        int count = position+1;
        holder.binding.tvCount.setText(""+count);

        holder.binding.tvSuraName.setOnClickListener(v-> listener.onItemClick(model));
        holder.binding.clickView.setOnClickListener(v-> listener.onItemClick(model));


    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemBookBinding binding;
        public ViewHolder_(ItemBookBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Book model);
    }

}
