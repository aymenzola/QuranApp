package com.app.dz.quranapp.ui.activities.NewBooksParte;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.R;

import java.util.List;

public class DrawerChapterAdapter extends RecyclerView.Adapter<DrawerChapterAdapter.ViewHolder> {
    private List<Chapter> itemList;
    DrawerBookAdapter.ChapterClickListener listener;

    public DrawerChapterAdapter(List<Chapter> itemList, DrawerBookAdapter.ChapterClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public List<Chapter> getItemList(){
        return itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chapter item = itemList.get(position);
        holder.textView.setText(item.chapterTitle_no_tachkil);
        holder.itemView.setOnClickListener(v -> {
            listener.onChapterClick(item, position,itemList);
        });

        holder.itemView.setOnLongClickListener(v->{
            Log.e("quran_tag","long click position "+position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.child_item_text);
        }
    }
}
