package com.example.stocks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stocks.R;
import com.example.stocks.adapter.NewsRecyclerViewAdapter;
import com.example.stocks.databinding.TradeDialogBinding;
import com.example.stocks.network.DataService;
import com.example.stocks.network.GsonCallBack;
import com.example.stocks.utils.Constants;
import com.example.stocks.utils.NewsItem;
import com.example.stocks.utils.PreferenceStorageManager;
import com.example.stocks.utils.StockItem;
import com.example.stocks.utils.TradeViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StockDetailActivity extends AppCompatActivity {
    private View detailContentView;
    private View progressView;
    private NestedScrollView detailScrollView;

    // stock info section
    private TextView tickerView;
    private TextView priceView;
    private TextView nameView;
    private TextView changeView;
    private WebView highChartView;

    // stock portfolio section
    private TextView portfolioFirstLine;
    private TextView portfoliosSecondLine;
    private Button tradeButtonView;
    private TradeDialogBinding tradeDialogBinding;
    private DecimalFormat decimalFormat;

    // stock stats section
    private TextView currentPriceView;
    private TextView lowPriceView;
    private TextView bidPriceView;
    private TextView openPriceView;
    private TextView midPriceView;
    private TextView highPriceView;
    private TextView volumeView;

    // about section
    private TextView descriptionView;
    private TextView showButton;

    // news section
    private List<NewsItem> newsList = new ArrayList<>();
    private RecyclerView newsRecyclerView;

    private StockItem stockItem;
    private String queryTicker;
    private boolean isInFavorite;
    private static final AtomicInteger apiSuccessfulCallbacks = new AtomicInteger(0);

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
        tradeButtonView = findViewById(R.id.trade_button);

        currentPriceView = findViewById(R.id.current_price);
        lowPriceView = findViewById(R.id.low_price);
        bidPriceView = findViewById(R.id.bid_price);
        openPriceView = findViewById(R.id.open_price);
        midPriceView = findViewById(R.id.mid_price);
        highPriceView = findViewById(R.id.high_price);
        volumeView = findViewById(R.id.volume);

        descriptionView = findViewById(R.id.description);
        showButton = findViewById(R.id.show_button);

        newsRecyclerView = findViewById(R.id.news_recycler_view);

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

        queryTicker = queryTicker.toUpperCase();
        stockItem = new StockItem(queryTicker);
        StockItem favoriteStock = PreferenceStorageManager.getStockItemByTickerFromStorage(Constants.FAVORITE_KEY, queryTicker);
        this.isInFavorite = favoriteStock != null;

        fetchStockOutlook();
        fetchStockSummary();
        fetchNewsData();
    }

    private void incrementOrRender() {
        int count = apiSuccessfulCallbacks.addAndGet(1);
        if (count == 3) {
            fetchHighChart();
            progressView.setVisibility(View.GONE);
            detailScrollView.setBackgroundColor(StockDetailActivity.this.getColor(R.color.white));
            detailContentView.setVisibility(View.VISIBLE);
            apiSuccessfulCallbacks.set(0);
        }
    }

    private void fetchStockOutlook() {
        DataService.getInstance().fetchStockOutlook(queryTicker, new GsonCallBack<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> result) throws Exception {
                initExpandableText();
                String name = result.get("name");
                tickerView.setText(queryTicker);
                nameView.setText(name);
                descriptionView.setText(result.get("description"));
                stockItem.stockName = name;

                if (stockItem.stockInfo == null) {
                    stockItem.stockInfo = name;
                }
                incrementOrRender();
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
                String defaultPrice = "0.0";
                String changePrice = result.get("change") == null ? defaultPrice : String.valueOf(result.get("change"));
                String currentPrice = result.get("last") == null ? defaultPrice : String.valueOf(result.get("last"));
                String lowPrice = result.get("low") == null ? defaultPrice : String.valueOf(result.get("low"));
                String bidPrice = result.get("bidPrice") == null ? defaultPrice : String.valueOf(result.get("bidPrice"));
                String openPrice = result.get("open") == null ? defaultPrice : String.valueOf(result.get("open"));
                String midPrice = (result.get("mid") == null || String.valueOf(result.get("mid")).equals("-"))? defaultPrice : String.valueOf(result.get("mid"));
                String highPrice = result.get("high") == null ? defaultPrice : String.valueOf(result.get("high"));
                String volume = result.get("volume") == null ? defaultPrice : String.valueOf(result.get("volume"));

                double change = Double.parseDouble(changePrice);
                String changeText = "$" + change;

                decimalFormat = new DecimalFormat("####0.00");
                decimalFormat.setGroupingUsed(true);
                decimalFormat.setGroupingSize(3);

                double volumeDouble = Double.parseDouble(volume);
                double priceDouble = Double.parseDouble(currentPrice);
                String formattedVolume = decimalFormat.format(volumeDouble);

                if (change > 0) {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.green));
                    stockItem.stockChangeColor = StockDetailActivity.this.getColor(R.color.green);
                    stockItem.stockPriceChangeIcon = R.drawable.ic_twotone_trending_up_24;
                } else if (change < 0) {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.red));
                    stockItem.stockChangeColor = StockDetailActivity.this.getColor(R.color.red);
                    stockItem.stockPriceChangeIcon = R.drawable.ic_baseline_trending_down_24;
                    changeText = "-" + changeText;
                } else {
                    changeView.setTextColor(StockDetailActivity.this.getColor(R.color.grey));
                    stockItem.stockChangeColor = StockDetailActivity.this.getColor(R.color.grey);
                }

                stockItem.stockPrice = round(currentPrice);
                stockItem.stockPriceChange = round(String.valueOf(change));

                String headerPrice = "$" + round(currentPrice);
                changeView.setText(changeText);
                priceView.setText(headerPrice);

                currentPrice = "Current Price: " + round(currentPrice);
                lowPrice = "Low: " + round(lowPrice);
                bidPrice = "Bid Price: " + round(bidPrice);
                openPrice = "OpenPrice: " + round(openPrice);
                midPrice = "Mid: " + round(midPrice);
                highPrice = "High: " + round(highPrice);
                formattedVolume = "Volume: " + formattedVolume;

                currentPriceView.setText(currentPrice);
                lowPriceView.setText(lowPrice);
                bidPriceView.setText(bidPrice);
                openPriceView.setText(openPrice);
                midPriceView.setText(midPrice);
                highPriceView.setText(highPrice);
                volumeView.setText(formattedVolume);

                StockItem portfolioStockItem = PreferenceStorageManager.getStockItemByTickerFromStorage(Constants.PORTFOLIO_KEY, queryTicker);
                updatePortfolioText(portfolioStockItem, priceDouble);

                // set dialog listeners
                tradeButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTradeDialog(priceDouble);
                    }
                });
                incrementOrRender();
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

    private void fetchNewsData() {
        DataService.getInstance().fetchNewsData(queryTicker, new GsonCallBack<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) throws Exception {
                 for (Map<String, Object> entry : result) {
                     String articleUrl = (String) entry.get("url");
                     String title = (String) entry.get("title");
                     String imageUrl = (String) entry.get("urlToImage");
                     String publishedAt = (String) entry.get("publishedAt");
                     Object sourceMap = entry.get("source");
                     String sourceName = "";
                     if (sourceMap instanceof Map) {
                         sourceName = ((Map<String, String>) sourceMap).get("name");
                     }

                     @SuppressLint("SimpleDateFormat")
                     SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM dd, yyyy");
                     Date publishedDate = inputFormat.parse(publishedAt);
                     Date todayDate = new Date();
                     long diff = todayDate.getTime() -  publishedDate.getTime();
                     long minute = TimeUnit.MILLISECONDS.toMinutes(diff);
                     long hour   = TimeUnit.MILLISECONDS.toHours(diff);
                     long day  = TimeUnit.MILLISECONDS.toDays(diff);

                     String ago;
                     String suffix = " ago";

                     if (minute < 60) {
                         if (minute == 1) {
                             ago = minute + " minute" + suffix;
                         } else {
                             ago = minute + " minutes" + suffix;
                         }
                     } else if (hour < 24) {
                         if (hour == 1) {
                             ago = hour + " hour" + suffix;
                         } else {
                             ago = hour + " hours" + suffix;
                         }
                     } else {
                         if (day == 1) {
                             ago = day + " day" + suffix;
                         } else {
                             ago = day + " days" + suffix;
                         }
                     }

                     newsList.add(new NewsItem(sourceName, title, ago, articleUrl, imageUrl));
                 }

                initNewsRecyclerView();
                incrementOrRender();
            }

            @Override
            public void onError(String result) throws Exception {
                Log.e(TAG, "onError: Cannot Fetch News data due to " + result);
            }
        });
    }

    private void initNewsRecyclerView() {
        NewsRecyclerViewAdapter adapter = new NewsRecyclerViewAdapter(StockDetailActivity.this, newsList);
        newsRecyclerView.setAdapter(adapter);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(StockDetailActivity.this));
        newsRecyclerView.setNestedScrollingEnabled(false);
        newsRecyclerView.addItemDecoration(new DividerItemDecoration(newsRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initExpandableText() {
        ViewTreeObserver vto = descriptionView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int lineCount = descriptionView.getLineCount();
                if (lineCount > 0) {
                    ViewTreeObserver obs = descriptionView.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                    if (lineCount < 2) {
                        showButton.setVisibility(View.GONE);
                        descriptionView.setGravity(Gravity.CENTER);
                    }
                }
            }
        });
    }

    public void onClickShow(View view) {
        int lineCount = descriptionView.getLineCount();
        if (lineCount == 2) {
            showButton.setText("Show less");
            descriptionView.setEllipsize(null);
            descriptionView.setMaxLines(Integer.MAX_VALUE);
        } else {
            showButton.setText("Show more...");
            descriptionView.setEllipsize(TextUtils.TruncateAt.END);
            descriptionView.setMaxLines(2);
        }
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.favorite_icon).setIcon(this.isInFavorite ?
                R.drawable.ic_baseline_star_24 : R.drawable.ic_baseline_star_border_24);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String prefix = "\"" + queryTicker + "\"";
        if (item.getItemId() == R.id.favorite_icon) {
            if (this.isInFavorite) {
                PreferenceStorageManager.deleteStockItemFromFavorite(stockItem.stockTicker);
                String suffix = " was removed from favorites";
                makeToast(prefix + suffix);
            } else {
                String suffix = " was added to favorites";
                PreferenceStorageManager.addStockItemToFavorite(stockItem);
                makeToast(prefix + suffix);
            }
            this.isInFavorite = !this.isInFavorite;
            invalidateOptionsMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void openTradeDialog(double priceDouble) {
        TradeViewModel model = new TradeViewModel(String.valueOf(priceDouble));

        Dialog dialog = new Dialog(StockDetailActivity.this);
        tradeDialogBinding = TradeDialogBinding.inflate(LayoutInflater.from(StockDetailActivity.this));
        tradeDialogBinding.setViewModel(model);
        dialog.setContentView(tradeDialogBinding.getRoot());

        EditText textInput = dialog.findViewById(R.id.trade_input);
        TextView dialogHeader = dialog.findViewById(R.id.trade_dialog_header);
        TextView tradeMoneyInfo = dialog.findViewById(R.id.trade_money_available);
        Button buyButton = dialog.findViewById(R.id.buy_button);
        Button sellButton = dialog.findViewById(R.id.sell_button);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double sharesToBuy = Double.parseDouble(model.getTradeAmount());
                    double cost = Double.parseDouble(model.getProduct());
                    if (sharesToBuy <= 0) {
                        makeToast(Constants.zeroOrNegativeBuy);
                    } else {
                        double cashLeft = Double.parseDouble(PreferenceStorageManager.getUninventedCash());
                        if (cost > cashLeft) {
                            makeToast(Constants.buyMoreToast);
                        } else {
                            stockItem.updateStockSharesAndInfo(String.valueOf(Double.parseDouble(stockItem.stockShares) + sharesToBuy));
                            PreferenceStorageManager.addOrUpdatePortfolio(stockItem, String.valueOf(cost));
                            updatePortfolioText(stockItem, priceDouble);
                            dialog.dismiss();
                            openTradeCompleteDialog(String.valueOf(sharesToBuy), true);
                        }
                    }
                } catch (NumberFormatException e) {
                    makeToast(Constants.invalidToast);
                }

            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double sharesToSell = Double.parseDouble(model.getTradeAmount());
                    double revenue = Double.parseDouble(model.getProduct());
                    if (sharesToSell <= 0) {
                        makeToast(Constants.zeroOrNegativeSell);
                    } else {
                        double sharesBought = Double.parseDouble(stockItem.stockShares);
                        if (sharesToSell > sharesBought) {
                            makeToast(Constants.sellMoreToast);
                        } else {
                            double sharesLeft = Double.parseDouble(stockItem.stockShares) - sharesToSell;
                            Log.e(TAG, "sell: " + sharesLeft);
                            stockItem.updateStockSharesAndInfo(String.valueOf(sharesLeft));
                            PreferenceStorageManager.deleteOrUpdatePortfolio(stockItem, String.valueOf(revenue));
                            updatePortfolioText(stockItem, priceDouble);
                            dialog.dismiss();
                            openTradeCompleteDialog(String.valueOf(sharesToSell), false);
                        }
                    }
                } catch (NumberFormatException e) {
                    makeToast(Constants.invalidToast);
                }

            }
        });

        String dialogHeaderText = "Trade " + stockItem.stockName + " shares";
        dialogHeader.setText(dialogHeaderText);

        textInput.setMovementMethod(null);
        textInput.setTextIsSelectable(true);

        double roundOff = Math.round(Double.parseDouble(PreferenceStorageManager.getUninventedCash()) * 100.0) / 100.0;
        String moneyLeft = "$" + roundOff;
        String moneyInfoText = moneyLeft + " available to buy " + queryTicker;
        tradeMoneyInfo.setText(moneyInfoText);

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(1400, 1350);
    }

    private void openTradeCompleteDialog(String shares, boolean isBuy) {
        Dialog dialog = new Dialog(StockDetailActivity.this);
        dialog.setContentView(R.layout.trade_complete_dialog);
        TextView completeMessage = dialog.findViewById(R.id.trade_message);
        Button doneButton = dialog.findViewById(R.id.done_button);

        double sharesDouble = Double.parseDouble(shares);
        String action = (isBuy ? "bought " : "sold ") + ((sharesDouble % 1) == 0 ? (int)sharesDouble : shares);
        String message = "You have successfully " + action + " shares of " + queryTicker;
        completeMessage.setText(message);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(1450, 1100);
    }

    private void makeToast(String msg) {
        Toast toast = Toast.makeText(StockDetailActivity.this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updatePortfolioText(StockItem portfolioStock, double priceDouble) {
        String firstLineText;
        String secondLineText;
        if (portfolioStock != null && Double.parseDouble(portfolioStock.stockShares) != 0) {
            firstLineText = "Shares owned: " + portfolioStock.stockShares;
            decimalFormat.setGroupingUsed(false);
            double marketValue = Double.parseDouble(portfolioStock.stockShares) * priceDouble;
            secondLineText = "Market Value: $" + decimalFormat.format(marketValue);
            stockItem.updateStockSharesAndInfo(portfolioStock.stockShares);
        } else {
            firstLineText = "You have 0 shares of " + queryTicker + ".";
            secondLineText = "Start Trading!";
            stockItem.updateStockSharesAndInfo("0");
        }
        portfolioFirstLine.setText(firstLineText);
        portfoliosSecondLine.setText(secondLineText);
    }

    private String round(String num) {
        return String.valueOf(Math.round(Double.parseDouble(num) * 100.0) / 100.0);
    }
}
