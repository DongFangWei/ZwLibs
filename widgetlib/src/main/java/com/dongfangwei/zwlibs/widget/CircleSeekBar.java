package com.dongfangwei.zwlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CircleSeekBar extends CircleProgressBar {
    private Drawable mThumb;
    //拖动滑块的大小
    private float mThumbSize;
    //拖动滑块的触摸半径
    private float mThumbTouchRadius;
    //拖动条改变监听
    private OnSeekBarChangeListener onSeekBarChangeListener;

    //旧的角度，用于判断滑动是增加进度还是减少进度
    private float mOldAngle;

    private boolean mEnabledSeek = true;


    public CircleSeekBar(Context context) {
        this(context, null);
    }

    public CircleSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleSeekBar, defStyleAttr, 0);
        Drawable thumb = a.getDrawable(R.styleable.CircleSeekBar_thumb);
        final float thumbSize = a.getDimension(R.styleable.CircleSeekBar_thumbSize, -1);
        a.recycle();
        if (thumb == null) {
            thumb = ContextCompat.getDrawable(getContext(), R.drawable.arc_seek_bar_default_thumb);
        }
        setThumb(thumb);
        setThumbSize(thumbSize);

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

    public boolean isEnabledSeek() {
        return mEnabledSeek;
    }

    public void setEnabledSeek(boolean enabledSeek) {
        this.mEnabledSeek = enabledSeek;
    }

    @Override
    public void onRefreshProgress(int progress, boolean fromUser) {
        super.onRefreshProgress(progress, fromUser);
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
        float angle = mProgressSweepAngle;
        canvas.translate(mCentre.x, mCentre.y);
        canvas.rotate(mStartAngle - 90 + angle);
//        //绘制滑块
        canvas.translate(0, mRadius);
        mThumb.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabledSeek && isEnabled()) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isTouchThumb(x, y)) {
                        setPressed(true);
                        if (onSeekBarChangeListener != null)
                            onSeekBarChangeListener.onStartTrackingTouch(this);
                        return true;
                    } else if (isTouchArc(x, y, 24, 24)) {
                        int progress = angleToProgress(computeTouchAngle(x, y));
                        if (progress >= getMin() && progress <= getMax()) {
                            setProgress(progress);
                            setPressed(true);
                            mOldAngle = mStartAngle + mMaxSweepAngle / 2;
                            if (onSeekBarChangeListener != null)
                                onSeekBarChangeListener.onStartTrackingTouch(this);
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isTouchArc(x, y, (int) mRadius >> 1, 64)) {
                        onTouchMove(computeTouchAngle(x, y));
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

    private void onTouchMove(float angle) {
        float oldAngle = mOldAngle;
        float currentAngle = mProgressSweepAngle;
        float newAngle = angle >= mStartAngle ? angle - mStartAngle : angle - mStartAngle + 360;

        if (getProgressType() == TYPE_CIRCLE) {
            if (currentAngle > oldAngle) {//增加
                if (currentAngle >= mMaxSweepAngle * 2 / 3 && currentAngle <= mMaxSweepAngle
                        && newAngle >= 0 && newAngle <= mMaxSweepAngle / 3) {
                    angle = mStartAngle + mMaxSweepAngle;
                }
            } else {//减少
                if (newAngle >= mMaxSweepAngle * 2 / 3 && newAngle <= 360f
                        && currentAngle >= 0 && currentAngle <= mMaxSweepAngle / 3) {
                    angle = mStartAngle;
                }
            }
        } else {
            if (newAngle > mMaxSweepAngle + 4 && newAngle < 356) {
                return;
            }
            if (currentAngle > oldAngle) {//增加
                if (currentAngle >= mMaxSweepAngle * 2 / 3 && currentAngle <= mMaxSweepAngle &&
                        ((newAngle >= 0 && newAngle <= mMaxSweepAngle / 3) ||
                                (newAngle > mMaxSweepAngle && newAngle <= 360))) {
                    angle = mStartAngle + mMaxSweepAngle;
                }
            } else {//减少
                if (newAngle >= mMaxSweepAngle * 2 / 3 && newAngle <= 360
                        && currentAngle >= 0 && currentAngle <= mMaxSweepAngle / 3) {
                    angle = mStartAngle;
                }
            }
        }
        if (setProgressInternal(angleToProgress(angle), true)) {
            mOldAngle = currentAngle;
        }
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
            PointF pointF = getThumbCenterPoint();
            return mThumbTouchRadius > getPointToCentre(x, y, pointF) + 1;
        }
    }

    /**
     * 按下时判断按下的点是否按在圆弧一定范围内
     *
     * @param x           触摸的x坐标
     * @param y           触摸的y坐标
     * @param innerOffset 向内偏移量
     * @param outerOffset 向外偏移量
     */
    private boolean isTouchArc(float x, float y, int innerOffset, int outerOffset) {
        double d = getPointToCentre(x, y, mCentre);
        return d >= mRadius - getProgressWidth() - innerOffset && d <= mRadius + outerOffset;
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

    /**
     * 根据触摸点的位置获取其在圆上的角度
     *
     * @param x 点的横坐标
     * @param y 点的纵坐标
     */
    private float computeTouchAngle(float x, float y) {
        float pointX = x - mCentre.x;
        float pointY = y - mCentre.y;
        double atan;//所在象限弧边angle
        float angle;
        if (pointX >= 0) {
            if (pointY <= 0) {//第一象限-右上角区域
                atan = Math.atan(pointX / -pointY);//求弧边
                angle = (float) Math.toDegrees(atan) + 270f;
            } else {//第四象限-右下角区域
                atan = Math.atan(pointY / pointX);//求弧边
                angle = (int) Math.toDegrees(atan);
            }
        } else {
            if (pointY <= 0) {//第二象限-左上角区域
                atan = Math.atan(-pointY / -pointX);//求弧边
                angle = (float) Math.toDegrees(atan) + 180f;
            } else {//第三象限-左下角区域
                atan = Math.atan(-pointX / pointY);//求弧边
                angle = (int) Math.toDegrees(atan) + 90f;
            }
        }
        return angle;
    }

    /**
     * 将圆上的角度转为进度
     *
     * @param angle 圆上的角度
     * @return 进度
     */
    private int angleToProgress(float angle) {
        if (angle >= mStartAngle) {
            angle -= mStartAngle;
        } else {
            angle += 360 - mStartAngle;
        }
        return Math.round(angle / mMaxSweepAngle * getMax());
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        boolean changed = false;
        final Drawable thumb = mThumb;
        if (thumb != null && thumb.isStateful()) {
            changed = thumb.setState(getDrawableState());
        }
        if (changed) {
            invalidate();
        }
    }

    /**
     * 获取滑块的圆心点
     */
    private PointF getThumbCenterPoint() {
        float angle = (float) ((mStartAngle + mProgressSweepAngle) * Math.PI / 180);
        PointF pointF = new PointF();
        pointF.x = (float) (mCentre.x + Math.cos(angle) * mRadius);
        pointF.y = (float) (mCentre.y + Math.sin(angle) * mRadius);
        return pointF;
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
         * @param progress 当前的进度，范围0到{@link CircleProgressBar#setMax(int)}。(默认值为0到100)
         * @param fromUser 如果进度改变是由用户触发的则为true
         */
        void onProgressChanged(CircleSeekBar seekBar, int progress, boolean fromUser);

        /**
         * 通知用户已开始触摸手势。客户端可能希望使用此选项来禁用拖动。
         *
         * @param seekBar 开始触摸手势的CircleSeekBar
         */
        void onStartTrackingTouch(CircleSeekBar seekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The CircleSeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(CircleSeekBar seekBar);
    }
}
