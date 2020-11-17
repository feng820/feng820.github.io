package com.example.stocks.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.stocks.utils.StockApplication;

public class VolleyController {
    private static VolleyController instance;
    private RequestQueue requestQueue;
    private static final Context context = StockApplication.getContext();

    private VolleyController() {
        requestQueue = getRequestQueue();
    }

    public static VolleyController getInstance() {
        if (instance == null) {
            synchronized (VolleyController.class) {
                if (instance == null) {
                    instance = new VolleyController();
                }
            }
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
