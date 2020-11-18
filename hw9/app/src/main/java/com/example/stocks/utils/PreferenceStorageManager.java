package com.example.stocks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class PreferenceStorageManager {

    private static final String STORAGE_KEY = "stock";
    private static final String TAG = "PreferenceStorageManage";

    private static SharedPreferences sharedPreferences;

    private PreferenceStorageManager() { }

    public static void init() {
        if (sharedPreferences == null) {
            sharedPreferences = StockApplication.getContext()
                    .getSharedPreferences(STORAGE_KEY, Context.MODE_PRIVATE);
        }
    }

    public static void clearAll() {
        List<StockItem> portfolioList = getSectionStockList(Constants.PORTFOLIO_KEY);
        List<StockItem> favorites = getSectionStockList(Constants.FAVORITE_KEY);
        portfolioList.clear();
        favorites.clear();
        updateStorage(Constants.PORTFOLIO_KEY, portfolioList);
        updateStorage(Constants.FAVORITE_KEY, favorites);
    }

    public static void addStockItemToSection(String key, StockItem stockItem) {
        List<StockItem> sectionStockList = getSectionStockList(key);
        sectionStockList.add(stockItem);
        updateStorage(key, sectionStockList);
    }

    public static List<StockItem> deleteStockItemFromSection(String key, StockItem stockItem) {
        List<StockItem> sectionStockList = getSectionStockList(key);
        if(!sectionStockList.remove(stockItem)) {
            Log.e(TAG, "deleteStockItemFromSection: This stock item is not in the storage");
        } else {
            updateStorage(key, sectionStockList);
        }
        return sectionStockList;
    }

    public static List<StockItem> getSectionStockList(String key) {
        String jsonList = sharedPreferences.getString(key, null);
        List<StockItem> stockSectionList = new Gson().fromJson(jsonList, new TypeToken<List<StockItem>>(){}.getType());
        return stockSectionList == null ? new ArrayList<>() : stockSectionList;
    }


    public static void updateStorage(String key, List<StockItem> sectionStockList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, new Gson().toJson(sectionStockList));
        editor.apply();
    }
}
