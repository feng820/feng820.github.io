package com.example.stocks;

import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;

public class HomeSection extends Section {
    private final List<StockItem> stockList;
    private final ClickListener clickListener;
    private final boolean isPortfolio;

    HomeSection(List<StockItem> stockList, ClickListener clickListener, boolean isPortfolio) {
        super(SectionParameters.builder()
                .headerResourceId(isPortfolio ? R.layout.home_portfolio_header : R.layout.home_favorite_header)
                .itemResourceId(R.layout.home_stock_item)
                .build());

        setHasHeader(true);

        this.stockList = stockList;
        this.clickListener = clickListener;
        this.isPortfolio = isPortfolio;
    }

    @Override
    public int getContentItemsTotal() {
        return stockList.size();
    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        if (this.isPortfolio) {
            return new PortfolioHeaderViewHolder(view);
        } else {
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, List<Object> payloads) {
        if (this.isPortfolio) {
            final PortfolioHeaderViewHolder headerHolder = (PortfolioHeaderViewHolder) holder;
            headerHolder.netWorth.setText((String) payloads.get(payloads.size() - 1));
        }
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new StockItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final StockItemViewHolder stockItemViewHolder = (StockItemViewHolder) holder;
        final StockItem stockItem = stockList.get(position);

        stockItemViewHolder.stockTicker.setText(stockItem.stockTicker);
        stockItemViewHolder.stockInfo.setText(stockItem.stockInfo);
        stockItemViewHolder.stockPrice.setText(stockItem.stockPrice);
        stockItemViewHolder.stockPriceChange.setText(stockItem.stockPriceChange);
        stockItemViewHolder.stockPriceChange.setTextColor(stockItem.stockChangeColor);
        stockItemViewHolder.stockPriceChangeIcon.setImageResource(stockItem.stockPriceChangeIcon);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position,
                                     final List<Object> payloads) {
        final StockItemViewHolder stockItemViewHolder = (StockItemViewHolder) holder;
        final StockItem stockItem = stockList.get(position);

        for (Object obj : payloads) {
            if (obj instanceof StockItemUpdate) {
                stockItemViewHolder.stockInfo.setText(stockItem.stockInfo);
                stockItemViewHolder.stockPrice.setText(stockItem.stockPrice);
                stockItemViewHolder.stockPriceChange.setText(stockItem.stockPriceChange);
                stockItemViewHolder.stockPriceChange.setTextColor(stockItem.stockChangeColor);
                stockItemViewHolder.stockPriceChangeIcon.setImageResource(stockItem.stockPriceChangeIcon);
            }
        }

        stockItemViewHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(this, stockItemViewHolder.getAdapterPosition())
        );
    }

    void updateStockItem(final int index, final String stockInfo, final String stockPrice,
                         final String stockPriceChange, final @ColorInt int stockChangeColor,
                         @DrawableRes final int stockPriceChangeIcon) {
        StockItem stockItem = stockList.get(index);

        stockItem.stockInfo = stockInfo;
        stockItem.stockPrice = stockPrice;
        stockItem.stockPriceChange = stockPriceChange;
        stockItem.stockChangeColor = stockChangeColor;
        stockItem.stockPriceChangeIcon = stockPriceChangeIcon;
    }

    static class StockItemUpdate { }

    interface ClickListener {
        void onItemRootViewClicked(@NonNull final HomeSection section, final int itemAdapterPosition);
    }
}
