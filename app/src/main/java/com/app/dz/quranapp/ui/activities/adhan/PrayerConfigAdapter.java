package com.app.dz.quranapp.ui.activities.adhan;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Communs.PrayerTimesHelper;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemPrayerConfigBinding;
import com.app.dz.quranapp.ui.models.adhan.DayPrayersConfig;
import com.app.dz.quranapp.ui.models.adhan.PrayerConfig;

import java.util.List;

public class PrayerConfigAdapter extends RecyclerView.Adapter<PrayerConfigAdapter.ViewHolder> {

    private final List<PrayerConfig> configList;
    private static OnAdapterClickListener listener;

    public PrayerConfigAdapter(List<PrayerConfig> configList,OnAdapterClickListener listener) {
        this.configList = configList;
        PrayerConfigAdapter.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPrayerConfigBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_prayer_config, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrayerConfig config = configList.get(position);
        holder.bind(config);
    }

    @Override
    public int getItemCount() {
        return configList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPrayerConfigBinding binding;

        public ViewHolder(@NonNull ItemPrayerConfigBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PrayerConfig config) {

            Log.e("logtag", "bind: " +config.toString());

            if (getBindingAdapterPosition()==1){
                //should hide checkbox and tvprayerAdhan
                binding.checkbox.setVisibility(android.view.View.GONE);
                binding.tvPrayerAdhan.setVisibility(android.view.View.GONE);
            } else {
                binding.checkbox.setVisibility(android.view.View.VISIBLE);
                binding.tvPrayerAdhan.setVisibility(android.view.View.VISIBLE);
            }

            //setting the default values
            binding.checkbox.setChecked(config.isNotifyOnSilentMode);
            binding.tvPrayerNameTime.setText(PrayerTimesHelper.getPrayerArabicName(config.name));
            updateView(config);


            //lookup for changes
            binding.checkbox.setOnCheckedChangeListener((buttonView,isChecked) -> {
                config.isNotifyOnSilentMode = isChecked;
                saveDayPrayersConfig(getBindingAdapterPosition(), config);
                listener.onItemChanged(getBindingAdapterPosition());
            });

            binding.imageNormal.setOnClickListener(v -> {
                config.soundType = PrayerTimesHelper.AdhanSound.NORMAL.name();
                updateView(config);
                saveDayPrayersConfig(getBindingAdapterPosition(), config);
                listener.onItemChanged(getBindingAdapterPosition());
            });

            binding.tvNormal.setOnClickListener(v -> {
                config.soundType = PrayerTimesHelper.AdhanSound.NORMAL.name();
                updateView(config);
                saveDayPrayersConfig(getBindingAdapterPosition(), config);
                listener.onItemChanged(getBindingAdapterPosition());
            });

            binding.imageSilent.setOnClickListener(v -> {
                config.soundType = PrayerTimesHelper.AdhanSound.SILENT.name();
                updateView(config);
                saveDayPrayersConfig(getBindingAdapterPosition(),config);
                listener.onItemChanged(getBindingAdapterPosition());
            });

            binding.tvSilent.setOnClickListener(v -> {
                config.soundType = PrayerTimesHelper.AdhanSound.SILENT.name();
                updateView(config);
                saveDayPrayersConfig(getBindingAdapterPosition(), config);
                listener.onItemChanged(getBindingAdapterPosition());
            });

            binding.imageVibration.setOnClickListener(v -> {
                config.soundType = PrayerTimesHelper.AdhanSound.VIBRATION.name();
                updateView(config);
                saveDayPrayersConfig(getBindingAdapterPosition(), config);
                listener.onItemChanged(getBindingAdapterPosition());
            });

            binding.tvVibration.setOnClickListener(v -> {
                config.soundType = PrayerTimesHelper.AdhanSound.VIBRATION.name();
                updateView(config);
                saveDayPrayersConfig(getBindingAdapterPosition(), config);
                listener.onItemChanged(getBindingAdapterPosition());
            });
        }

        private void updateView(PrayerConfig config) {
            setImageViewTint(binding.tvNormal,binding.imageNormal, config.soundType.equals(PrayerTimesHelper.AdhanSound.NORMAL.name()) ? R.color.purple_500 : R.color.tv_gri_color);
            setImageViewTint(binding.tvSilent,binding.imageSilent, config.soundType.equals(PrayerTimesHelper.AdhanSound.SILENT.name()) ? R.color.purple_500 : R.color.tv_gri_color);
            setImageViewTint(binding.tvVibration,binding.imageVibration, config.soundType.equals(PrayerTimesHelper.AdhanSound.VIBRATION.name()) ? R.color.purple_500 : R.color.tv_gri_color);
        }

        public void saveDayPrayersConfig(int position, PrayerConfig prayerConfig) {
            DayPrayersConfig currentDayPrayersConfig = PrayerTimesHelper.getDayPrayersConfig(binding.getRoot().getContext());
            currentDayPrayersConfig.updatePrayerConfigAtPosition(prayerConfig,position);
            Log.e("logtag", "saveDayPrayersConfig: " + currentDayPrayersConfig.toString());
            PrayerTimesHelper.getInstance(itemView.getContext()).saveDayPrayersConfig(currentDayPrayersConfig, binding.getRoot().getContext());
        }

        private void setImageViewTint(TextView tvMode, ImageView imageView,int colorRes) {
            imageView.setColorFilter(imageView.getContext().getResources().getColor(colorRes), PorterDuff.Mode.SRC_ATOP);
            tvMode.setTextColor(tvMode.getContext().getResources().getColor(colorRes));
        }
    }

    public interface OnAdapterClickListener {
        void onItemChanged(int position);
    }



}
