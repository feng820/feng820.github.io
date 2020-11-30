package com.example.stocks.utils;

import android.util.Log;

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
    public String stockShares;

    private static final String TAG = "StockItem";

    public StockItem(String stockTicker) {
        this.stockTicker = stockTicker;
    }

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

        if (Double.parseDouble(stockShares) == 0) {
            this.stockInfo = this.stockName;
        } else {
            this.stockInfo = formatShares(stockShares);
        }

    }

    public void updateStockSharesAndInfo(String stockShares) {
        if (this.stockShares != null && this.stockShares.equals(stockShares)) {
            return;
        }
        this.stockShares = stockShares;
        this.stockInfo = Double.parseDouble(stockShares) == 0 ? this.stockName : formatShares(stockShares);
    }

    private String formatShares(String shares) {
        double sharesDouble = Double.parseDouble(shares);
        if (sharesDouble < 1) {
            return sharesDouble + " Share";
        }

        if ((sharesDouble % 1) == 0) {
            if (sharesDouble == 1) {
                return sharesDouble + " Share";
            }
            double roundOff = Math.round(sharesDouble * 10.0) / 10.0;
            return roundOff + " Shares";
        } else {
            return sharesDouble + " Shares";
        }
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
