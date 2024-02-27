package com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment;


import static com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment.HomeFragment.QURAN_TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.Juz;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemJuzaBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JuzaAdapter extends RecyclerView.Adapter<JuzaAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Juz> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;
    private int selectedPosition = -1;

    // creating a constructor class for our adapter class.
    public JuzaAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void setItems(List<Juz> items) {
        Log.e(QURAN_TAG, "set item "+items.size());
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJuzaBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_juza, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Juz model = arrayList.get(position);
        holder.binding.tvSuraName.setText("الجزء " + model.getId());

        int p = position+1;
        holder.binding.tvCount.setText(""+p);
        holder.binding.tvSuraName.setTextColor(Color.BLACK);

        holder.binding.clickView.setOnClickListener(v -> listener.onItemClick(model));
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemJuzaBinding binding;

        public ViewHolder_(ItemJuzaBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Juz juz);
    }

}
