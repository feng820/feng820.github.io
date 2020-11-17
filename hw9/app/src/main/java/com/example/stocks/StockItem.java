package com.example.stocks;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class StockItem{

    final String stockTicker;
    String stockName;
    String stockPrice;
    String stockInfo;
    String stockPriceChange;
    @ColorInt
    int stockChangeColor;
    @DrawableRes
    int stockPriceChangeIcon;
    String stockShares;

    StockItem(@NonNull final String stockTicker, String stockName, @NonNull String stockPrice,
              String stockPriceChange, @ColorInt int stockChangeColor,
              @DrawableRes int stockPriceChangeIcon) {
        this(stockTicker, stockName, stockPrice, stockPriceChange, stockChangeColor,
                stockPriceChangeIcon, "0");
    }

    StockItem(@NonNull final String stockTicker, String stockName, @NonNull String stockPrice,
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
