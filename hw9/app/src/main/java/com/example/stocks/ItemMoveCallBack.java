//package com.example.stocks;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.stocks.utils.PreferenceStorageManager;
//
//import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
//
//public class ItemMoveCallBack extends ItemTouchHelper.Callback {
//    interface ItemTouchHelperContract {
//        void onRowMoved(int fromPosition, int toPosition, SectionAdapter sectionAdapter);
//        void onRowSelected(StockItemViewHolder myViewHolder);
//        void onRowClear(StockItemViewHolder myViewHolder);
//    }
//
//    private final HomeSection section;
//    private final SectionAdapter sectionAdapter;
//
//    public ItemMoveCallBack(HomeSection section, SectionAdapter sectionAdapter) {
//        this.section = section;
//        this.sectionAdapter = sectionAdapter;
//    }
//
//
//    @Override
//    public boolean isLongPressDragEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean isItemViewSwipeEnabled() { return false; }
//
//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }
//
//    @Override
//    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        return makeMovementFlags(dragFlags, 0);
//    }
//
//    //  This is used for drag and drop. If not needed, return false here.
//    @Override
//    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//        StockItemViewHolder fromViewHolder = (StockItemViewHolder) viewHolder;
//        StockItemViewHolder targetViewHolder = (StockItemViewHolder) target;
//        int fromPosition = section.findIndexOfStockByTicker(fromViewHolder.stockTickerView.getText().toString());
//        int toPosition = section.findIndexOfStockByTicker(targetViewHolder.stockTickerView.getText().toString());
//        section.onRowMoved(fromPosition, toPosition, sectionAdapter);
//        return true;
//    }
//
//    // Based on the current state of the RecyclerView and whether it’s pressed or swiped,
//    // this method gets triggered. Here we can customize the RecyclerView row.
//    // For example, changing the background color.
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            if (viewHolder instanceof StockItemViewHolder) {
//                StockItemViewHolder myViewHolder=
//                        (StockItemViewHolder) viewHolder;
//                section.onRowSelected(myViewHolder);
//            }
//
//        }
//        super.onSelectedChanged(viewHolder, actionState);
//    }
//
//    // This method gets triggered when the user interaction stops with the RecyclerView row.
//    @Override
//    public void clearView(@NonNull RecyclerView recyclerView,
//                          @NonNull RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//
//        if (viewHolder instanceof StockItemViewHolder) {
//            StockItemViewHolder myViewHolder=
//                    (StockItemViewHolder) viewHolder;
//            section.onRowClear(myViewHolder);
//        }
//    }
//
//}
