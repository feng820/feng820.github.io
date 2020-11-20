package com.example.stocks.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stocks.R;
import com.example.stocks.activity.MainActivity;
import com.example.stocks.activity.StockDetailActivity;
import com.example.stocks.adapter.StockSectionedRecyclerViewAdapter;
import com.example.stocks.network.DataService;
import com.example.stocks.network.GsonCallBack;
import com.example.stocks.utils.Constants;
import com.example.stocks.utils.PreferenceStorageManager;
import com.example.stocks.utils.StockItem;
import com.example.stocks.utils.SwipeAndDragDropCallBack;
import com.example.stocks.viewholder.HomeSection;
import com.example.stocks.viewholder.StockItemViewHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;


public class HomeFragment extends Fragment implements HomeSection.ClickListener {

    private StockSectionedRecyclerViewAdapter sectionedAdapter;
    private HomeSection portfolioSection;
    private HomeSection favoriteSection;
    private RecyclerView recyclerView;

    private final Handler handler = new Handler();
    private static final String TAG = "HomeFragment";

    private View homeRootView;
    private View homeContentView;
    private View homeProgressView;
    private TextView homeFooterView;
    private TextView dateView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeRootView = inflater.inflate(R.layout.fragment_home, container, false);
        return homeRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sectionedAdapter = new StockSectionedRecyclerViewAdapter();
        recyclerView = homeRootView.findViewById(R.id.recyclerview);
        homeContentView = homeRootView.findViewById(R.id.home_content);
        homeProgressView = homeRootView.findViewById(R.id.progress_content);
        homeFooterView = homeRootView.findViewById(R.id.footer);
        dateView = homeRootView.findViewById(R.id.date);

        homeContentView.setVisibility(View.GONE);

        initHomeRecyclerView(recyclerView);
    }

    @Override
    public void onItemArrowClicked(String ticker) {
        Intent intent = new Intent(getContext(), StockDetailActivity.class);
        intent.putExtra("ticker", ticker);
        getContext().startActivity(intent);
    }

    private void initHomeRecyclerView(RecyclerView recyclerView) {
        homeFooterView.setOnClickListener(v -> {
            Uri url = Uri.parse("https://www.tiingo.com/");
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
            launchBrowser.setData(url);
            startActivity(launchBrowser);
        });

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
//                int position = parent.getChildAdapterPosition(view);
//                if (position == state.getItemCount() - 1) {
//                    outRect.setEmpty();
//                } else {
//                    super.getItemOffsets(outRect, view, parent, state);
//                }
            }
        });

        enableSwipeAndDragDrop();
        updateCurrentDate();
        fetchLatestData();
    }

    private void fetchLatestData() {
        List<StockItem> portfolioList = PreferenceStorageManager.getSectionStockList(Constants.PORTFOLIO_KEY);
        List<StockItem> favoriteList = PreferenceStorageManager.getSectionStockList(Constants.FAVORITE_KEY);

        Set<StockItem> allUniqueStockItems = new HashSet<>();
        allUniqueStockItems.addAll(portfolioList);
        allUniqueStockItems.addAll(favoriteList);

        List<String> tickers = new ArrayList<>();
        for (StockItem item : allUniqueStockItems) {
            tickers.add(item.stockTicker);
        }

        if (tickers.size() == 0) {
            portfolioSection = new HomeSection(portfolioList, HomeFragment.this, true);
            favoriteSection = new HomeSection(favoriteList, HomeFragment.this, false);
            sectionedAdapter.addSection(portfolioSection);
            sectionedAdapter.addSection(favoriteSection);
            recyclerView.setAdapter(sectionedAdapter);

            homeProgressView.setVisibility(View.GONE);
            homeContentView.setVisibility(View.VISIBLE);
            return;
        }

        DataService.getInstance().fetchLatestHomeData(tickers, new GsonCallBack<List<Map<String, String>>>() {
            @Override
            public void onSuccess(List<Map<String, String>> result) throws Exception {
                Map<String, Map<String, String>> latestMap = new HashMap<>();
                for (Map<String, String> entry : result) {
                    latestMap.put(entry.get("ticker"), entry);
                }

                updateSectionList(Constants.PORTFOLIO_KEY, portfolioList, latestMap);
                updateSectionList(Constants.FAVORITE_KEY, favoriteList, latestMap);

                portfolioSection = new HomeSection(portfolioList, HomeFragment.this, true);
                favoriteSection = new HomeSection(favoriteList, HomeFragment.this, false);
                sectionedAdapter.addSection(portfolioSection);
                sectionedAdapter.addSection(favoriteSection);
                recyclerView.setAdapter(sectionedAdapter);

                homeProgressView.setVisibility(View.GONE);
                homeContentView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String result) throws Exception {
                Log.e(TAG, "onError: Cannot Fetch Latest Data due to " + result);
            }
        });
    }

    private void updateSectionList(String key, List<StockItem> sectionList, Map<String, Map<String, String>> latestMap) {
        for (StockItem item : sectionList) {
            Map<String, String> obj = latestMap.get(item.stockTicker);
            double change = Double.parseDouble(String.valueOf(obj.get("change")));

            item.stockPrice = String.valueOf(obj.get("price"));
            item.stockPriceChange = String.valueOf(Math.abs(change));

            if (change > 0) {
                item.stockChangeColor = getContext().getColor(R.color.green);
                item.stockPriceChangeIcon = R.drawable.ic_twotone_trending_up_24;
            } else if (change < 0) {
                item.stockChangeColor = getContext().getColor(R.color.red);
                item.stockPriceChangeIcon = R.drawable.ic_baseline_trending_down_24;
            } else {
                item.stockChangeColor = getContext().getColor(R.color.grey);
            }
        }

        PreferenceStorageManager.updateStorage(key, sectionList);
    }

    private void enableSwipeAndDragDrop() {
        SwipeAndDragDropCallBack swipeAndDragDropCallBack = new SwipeAndDragDropCallBack(getContext(), sectionedAdapter) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
                if (!(source instanceof StockItemViewHolder) || !(target instanceof StockItemViewHolder)) {
                    return false;
                }
                StockItemViewHolder sourceViewHolder = (StockItemViewHolder) source;
                StockItemViewHolder targetViewHolder = (StockItemViewHolder) target;
                if (sourceViewHolder.sectionKey.equals(targetViewHolder.sectionKey)) {
                    HomeSection currentSection =
                            sourceViewHolder.sectionKey.equals(Constants.PORTFOLIO_KEY) ?
                            portfolioSection : favoriteSection;
                    int fromPosition = currentSection.findIndexOfStockByTicker(sourceViewHolder.stockTickerView.getText().toString());
                    int toPosition = currentSection.findIndexOfStockByTicker(targetViewHolder.stockTickerView.getText().toString());

                    SectionAdapter sectionAdapter = sectionedAdapter.getAdapterForSection(currentSection);
                    sectionedAdapter.onRowMoved(fromPosition, toPosition, currentSection);
                    sectionAdapter.notifyItemMoved(fromPosition, toPosition);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.e(TAG, "onSwiped: trigger on swipe" );
                final StockItemViewHolder stockItemViewHolder = (StockItemViewHolder) viewHolder;
                String sectionKey = stockItemViewHolder.sectionKey;

                if (sectionKey.equals(Constants.FAVORITE_KEY)) {
                    int position = favoriteSection.findIndexOfStockByTicker(stockItemViewHolder.stockTickerView.getText().toString());
                    SectionAdapter sectionAdapter = sectionedAdapter.getAdapterForSection(favoriteSection);
                    favoriteSection.deleteStockItemByIndex(position);
                    sectionAdapter.notifyItemRemoved(position);
                    sectionAdapter.notifyItemRangeChanged(position, favoriteSection.getContentItemsTotal() - position);
                }
            }
        };
        ItemTouchHelper itemSwipeHelper = new ItemTouchHelper(swipeAndDragDropCallBack);
        itemSwipeHelper.attachToRecyclerView(recyclerView);
    }

    private void updateCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = dtf.format(now);
        dateView.setText(formattedDate);
    }
}
