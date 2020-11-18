package com.example.stocks.adapter;

import android.graphics.Color;

import com.example.stocks.viewholder.HomeSection;
import com.example.stocks.R;
import com.example.stocks.viewholder.StockItemViewHolder;
import com.example.stocks.utils.SwipeAndDragDropCallBack;
import com.example.stocks.utils.StockApplication;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class StockSectionedRecyclerViewAdapter extends SectionedRecyclerViewAdapter
        implements SwipeAndDragDropCallBack.ItemTouchHelperContract {


    @Override
    public void onRowMoved(int fromPosition, int toPosition, HomeSection section) {
        section.onRowMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(StockItemViewHolder stockItemViewHolder) {
        stockItemViewHolder.rootView.setBackgroundColor(Color.GRAY);
        stockItemViewHolder.stockTickerView.setBackgroundColor(Color.GRAY);
        stockItemViewHolder.stockPriceView.setBackgroundColor(Color.GRAY);
        stockItemViewHolder.stockInfoView.setBackgroundColor(Color.GRAY);
        stockItemViewHolder.stockPriceChangeView.setBackgroundColor(Color.GRAY);
        stockItemViewHolder.stockPriceChangeIconView.setBackgroundColor(Color.GRAY);
        stockItemViewHolder.arrowRightImage.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(StockItemViewHolder stockItemViewHolder) {
        stockItemViewHolder.rootView.setBackgroundColor(StockApplication.getContext().getColor(R.color.background_color));
        stockItemViewHolder.stockTickerView.setBackgroundColor(StockApplication.getContext().getColor(R.color.background_color));
        stockItemViewHolder.stockPriceView.setBackgroundColor(StockApplication.getContext().getColor(R.color.background_color));
        stockItemViewHolder.stockInfoView.setBackgroundColor(StockApplication.getContext().getColor(R.color.background_color));
        stockItemViewHolder.stockPriceChangeView.setBackgroundColor(StockApplication.getContext().getColor(R.color.background_color));
        stockItemViewHolder.stockPriceChangeIconView.setBackgroundColor(StockApplication.getContext().getColor(R.color.background_color));
        stockItemViewHolder.arrowRightImage.setBackgroundColor(StockApplication.getContext().getColor(R.color.background_color));
    }
}
