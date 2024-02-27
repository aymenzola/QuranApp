package com.app.dz.quranapp.ui.activities.subha;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemAddDikrBinding;
import com.app.dz.quranapp.databinding.ItemAdkarBinding;
import com.app.dz.quranapp.databinding.ItemBooksBinding;
import com.app.dz.quranapp.databinding.ItemChooseDikrBinding;
import com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte.BooksAdapter;

import java.util.List;

public class AdkarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_ADD = 1;
    private final OnAdapterClickListener listener;

    private List<DikrItem> adkarList;

    public AdkarListAdapter(List<DikrItem> adkarList, OnAdapterClickListener listener) {
        this.adkarList = adkarList;
        this.listener = listener;
    }

    public void setItems(List<DikrItem> items) {
        this.adkarList.addAll(items);
        notifyDataSetChanged();
    }

    //function to add  Item For to insert newdikr at position 0
    public void addNewItem() {
        adkarList.add(0, new DikrItem("", 0));
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            ItemChooseDikrBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_choose_dikr, parent, false);
            return new ViewHolder_ChooseDikr(item);
        } else {
            ItemAddDikrBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_add_dikr, parent, false);
            return new ViewHolder_AddDikr(item);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DikrItem model = adkarList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ViewHolder_ChooseDikr viewHolder = (ViewHolder_ChooseDikr) holder;
            viewHolder.binding.tvDikrText.setText(model.dikr);
            viewHolder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(model));
        } else {
            ViewHolder_AddDikr viewHolder = (ViewHolder_AddDikr) holder;
            viewHolder.binding.editDikr.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    model.dikr = s.toString();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return adkarList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (adkarList.get(position).type == 0) {
            return VIEW_TYPE_ADD;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void saveNewItem() {
        DikrItem dikrItem = adkarList.get(0);
        if (dikrItem.dikr.isEmpty()) {
            adkarList.remove(0);
            notifyItemRemoved(0);
            listener.onDikrCancel(dikrItem);
        } else {
            dikrItem.type = 1;
            listener.onDikrAdded(dikrItem);
        }
    }

    public void cancelNewItem() {
        adkarList.remove(0);
        notifyItemRemoved(0);
    }

    public void updateList(List<DikrItem> adkarList) {
        this.adkarList = adkarList;
        notifyDataSetChanged();
    }


    public static class ViewHolder_ChooseDikr extends RecyclerView.ViewHolder {
        ItemChooseDikrBinding binding;

        public ViewHolder_ChooseDikr(ItemChooseDikrBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public static class ViewHolder_AddDikr extends RecyclerView.ViewHolder {
        ItemAddDikrBinding binding;

        public ViewHolder_AddDikr(ItemAddDikrBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    interface OnAdapterClickListener {
        void onItemClick(DikrItem model);

        void onDikrAdded(DikrItem model);

        void onDikrCancel(DikrItem model);
    }

    static class DikrItem {
        public String dikr;
        public int type;

        public DikrItem(String dikr, int type) {
            this.dikr = dikr;
            this.type = type;
        }
    }

}