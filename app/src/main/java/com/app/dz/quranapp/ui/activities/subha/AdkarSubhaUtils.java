package com.app.dz.quranapp.ui.activities.subha;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdkarSubhaUtils {
    private static final String PREFS_NAME = "com.app.dz.quranapp";
    private static final String ADKAR_KEY = "adkar_subha";
    private static final String LAST_DIKR_KEY = "last_dikr";

    private static void saveAdkarList(Context context, List<AdkarListAdapter.DikrItem> dikrItems) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dikrItems, new TypeToken<List<AdkarListAdapter.DikrItem>>() {}.getType());
        editor.putString(ADKAR_KEY,json);
        editor.apply();
    }

    public static List<AdkarListAdapter.DikrItem> getAdkarList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(ADKAR_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<AdkarListAdapter.DikrItem>>() {}.getType();
            List<AdkarListAdapter.DikrItem> adkarList = gson.fromJson(json,type);
            Collections.reverse(adkarList);
            return adkarList;
        }
        List<AdkarListAdapter.DikrItem> defaultList = getDefaulAdkarList();
        Collections.reverse(defaultList);
        return defaultList;
    }

    public static void addDikrItem(Context context, AdkarListAdapter.DikrItem dikrItem) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        List<AdkarListAdapter.DikrItem> savedBookWithCounts = getAdkarList(context);

        boolean isBookWithCountFound = false;
        for (int i = 0; i < savedBookWithCounts.size(); i++) {
            if (savedBookWithCounts.get(i).dikr.equals(dikrItem.dikr)) {
                savedBookWithCounts.set(i,dikrItem);
                isBookWithCountFound = true;
                break;
            }
        }
        if (!isBookWithCountFound) {
            savedBookWithCounts.add(dikrItem);
        }

        editor.putString(LAST_DIKR_KEY,dikrItem.dikr).apply();
        saveAdkarList(context,savedBookWithCounts);
    }

    //get last dikr
    public static String getLastDikr(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(LAST_DIKR_KEY, "سبحان الله");
    }

    private static List<AdkarListAdapter.DikrItem> getDefaulAdkarList() {
        List<AdkarListAdapter.DikrItem> adkarList = new ArrayList<>();
        adkarList.add(new AdkarListAdapter.DikrItem("سبحان الله", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("الحمد لله", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("الله اكبر", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("لا اله الا الله", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("استغفر الله", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم صلي على محمد", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اسالك الجنه", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من النار", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من الفقر", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من الكفر", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من الشرك", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من النفاق", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من الفتن", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من عذاب القبر", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من عذاب النار", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من فتنة المحيا والممات", 1));
        adkarList.add(new AdkarListAdapter.DikrItem("اللهم اني اعوذ بك من الفتنة المسيح الدجال", 1));

        return adkarList;
    }

}