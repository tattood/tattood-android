package com.tattood.tattood;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by eksi on 27/04/17.
 */

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable divider;
    private Context context;

    /**
     * Default divider will be used
     */
    public SimpleDividerItemDecoration(Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
        this.context = context;
    }

    /**
     * Custom divider will be used
     */
    public SimpleDividerItemDecoration(Context context, int resId) {
        divider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //int left = parent.getPaddingLeft();
        //int right = parent.getWidth() - parent.getPaddingRight();
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
        int top = parent.getPaddingTop();
        int bottom = top + px;

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int left = child.getRight() + params.leftMargin;
            int right = left + divider.getIntrinsicWidth();
            //int top = child.getBottom() + params.bottomMargin;
            //int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
