package com.example.stocks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockItemViewHolder extends RecyclerView.ViewHolder {
    final View rootView;
    final TextView stockTicker;
    final TextView stockPrice;
    final TextView stockInfo;
    final TextView stockPriceChange;
    final ImageView stockPriceChangeIcon;

    public StockItemViewHolder(@NonNull View itemView) {
        super(itemView);

        rootView = itemView;
        stockTicker = itemView.findViewById(R.id.stock_ticker);
        stockPrice = itemView.findViewById(R.id.stock_price);
        stockInfo = itemView.findViewById(R.id.stock_info);
        stockPriceChange = itemView.findViewById(R.id.price_change);
        stockPriceChangeIcon = itemView.findViewById(R.id.price_change_icon);
    }
}
