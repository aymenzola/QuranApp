package com.app.dz.quranapp.AdkarDetailsParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Hadith;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarModel;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAdkarDetailsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdkarDetailsAdapter extends RecyclerView.Adapter<AdkarDetailsAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Hadith> arrayList = new ArrayList<>();

    // creating a constructor class for our adapter class.
    public AdkarDetailsAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void setItems(List<Hadith> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdkarDetailsBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_adkar_details, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Hadith model = arrayList.get(position);
        holder.binding.tvDikr.setText(Html.fromHtml(model.body));
//        holder.binding.tvDescription.setText(model.getSource());

        holder.binding.clickView.setOnClickListener(v -> {

        });


    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemAdkarDetailsBinding binding;

        public ViewHolder_(ItemAdkarDetailsBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

}
