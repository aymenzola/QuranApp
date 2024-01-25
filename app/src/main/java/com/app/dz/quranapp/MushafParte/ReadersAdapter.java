package com.app.dz.quranapp.MushafParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemReaderBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReadersAdapter extends RecyclerView.Adapter<ReadersAdapter.ViewHolder_Reader> {

    private final Context mCtx;
    private final List<ReaderAudio> arrayList;
    private final OnAdapterClickListener listener;
    private int lastselcted = -1;

    public ReadersAdapter(List<ReaderAudio> arrayList, Context mCtx, OnAdapterClickListener listener1) {
        this.arrayList = arrayList;
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public ReaderAudio getItem(int position) {
        return arrayList.get(position);
    }

    public void selectItem(int position) {
        if (lastselcted != -1) {
            ReaderAudio reader = arrayList.get(lastselcted);
            reader.setSelected(false);
            arrayList.set(lastselcted, reader);
            notifyItemChanged(lastselcted);
        }
        lastselcted = position;
        ReaderAudio reader = arrayList.get(position);
        reader.setSelected(true);
        arrayList.set(position, reader);
        notifyItemChanged(position);
    }

    @NonNull
    public ViewHolder_Reader onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReaderBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_reader, parent, false);
        return new ViewHolder_Reader(item);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_Reader holder, int position) {

        ReaderAudio reader = arrayList.get(position);

        holder.binding.itemName.setText(reader.getName());

        if (reader.isSelected()) {
            holder.binding.linearParent.setBackgroundColor(mCtx.getResources().getColor(R.color.selectitem_color));
        } else
            holder.binding.linearParent.setBackgroundColor(mCtx.getResources().getColor(R.color.white));

        switch (reader.getId()) {
            case 1:
                Glide.with(mCtx).load(R.drawable.alafasy).into(holder.binding.readerImage);
                break;
            case 2:
                Glide.with(mCtx).load(R.drawable.sharum).into(holder.binding.readerImage);
                break;
            case 3:
                Glide.with(mCtx).load(R.drawable.sudais).into(holder.binding.readerImage);
                break;
            case 4:
                Glide.with(mCtx).load(R.drawable.khalil_hosary).into(holder.binding.readerImage);
                break;
            default:
                Glide.with(mCtx).load(R.drawable.abd_baset).into(holder.binding.readerImage);
                break;

        }

        holder.binding.viewId.setOnClickListener(v -> {
            selectItem(position);
            listener.onClick(reader, position);
        });
        holder.binding.audioImage.setOnClickListener(v -> {
            selectItem(position);
            listener.onAudioPlayClicked(reader, position);
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder_Reader extends RecyclerView.ViewHolder {
        ItemReaderBinding binding;

        public ViewHolder_Reader(ItemReaderBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onClick(ReaderAudio reader, int position);

        void onAudioPlayClicked(ReaderAudio reader, int position);
    }

}
