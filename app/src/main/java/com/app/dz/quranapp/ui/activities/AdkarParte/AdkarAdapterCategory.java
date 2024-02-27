package com.app.dz.quranapp.ui.activities.AdkarParte;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAdkrCategoryBinding;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdkarAdapterCategory extends RecyclerView.Adapter<AdkarAdapterCategory.ViewHolder> {

    private final List<AdkarModel> adkarList;
    private final ClickListener clickListener;
    private final Context context;


    public AdkarAdapterCategory(List<AdkarModel> adkarList, ClickListener clickListener, Context contextt) {
        this.adkarList = adkarList;
        this.clickListener = clickListener;
        this.context = contextt;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdkrCategoryBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_adkr_category, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AdkarModel adkarModel = adkarList.get(position);
        holder.binding.tvDikrCategory.setText(adkarModel.getCategory());


        Log.e("TAG", "lunching images in adapter");
        switch (position) {
            case 0 -> holder.binding.img.setImageResource(R.drawable.svg1);
            case 1 -> holder.binding.img.setImageResource(R.drawable.svg2);
            case 2 -> holder.binding.img.setImageResource(R.drawable.ic_adkar_sabah);
            case 3 -> holder.binding.img.setImageResource(R.drawable.ic_adkar_masa);
            case 4 -> holder.binding.img.setImageResource(R.drawable.ic_adkar_image_safar);
            case 5 -> holder.binding.img.setImageResource(R.drawable.ic_adkar_image_tahsin);
        }


        holder.binding.getRoot().setOnClickListener(v -> clickListener.onClick(adkarModel));
        holder.binding.img.setOnClickListener(v -> clickListener.onClick(adkarModel));
    }

    @Override
    public int getItemCount() {
        return adkarList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // declare other views as needed
        ItemAdkrCategoryBinding binding;

        public ViewHolder(ItemAdkrCategoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface ClickListener {
        void onClick(AdkarModel adkarModel);
    }

}