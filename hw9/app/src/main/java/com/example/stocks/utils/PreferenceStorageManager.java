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
    private static final String DEFAULT_CASH = "20000.00";

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
        portfolioList.clear();
        updateSectionStockList(Constants.PORTFOLIO_KEY, portfolioList);

        List<StockItem> favorites = getSectionStockList(Constants.FAVORITE_KEY);
        favorites.clear();
        updateSectionStockList(Constants.FAVORITE_KEY, favorites);
    }

    public static void addOrUpdatePortfolio(StockItem stockItem, String cost) {
        List<StockItem> portfolioList = getSectionStockList(Constants.PORTFOLIO_KEY);
        if (stockItem == null) {
            Log.e(TAG, "Invalid stock to add to the portfolio section");
            return;
        }

        StockItem oldStockItem = getStockItemByTickerFromList(portfolioList, stockItem.stockTicker);
        if (oldStockItem != null) {
            oldStockItem.updateStockSharesAndInfo(stockItem.stockShares);
        } else {
            portfolioList.add(stockItem);
        }
        updateCashLeft(String.valueOf(Double.parseDouble(getUninventedCash()) - Double.parseDouble(cost)));
        updateSectionStockList(Constants.PORTFOLIO_KEY, portfolioList);

        List<StockItem> favoriteList = getSectionStockList(Constants.FAVORITE_KEY);
        StockItem itemToAddInFavorite = getStockItemByTickerFromList(favoriteList, stockItem.stockTicker);
        if (itemToAddInFavorite != null) {
            itemToAddInFavorite.updateStockSharesAndInfo(stockItem.stockShares);
            updateSectionStockList(Constants.FAVORITE_KEY, favoriteList);
        }
    }

    public static void deleteOrUpdatePortfolio(StockItem stockItem, String revenue) {
        List<StockItem> portfolioList = getSectionStockList(Constants.PORTFOLIO_KEY);
        if (stockItem == null) {
            Log.e(TAG, "Invalid Stock ticker to delete from portfolio section");
            return;
        }

        StockItem oldStockItem = getStockItemByTickerFromList(portfolioList, stockItem.stockTicker);
        if (oldStockItem == null) {
            Log.e(TAG, "Stock item is not in the portfolio section");
            return;
        }

        if (Double.parseDouble(stockItem.stockShares) == 0) {
            portfolioList.remove(oldStockItem);
        } else {
            oldStockItem.updateStockSharesAndInfo(stockItem.stockShares);
        }
        updateCashLeft(String.valueOf(Double.parseDouble(getUninventedCash()) + Double.parseDouble(revenue)));
        updateSectionStockList(Constants.PORTFOLIO_KEY, portfolioList);

        List<StockItem> favoriteList = getSectionStockList(Constants.FAVORITE_KEY);
        StockItem itemToDeleteInFavorite = getStockItemByTickerFromList(favoriteList, stockItem.stockTicker);
        if (itemToDeleteInFavorite != null) {
            itemToDeleteInFavorite.updateStockSharesAndInfo(String.valueOf(stockItem.stockShares));
            updateSectionStockList(Constants.FAVORITE_KEY, favoriteList);
        }
    }

    public static void addStockItemToFavorite(StockItem stockItem) {
        List<StockItem> favoriteList = getSectionStockList(Constants.FAVORITE_KEY);
        if (stockItem == null) {
            Log.e(TAG, "Invalid stock to add to the favorite section");
            return;
        }

        if (favoriteList.contains(stockItem)) {
            Log.e(TAG, "Stock is already in the favorite section");
            return;
        }

        favoriteList.add(stockItem);
        updateSectionStockList(Constants.FAVORITE_KEY, favoriteList);
    }

    public static List<StockItem> deleteStockItemFromFavorite(String stockTicker) {
        List<StockItem> favoriteList = getSectionStockList(Constants.FAVORITE_KEY);
        if (stockTicker == null) {
            Log.e(TAG, "Invalid Stock ticker to delete from favorite section");
            return favoriteList;
        }

        StockItem itemToDelete = getStockItemByTickerFromList(favoriteList, stockTicker);
        if (itemToDelete == null) {
            Log.e(TAG, "Stock item is not in the favorite section");
            return favoriteList;
        }

        favoriteList.remove(itemToDelete);
        updateSectionStockList(Constants.FAVORITE_KEY, favoriteList);
        return favoriteList;
    }

    public static StockItem getStockItemByTickerFromStorage(String key, String ticker) {
        List<StockItem> sectionStockList = getSectionStockList(key);
        for (StockItem item : sectionStockList) {
            if (item.stockTicker.equals(ticker)) {
                return item;
            }
        }
        return null;
    }

    public static StockItem getStockItemByTickerFromList(List<StockItem> sectionStockList, String ticker) {
        for (StockItem item : sectionStockList) {
            if (item.stockTicker.equals(ticker)) {
                return item;
            }
        }
        return null;
    }

    public static List<StockItem> getSectionStockList(String key) {
        String jsonList = sharedPreferences.getString(key, null);
        List<StockItem> stockSectionList = new Gson().fromJson(jsonList, new TypeToken<List<StockItem>>(){}.getType());
        return stockSectionList == null ? new ArrayList<>() : stockSectionList;
    }

    public static void updateSectionStockList(String key, List<StockItem> sectionStockList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, new Gson().toJson(sectionStockList));
        editor.apply();
    }

    public static void updateCashLeft(String cashLeft) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.CASH_LEFT, new Gson().toJson(cashLeft));
        editor.apply();
    }

    public static String getUninventedCash() {
        String jsonList = sharedPreferences.getString(Constants.CASH_LEFT, null);
        String cash = new Gson().fromJson(jsonList, new TypeToken<String>(){}.getType());
        if (cash == null) {
            updateCashLeft(DEFAULT_CASH);
        }
        return cash == null ? DEFAULT_CASH : cash;
    }

    public static String getNetWorth() {
        List<StockItem> portfolioList = getSectionStockList(Constants.PORTFOLIO_KEY);
        double stockValue = 0;
        for (StockItem item : portfolioList) {
            stockValue += Double.parseDouble(item.stockShares) * Double.parseDouble(item.stockPrice);
        }

        return String.valueOf(Double.parseDouble(getUninventedCash()) + stockValue);
    }
}
