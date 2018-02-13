package com.getprofitam.android.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by welcome on 08-02-2017.
 */

public class SpacesItemDecorationGrid extends RecyclerView.ItemDecoration   {
    /*private final int mSpace;
    public SpacesItemDecorationGrid(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0){
            outRect.top = mSpace;
        } else {
            outRect.top = 0;
        }

    }*/
    private int mItemOffset;

    public SpacesItemDecorationGrid(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public SpacesItemDecorationGrid(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }


}
