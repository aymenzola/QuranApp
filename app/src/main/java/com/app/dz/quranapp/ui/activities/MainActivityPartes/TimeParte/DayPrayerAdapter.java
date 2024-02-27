package com.app.dz.quranapp.ui.activities.MainActivityPartes.TimeParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Communs.PrayerTimesHelper;
import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.IncludePrayerTime2Binding;
import com.app.dz.quranapp.databinding.IncludePrayerTimeBinding;
import com.app.dz.quranapp.databinding.ItemDayTimingNewBinding;
import com.app.dz.quranapp.ui.models.adhan.DayPrayersConfig;
import com.app.dz.quranapp.ui.models.adhan.PrayerConfig;
import com.bumptech.glide.Glide;

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
        ItemDayTimingNewBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_day_timing_new, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {
        DayPrayerTimes times = arrayList.get(position);


        //hide the checkbox and adhan in shourok
        holder.binding.includePtShorok.tvPrayerAdhan.setVisibility(View.GONE);
        holder.binding.includePtShorok.checkbox.setVisibility(View.GONE);

        // set on click listener for the adhan text view
        holder.binding.includePtFajr.tvPrayerAdhan.setOnClickListener((v) -> listener.onItemChanged());
        holder.binding.includePtDuhr.tvPrayerAdhan.setOnClickListener((v) -> listener.onItemChanged());
        holder.binding.includePtAsr.tvPrayerAdhan.setOnClickListener((v) -> listener.onItemChanged());
        holder.binding.includePtMaghrib.tvPrayerAdhan.setOnClickListener((v) -> listener.onItemChanged());
        holder.binding.includePtIsha.tvPrayerAdhan.setOnClickListener((v) -> listener.onItemChanged());

        // prevent the checkbox from being checked
        holder.binding.includePtFajr.checkbox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Handle the click event here
                listener.onItemChanged();
                // Consume the event and prevent the checkbox from being checked
                return true;
            }
            return false;
        });
        holder.binding.includePtDuhr.checkbox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Handle the click event here
                listener.onItemChanged();
                // Consume the event and prevent the checkbox from being checked
                return true;
            }
            return false;
        });
        holder.binding.includePtAsr.checkbox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Handle the click event here
                listener.onItemChanged();
                // Consume the event and prevent the checkbox from being checked
                return true;
            }
            return false;
        });
        holder.binding.includePtMaghrib.checkbox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Handle the click event here
                listener.onItemChanged();
                // Consume the event and prevent the checkbox from being checked
                return true;
            }
            return false;
        });
        holder.binding.includePtIsha.checkbox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Handle the click event here
                listener.onItemChanged();
                // Consume the event and prevent the checkbox from being checked
                return true;
            }
            return false;
        });


        // For Fajr
        PrayerConfig config1 = getDayConfig(mCtx, 1);
        updateViews(config1, holder.binding.includePtFajr.tvPrayerNameTime, holder.binding.includePtFajr.checkbox, "الفجر " + times.getFajr());

        holder.binding.includePtFajr.linearModeSilent.setOnClickListener(v -> listener.onItemChanged());

        if (isActive(config1, PrayerTimesHelper.AdhanSound.NORMAL) == 1) {
            activeView(R.drawable.ic_adhan_active, holder.binding.includePtFajr.imageMode, holder.binding.includePtFajr.tvMode, "الوضع المرتفع");
        } else if (isActive(config1, PrayerTimesHelper.AdhanSound.SILENT) == 1) {
            activeView(R.drawable.ic_adhan_silent, holder.binding.includePtFajr.imageMode, holder.binding.includePtFajr.tvMode, "الوضع الصامت");
        } else {
            activeView(R.drawable.ic_adhan_vibration, holder.binding.includePtFajr.imageMode, holder.binding.includePtFajr.tvMode, "وضع الاهتزاز");
        }


        // For shourok or sunrise
        PrayerConfig config6 = getDayConfig(mCtx, 2);

        holder.binding.includePtShorok.tvPrayerNameTime.setText("الشروق " + times.getSunrise());
        holder.binding.includePtShorok.linearModeSilent.setOnClickListener(v -> listener.onItemChanged());

        if (isActive(config6,PrayerTimesHelper.AdhanSound.NORMAL) == 1) {
            activeView(R.drawable.ic_adhan_active, holder.binding.includePtShorok.imageMode, holder.binding.includePtShorok.tvMode, "الوضع المرتفع");
        } else if (isActive(config6, PrayerTimesHelper.AdhanSound.SILENT) == 1) {
            activeView(R.drawable.ic_adhan_silent, holder.binding.includePtShorok.imageMode, holder.binding.includePtShorok.tvMode, "الوضع الصامت");
        } else {
            activeView(R.drawable.ic_adhan_vibration, holder.binding.includePtShorok.imageMode, holder.binding.includePtShorok.tvMode, "وضع الاهتزاز");
        }


        // For Dhuhr
        PrayerConfig config2 = getDayConfig(mCtx, 3);
        updateViews(config2, holder.binding.includePtDuhr.tvPrayerNameTime, holder.binding.includePtDuhr.checkbox, "الظهر " + times.getDhuhr());

        holder.binding.includePtDuhr.checkbox.setOnClickListener((buttonView) -> listener.onItemChanged());

        holder.binding.includePtDuhr.linearModeSilent.setOnClickListener(v -> listener.onItemChanged());

        if (isActive(config2, PrayerTimesHelper.AdhanSound.NORMAL) == 1) {
            activeView(R.drawable.ic_adhan_active, holder.binding.includePtDuhr.imageMode, holder.binding.includePtDuhr.tvMode, "الوضع المرتفع");
        } else if (isActive(config2, PrayerTimesHelper.AdhanSound.SILENT) == 1) {
            activeView(R.drawable.ic_adhan_silent, holder.binding.includePtDuhr.imageMode, holder.binding.includePtDuhr.tvMode, "الوضع الصامت");
        } else {
            activeView(R.drawable.ic_adhan_vibration, holder.binding.includePtDuhr.imageMode, holder.binding.includePtDuhr.tvMode, "وضع الاهتزاز");
        }

// For Asr
        PrayerConfig config3 = getDayConfig(mCtx, 4);
        updateViews(config3, holder.binding.includePtAsr.tvPrayerNameTime, holder.binding.includePtAsr.checkbox, "العصر " + times.getAsr());

        holder.binding.includePtAsr.checkbox.setOnClickListener((buttonView) -> listener.onItemChanged());

        holder.binding.includePtAsr.linearModeSilent.setOnClickListener(v -> listener.onItemChanged());

        if (isActive(config3, PrayerTimesHelper.AdhanSound.NORMAL) == 1) {
            activeView(R.drawable.ic_adhan_active, holder.binding.includePtAsr.imageMode, holder.binding.includePtAsr.tvMode, "الوضع المرتفع");
        } else if (isActive(config3, PrayerTimesHelper.AdhanSound.SILENT) == 1) {
            activeView(R.drawable.ic_adhan_silent, holder.binding.includePtAsr.imageMode, holder.binding.includePtAsr.tvMode, "الوضع الصامت");
        } else {
            activeView(R.drawable.ic_adhan_vibration, holder.binding.includePtAsr.imageMode, holder.binding.includePtAsr.tvMode, "وضع الاهتزاز");
        }

// For Maghrib
        PrayerConfig config4 = getDayConfig(mCtx, 5);
        updateViews(config4, holder.binding.includePtMaghrib.tvPrayerNameTime, holder.binding.includePtMaghrib.checkbox, "المغرب " + times.getMaghrib());

        holder.binding.includePtMaghrib.checkbox.setOnClickListener((buttonView) -> listener.onItemChanged());

        holder.binding.includePtMaghrib.linearModeSilent.setOnClickListener(v -> listener.onItemChanged());

        if (isActive(config4, PrayerTimesHelper.AdhanSound.NORMAL) == 1) {
            activeView(R.drawable.ic_adhan_active, holder.binding.includePtMaghrib.imageMode, holder.binding.includePtMaghrib.tvMode, "الوضع المرتفع");
        } else if (isActive(config4, PrayerTimesHelper.AdhanSound.SILENT) == 1) {
            activeView(R.drawable.ic_adhan_silent, holder.binding.includePtMaghrib.imageMode, holder.binding.includePtMaghrib.tvMode, "الوضع الصامت");
        } else {
            activeView(R.drawable.ic_adhan_vibration, holder.binding.includePtMaghrib.imageMode, holder.binding.includePtMaghrib.tvMode, "وضع الاهتزاز");
        }

// For Isha
        PrayerConfig config5 = getDayConfig(mCtx, 6);
        updateViews(config5, holder.binding.includePtIsha.tvPrayerNameTime, holder.binding.includePtIsha.checkbox, "العشاء " + times.getIsha());

        holder.binding.includePtIsha.checkbox.setOnClickListener((buttonView) -> listener.onItemChanged());

        holder.binding.includePtIsha.linearModeSilent.setOnClickListener(v -> listener.onItemChanged());

        if (isActive(config5, PrayerTimesHelper.AdhanSound.NORMAL) == 1) {
            activeView(R.drawable.ic_adhan_active, holder.binding.includePtIsha.imageMode, holder.binding.includePtIsha.tvMode, "الوضع المرتفع");
        } else if (isActive(config5, PrayerTimesHelper.AdhanSound.SILENT) == 1) {
            activeView(R.drawable.ic_adhan_silent, holder.binding.includePtIsha.imageMode, holder.binding.includePtIsha.tvMode, "الوضع الصامت");
        } else {
            activeView(R.drawable.ic_adhan_vibration, holder.binding.includePtIsha.imageMode, holder.binding.includePtIsha.tvMode, "وضع الاهتزاز");
        }


    }

    private void activeView(int ic_image, ImageView imageMode, TextView tvMode, String text) {
        Glide.with(mCtx).load(ic_image).into(imageMode);
        tvMode.setText(text);
        //tvMode.setTextColor(mCtx.getResources().getColor(R.color.purple_500));
    }


    private void updateViews(PrayerConfig config, TextView tvPrayerNameTime, CheckBox checkbox, String timeName) {
        tvPrayerNameTime.setText(timeName);
        checkbox.setChecked(config.isNotifyOnSilentMode);
    }


    private static int getColor(PrayerConfig config, PrayerTimesHelper.AdhanSound soundType) {
        return config.soundType.equals(soundType.name()) ? R.color.purple_500 : R.color.tv_gri_color;
    }

    private static int isActive(PrayerConfig config, PrayerTimesHelper.AdhanSound soundType) {
        return config.soundType.equals(soundType.name()) ? 1 : 0;
    }

    private void setImageViewTint(ImageView imageView, TextView textView, int colorRes) {
        imageView.setColorFilter(imageView.getContext().getResources().getColor(colorRes), PorterDuff.Mode.SRC_ATOP);
        textView.setTextColor(imageView.getContext().getResources().getColor(colorRes));
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemDayTimingNewBinding binding;

        public ViewHolder_(ItemDayTimingNewBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public PrayerConfig getDayConfig(Context mCtx, int prayerRank) {
        DayPrayersConfig dayPrayersConfig = PrayerTimesHelper.getDayPrayersConfig(mCtx);
        return switch (prayerRank) {
            case 1 -> dayPrayersConfig.FajrConfig;
            case 2 -> dayPrayersConfig.ShourokConfig;
            case 3 -> dayPrayersConfig.DuhrConfig;
            case 4 -> dayPrayersConfig.AsrConfig;
            case 5 -> dayPrayersConfig.MaghribConfig;
            default -> dayPrayersConfig.IchaaConfig;
        };
    }


    public interface OnAdapterClickListener {
        void onItemClick(DayPrayerTimes dayPrayerTimes);

        void onItemChanged();
    }

}
