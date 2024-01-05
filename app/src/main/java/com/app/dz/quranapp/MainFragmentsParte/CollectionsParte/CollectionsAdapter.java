package com.app.dz.quranapp.MainFragmentsParte.CollectionsParte;


import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_CANCEL_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_COMPLETE_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_PREPAREING_FILES_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.NORMAL_ACTION;
import static com.app.dz.quranapp.PlayerAudioNotification.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.BookCollection;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemCollectionAdapterrBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.ViewHolder_> {

    private Context mCtx;
    private List<BookCollection> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public CollectionsAdapter(List<BookCollection> list,Context mCtx, OnAdapterClickListener listener1) {
        this.arrayList = list;
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public BookCollection getItem(int position) {
        return arrayList.get(position);
    }

    public void updateItem(int position) {
        arrayList.get(position).isDownloaded = true;
        notifyItemChanged(position);
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCollectionAdapterrBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_collection_adapterr, parent, false);
        return new ViewHolder_(item);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        BookCollection model = arrayList.get(position);
        switch (model.CollectionName) {
            case "bukhari":
                Glide.with(mCtx).load(R.drawable.saheehalbukhari1).into(holder.binding.bookImage);
                break;
            case "muslim":
                Glide.with(mCtx).load(R.drawable.sahih_muslim).into(holder.binding.bookImage);
                break;
            case "nasai":
                Glide.with(mCtx).load(R.drawable.sunan_al_nisaee).into(holder.binding.bookImage);
                break;
            case "ibnmajah":
                Glide.with(mCtx).load(R.drawable.sibnmaja).into(holder.binding.bookImage);
                break;
            case "hisn":
                Glide.with(mCtx).load(R.drawable.hisn_moslum).into(holder.binding.bookImage);
                break;
            case "abudawud":
                Glide.with(mCtx).load(R.drawable.sunan_abi_daoud).into(holder.binding.bookImage);
                break;
            case "nawawi40":
                Glide.with(mCtx).load(R.drawable.nawawi_40).into(holder.binding.bookImage);
                break;

        }
        holder.binding.tvBookName.setText(model.arabicName);
        holder.binding.tvBookDescription.setText("المؤلف : " + model.CollectionWriter);
        holder.binding.clickView.setOnClickListener(v -> listener.onItemClick(model, position));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

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

    public interface OnAdapterClickListener {
        void onItemClick(BookCollection model, int position);
    }

}
