package com.example.stocks.viewholder;

import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocks.R;
import com.example.stocks.utils.Constants;
import com.example.stocks.utils.PreferenceStorageManager;
import com.example.stocks.utils.StockItem;

import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;

public class HomeSection extends Section {
    public interface ClickListener {
        void onStockItemClicked(String ticker);
    }

    public static class StockItemUpdate { }

    private List<StockItem> stockList;
    private final ClickListener clickListener;
    private final String sectionKey;

    private static final String TAG = "HomeSection";

    public HomeSection(List<StockItem> stockList, ClickListener clickListener, boolean isPortfolio) {
        super(SectionParameters.builder()
                .headerResourceId(isPortfolio ? R.layout.home_portfolio_header : R.layout.home_favorite_header)
                .itemResourceId(R.layout.home_stock_item)
                .build());

        setHasHeader(true);

        this.stockList = stockList;
        this.clickListener = clickListener;
        this.sectionKey = isPortfolio ? Constants.PORTFOLIO_KEY : Constants.FAVORITE_KEY;
    }

    @Override
    public int getContentItemsTotal() {
        return stockList.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        if (sectionKey.equals(Constants.PORTFOLIO_KEY)) {
            return new PortfolioHeaderViewHolder(view);
        } else {
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        if (sectionKey.equals(Constants.PORTFOLIO_KEY)) {
            final PortfolioHeaderViewHolder headerHolder = (PortfolioHeaderViewHolder) holder;
            double roundOff = Math.round(Double.parseDouble(PreferenceStorageManager.getNetWorth()) * 100.0) / 100.0;
            headerHolder.netWorth.setText(String.valueOf(roundOff));
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, List<Object> payloads) {
        if (sectionKey.equals(Constants.PORTFOLIO_KEY)) {
            final PortfolioHeaderViewHolder headerHolder = (PortfolioHeaderViewHolder) holder;
            headerHolder.netWorth.setText((String) payloads.get(payloads.size() - 1));
        }
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new StockItemViewHolder(view, sectionKey);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final StockItemViewHolder stockItemViewHolder = (StockItemViewHolder) holder;
        final StockItem stockItem = stockList.get(position);

        stockItemViewHolder.stockTickerView.setText(stockItem.stockTicker);
        stockItemViewHolder.stockInfoView.setText(stockItem.stockInfo);
        stockItemViewHolder.stockPriceView.setText(stockItem.stockPrice);
        stockItemViewHolder.stockPriceChangeView.setText(stockItem.stockPriceChange);
        stockItemViewHolder.stockPriceChangeView.setTextColor(stockItem.stockChangeColor);
        stockItemViewHolder.stockPriceChangeIconView.setImageResource(stockItem.stockPriceChangeIcon);

        stockItemViewHolder.rootView.setOnClickListener(v ->
                clickListener.onStockItemClicked(stockItem.stockTicker));
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position,
                                     final List<Object> payloads) {
        final StockItemViewHolder stockItemViewHolder = (StockItemViewHolder) holder;
        final StockItem stockItem = stockList.get(position);
        for (Object obj : payloads) {
            if (obj instanceof StockItemUpdate) {
                stockItemViewHolder.stockInfoView.setText(stockItem.stockInfo);
                stockItemViewHolder.stockPriceView.setText(stockItem.stockPrice);
                stockItemViewHolder.stockPriceChangeView.setText(stockItem.stockPriceChange);
                stockItemViewHolder.stockPriceChangeView.setTextColor(stockItem.stockChangeColor);
                stockItemViewHolder.stockPriceChangeIconView.setImageResource(stockItem.stockPriceChangeIcon);
            }
        }

    }

    // gets called when the drag and drop is done.
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(stockList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(stockList, i, i - 1);
            }
        }
        PreferenceStorageManager.updateSectionStockList(sectionKey, stockList);
    }

    public void deleteStockItemByIndex(int position) {
        stockList = PreferenceStorageManager.deleteStockItemFromFavorite(stockList.get(position).stockTicker);
    }

    public void updateStockItem(final int index, final String stockPrice, final String stockPriceChange,
                                final @ColorInt int stockChangeColor,
                                @DrawableRes final int stockPriceChangeIcon) {
        StockItem stockItem = stockList.get(index);

        stockItem.stockPrice = stockPrice;
        stockItem.stockPriceChange = stockPriceChange;
        stockItem.stockChangeColor = stockChangeColor;
        stockItem.stockPriceChangeIcon = stockPriceChangeIcon;
    }

    public int findIndexOfStockByTicker(String ticker) {
        for (int i = 0; i < stockList.size(); i++) {
            if (stockList.get(i).stockTicker.equals(ticker)) {
                return i;
            }
        }
        return -1;
    }

    public void updateStockList(String key) {
        List<StockItem> newList = PreferenceStorageManager.getSectionStockList(key);
        this.stockList.clear();
        this.stockList.addAll(newList);
    }
}
