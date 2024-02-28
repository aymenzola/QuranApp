package com.app.dz.quranapp.ui.activities.AdkarParte;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.databinding.ItemSavedDikrBinding;
import com.app.dz.quranapp.ui.activities.CollectionParte.motonParte.SavedMatnPage;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ChaptersSavedAdapter extends RecyclerView.Adapter<ChaptersSavedAdapter.ViewHolder> {

    private final List<Object> dataList = new ArrayList<>();
    private final ClickListener clickListener;

    public ChaptersSavedAdapter(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void addBooks(List<BookWithCount> bookWithCountList) {
        this.dataList.addAll(bookWithCountList);
    }

    public void addMoton(List<SavedMatnPage> savedMatnPageList) {
        this.dataList.addAll(savedMatnPageList);
    }

    public void addChapters(List<Chapter> chapterList) {
        this.dataList.addAll(chapterList);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSavedDikrBinding item = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_saved_dikr, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Object data = dataList.get(position);

        if (data instanceof Chapter chapter) {
            bindChapter(holder, chapter, position);
        } else if (data instanceof BookWithCount book) {
            bindBooks(holder, book, position);
        } else if (data instanceof SavedMatnPage savedMatnPage) {
            bindMatn(holder, savedMatnPage, position);
        }


    }

    private void bindMatn(ViewHolder holder, SavedMatnPage savedMatnPage, int position) {
        holder.binding.tvDikrCategory.setText(savedMatnPage.bookTitle);
        holder.binding.tvDikrTitle.setText(savedMatnPage.pageTitle);

        holder.binding.tvOpenRead.setOnClickListener(v -> clickListener.onOpenMatnClicked(savedMatnPage));

        Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);

        holder.binding.imgSave.setOnClickListener(v -> {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
            clickListener.onMatnSaveClick(savedMatnPage, position);
        });

        holder.binding.imgSave.setOnClickListener(v -> {
            // Create a new Handler
            Handler handler = new Handler();

            // Show the Snackbar with the cancel button
            Snackbar snackbar = Snackbar.make(v, "الحدف من المحوظات بعد 4 ثواني", Snackbar.LENGTH_LONG);
            snackbar.setAction("Cancel", v1 -> {
                // If the user presses cancel, remove the callbacks from the handler
                handler.removeCallbacksAndMessages(null);
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
            });
            snackbar.show();

            // Post a delayed Runnable to the handler
            handler.postDelayed(() -> {
                // This code will be executed after 4 seconds unless the user presses cancel
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                clickListener.onMatnSaveClick(savedMatnPage, position);
            }, 4000); // 4 seconds delay

        });
    }

    private void bindChapter(ViewHolder holder, Chapter chapter, int position) {
        holder.binding.tvDikrTitle.setText("عدد الابواب " + chapter.bookNumber);
        holder.binding.tvDikrCategory.setText(chapter.chapterTitle);

        holder.binding.tvOpenRead.setOnClickListener(v -> clickListener.onOpenChapterClicked(chapter));

        if (!chapter.isSaved) {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
        } else {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
        }

        holder.binding.imgSave.setOnClickListener(v -> {
            if (!chapter.isSaved) {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                chapter.isSaved = true;
                clickListener.onChapterSaveClick(chapter, true, position);
            } else {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                chapter.isSaved = false;
                clickListener.onChapterSaveClick(chapter, false, position);
            }
        });

        holder.binding.imgSave.setOnClickListener(v -> {
            if (chapter.isSaved) {
                // Create a new Handler
                Handler handler = new Handler();

                // Show the Snackbar with the cancel button
                Snackbar snackbar = Snackbar.make(v, "الحدف من المحوظات بعد 4 ثواني", Snackbar.LENGTH_LONG);
                snackbar.setAction("Cancel", v1 -> {
                    // If the user presses cancel, remove the callbacks from the handler
                    handler.removeCallbacksAndMessages(null);
                    Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                });
                snackbar.show();

                // Post a delayed Runnable to the handler
                handler.postDelayed(() -> {
                    // This code will be executed after 4 seconds unless the user presses cancel
                    Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                    chapter.isSaved = false;
                    clickListener.onChapterSaveClick(chapter, false, position);
                }, 4000); // 4 seconds delay
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bindBooks(ViewHolder holder, BookWithCount bookWithCount, int position) {
        holder.binding.tvDikrCategory.setText(bookWithCount.bookName);
        holder.binding.tvDikrTitle.setText(bookWithCount.firstChapterTitle);

        holder.binding.tvOpenRead.setOnClickListener(v -> clickListener.onOpenBookClicked(bookWithCount));

        if (!bookWithCount.isSaved) {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
        } else {
            Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
        }

        holder.binding.imgSave.setOnClickListener(v -> {
            if (!bookWithCount.isSaved) {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                bookWithCount.isSaved = true;
                clickListener.onBookSaveClick(bookWithCount, true, position);
            } else {
                Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                bookWithCount.isSaved = false;
                clickListener.onBookSaveClick(bookWithCount, false, position);
            }
        });

        holder.binding.imgSave.setOnClickListener(v -> {
            if (bookWithCount.isSaved) {
                // Create a new Handler
                Handler handler = new Handler();

                // Show the Snackbar with the cancel button
                Snackbar snackbar = Snackbar.make(v, "الحدف من المحوظات بعد 4 ثواني", Snackbar.LENGTH_LONG);
                snackbar.setAction("Cancel", v1 -> {
                    // If the user presses cancel, remove the callbacks from the handler
                    handler.removeCallbacksAndMessages(null);
                    Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_saved_new).into(holder.binding.imgSave);
                });
                snackbar.show();

                // Post a delayed Runnable to the handler
                handler.postDelayed(() -> {
                    // This code will be executed after 4 seconds unless the user presses cancel
                    Glide.with(holder.binding.imgSave.getContext()).load(R.drawable.ic_unsaved_new).into(holder.binding.imgSave);
                    bookWithCount.isSaved = false;
                    clickListener.onBookSaveClick(bookWithCount, false, position);
                }, 4000); // 4 seconds delay
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void removeItem(int position) {
        this.dataList.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSavedDikrBinding binding;

        public ViewHolder(ItemSavedDikrBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface ClickListener {
        void onOpenChapterClicked(Chapter chapter);

        void onOpenBookClicked(BookWithCount bookWithCount);

        void onOpenMatnClicked(SavedMatnPage savedMatnPage);

        void onChapterSaveClick(Chapter chapter, boolean isSaved, int position);

        void onBookSaveClick(BookWithCount bookWithCount, boolean isSaved, int position);

        void onMatnSaveClick(SavedMatnPage savedMatnPage, int position);
    }
}