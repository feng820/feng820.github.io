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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stocks.adapter.StockSectionedRecyclerViewAdapter;
import com.example.stocks.utils.Constants;
import com.example.stocks.utils.PreferenceStorageManager;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class HomeFragment extends Fragment implements HomeSection.ClickListener {

    private StockSectionedRecyclerViewAdapter sectionedAdapter;
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

        sectionedAdapter = new StockSectionedRecyclerViewAdapter();
//        PreferenceStorageManager.clearAll();
        portfolioSection = new HomeSection(getPortfolio(), this, true);
        favoriteSection = new HomeSection(getFavorites(), this, false);
        recyclerView = homeRootView.findViewById(R.id.recyclerview);

        homeContentView = homeRootView.findViewById(R.id.home_content);
        homeProgressView = homeRootView.findViewById(R.id.progress_content);
//        homeFooterView = homeRootView.findViewById(R.id.footer);

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
    public void onItemRootViewClicked(String sectionKey, StockItem stockItem, int itemAdapterPosition) {
//
//        Toast.makeText(getActivity(), "Item:" + stockItem.stockTicker + "index: " +  itemAdapterPosition,
//                Toast.LENGTH_SHORT).show();
//        Log.e(TAG, "onItemRootViewClicked: clicked");

    }

    private List<StockItem> getPortfolio() {
        return PreferenceStorageManager.getSectionStockList(Constants.PORTFOLIO_KEY);
    }

    private List<StockItem> getFavorites() {
        return PreferenceStorageManager.getSectionStockList(Constants.FAVORITE_KEY);
    }

    private void initHomeContentView(RecyclerView recyclerView) {
        sectionedAdapter.addSection(portfolioSection);
        sectionedAdapter.addSection(favoriteSection);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedAdapter);
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

        recyclerView.setNestedScrollingEnabled(false);

//        homeFooterView.setOnClickListener(v -> {
//            Uri url = Uri.parse("https://www.tiingo.com/");
//            Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
//            launchBrowser.setData(url);
//            startActivity(launchBrowser);
//        });

        enableSwipeToDelete();
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallBack swipeToDeleteCallBack = new SwipeToDeleteCallBack(getContext(), sectionedAdapter) {
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
                final StockItemViewHolder stockItemViewHolder = (StockItemViewHolder) viewHolder;
                String sectionKey = stockItemViewHolder.sectionKey;

                if (sectionKey.equals(Constants.FAVORITE_KEY)) {
                    int position = favoriteSection.findIndexOfStockByTicker(stockItemViewHolder.stockTickerView.getText().toString());
                    SectionAdapter sectionAdapter = sectionedAdapter.getAdapterForSection(favoriteSection);
                    favoriteSection.deleteStockItemByIndex(position);
                    sectionAdapter.notifyItemRemoved(position);
                    sectionAdapter.notifyItemRangeChanged(position, favoriteSection.getContentItemsTotal());
                }
            }
        };
        ItemTouchHelper itemSwipeHelper = new ItemTouchHelper(swipeToDeleteCallBack);
        itemSwipeHelper.attachToRecyclerView(recyclerView);
    }
}
