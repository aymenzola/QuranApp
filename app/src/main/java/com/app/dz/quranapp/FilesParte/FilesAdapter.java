package com.app.dz.quranapp.FilesParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.MushafParte.AudioFile;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemFileAdapterBinding;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder_> {

    private Context mCtx;
    private List<AudioFile> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;
    private int readerId;

    // creating a constructor class for our adapter class.
    public FilesAdapter(int readerId,Context mCtx, List<AudioFile> audioFileNames, OnAdapterClickListener listener1) {
        this.readerId = readerId;
        this.mCtx = mCtx;
        this.listener = listener1;
        this.arrayList = audioFileNames;
    }

    public AudioFile getItem(int position) {
        return arrayList.get(position);
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFileAdapterBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_file_adapter, parent, false);
        return new ViewHolder_(item);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        AudioFile model = arrayList.get(position);
        holder.binding.tvFileName.setText(model.fileName);
        holder.binding.tvBookDescription.setText(model.fileSize+" MB ");
        holder.binding.clickView.setOnClickListener(v -> {});
        holder.binding.tvFileRemove.setOnClickListener(v -> listener.onItemClick(model,position));

        switch (readerId) {
            case 1:
                Glide.with(mCtx).load(R.drawable.alafasy).into(holder.binding.fileImage);
                break;
            case 2:
                Glide.with(mCtx).load(R.drawable.sharum).into(holder.binding.fileImage);
                break;
            case 3:
                Glide.with(mCtx).load(R.drawable.sudais).into(holder.binding.fileImage);
                break;
            case 4:
                Glide.with(mCtx).load(R.drawable.al_manshawi).into(holder.binding.fileImage);
                break;
            default:
                Glide.with(mCtx).load(R.drawable.abd_baset).into(holder.binding.fileImage);
                break;
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void fileDeleted(int position) {
        arrayList.remove(position);
        notifyDataSetChanged();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemFileAdapterBinding binding;

        public ViewHolder_(ItemFileAdapterBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(AudioFile model, int position);
    }

}
