package com.example.stocks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class HomeFragment extends Fragment implements HomeSection.ClickListener{

    private SectionedRecyclerViewAdapter sectionedAdapter;
    private HomeSection portfolioSection;
    private HomeSection favoriteSection;
    private final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        sectionedAdapter = new SectionedRecyclerViewAdapter();
        portfolioSection = new HomeSection(getPortfolio(), this, true);
        favoriteSection = new HomeSection(getPortfolio(), this, false);

        sectionedAdapter.addSection(portfolioSection);
        sectionedAdapter.addSection(favoriteSection);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        return view;
    }

    private List<StockItem> getPortfolio() {
        final List<StockItem> list = new ArrayList<>();
        list.add(new StockItem("MSFT", "202.68", "8.0 shares",
                "10.57", ResourcesCompat.getColor(getResources(), R.color.red,
                null), R.drawable.ic_baseline_trending_down_24));

        list.add(new StockItem("AAPL", "115.05", "Apple Inc",
                "0.01", ResourcesCompat.getColor(getResources(), R.color.green,
                null), R.drawable.ic_twotone_trending_up_24));


        return list;
    }

    @Override
    public void onItemRootViewClicked(@NonNull HomeSection section, int itemAdapterPosition) {

    }
}