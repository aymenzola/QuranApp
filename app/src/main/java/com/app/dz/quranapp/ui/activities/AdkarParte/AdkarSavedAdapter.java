package com.app.dz.quranapp.ui.activities.AdkarParte;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAdkarBinding;
import com.app.dz.quranapp.databinding.ItemSavedDikrBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AdkarSavedAdapter extends RecyclerView.Adapter<AdkarSavedAdapter.ViewHolder> {

    private List<AdkarModel> adkarList;
    private ClickListener clickListener;

    public AdkarSavedAdapter(List<AdkarModel> adkarList, ClickListener clickListener) {
        this.adkarList = adkarList;
        this.clickListener = clickListener;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSavedDikrBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_saved_dikr, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AdkarModel adkarModel = adkarList.get(position);

        holder.binding.tvDikrTitle.setText(adkarModel.getDikrTitle());
        holder.binding.tvDikrCategory.setText(adkarModel.getCategory());
        holder.binding.tvOpenRead.setOnClickListener(v-> clickListener.onOpenClicked(adkarModel));

        if (adkarModel.getIsSaved() == 0) {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
        } else {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
        }

        holder.binding.imgSave.setOnClickListener(v -> {
            if (adkarModel.getIsSaved() == 0) {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                adkarModel.setIsSaved(1);
                clickListener.onClick(adkarModel,1);
            } else {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                adkarModel.setIsSaved(0);
                clickListener.onClick(adkarModel,0);
            }
        });

        holder.binding.imgSave.setOnClickListener(v -> {
            if (adkarModel.getIsSaved() == 1) {
                // Create a new Handler
                Handler handler = new Handler();

                // Show the Snackbar with the cancel button
                Snackbar snackbar = Snackbar.make(v, "الحدف من المحوظات بعد 4 ثواني", Snackbar.LENGTH_LONG);
                snackbar.setAction("Cancel", v1 -> {
                    // If the user presses cancel, remove the callbacks from the handler
                    handler.removeCallbacksAndMessages(null);
                    Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                });
                snackbar.show();

                // Post a delayed Runnable to the handler
                handler.postDelayed(() -> {
                    // This code will be executed after 4 seconds unless the user presses cancel
                    Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                    adkarModel.setIsSaved(0);
                    clickListener.onClick(adkarModel,0);
                }, 4000); // 4 seconds delay
            }
        });
    }

    @Override
    public int getItemCount() {
        return adkarList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSavedDikrBinding binding;
        public ViewHolder(ItemSavedDikrBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface ClickListener {
        void onClick(AdkarModel adkarModel,int isSaved);
        void onOpenClicked(AdkarModel adkarModel);
    }
}