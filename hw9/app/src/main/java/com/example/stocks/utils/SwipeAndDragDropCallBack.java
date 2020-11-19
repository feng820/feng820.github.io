package com.example.stocks.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocks.viewholder.HomeSection;
import com.example.stocks.R;
import com.example.stocks.viewholder.StockItemViewHolder;
import com.example.stocks.adapter.StockSectionedRecyclerViewAdapter;

abstract public class SwipeAndDragDropCallBack extends ItemTouchHelper.Callback {
    public interface ItemTouchHelperContract {
        void onRowMoved(int fromPosition, int toPosition, HomeSection section);
        void onRowSelected(StockItemViewHolder myViewHolder);
        void onRowClear(StockItemViewHolder myViewHolder);
    }

    Context mContext;
    private final Paint mClearPaint;
    private final ColorDrawable mBackground;
    private final int backgroundColor;
    private final Drawable deleteDrawable;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private final StockSectionedRecyclerViewAdapter adapter;
    private static final String TAG = "SwipeAndDragDropCallBac";

    public SwipeAndDragDropCallBack(Context context, StockSectionedRecyclerViewAdapter adapter) {
        mContext = context;
        mBackground = new ColorDrawable();
        backgroundColor = Color.parseColor("#b80f0a");
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_delete_24);
        if (deleteDrawable != null) {
            intrinsicWidth = deleteDrawable.getIntrinsicWidth();
            intrinsicHeight = deleteDrawable.getIntrinsicHeight();
        }
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() { return true; }

    // Based on the current state of the RecyclerView and whether it’s pressed or swiped,
    // this method gets triggered. Here we can customize the RecyclerView row.
    // For example, changing the background color.
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof StockItemViewHolder) {
                StockItemViewHolder myViewHolder=
                        (StockItemViewHolder) viewHolder;
                adapter.onRowSelected(myViewHolder);
            }

        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    // This method gets triggered when the user interaction stops with the RecyclerView row.
    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof StockItemViewHolder) {
            StockItemViewHolder myViewHolder=
                    (StockItemViewHolder) viewHolder;
            adapter.onRowClear(myViewHolder);
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof StockItemViewHolder) {
            final StockItemViewHolder stockItemViewHolder = (StockItemViewHolder) viewHolder;
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = stockItemViewHolder.sectionKey.equals(Constants.FAVORITE_KEY)
                    ? ItemTouchHelper.LEFT : 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            return makeMovementFlags(0, 0);
        }
    }

    // Here we’ll create our custom view that shows that the swipe is happening.
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;


        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }

    // Here we return the float value. example 0.5f means that a 50 percent swipe on the RecyclerView row would be considered as a swipe.
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }

}
