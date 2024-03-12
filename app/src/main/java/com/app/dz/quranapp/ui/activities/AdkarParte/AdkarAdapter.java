package com.app.dz.quranapp.ui.activities.AdkarParte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAdkarBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdkarAdapter extends RecyclerView.Adapter<AdkarAdapter.ViewHolder> {

    private final List<AdkarModel> adkarList;
    private final ClickListener clickListener;

    public AdkarAdapter(List<AdkarModel> adkarList, ClickListener clickListener) {
        this.adkarList = adkarList;
        this.clickListener = clickListener;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdkarBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_adkar, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AdkarModel adkarModel = adkarList.get(position);

        if (adkarModel.getDikrTitle()==null || adkarModel.getDikrTitle().isEmpty()) {
            holder.binding.tvTitle.setText("الذكر " + (position + 1));
        } else {
            holder.binding.tvTitle.setText(adkarModel.getDikrTitle());
        }

        holder.binding.tvDikrText.setText(adkarModel.getDikr());


        if (adkarModel.isExpanded &&  holder.binding.tvDikrText.getVisibility() == View.GONE) {
            holder.binding.tvDikrText.setVisibility(TextView.VISIBLE);
        } else if (!adkarModel.isExpanded &&  holder.binding.tvDikrText.getVisibility() == View.VISIBLE){
            holder.binding.tvDikrText.setVisibility(TextView.GONE);
        }

        holder.binding.tvTitle.setOnClickListener(v -> {
            if (adkarModel.isExpanded) {
                holder.binding.tvDikrText.setVisibility(TextView.GONE);
                adkarModel.isExpanded = false;
            } else {
                holder.binding.tvDikrText.setVisibility(TextView.VISIBLE);
                adkarModel.isExpanded = true;
            }
        });

        if (adkarModel.getIsSaved() == 0) {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
        } else {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
        }

        holder.binding.imgSave.setOnClickListener(v -> {
            if (adkarModel.getIsSaved() == 0) {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                adkarModel.setIsSaved(1);
                clickListener.onClick(adkarModel, 1);
            } else {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                adkarModel.setIsSaved(0);
                clickListener.onClick(adkarModel, 0);
            }
        });

    }

    @Override
    public int getItemCount() {
        return adkarList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemAdkarBinding binding;

        public ViewHolder(ItemAdkarBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface ClickListener {
        void onClick(AdkarModel adkarModel, int isSaved);
    }
}