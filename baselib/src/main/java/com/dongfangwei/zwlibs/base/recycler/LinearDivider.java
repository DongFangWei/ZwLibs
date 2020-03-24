package com.dongfangwei.zwlibs.base.recycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LinearDivider extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private static final int DIVIDER_COLOR_DEFAULT = 0xFFE1E2E3;
    //RecyclerView布局的方向
    private int mOrientation;
    private int mDividerSize;
    private Paint mPaint;
    private final Rect mBounds = new Rect();

    /**
     * @see #LinearDivider(int)
     */
    public LinearDivider() {
        this(HORIZONTAL);
    }

    /**
     * @see #LinearDivider(int, int)
     */
    public LinearDivider(int orientation) {
        this(orientation, 1);
    }

    /**
     * @see #LinearDivider(int, int, int)
     */
    public LinearDivider(int orientation, int dividerSize) {
        this(orientation, dividerSize, DIVIDER_COLOR_DEFAULT);
    }

    /**
     * 线性分割线构造函数
     *
     * @param orientation  分割线方向
     * @param dividerSize  分割线尺寸
     * @param dividerColor 分割线颜色
     */
    public LinearDivider(int orientation, int dividerSize, @ColorInt int dividerColor) {
        if (orientation != VERTICAL && orientation != HORIZONTAL) {
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        } else {
            this.mOrientation = orientation;
            this.mDividerSize = dividerSize;
            initPaint(dividerColor);
        }
    }

    private void initPaint(@ColorInt int color) {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView
            parent, @NonNull RecyclerView.State state) {
        if (this.mOrientation == HORIZONTAL) {
            outRect.set(0, 0, 0, this.mDividerSize);
        } else {
            outRect.set(0, 0, this.mDividerSize, 0);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView
            parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() != null) {
            if (this.mOrientation == HORIZONTAL) {
                this.drawHorizontal(c, parent);
            } else {
                this.drawVertical(c, parent);
            }
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount - 1; ++i) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);
            int bottom = this.mBounds.bottom + Math.round(child.getTranslationY());
            int top = bottom - this.mDividerSize;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }

        canvas.restore();
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; ++i) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);
            int right = this.mBounds.right + Math.round(child.getTranslationX());
            int left = right - this.mDividerSize;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }

        canvas.restore();
    }
}
