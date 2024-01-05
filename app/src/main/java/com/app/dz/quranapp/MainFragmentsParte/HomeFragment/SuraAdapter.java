package com.app.dz.quranapp.MainFragmentsParte.HomeFragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Sura;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemSuraSmallBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SuraAdapter extends RecyclerView.Adapter<SuraAdapter.ViewHolder_> {

    private Context mCtx;
    private List<Sura> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public SuraAdapter(List<Sura> items, Context mCtx, OnAdapterClickListener listener1) {
        this.arrayList = items;
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSuraSmallBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_sura_small,parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

        Sura model = arrayList.get(position);
        holder.binding.tvSuraName.setText(model.getName());
        Log.e("logdata","sura data "+model.toString());

        int p = position+1;
        holder.binding.tvCount.setText(""+p);
        holder.binding.tvDescription.setText("عدد الايات "+model.getAyas());
        holder.binding.tvSuraName.setOnClickListener(v-> listener.onItemClick(model));
        holder.binding.clickView.setOnClickListener(v-> listener.onItemClick(model));

    }




    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemSuraSmallBinding binding;
        public ViewHolder_(ItemSuraSmallBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(Sura model);
    }

}
