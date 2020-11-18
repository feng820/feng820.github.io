package com.example.stocks.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocks.R;

public class PortfolioHeaderViewHolder extends RecyclerView.ViewHolder {

    public final TextView netWorth;
    public PortfolioHeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        netWorth = itemView.findViewById(R.id.net_worth_value);
    }
}
