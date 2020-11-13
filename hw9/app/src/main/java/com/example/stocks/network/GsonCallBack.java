package com.example.stocks.network;

public interface GsonCallBack<T> {
    void onSuccess(T result) throws Exception;
    void onError(String result) throws Exception;
}
