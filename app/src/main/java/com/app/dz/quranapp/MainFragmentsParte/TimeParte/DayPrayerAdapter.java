package com.app.dz.quranapp.MainFragmentsParte.TimeParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemDayTimingBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DayPrayerAdapter extends RecyclerView.Adapter<DayPrayerAdapter.ViewHolder_> {

    private Context mCtx;
    private List<DayPrayerTimes> arrayList;
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public DayPrayerAdapter(Context mCtx, List<DayPrayerTimes> weekTimesList, OnAdapterClickListener listener1) {
        this.arrayList = weekTimesList;
        this.mCtx = mCtx;
        this.listener = listener1;
    }


    public DayPrayerTimes getItem(int position) {
        return arrayList.get(position);
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDayTimingBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_day_timing, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {
        DayPrayerTimes times = arrayList.get(position);
        holder.binding.tvFajrTime.setText(times.getFajr());
        holder.binding.tvSunriseTime.setText(times.getSunrise());
        holder.binding.tvDuhrTime.setText(times.getDhuhr());
        holder.binding.tvAssarTime.setText(times.getAsr());
        holder.binding.tvMaghribTime.setText(times.getMaghrib());
        holder.binding.tvIshaaTime.setText(times.getIsha());
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemDayTimingBinding binding;

        public ViewHolder_(ItemDayTimingBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(DayPrayerTimes dayPrayerTimes);
    }

}
