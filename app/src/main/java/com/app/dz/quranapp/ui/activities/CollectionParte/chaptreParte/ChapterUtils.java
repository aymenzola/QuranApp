package com.app.dz.quranapp.ui.activities.CollectionParte.chaptreParte;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.app.dz.quranapp.data.room.Entities.Chapter;
import com.app.dz.quranapp.ui.activities.CollectionParte.HadithDetailsParte.ActivityHadithDetailsListDev;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChapterUtils {
    private static final String PREFS_NAME = "com.app.dz.quranapp";
    private static final String SAVED_CHAPTERS_KEY = "saved_chapters";
    private static final String LAST_SAVED_CHAPTERS_KEY = "last_saved_chapters";

    private static void saveChapters(Context context, List<Chapter> chapters) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chapters);
        editor.putString(SAVED_CHAPTERS_KEY, json);
        editor.apply();
    }

    public static void saveLastChapter(Context context, Chapter chapter) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chapter, Chapter.class);
        Log.e("quran_tag", "we are saving last chapter "+json);
        editor.putString(LAST_SAVED_CHAPTERS_KEY, json);
        editor.apply();
    }

    public static Chapter getLastSavedChapter(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //should get the last saved chapter
        String json = prefs.getString(LAST_SAVED_CHAPTERS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Chapter>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return null;
    }

    public static List<Chapter> getSavedChapters(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(SAVED_CHAPTERS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Chapter>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }


    public static void updateChapter(Context context, Chapter chapter) {
        List<Chapter> savedChapters = getSavedChapters(context);
        boolean isChapterFound = false;
        for (int i = 0; i < savedChapters.size(); i++) {
            if (savedChapters.get(i).getCompleteChapterId().equals(chapter.getCompleteChapterId())) {
                if (!chapter.isSaved) {
                    savedChapters.remove(i); // Remove the chapter if it is not saved
                } else {
                    savedChapters.set(i,chapter); // Update the chapter if it is saved
                }
                isChapterFound = true;
                break;
            }
        }
        if (!isChapterFound && chapter.isSaved) {
            savedChapters.add(chapter); // Add the chapter only if it is not found and it is saved
        }
        saveChapters(context, savedChapters);
    }

    public static boolean isChapterSaved(Context context, Chapter chapter) {
        List<Chapter> savedChapters = getSavedChapters(context);
        for (Chapter savedChapter : savedChapters) {
            if (savedChapter.getCompleteChapterId().equals(chapter.getCompleteChapterId())) {
                return true;
            }
        }
        return false;
    }


    public static void moveToChapterDetails(Context context, Chapter chapter) {
        Intent intent = new Intent(context, ActivityHadithDetailsListDev.class);
        intent.putExtra("chapter", new Gson().toJson(chapter, Chapter.class));
        context.startActivity(intent);
    }

}