package com.app.dz.quranapp.ui.activities.NewBooksParte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.Chapter;

import java.util.ArrayList;
import java.util.List;

public class DrawerBookAdapter extends RecyclerView.Adapter<DrawerBookAdapter.ParentViewHolder> {

    private List<BookWithCount> parentItemList = new ArrayList<>();
    private boolean isInSearchMode;
    private static ChapterClickListener listener;

    public DrawerBookAdapter(List<BookWithCount> parentItemList, ChapterClickListener listener) {
        this.parentItemList = parentItemList;
        this.isInSearchMode = false;
        this.listener = listener;
    }

    public void filterList(List<BookWithCount> filteredList) {
        parentItemList = filteredList;
        notifyDataSetChanged();
    }

    public void setList(List<BookWithCount> list) {
        clear();
        parentItemList= new ArrayList<>(list);
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
        BookWithCount parentItem = parentItemList.get(position);
        holder.bind(parentItem, isInSearchMode);
    }

    @Override
    public int getItemCount() {
        return parentItemList.size();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView parentTextView;
        private RecyclerView childRecyclerView;
        private boolean isExpanded;
        private boolean isInSearchMode;

        ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            parentTextView = itemView.findViewById(R.id.parent_item_text);
            childRecyclerView = itemView.findViewById(R.id.child_recycler_view);
            isExpanded = false;
            isInSearchMode = false;
            parentTextView.setOnClickListener(this);
        }

        void bind(BookWithCount parentItem, boolean isInSearchMode) {
            this.isInSearchMode = isInSearchMode;
            parentTextView.setText(parentItem.bookName);
            if (isExpanded || isInSearchMode) {
                childRecyclerView.setVisibility(View.VISIBLE);
            } else {
                childRecyclerView.setVisibility(View.GONE);
            }
            childRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            childRecyclerView.setAdapter(new DrawerChapterAdapter(parentItem.chaptersList, (chapter, position, itemList) -> {
                if (listener != null) {
                    chapter.collectionName = parentItem.bookCollection;
                    chapter.bookNumber = parentItem.bookNumber;
                    chapter.bookName = parentItem.bookName;

                    listener.onChapterClick(chapter,position, itemList);
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

    public interface ChapterClickListener {
        void onChapterClick(Chapter chapter, int position, List<Chapter> itemList);
    }
}
