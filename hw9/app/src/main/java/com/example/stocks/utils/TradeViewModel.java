package com.example.stocks.utils;

import android.content.Context;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.stocks.BR;

public class TradeViewModel extends BaseObservable {
    private Context context;
    private String tradeAmount;
    private String currentPrice;
    private String product;
    private static final String TAG = "TradeViewModel";

    public TradeViewModel(Context context, String currentPrice) {
        this.context = context;
        this.currentPrice = currentPrice;
        this.product = "0.00";
        this.tradeAmount = "";
    }

    @Bindable
    public String getTradeAmount() {
        return tradeAmount;
    }

    @Bindable
    public String getCurrentPrice() {
        return currentPrice;
    }

    @Bindable
    public String getProduct() {
        return product;
    }

    public void setTradeAmount(String value) {
        if (!tradeAmount.equals(value)) {
            tradeAmount = value;
            if (!value.isEmpty()) {
                try {
                    double amount = Double.parseDouble(value);
                    double price = Double.parseDouble(currentPrice);
                    double roundOff = Math.round(amount * price * 100.0) / 100.0;
                    product = String.valueOf(roundOff);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "setTradeAmount: Invalid input");
                }
            } else {
                product = "0.00";
            }
            notifyPropertyChanged(BR.tradeAmount);
            notifyPropertyChanged(BR.product);
        }
    }
}
