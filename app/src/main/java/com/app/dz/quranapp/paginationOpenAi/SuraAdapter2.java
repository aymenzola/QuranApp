package com.app.dz.quranapp.paginationOpenAi;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Sura;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemSuraSmallBinding;

public class SuraAdapter2 extends PagedListAdapter<Sura, SuraAdapter2.ViewHolder_> {
    protected SuraAdapter2() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSuraSmallBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_sura_small,parent, false);
        return new ViewHolder_(item);
    }

    public class ViewHolder_ extends RecyclerView.ViewHolder {
        ItemSuraSmallBinding binding;
        public ViewHolder_(ItemSuraSmallBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ holder,int position) {
        Sura concert = getItem(position);
        if (concert != null) {
            Sura model = getItem(position);
            holder.binding.tvSuraName.setText(model.getName());
            Log.e("logdata","sura data "+model.toString());

            int p = position+1;
            holder.binding.tvCount.setText(""+p);
            holder.binding.tvDescription.setText("عدد الايات "+model.getAyas());

        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.

            //holder.binding.clear();
        }
    }

    private static DiffUtil.ItemCallback<Sura> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Sura>() {
        // Sura details may have changed if reloaded from the database,
        // but ID is fixed.
        @Override
        public boolean areItemsTheSame(Sura oldConcert, Sura newConcert) {
            return oldConcert.getId() == newConcert.getId();
        }

        @Override
        public boolean areContentsTheSame(Sura oldConcert,Sura newConcert) {
            return oldConcert.equals(newConcert);
        }
    };
}