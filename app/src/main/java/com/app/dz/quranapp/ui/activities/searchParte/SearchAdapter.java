package com.app.dz.quranapp.ui.activities.searchParte;


import static com.app.dz.quranapp.ui.activities.searchParte.SearchActivity.SEARCH_TYPE_HADITH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ItemBookBinding;
import com.app.dz.quranapp.databinding.ItemSearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HADITH = 0;
    private static final int VIEW_TYPE_BOOK = 1;
    private static final int VIEW_TYPE_CHAPTER = 2;

    private Context mCtx;
    private List<Object> arrayList = new ArrayList<>();
    private OnAdapterClickListener listener;

    // creating a constructor class for our adapter class.
    public SearchAdapter(Context mCtx, OnAdapterClickListener listener1) {
        this.mCtx = mCtx;
        this.listener = listener1;
    }

    public void addHadiths(List<Hadith> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        this.arrayList.clear();
        notifyDataSetChanged();
    }

    public void addBooks(List<Book> items) {
        this.arrayList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HADITH) {

            ItemSearchBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_search, parent, false);
            return new ViewHolder_Hadith(item);

        } else if (viewType == VIEW_TYPE_CHAPTER) {

            ItemSearchBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_search, parent, false);
            return new ViewHolder_Chapter(item);

        } else {

            ItemBookBinding item = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.item_book, parent, false);
            return new ViewHolder_Book(item);

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HADITH) {
            ViewHolder_Hadith holderp = (ViewHolder_Hadith) holder;
            int p = position+1;
            holderp.binding.tvCount.setText(""+p);

            Hadith model = (Hadith) arrayList.get(position);
            //holderp.binding.tvSuraName.setText(Html.fromHtml(model.body_no_tachkil));
            holderp.binding.tvSuraName.setText(model.stringBuilder);
            holderp.binding.tvDescription.setText(getArabicName(model.collection)+" / الكتاب "+model.bookNumber);
            holderp.binding.clickView.setOnClickListener(v -> listener.onHadithClick(model));

        } else if (getItemViewType(position) == VIEW_TYPE_CHAPTER) {

            ViewHolder_Chapter holderp = (ViewHolder_Chapter) holder;
            int p = position+1;
            holderp.binding.tvCount.setText(""+p);
            Hadith model = (Hadith) arrayList.get(position);
            holderp.binding.tvSuraName.setText(model.chapterTitle);
            holderp.binding.tvDescription.setText(getArabicName(model.collection)+" / الكتاب "+model.bookNumber);
            holderp.binding.clickView.setOnClickListener(v -> listener.onChapterClick(model));


        } else {
            ViewHolder_Book holderp = (ViewHolder_Book) holder;

            Book model = (Book) arrayList.get(position);

            int p = position+1;
            holderp.binding.tvCount.setText(""+p);

            holderp.binding.tvDescription.setText(getArabicName(model.bookCollection));
            holderp.binding.tvSuraName.setText(model.bookName);
            holderp.binding.tvSuraName.setOnClickListener(v -> listener.onBookClick(model));
        }
    }

    private String getArabicName(String bookCollection) {
        switch (bookCollection) {
            case "bukhari":
                return "صحيح البخاري";
            case "muslim":
                return "صحيح مسلم";
            case "nasai":
                return "سنن النسائي";

            case "ibnmajah":
                return "سنن ابن ماجة";

            case "hisn":
                return "حصن المسلم";

            case "abudawud":
                return " سنن أبي داود";

            default:
                return " الأربعون النووية";

        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder_Book extends RecyclerView.ViewHolder {
        ItemBookBinding binding;

        public ViewHolder_Book(ItemBookBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public class ViewHolder_Chapter extends RecyclerView.ViewHolder {
        ItemSearchBinding binding;

        public ViewHolder_Chapter(ItemSearchBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    public class ViewHolder_Hadith extends RecyclerView.ViewHolder {
        ItemSearchBinding binding;

        public ViewHolder_Hadith(ItemSearchBinding bindingg) {
            super(bindingg.getRoot());
            binding = bindingg;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) instanceof Hadith) {
            Hadith hadith = (Hadith) arrayList.get(position);
            if (hadith.type == SEARCH_TYPE_HADITH) return VIEW_TYPE_HADITH;
            else return VIEW_TYPE_CHAPTER;
        } else return VIEW_TYPE_BOOK;
    }

    public interface OnAdapterClickListener {
        void onHadithClick(Hadith hadith);

        void onChapterClick(Hadith hadith_chapter);

        void onBookClick(Book book);
    }

}
