package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.BooksParte;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Ignore;

import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.data.room.Entities.BookWithCount;
import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BooksUtils {
    private static final String PREFS_NAME = "com.app.dz.quranapp";
    private static final String SAVED_BOOKS_KEY = "saved_books";

    private static void savebooks(Context context, List<BookWithCount> chapters) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chapters);
        Log.e("savebooks", "savebooks: " + json);
        editor.putString(SAVED_BOOKS_KEY, json);
        editor.apply();
    }



    public static List<BookWithCount> getSavedBooksList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(SAVED_BOOKS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<BookWithCount>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void updateBook(Context context,BookWithCount book) {
        AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
        BookDao bookdao = db.getBookDao();

        new Thread(() -> {
            try {
                if (book.isSaved) book.firstChapterTitle = bookdao.getFirstChapterInBook(book.bookCollection,book.bookNumber);

                List<BookWithCount> savedBookWithCounts = getSavedBooksList(context);
                boolean isBookWithCountFound = false;
                for (int i = 0; i < savedBookWithCounts.size(); i++) {
                    if (savedBookWithCounts.get(i).getCompleteBookId().equals(book.getCompleteBookId())) {
                        if (!book.isSaved) {
                            savedBookWithCounts.remove(i); // Remove the book if it is not saved
                        } else {
                            savedBookWithCounts.set(i, book); // Update the book if it is saved
                        }
                        isBookWithCountFound = true;
                        break;
                    }
                }
                if (!isBookWithCountFound && book.isSaved) {
                    savedBookWithCounts.add(book); // Add the book only if it is not found and it is saved
                }
                savebooks(context,savedBookWithCounts);
                // Process the result here



            } catch (Exception e) {
                // Handle exception
            }
        }).start();
    }

    public static boolean isBookSaved(Context context, BookWithCount book) {
        List<BookWithCount> savedBookWithCounts = getSavedBooksList(context);
        for (BookWithCount savedBookWithCount : savedBookWithCounts) {
            if (savedBookWithCount.getCompleteBookId().equals(book.getCompleteBookId())) {
                return true;
            }
        }
        return false;
    }

}