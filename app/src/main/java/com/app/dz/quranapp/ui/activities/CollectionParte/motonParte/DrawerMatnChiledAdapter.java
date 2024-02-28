package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.ui.activities.NewBooksParte.DrawerBookAdapter;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class DrawerMatnChiledAdapter extends RecyclerView.Adapter<DrawerMatnChiledAdapter.ViewHolder> {
    private List<PdfDocument.Bookmark> itemList;
    private DrawerMatnParentAdapter.MatnClickListener listener;

    public DrawerMatnChiledAdapter(List<PdfDocument.Bookmark> itemList, DrawerMatnParentAdapter.MatnClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public List<PdfDocument.Bookmark> getItemList(){
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
        PdfDocument.Bookmark item = itemList.get(position);
        holder.textView.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> {
            listener.onMatnClick(item,position);
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
