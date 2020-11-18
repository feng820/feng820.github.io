package com.example.stocks.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocks.R;

public class StockItemViewHolder extends RecyclerView.ViewHolder {
    public final View rootView;
    public final TextView stockTickerView;
    public final TextView stockPriceView;
    public final TextView stockInfoView;
    public final TextView stockPriceChangeView;
    public final ImageView stockPriceChangeIconView;
    public final ImageView arrowRightImage;
    public final String sectionKey;

    public StockItemViewHolder(@NonNull View itemView, String sectionKey) {
        super(itemView);

        rootView = itemView;
        stockTickerView = itemView.findViewById(R.id.stock_ticker);
        stockPriceView = itemView.findViewById(R.id.stock_price);
        stockInfoView = itemView.findViewById(R.id.stock_info);
        stockPriceChangeView = itemView.findViewById(R.id.price_change);
        stockPriceChangeIconView = itemView.findViewById(R.id.price_change_icon);
        arrowRightImage = itemView.findViewById(R.id.arrow_right);
        this.sectionKey = sectionKey;
    }
}
