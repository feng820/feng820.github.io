package com.example.stocks.network;

import android.util.Log;

import com.android.volley.Response;
import com.example.stocks.utils.StockApplication;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class DataService {
    private static DataService instance;
    private static final String BASE_URL = "http://linfengj-571-hw8.us-east-1.elasticbeanstalk.com/api/";
    private static final String TAG = "DataService";

    public static DataService getInstance() {
        if (instance == null) {
            synchronized (DataService.class) {
                if (instance == null) {
                    instance = new DataService();
                }
            }
        }
        return instance;
    }

    public void getAutoSuggestions(String query, GsonCallBack<List<Map<String, String>>> callback) {
        String url = BASE_URL + "search/" + query;
        GsonRequest<List<Map<String, String>>> jsonObjectRequest = new GsonRequest
                (url, List.class, null,
                        (Response.Listener<List<Map<String, String>>>) response -> {
                            if (response != null) {
                                try {
                                    callback.onSuccess(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, error -> {
                            if (error.getMessage() != null && error.getMessage().length() > 0) {
                                try {
                                    callback.onError(error.getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "getAutoSuggestions: Unexpected Error");
                            }
                        });

        VolleyController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void fetchLatestHomeData(List<String> tickers, GsonCallBack<List<Map<String, String>>> callback) {
        String query = String.join(",", tickers);
        String url = BASE_URL + "price/" + query;
        GsonRequest<List<Map<String, String>>> jsonObjectRequest = new GsonRequest
                (url, List.class, null,
                        (Response.Listener<List<Map<String, String>>>) response -> {
                            if (response != null) {
                                try {
                                    callback.onSuccess(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, error -> {
                            if (error.getMessage() != null && error.getMessage().length() > 0) {
                                try {
                                    callback.onError(error.getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "getAutoSuggestions: Unexpected Error");
                            }
                        });

        VolleyController.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void fetchStockOutlook(String ticker, GsonCallBack<Map<String, String>> callback) {
        String url = BASE_URL + "outlook/" + ticker;
        GsonRequest<Map<String, String>> jsonObjectRequest = new GsonRequest
                (url, Map.class, null,
                        (Response.Listener<Map<String, String>>) response -> {
                            if (response != null) {
                                try {
                                    callback.onSuccess(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, error -> {
                            if (error.getMessage() != null && error.getMessage().length() > 0) {
                                Log.d("error", error.getMessage());
                            }

                        });

        VolleyController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void fetchStockSummary(String ticker, GsonCallBack<Map<String, String>> callback) {
        String url = BASE_URL + "summary/" + ticker;
        GsonRequest<Map<String, String>> jsonObjectRequest = new GsonRequest
                (url, Map.class, null,
                        (Response.Listener<Map<String, String>>) response -> {
                            if (response != null) {
                                try {
                                    callback.onSuccess(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, error -> {
                    if (error.getMessage() != null && error.getMessage().length() > 0) {
                        Log.d("error", error.getMessage());
                    }

                });

        VolleyController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void fetchNewsData(String ticker, GsonCallBack<List<Map<String, Object>>> callback) {
        String url = BASE_URL + "news/" + ticker;
        GsonRequest<List<Map<String, Object>>> jsonObjectRequest = new GsonRequest
                (url, List.class, null,
                        (Response.Listener<List<Map<String, Object>>>) response -> {
                            if (response != null) {
                                try {
                                    callback.onSuccess(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, error -> {
                    if (error.getMessage() != null && error.getMessage().length() > 0) {
                        Log.d("error", error.getMessage());
                    }

                });

        VolleyController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
