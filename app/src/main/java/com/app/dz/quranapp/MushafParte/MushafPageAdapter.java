package com.app.dz.quranapp.MushafParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaString;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAyatStringBinding;
import com.app.dz.quranapp.databinding.ItemBasmalaBinding;
import com.app.dz.quranapp.databinding.ItemCustomViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MushafPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_AYA = 0;
    private static final int VIEW_TYPE_SURA_STAR = 1;
    private static final int VIEW_TYPE_CUSTOM_VIEW = 2;


    private Context mCtx;
    private List<Object> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;
    private int  textSize;

    // creating a constructor class for our adapter class.
    public MushafPageAdapter(Context mCtx,int textsize, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
        this.textSize = textsize;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void additems(List<AyaString> items) {
        arrayList.clear();
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public AyaString getItem(int position) {
        return (AyaString) arrayList.get(position);
    }


    public void selectView(int position, SpannableStringBuilder stringBuilder) {

        if (arrayList.get(position) instanceof AyaString) {
            Log.e("logcheck2", "we are in adapter " + position);
            AyaString ayaString = (AyaString) arrayList.get(position);
            ayaString.setStringBuilder(stringBuilder);
            arrayList.set(position, ayaString);
            notifyItemChanged(position);
        }

    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_AYA) {
            ItemAyatStringBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_ayat_string, parent, false);
            return new ViewHolder_(item);
        } else if (viewType == VIEW_TYPE_SURA_STAR) {
            ItemBasmalaBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_basmala, parent, false);
            return new ViewHolder_Sura(item);
        } else {

            ItemCustomViewBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_custom_view, parent, false);
            return new ViewHolder_CUSTOM(item);

        }
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_AYA) {
            ViewHolder_ holderp = (ViewHolder_) holder;
            AyaString model = (AyaString) arrayList.get(position);
            Log.e("tvsizetag", "we write again ................ ");
            holderp.binding.tvAyatText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            holderp.binding.tvAyatText.setText(colorNumbersInText(model.getStringBuilder()));

            holderp.binding.tvAyatText.setOnTouchListener((view, event) -> {
                listener.onItemTouch(model, position, view, event);
                return false;
            });
            holderp.binding.tvAyatText.setOnClickListener(v -> listener.onItemClick(model, position));


        } else if (getItemViewType(position) == VIEW_TYPE_SURA_STAR) {
            ViewHolder_Sura holderp = (ViewHolder_Sura) holder;
            holderp.binding.tvAyatText.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            holderp.binding.tvAyatText.setText("بِسْمِ اللَّـهِ الرَّحْمَـٰنِ الرَّحِيمِ");
        } else {
            ViewHolder_CUSTOM holderp = (ViewHolder_CUSTOM) holder;
            AyaString model = (AyaString) arrayList.get(position);

            holderp.binding.tvSuraName.setText("" + model.getSuraTitle());
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_CUSTOM extends RecyclerView.ViewHolder {
        ItemCustomViewBinding binding;

        public ViewHolder_CUSTOM(ItemCustomViewBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemAyatStringBinding binding;

        public ViewHolder_(ItemAyatStringBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public class ViewHolder_Sura extends RecyclerView.ViewHolder {
        ItemBasmalaBinding binding;

        public ViewHolder_Sura(ItemBasmalaBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    @Override
    public int getItemViewType(int position) {
        AyaString ayaString = (AyaString) arrayList.get(position);
        if (ayaString.isSuraStar())
            return VIEW_TYPE_SURA_STAR;
        else if (ayaString.isIscustomview()) return VIEW_TYPE_CUSTOM_VIEW;
        else return VIEW_TYPE_AYA;
    }

    public interface OnAdapterClickListener {
        void onClick(Aya aya);

        void onItemClick(AyaString ayaString, int position);

        void onItemTouch(AyaString ayaString, int position, View view, MotionEvent event);

    }

    public SpannableStringBuilder colorNumbersInText(SpannableStringBuilder builder) {
        // Find all the number patterns in the text
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(builder);

        while (matcher.find()) {
            // Get the start and end indices of the matched number
            int start = matcher.start();
            int end = matcher.end();

            // Apply the color to the matched number
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#B07A1A")), start-2, end+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return builder;
    }

}
