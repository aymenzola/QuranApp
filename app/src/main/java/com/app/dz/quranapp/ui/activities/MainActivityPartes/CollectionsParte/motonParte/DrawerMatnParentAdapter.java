package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.shockwave.pdfium.PdfDocument;

import java.util.ArrayList;
import java.util.List;

public class DrawerMatnParentAdapter extends RecyclerView.Adapter<DrawerMatnParentAdapter.ParentViewHolder> {

    private List<PdfDocument.Bookmark> parentItemList = new ArrayList<>();
    private boolean isInSearchMode;
    private static MatnClickListener listener;

    public DrawerMatnParentAdapter(List<PdfDocument.Bookmark> parentItemList, MatnClickListener listener) {
        this.parentItemList = parentItemList != null ? parentItemList : new ArrayList<>();
        this.isInSearchMode = false;
        DrawerMatnParentAdapter.listener = listener;
    }

    public void filterList(List<PdfDocument.Bookmark> filteredList) {
        parentItemList = filteredList;
        notifyDataSetChanged();
    }

    public void setList(List<PdfDocument.Bookmark> list) {
        clear();
        parentItemList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void clear() {
        parentItemList.clear();
        notifyDataSetChanged();
    }

    public void setSearchMode(boolean isInSearchMode) {
        this.isInSearchMode = isInSearchMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_item_layout, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        PdfDocument.Bookmark parentItem = parentItemList.get(position);
        holder.bind(parentItem, isInSearchMode);
    }

    @Override
    public int getItemCount() {
        return parentItemList.size();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView parentTextView;
        private final TextView tvChapterCount;
        private final RecyclerView childRecyclerView;
        private boolean isExpanded;
        private boolean isInSearchMode;

        ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterCount = itemView.findViewById(R.id.tv_book_number);
            parentTextView = itemView.findViewById(R.id.parent_item_text);
            childRecyclerView = itemView.findViewById(R.id.child_recycler_view);
            isExpanded = false;
            isInSearchMode = false;
            parentTextView.setOnClickListener(this);
        }

        void bind(PdfDocument.Bookmark parentItem, boolean isInSearchMode) {
            this.isInSearchMode = isInSearchMode;
            parentTextView.setText(parentItem.getTitle());
            tvChapterCount.setText(String.valueOf(parentItem.getChildren().size()));

            if (parentItem.getChildren().size() == 0)
             parentTextView.setOnClickListener(v -> {
                if (listener != null) listener.onMatnClick(parentItem,0);
            });

            if (isExpanded || isInSearchMode) {
                childRecyclerView.setVisibility(View.VISIBLE);
            } else {
                childRecyclerView.setVisibility(View.GONE);
            }
            childRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            childRecyclerView.setAdapter(new DrawerMatnChiledAdapter(parentItem.getChildren(), (bookmark, position) -> {
                if (listener != null) {
                    listener.onMatnClick(bookmark, position);
                }
            }));
        }

        @Override
        public void onClick(View v) {
            if (!isInSearchMode) {
                if (isExpanded) {
                    childRecyclerView.setVisibility(View.GONE);
                } else {
                    childRecyclerView.setVisibility(View.VISIBLE);
                }
                isExpanded = !isExpanded;
            }
        }
    }


    public interface MatnClickListener {
        void onMatnClick(PdfDocument.Bookmark bookmark, int position);
    }

}
