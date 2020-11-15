package com.example.stocks;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class StockItem {
    final String stockTicker;
    String stockPrice;
    String stockInfo;
    String stockPriceChange;
    @ColorInt
    int stockChangeColor;
    @DrawableRes
    int stockPriceChangeIcon;

    StockItem(@NonNull final String stockTicker, @NonNull String stockPrice, String stockInfo,
              String stockPriceChange, @ColorInt int stockChangeColor,
              @DrawableRes int stockPriceChangeIcon) {
        this.stockTicker = stockTicker;
        this.stockPrice = stockPrice;
        this.stockInfo = stockInfo;
        this.stockPriceChange = stockPriceChange;
        this.stockChangeColor = stockChangeColor;
        this.stockPriceChangeIcon = stockPriceChangeIcon;
    }
}
