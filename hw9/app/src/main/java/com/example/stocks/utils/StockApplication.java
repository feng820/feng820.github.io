package com.example.stocks.utils;

import android.app.Application;

public class StockApplication extends Application {

    private static StockApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static StockApplication getContext() {
        return context;
    }
}
