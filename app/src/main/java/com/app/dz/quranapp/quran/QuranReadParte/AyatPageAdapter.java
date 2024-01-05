package com.app.dz.quranapp.quran.QuranReadParte;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemPageAdapterBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AyatPageAdapter extends RecyclerView.Adapter<AyatPageAdapter.ViewHolder_> {

    private Context mCtx;
    private List<PageModel> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public AyatPageAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void setItems(List<PageModel> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public PageModel getItem(int position) {
        return this.arrayList.get(position);
    }

    public void selcetView(int lastSelectedItem, int position, PageModel pageModel) {
        Log.e("logcheck3", "lastSelectedItem " + lastSelectedItem + " position " + position);
        if (lastSelectedItem != -1 && lastSelectedItem != position) {
            PageModel prevPageModel = this.arrayList.get(lastSelectedItem);
            String noramlText = prevPageModel.ayatText.toString();
            SpannableStringBuilder ssbnormal = new SpannableStringBuilder(noramlText);
            prevPageModel.ayatText = ssbnormal;
            arrayList.set(lastSelectedItem, prevPageModel);
            notifyItemChanged(lastSelectedItem, prevPageModel);
        }
/*
        Aya aya = this.arrayList.get(position);
        aya.setIselected(true);*/
        arrayList.set(position, pageModel);
        notifyItemChanged(position, pageModel);
    }

    public void unSelcetView(int position, PageModel pageModel) {
        arrayList.set(position, pageModel);
        notifyItemChanged(position, pageModel);
    }


    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPageAdapterBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_page_adapter, parent, false);
        return new ViewHolder_(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder_ holder, int position) {

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        if (!payloads.isEmpty()) {
            // Perform a partial update
            PageModel newPageModel = (PageModel) payloads.get(0);

            holder.binding.tvJuza.setText(getJuzaName(newPageModel.AyaList.get(0).getJuz()));
            holder.binding.tvSura.setText("سورة " + newPageModel.suraName);
            //sura Title
            if (position == getItemCount()-1) {
                holder.binding.tvSuraStart.setText("سورة " + newPageModel.suraName);
                holder.binding.relativeSuraStart.setVisibility(View.VISIBLE);
            } else holder.binding.relativeSuraStart.setVisibility(View.GONE);

            //Basmala
            if (newPageModel.page != 1 && newPageModel.page != 187 && position == getItemCount()-1)
                holder.binding.tvBasmala.setVisibility(View.VISIBLE);
            else
                holder.binding.tvBasmala.setVisibility(View.GONE);

            holder.binding.tvAyatText.setText(Html.fromHtml(newPageModel.ayatText.toString()));
            holder.binding.tvAyatText.setMovementMethod(new ScrollingMovementMethod());

        } else {
            PageModel model = arrayList.get(position);

            holder.binding.tvJuza.setText(getJuzaName(model.AyaList.get(0).getJuz()));
            holder.binding.tvSura.setText("سورة " + model.suraName);
            //suraTitle
            if (position == getItemCount()-1) {
                holder.binding.tvSuraStart.setText("سورة " + model.suraName);
                holder.binding.relativeSuraStart.setVisibility(View.VISIBLE);
            } else
                holder.binding.relativeSuraStart.setVisibility(View.GONE);

            //Basmala
            if (model.page != 1 && model.page != 187 && position == getItemCount()-1)
                holder.binding.tvBasmala.setVisibility(View.VISIBLE);
            else
                holder.binding.tvBasmala.setVisibility(View.GONE);

            holder.binding.tvAyatText.setText(Html.fromHtml(model.ayatText.toString()));
            holder.binding.tvAyatText.setMovementMethod(new ScrollingMovementMethod());
            holder.binding.tvAyatPage.setText("" + model.page);

            holder.binding.tvAyatText.setOnTouchListener((view, event) -> {
                listener.onItemTouch(model, position, view, event);
                return false;
            });
            holder.binding.tvAyatText.setOnClickListener(v -> listener.onItemClick(model, position));


        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemPageAdapterBinding binding;

        public ViewHolder_(ItemPageAdapterBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;
        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(PageModel model, int position);

        void onItemTouch(PageModel model, int position, View view, MotionEvent event);
    }

    private String getJuzaName(int JuzaInt) {
        String prefex = "الجزء ";
        switch (JuzaInt) {
            case 1: return prefex + " الأول ";
            case 2: return prefex + " الثاني ";
            case 3: return prefex + " الثالث ";
            case 4: return prefex + " الرابع ";
            case 5: return prefex + " الخامس ";
            case 6: return prefex + " السادس ";
            case 7: return prefex + " السابع ";
            case 8: return prefex + " الثامن ";
            case 9: return prefex + " التاسع ";
            case 10: return prefex + " العاشر ";
            case 11: return prefex + " الحادي عشر ";
            case 12: return prefex + " الثاني عشر ";
            case 13: return prefex + " الثالث عشر ";
            case 14: return prefex + " الرابع عشر ";
            case 15: return prefex + " الخامس عشر ";
            case 16: return prefex + " السادس عشر ";
            case 17: return prefex + " السابع عشر ";
            case 18: return prefex + " الثامن عشر ";
            case 19: return prefex + " التاسع عشر ";
            case 20: return prefex + " العشرون ";
            case 21: return prefex + " الواحد و العشرون ";
            case 22: return prefex + " الثاني و العشرون ";
            case 23: return prefex + " الثالث و العشرون";
            case 24: return prefex + " الرابع و العشرون";
            case 25: return prefex + " الخامس و العشرون";
            case 26: return prefex + " السادس و العشرون";
            case 27: return prefex + " السابع و العشرون";
            case 28: return prefex + " الثامن و العشرون";
            case 29: return prefex + " التاسع و العشرون";
            default: return prefex+" الثلاثون ";
        }
    }

}
