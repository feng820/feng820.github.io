package com.example.stocks;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class HomeFragment extends Fragment implements HomeSection.ClickListener{

    private SectionedRecyclerViewAdapter sectionedAdapter;
    private HomeSection portfolioSection;
    private HomeSection favoriteSection;
    private RecyclerView recyclerView;

    private final Handler handler = new Handler();
    private static final String TAG = "HomeFragment";

    private View homeContentView;
    private View homeProgressView;
    private TextView homeFooterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View homeRootView = inflater.inflate(R.layout.fragment_home, container, false);

        sectionedAdapter = new SectionedRecyclerViewAdapter();
        portfolioSection = new HomeSection(getPortfolio(), this, true);
        favoriteSection = new HomeSection(getPortfolio(), this, false);
        recyclerView = homeRootView.findViewById(R.id.recyclerview);

        homeContentView = homeRootView.findViewById(R.id.home_content);
        homeProgressView = homeRootView.findViewById(R.id.progress_content);
        homeFooterView = homeRootView.findViewById(R.id.footer);

        return homeRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeContentView.setVisibility(View.GONE);

        initHomeContentView(recyclerView);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            homeProgressView.setVisibility(View.GONE);
            homeContentView.setVisibility(View.VISIBLE);
        }, 2000);

    }

    @Override
    public void onItemRootViewClicked(@NonNull HomeSection section, int itemAdapterPosition) {

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

    private void initHomeContentView(RecyclerView recyclerView) {
        sectionedAdapter.addSection(portfolioSection);
        sectionedAdapter.addSection(favoriteSection);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
//                Log.e(TAG, "getItemOffsets: " + position);

                // hide the last position of both portfolio and favorites
                // TODO: replace with portfolio size and portfolio size + 1
                if (position == 2 || position == 3 || position == state.getItemCount() - 1) {
                    outRect.setEmpty();
                } else {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        });

        homeFooterView.setOnClickListener(v -> {
            Uri url = Uri.parse("https://www.tiingo.com/");
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
            launchBrowser.setData(url);
            startActivity(launchBrowser);
        });
    }

}