package com.app.dz.quranapp.MushafParte.TafsirParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaTafsir;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAyatTafsirBinding;
import com.app.dz.quranapp.databinding.ItemBasmalaBinding;
import com.app.dz.quranapp.databinding.ItemCustomViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MushafPageTafsirAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_AYA = 0;
    private static final int VIEW_TYPE_SURA_STAR = 1;
    private static final int VIEW_TYPE_CUSTOM_VIEW = 2;


    private Context mCtx;
    private List<Object> arrayList = new ArrayList<>();
    private OnAyaClickListener listener;
    private int textSize;

    // creating a constructor class for our adapter class.
    public MushafPageTafsirAdapter(Context mCtx, int textsize, OnAyaClickListener listener1) {
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

    public void additems(List<AyaTafsir> items) {
        arrayList.clear();
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public AyaTafsir getItem(int position) {
        return (AyaTafsir) arrayList.get(position);
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_AYA) {
            ItemAyatTafsirBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_ayat_tafsir, parent, false);
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
            AyaTafsir model = (AyaTafsir) arrayList.get(position);
            Log.e("tvsizetag", "we write again ................ ");
            //holderp.binding.tvAyatText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            holderp.binding.tvAyatText.setText(colorNumbersInText(" ﴿ "+model.getAya().getSuraAya()+" ﴾ "+model.getAya().getText()));

            //holderp.binding.tvAyaTafsir.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            holderp.binding.tvAyaTafsir.setText(model.getAya().getTafseer());

            holderp.binding.tvAyatText.setOnClickListener(v -> listener.onClick(model.getAya()));
            holderp.binding.tvAyaTafsir.setOnClickListener(v -> listener.onClick(model.getAya()));


        } else if (getItemViewType(position) == VIEW_TYPE_SURA_STAR) {
            ViewHolder_Sura holderp = (ViewHolder_Sura) holder;
            //holderp.binding.tvAyatText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            holderp.binding.tvAyatText.setText("بِسْمِ اللَّـهِ الرَّحْمَـٰنِ الرَّحِيمِ");
        } else {
            ViewHolder_CUSTOM holderp = (ViewHolder_CUSTOM) holder;
            AyaTafsir model = (AyaTafsir) arrayList.get(position);

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
        ItemAyatTafsirBinding binding;

        public ViewHolder_(ItemAyatTafsirBinding bindingg) {
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
        AyaTafsir ayaString = (AyaTafsir) arrayList.get(position);
        if (ayaString.isSuraStar())
            return VIEW_TYPE_SURA_STAR;
        else if (ayaString.isIscustomview()) return VIEW_TYPE_CUSTOM_VIEW;
        else return VIEW_TYPE_AYA;
    }

    public interface OnAyaClickListener {
        void onClick(Aya aya);
    }

    public SpannableStringBuilder colorNumbersInText(String ayaText) {
        SpannableStringBuilder builder = new SpannableStringBuilder(ayaText);
        // Find all the number patterns in the text
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(builder);

        while (matcher.find()) {
            // Get the start and end indices of the matched number
            int start = matcher.start();
            int end = matcher.end();

            // Apply the color to the matched number
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#B07A1A")), start - 2, end + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return builder;
    }

}
