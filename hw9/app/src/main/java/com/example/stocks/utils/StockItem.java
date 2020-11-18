package com.example.stocks.utils;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class StockItem{

    public final String stockTicker;
    public String stockName;
    public String stockPrice;
    public String stockInfo;
    public String stockPriceChange;
    @ColorInt
    public int stockChangeColor;
    @DrawableRes
    public int stockPriceChangeIcon;
    String stockShares;

    public StockItem(@NonNull final String stockTicker, String stockName, @NonNull String stockPrice,
              String stockPriceChange, @ColorInt int stockChangeColor,
              @DrawableRes int stockPriceChangeIcon) {
        this(stockTicker, stockName, stockPrice, stockPriceChange, stockChangeColor,
                stockPriceChangeIcon, "0");
    }

    public StockItem(@NonNull final String stockTicker, String stockName, @NonNull String stockPrice,
              String stockPriceChange, @ColorInt int stockChangeColor,
              @DrawableRes int stockPriceChangeIcon, String stockShares) {
        this.stockTicker = stockTicker;
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.stockPriceChange = stockPriceChange;
        this.stockChangeColor = stockChangeColor;
        this.stockPriceChangeIcon = stockPriceChangeIcon;
        this.stockShares = stockShares;

        if (Integer.parseInt(stockShares) == 0) {
            this.stockInfo = this.stockName;
        } else {
            this.stockInfo = stockShares + ".0 Shares";
        }

    }

    public void updateStockSharesAndInfo(String stockShares) {
        if (this.stockShares.equals(stockShares)) {
            return;
        }
        this.stockShares = stockShares;
        this.stockInfo = Integer.parseInt(stockShares) == 0 ? this.stockName : stockShares + ".0 Shares";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StockItem)) {
            return false;
        }

        StockItem another = (StockItem) obj;
        return this.stockTicker.equals(another.stockTicker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.stockTicker);
    }
}
