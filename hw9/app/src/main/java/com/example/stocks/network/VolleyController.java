package com.example.stocks.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyController {
    private static VolleyController instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private VolleyController(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static VolleyController getInstance(Context context) {
        if (instance == null) {
            synchronized (VolleyController.class) {
                if (instance == null) {
                    instance = new VolleyController(context);
                }
            }
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
