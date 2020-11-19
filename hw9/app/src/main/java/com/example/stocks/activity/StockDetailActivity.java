package com.example.stocks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.stocks.R;
import com.example.stocks.network.DataService;
import com.example.stocks.network.GsonCallBack;

import java.util.Map;

public class StockDetailActivity extends AppCompatActivity {
    private View detailContentView;
    private View progressView;
    private ScrollView detailScrollView;
    private TextView tickerView;
    private TextView priceView;
    private TextView nameView;
    private TextView changeView;
    private WebView highChartView;

    private String queryTicker;
    private String description;

    private static final String TAG = "StockDetailActivity";
    private static final String HIGHCHART_URL = "file:///android_asset/highcharts.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        tickerView = findViewById(R.id.stock_detail_ticker);
        priceView = findViewById(R.id.stock_detail_price);
        nameView = findViewById(R.id.stock_detail_name);
        changeView = findViewById(R.id.stock_detail_change);
        detailContentView = findViewById(R.id.detail_content);
        detailScrollView = findViewById(R.id.detail_scroll_view);
        progressView = findViewById(R.id.detail_progress_content);
        highChartView = findViewById(R.id.highchart);

        detailScrollView.setBackgroundColor(StockDetailActivity.this.getColor(R.color.background_color));
        detailContentView.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.stock_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        handleIntent(getIntent());
    }

    private void fetchStockOutlook() {
        DataService.getInstance().fetchStockOutlook(queryTicker, new GsonCallBack<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> result) throws Exception {
                tickerView.setText(queryTicker);
                nameView.setText(result.get("name"));
                description = result.get("description");
            }

            @Override
            public void onError(String result) throws Exception {
                Log.e(TAG, "onError: Cannot Fetch Stock Outlook due to " + result);
            }
        });
    }

    private void fetchSummaryOutlook() {
        DataService.getInstance().fetchStockSummary(queryTicker, new GsonCallBack<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> result) throws Exception {
                double change = Double.parseDouble(String.valueOf(result.get("change")));
                String changeText = "$" + String.valueOf(change);
                String priceText = "$" + String.valueOf(result.get("last"));

                if (change > 0) {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.green));
                } else if (change < 0) {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.red));
                    changeText = "-" + changeText;
                } else {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.grey));
                }

                changeView.setText(changeText);
                priceView.setText(priceText);

                progressView.setVisibility(View.GONE);
                detailScrollView.setBackgroundColor(StockDetailActivity.this.getColor(R.color.white));
                detailContentView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String result) throws Exception {
                Log.e(TAG, "onError: Cannot Fetch Stock Summary due to " + result);
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void fetchHighChart() {
        highChartView.getSettings().setJavaScriptEnabled(true);
        highChartView.loadUrl(HIGHCHART_URL + "?ticker=" + queryTicker);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            queryTicker = query.split(" - ")[0];
        } else {
            queryTicker = intent.getStringExtra("ticker");
        }
        fetchStockOutlook();
        fetchSummaryOutlook();
        fetchHighChart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_detail_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}