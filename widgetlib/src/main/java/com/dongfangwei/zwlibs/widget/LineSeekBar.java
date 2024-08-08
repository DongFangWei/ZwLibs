package com.dongfangwei.zwlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 线性拖动条
 *
 * @author zhangwei
 * @date 2024/8/8 11:21
 */
public class LineSeekBar extends LineProgressBar {
    /**
     * 拖动滑块
     */
    private Drawable mThumb;
    /**
     * 拖动滑块的大小
     */
    private float mThumbSize;
    /**
     * 拖动滑块的触摸半径
     */
    private float mThumbTouchRadius;
    /**
     * 拖动条改变监听
     */
    private OnSeekBarChangeListener onSeekBarChangeListener;
    /**
     * 是否启用拖动
     */
    private boolean enabledSeek = true;
    /**
     * 滑块中心点
     */
    private final PointF mThumbCentre = new PointF();

    public LineSeekBar(Context context) {
        this(context, null);
    }

    public LineSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineSeekBar, defStyleAttr, 0);
        Drawable thumb = a.getDrawable(R.styleable.LineSeekBar_thumb);
        final float thumbSize = a.getDimension(R.styleable.LineSeekBar_thumbSize, -1);
        a.recycle();
        if (thumb == null) {
            thumb = ContextCompat.getDrawable(getContext(), R.drawable.arc_seek_bar_default_thumb);
        }
        setThumb(thumb);
        setThumbSize(thumbSize);
    }


    public boolean isEnabledSeek() {
        return enabledSeek;
    }

    public void setEnabledSeek(boolean enableSeek) {
        this.enabledSeek = enableSeek;
    }

    public void setThumb(Drawable thumb) {
        this.mThumb = thumb;
        initThumb();
    }

    public float getThumbSize() {
        return mThumbSize;
    }

    public void setThumbSize(float thumbSize) {
        this.mThumbSize = thumbSize;
        initThumb();
    }

    private void initThumb() {
        if (mThumb != null) {
            int halfWidth;
            int halfHeight;
            if (mThumbSize > 0) {
                halfWidth = (int) mThumbSize >> 1;
                halfHeight = mThumb.getIntrinsicWidth() == mThumb.getIntrinsicHeight() ? halfWidth :
                        (int) (mThumbSize * mThumb.getIntrinsicHeight() / mThumb.getIntrinsicWidth()) >> 1;
            } else {
                halfWidth = mThumb.getIntrinsicWidth() >> 1;
                halfHeight = mThumb.getIntrinsicHeight() >> 1;
            }
            mThumbTouchRadius = Math.min(halfWidth, halfHeight);
            this.mThumb.setBounds(-halfWidth, -halfHeight, halfWidth, halfHeight);
        }
    }

    /**
     * 刷新滑块的中心位置
     */
    private void refreshThumbCentre() {
        if (mThumbCentre != null) {
            mThumbCentre.x = progressRight();
            mThumbCentre.y = mProgressRectF.top + mProgressRectF.height() / 2;
        }
    }

    /**
     * 设置进度条的进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress, boolean b) {
        setProgressInternal(progress, b);
    }

    @Override
    protected void onLayoutProgress(int left, int top, int right, int bottom) {
        float paddingLeft = getPaddingLeft() + mThumbTouchRadius;
        float paddingTop = getPaddingTop() + mThumbTouchRadius;
        float paddingRight = getPaddingRight() + mThumbTouchRadius;
        float paddingBottom = getPaddingBottom() + mThumbTouchRadius;
//        int width = right - left - paddingLeft - paddingRight;
        float height = bottom - top - paddingTop - paddingBottom;

        mProgressRectF.left = paddingLeft;
        mProgressRectF.right = right - left - paddingRight;
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
        refreshThumbCentre();
    }

    @Override
    public void onRefreshProgress(int progress, boolean fromUser) {
        super.onRefreshProgress(progress, fromUser);
        refreshThumbCentre();
        System.out.println(progress);
        if (onSeekBarChangeListener != null)
            onSeekBarChangeListener.onProgressChanged(this, progress, fromUser);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mThumb != null) drawThumb(canvas);
    }

    protected void drawThumb(Canvas canvas) {
        final int saveCount = canvas.save();
        //绘制滑块
        canvas.translate(mThumbCentre.x, mThumbCentre.y);
        mThumb.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enabledSeek && isEnabled()) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isTouchThumb(x, y)) {
                        setPressed(true);
                        if (onSeekBarChangeListener != null)
                            onSeekBarChangeListener.onStartTrackingTouch(this);
                        return true;
                    } else if (isTouchBar(x, y)) {
                        int progress = pointToProgress(x, y);
                        if (setProgressInternal(progress, true)) {
                            setPressed(true);
                            if (onSeekBarChangeListener != null)
                                onSeekBarChangeListener.onStartTrackingTouch(this);
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isPressed()) {
                        int progress = pointToProgress(x, y);
                        setProgressInternal(progress, true);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (onSeekBarChangeListener != null)
                        onSeekBarChangeListener.onStopTrackingTouch(this);

                    if (isPressed()) {
                        setPressed(false);
                        return true;
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断是否触摸Thumb
     *
     * @param x 触摸的x坐标
     * @param y 触摸的y坐标
     * @return 触摸的是否是滑块
     */
    private boolean isTouchThumb(float x, float y) {
        if (mThumb == null)
            return false;
        else {
            return mThumbTouchRadius > getPointToCentre(x, y, mThumbCentre) + 1;
        }
    }

    /**
     * 按下时判断按下的点是否按在进度条范围内
     *
     * @param x 触摸的x坐标
     * @param y 触摸的y坐标
     */
    private boolean isTouchBar(float x, float y) {
        return x >= mProgressRectF.left && x <= mProgressRectF.right && y >= mProgressRectF.top && y <= mProgressRectF.bottom;
    }

    private int pointToProgress(float x, float y) {
//        mProgressRectF.left + mProgressRectF.width() * (mProgress - mMin) / (mMax - mMin)=x
        return Math.round((x - mProgressRectF.left) * (mMax - mMin) / mProgressRectF.width()) + mMin;
    }

    /**
     * 计算某点到圆心的距离
     *
     * @param x x坐标点
     * @param y y坐标点
     */
    private double getPointToCentre(float x, float y, PointF centre) {
        float cx = x - centre.x;
        float cy = y - centre.y;
        return Math.hypot(cx, cy);
    }

    public OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return onSeekBarChangeListener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener {

        /**
         * 通知进度级别已更改。客户端可以使用from user参数将用户发起的更改与以编程方式发生的更改区分开来。
         *
         * @param seekBar  进度改变的CircleSeekBar
         * @param progress 当前的进度，范围0到{@link LineProgressBar#setMax(int)}。(默认值为0到100)
         * @param fromUser 如果进度改变是由用户触发的则为true
         */
        void onProgressChanged(LineSeekBar seekBar, int progress, boolean fromUser);

        /**
         * 通知用户已开始触摸手势。客户端可能希望使用此选项来禁用拖动。
         *
         * @param seekBar 开始触摸手势的LineProgressBar
         */
        void onStartTrackingTouch(LineSeekBar seekBar);

        /**
         * 用户已完成触摸手势的通知。客户可能希望使用此功能重新启用搜索栏。
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The LineProgressBar in which the touch gesture began
         */
        void onStopTrackingTouch(LineSeekBar seekBar);
    }
}
