package com.example.stocks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.stocks.R;
import com.example.stocks.network.DataService;
import com.example.stocks.network.GsonCallBack;
import com.example.stocks.utils.Constants;
import com.example.stocks.utils.PreferenceStorageManager;
import com.example.stocks.utils.StockItem;

import java.text.DecimalFormat;
import java.util.Map;

public class StockDetailActivity extends AppCompatActivity {
    private View detailContentView;
    private View progressView;
    private ScrollView detailScrollView;

    // stock info section
    private TextView tickerView;
    private TextView priceView;
    private TextView nameView;
    private TextView changeView;
    private WebView highChartView;

    // stock portfolio section
    private TextView portfolioFirstLine;
    private TextView portfoliosSecondLine;

    // stock stats section
    private TextView currentPriceView;
    private TextView lowPriceView;
    private TextView bidPriceView;
    private TextView openPriceView;
    private TextView midPriceView;
    private TextView highPriceView;
    private TextView volumeView;


    private String queryTicker;
    private String description;

    private static final String TAG = "StockDetailActivity";
    private static final String HIGHCHART_URL = "file:///android_asset/highcharts.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        detailContentView = findViewById(R.id.detail_content);
        detailScrollView = findViewById(R.id.detail_scroll_view);
        progressView = findViewById(R.id.detail_progress_content);

        tickerView = findViewById(R.id.stock_detail_ticker);
        priceView = findViewById(R.id.stock_detail_price);
        nameView = findViewById(R.id.stock_detail_name);
        changeView = findViewById(R.id.stock_detail_change);
        highChartView = findViewById(R.id.highchart);

        portfolioFirstLine = findViewById(R.id.portfolio_first_line);
        portfoliosSecondLine = findViewById(R.id.portfolio_second_line);

        currentPriceView = findViewById(R.id.current_price);
        lowPriceView = findViewById(R.id.low_price);
        bidPriceView = findViewById(R.id.bid_price);
        openPriceView = findViewById(R.id.open_price);
        midPriceView = findViewById(R.id.mid_price);
        highPriceView = findViewById(R.id.high_price);
        volumeView = findViewById(R.id.volume);

        detailScrollView.setBackgroundColor(StockDetailActivity.this.getColor(R.color.background_color));
        detailContentView.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.stock_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            queryTicker = query.split(" - ")[0];
        } else {
            queryTicker = intent.getStringExtra("ticker");
        }

        fetchStockOutlook();
        fetchStockSummary();
        fetchHighChart();
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

    private void fetchStockSummary() {
        DataService.getInstance().fetchStockSummary(queryTicker, new GsonCallBack<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> result) throws Exception {
                double change = Double.parseDouble(String.valueOf(result.get("change")));

                String defaultPrice = "0.0";
                String changeText = "$" + String.valueOf(change);
                String currentPrice = result.get("last") == null ? defaultPrice : String.valueOf(result.get("last"));
                String lowPrice = result.get("low") == null ? defaultPrice : String.valueOf(result.get("low"));
                String bidPrice = result.get("bidPrice") == null ? defaultPrice : String.valueOf(result.get("bidPrice"));
                String openPrice = result.get("open") == null ? defaultPrice : String.valueOf(result.get("open"));
                String midPrice = (result.get("mid") == null || result.get("mid").equals("-"))? defaultPrice : String.valueOf(result.get("mid"));
                String highPrice = result.get("high") == null ? defaultPrice : String.valueOf(result.get("high"));
                String volume = result.get("volume") == null ? defaultPrice : String.valueOf(result.get("volume"));

                DecimalFormat decimalFormat = new DecimalFormat("####0.00");
                decimalFormat.setGroupingUsed(true);
                decimalFormat.setGroupingSize(3);

                double volumeDouble = Double.parseDouble(volume);
                double priceDouble = Double.parseDouble(currentPrice);
                String formattedVolume = decimalFormat.format(volumeDouble);

                if (change > 0) {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.green));
                } else if (change < 0) {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.red));
                    changeText = "-" + changeText;
                } else {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.grey));
                }

                String headerPrice = "$" + currentPrice;
                changeView.setText(changeText);
                priceView.setText(headerPrice);

                currentPrice = "Current Price: " + currentPrice;
                lowPrice = "Low: " + lowPrice;
                bidPrice = "Bid Price: " + bidPrice;
                openPrice = "OpenPrice: " + openPrice;
                midPrice = "Mid: " + midPrice;
                highPrice = "High: " + highPrice;
                formattedVolume = "Volume: " + formattedVolume;

                currentPriceView.setText(currentPrice);
                lowPriceView.setText(lowPrice);
                bidPriceView.setText(bidPrice);
                openPriceView.setText(openPrice);
                midPriceView.setText(midPrice);
                highPriceView.setText(highPrice);
                volumeView.setText(formattedVolume);

                String firstLineText;
                String secondLineText;
                StockItem portfolioStock = PreferenceStorageManager.getStockItemByTicker(Constants.PORTFOLIO_KEY, queryTicker);
                if (portfolioStock != null) {
                    firstLineText = "Shares owned: " + portfolioStock.stockShares + ".0";
                    decimalFormat.setGroupingUsed(false);
                    double marketValue = Double.parseDouble(portfolioStock.stockShares) * priceDouble;
                    secondLineText = "Market Value: $" + decimalFormat.format(marketValue);
                } else {
                    firstLineText = "You have 0 shares of " + queryTicker + ".";
                    secondLineText = "Start Trading!";
                }
                portfolioFirstLine.setText(firstLineText);
                portfoliosSecondLine.setText(secondLineText);

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

    private void initPortfolioData() {

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
