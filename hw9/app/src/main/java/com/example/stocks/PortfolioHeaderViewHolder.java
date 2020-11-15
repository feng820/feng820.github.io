package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortfolioHeaderViewHolder extends RecyclerView.ViewHolder {

    final TextView netWorth;
    public PortfolioHeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        netWorth = itemView.findViewById(R.id.net_worth_value);
    }
}
