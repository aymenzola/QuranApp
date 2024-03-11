package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte;


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
import com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte.Book;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CollectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mCtx;
    int TYPE_ITEM_COLLECTION = 1;
    int TYPE_ITEM_BOOK = 2;
    private List<Object> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public CollectionsAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void addBookList(List<Book> bookList) {
        int positionStart = arrayList.size();
        this.arrayList.addAll(bookList);
        notifyItemRangeInserted(positionStart,bookList.size());
    }

    public void addCollectionList(List<BookCollection> collectionList) {
        int positionStart = arrayList.size();
        this.arrayList.addAll(collectionList);
        notifyItemRangeInserted(positionStart,collectionList.size());
    }

    public Object getItem(int position) {
        return arrayList.get(position);
    }

    public void updateItem(int position) {
        if (arrayList.get(position) instanceof Book)
            ((Book) arrayList.get(position)).isDownloaded = true;
        else
            ((BookCollection) arrayList.get(position)).isDownloaded = true;
        notifyItemChanged(position);
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM_COLLECTION) {
            ItemCollectionAdapterrBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_collection_adapterr, parent, false);
            return new ViewHolder_(item);
        } else {
            ItemMatnBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),R.layout.item_matn,parent,false);
            return new ViewHolder_book(item);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM_COLLECTION)
            bindCollection((ViewHolder_) holder, position);
        else
            bindBook((ViewHolder_book) holder, position);
    }

    private void bindBook(ViewHolder_book holder, int position) {
        Book model = (Book) arrayList.get(position);

        holder.binding.tvMatnName.setText(model.bookTitle);
        holder.binding.tvMatnDescription.setText(model.bookDescription);
        Glide.with(mCtx).load(model.bookImage).into(holder.binding.matnImage);

        if (model.isParent()) {
            holder.binding.btnRead.setVisibility(View.GONE);
            holder.binding.getRoot().setOnClickListener(v -> listener.onItemBookClick(model, position));
        } else {
            holder.binding.btnRead.setOnClickListener(v -> listener.onItemBookClick(model, position));

            if (model.isDownloaded) {
                holder.binding.btnRead.setText("قراءة");
                holder.binding.btnRead.setBackgroundResource(R.drawable.shape_button_selected);
                holder.binding.getRoot().setOnClickListener(v -> listener.onItemBookClick(model, position));
            } else {
                holder.binding.btnRead.setText("تحميل");
                holder.binding.btnRead.setBackgroundResource(R.drawable.shape_button_download);
            }
        }
    }

    private void bindCollection(ViewHolder_ holder, int position) {
        BookCollection model = (BookCollection) arrayList.get(position);
        switch (model.CollectionName) {
            case "bukhari" ->
                    Glide.with(mCtx).load(R.drawable.saheehalbukhari1).into(holder.binding.bookImage);
            case "muslim" ->
                    Glide.with(mCtx).load(R.drawable.sahih_muslim).into(holder.binding.bookImage);
            case "nasai" ->
                    Glide.with(mCtx).load(R.drawable.sunan_al_nisaee).into(holder.binding.bookImage);
            case "ibnmajah" ->
                    Glide.with(mCtx).load(R.drawable.sibnmaja).into(holder.binding.bookImage);
            case "hisn" ->
                    Glide.with(mCtx).load(R.drawable.hisn_moslum).into(holder.binding.bookImage);
            case "abudawud" ->
                    Glide.with(mCtx).load(R.drawable.sunan_abi_daoud).into(holder.binding.bookImage);
            case "nawawi40" ->
                    Glide.with(mCtx).load(R.drawable.nawawi_40).into(holder.binding.bookImage);
        }
        holder.binding.tvBookName.setText(model.arabicName);
        holder.binding.tvBookDescription.setText("" + model.CollectionWriter);

        if (model.isDownloaded) {
            holder.binding.btnRead.setText("قراءة");
            holder.binding.btnRead.setBackgroundResource(R.drawable.shape_button_selected);
            holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(model, position));
        } else {
            holder.binding.btnRead.setText("تحميل");
            holder.binding.btnRead.setBackgroundResource(R.drawable.shape_button_download);
        }

        holder.binding.btnRead.setOnClickListener(v -> listener.onItemClick(model, position));

    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) instanceof BookCollection) {
            return TYPE_ITEM_COLLECTION;
        } else {
            return TYPE_ITEM_BOOK;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemCollectionAdapterrBinding binding;

        public ViewHolder_(ItemCollectionAdapterrBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public class ViewHolder_book extends RecyclerView.ViewHolder {
        ItemMatnBinding binding;

        public ViewHolder_book(ItemMatnBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }


    public interface OnAdapterClickListener {
        void onItemClick(BookCollection model, int position);

        void onItemBookClick(Book model, int position);
    }

}
