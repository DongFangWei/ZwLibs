package com.dongfangwei.zwlibs.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;


public class LineProgressBar extends View {
    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 进度条的最大进度值
     */
    protected int mMax;
    /**
     * 进度条的最小进度值
     */
    protected int mMin;
    /**
     * 进度条的进度
     */
    protected int mProgress;

    /**
     * 进度区域
     */
    protected RectF mProgressRectF = new RectF();

    /**
     * 进度的圆角半径
     */
    protected float mRadius = -1;
    /**
     * 进度条宽度
     */
    protected float mProgressWidth;
    /**
     * 进度颜色
     */
    @ColorInt
    private int mProgressColor;
    /**
     * 进度背景颜色
     */
    @ColorInt
    private int mProgressBackground;

    /**
     * 对齐方式
     */
    protected int mGravity;

    /**
     * 是否改变布局（用于避免重复变化）
     */
    private boolean isChangeLayout;

    public LineProgressBar(Context context) {
        this(context, null);
    }

    public LineProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineProgressBar, defStyleAttr, 0);
        final int max = a.getInt(R.styleable.LineProgressBar_android_max, 100);
        final int min = a.getInt(R.styleable.LineProgressBar_min, 0);
        final int progress = a.getInt(R.styleable.LineProgressBar_android_progress, 0);
        final float progressWidth = a.getDimension(R.styleable.LineProgressBar_progressWidth, 8);
        final int progressColor = a.getColor(R.styleable.LineProgressBar_progressColor, 0xFF22C434);
        final int progressBackground = a.getColor(R.styleable.LineProgressBar_progressBackground, 0xFFF1F2F1);
        final int gravity = a.getInt(R.styleable.LineProgressBar_android_gravity, Gravity.CENTER_VERTICAL);
        a.recycle();

        initPaint();
        setMax(max);
        setMin(min);
        setProgressWidth(progressWidth);
        setProgressColor(progressColor);
        setProgressBackground(progressBackground);
        setGravity(gravity);
        setProgressInternal(progress, false);
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        //抗锯齿
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        if (this.mMax != max && this.mMin < max) {
            this.mMax = max;

            int progress = Math.min(mProgress, max);
            refreshProgress(progress, false);
        }
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        if (this.mMin != min && min < this.mMax) {
            this.mMin = Math.max(0, min);

            int progress = Math.max(mProgress, mMin);
            refreshProgress(progress, false);
        }
    }

    public int getProgress() {
        return mProgress;
    }

    /**
     * 设置进度条的进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        setProgressInternal(progress, true);
    }

    /**
     * 设置进度条的内部方法
     *
     * @param progress 进度
     * @param fromUser 是否由用户改变
     */
    boolean setProgressInternal(int progress, boolean fromUser) {
        if (progress > mMax) {
            progress = mMax;
        } else if (progress < mMin) {
            progress = mMin;
        }
        if (progress != mProgress) {
            refreshProgress(progress, fromUser);
            return true;
        } else {
            return false;
        }
    }

    public float getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        this.mProgressWidth = progressWidth;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
    }

    public int getProgressBackground() {
        return mProgressBackground;
    }

    public void setProgressBackground(int progressBackground) {
        this.mProgressBackground = progressBackground;
    }

    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
        }
    }

    /**
     * 刷新进度
     *
     * @param progress 进度（已经经过验证与当前进度不同）
     * @param fromUser 是否由用户触发
     */
    void refreshProgress(int progress, boolean fromUser) {
        this.mProgress = progress;
        onRefreshProgress(progress, fromUser);
        invalidate();
    }

    /**
     * 进度刷新。当进度发生刷新时调用
     *
     * @param progress 已经刷新的进度
     * @param fromUser 是否由用户触发
     */
    protected void onRefreshProgress(int progress, boolean fromUser) {

    }

    public Paint getPaint() {
        return mPaint;
    }


    /**
     * 布局改变时的回调，用于重新计算进度条区域
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    protected void onLayoutProgress(int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
//        int width = right - left - paddingLeft - getPaddingRight();
        int height = bottom - top - paddingTop - getPaddingBottom();

        mProgressRectF.left = paddingLeft;
        mProgressRectF.right = right - left - getPaddingRight();
        if (mProgressWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            mProgressRectF.top = paddingTop;
            mProgressRectF.bottom = height;
            if (mRadius == -1) {
                this.mRadius = height / 2f;
            }
        } else {
            if (mGravity == Gravity.CENTER_VERTICAL) {
                mProgressRectF.top = paddingTop + (height - mProgressWidth) / 2;
                mProgressRectF.bottom = mProgressRectF.top + mProgressWidth;
            } else if (mGravity == Gravity.TOP) {
                mProgressRectF.top = paddingTop;
                mProgressRectF.bottom = mProgressWidth;
            } else {
                mProgressRectF.bottom = height;
                mProgressRectF.top = mProgressRectF.bottom - mProgressWidth;
            }
            if (mRadius == -1) {
                this.mRadius = mProgressWidth / 2;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || isChangeLayout) {//当控件布局改变时，重新计算内容位置
            isChangeLayout = false;
            onLayoutProgress(left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgressBackground(canvas);
        drawProgress(canvas);
    }

    /**
     * 绘制进度背景
     *
     * @param canvas 画布
     */
    protected void drawProgressBackground(Canvas canvas) {
        mPaint.setColor(mProgressBackground);
        canvas.drawRoundRect(mProgressRectF, mRadius, mRadius, mPaint);
    }

    /**
     * 绘制进度
     *
     * @param canvas 画布
     */
    protected void drawProgress(Canvas canvas) {
        float progress = mProgress;
        if (progress > 0) {
            mPaint.setColor(mProgressColor);
            //绘制进度
            canvas.drawRoundRect(mProgressRectF.left, mProgressRectF.top, progressRight(), mProgressRectF.bottom, mRadius, mRadius, mPaint);
        }
    }

    /**
     * 获取当前进度的右边界值
     *
     * @return 右边界值
     */
    protected float progressRight() {
        return mProgressRectF.left + mProgressRectF.width() * (mProgress - mMin) / (mMax - mMin);
    }
}
