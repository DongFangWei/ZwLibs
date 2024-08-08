package com.dongfangwei.zwlibs.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;

public class CircleProgressBar extends View {
    /* 进度条类型 */
    /**
     * 进度条类型-圆形
     */
    public final static int TYPE_CIRCLE = 0x00000001;
    /**
     * 进度条类型-弧形
     */
    public final static int TYPE_ARC = 0x00000010;
    /**
     * 默认的初始角度
     */
    protected final static float START_ANGLE = 165f;
    /**
     * 类型是圆形时-圆弧可扫过的最大角度
     */
    protected final static float SWEEP_ANGLE_CIRCLE = 360f;
    /**
     * 类型是弧形时-圆弧可扫过的最大角度
     */
    protected final static float SWEEP_ANGLE_ARC = 210f;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 文字画笔
     */
    private TextPaint mTextPaint;
    /**
     * 文字Layout
     */
    private Layout mLayout;


    /**
     * 进度条的最大进度值
     */
    private int mMax;
    /**
     * 进度条的最小进度值
     */
    private int mMin;
    /**
     * 进度条的进度
     */
    private int mProgress;

    /**
     * 进度圆弧区域
     */
    protected RectF mProgressRectF = new RectF();
    /**
     * 半径
     */
    protected float mRadius;
    /**
     * 圆心
     */
    protected PointF mCentre = new PointF();
    /**
     * 圆弧的起始角度
     */
    protected float mStartAngle;
    /**
     * 进度扫过的最大角度
     */
    protected float mMaxSweepAngle;
    /**
     * 进度扫过的角度
     */
    protected float mProgressSweepAngle;

    /**
     * 进度条宽度
     */
    private float mProgressWidth;
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
     * 进度条的类型
     */
    private int mProgressType;

    /**
     * 用于格式化进度，最多保留两位小数
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    /**
     * 文字颜色
     */
    private ColorStateList mTextColors;
    /**
     * 是否显示文字
     */
    private boolean mShowText;
    /**
     * 是否改变布局（用于避免重复变化）
     */
    private boolean isChangeLayout;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);
        final int max = a.getInt(R.styleable.CircleProgressBar_android_max, 100);
        final int min = a.getInt(R.styleable.CircleProgressBar_min, 0);
        final int progress = a.getInt(R.styleable.CircleProgressBar_android_progress, 0);
        final float progressWidth = a.getDimension(R.styleable.CircleProgressBar_progressWidth, 8);
        final int progressColor = a.getColor(R.styleable.CircleProgressBar_progressColor, 0xFF22C434);
        final int progressBackground = a.getColor(R.styleable.CircleProgressBar_progressBackground, 0xFFF1F2F1);
        final int progressType = a.getInt(R.styleable.CircleProgressBar_progressType, TYPE_CIRCLE);
        final float startAngle = a.getFloat(R.styleable.CircleProgressBar_startAngle, START_ANGLE);
        final float textSize = a.getDimension(R.styleable.CircleProgressBar_android_textSize, 14);
        ColorStateList textColors = a.getColorStateList(R.styleable.CircleProgressBar_android_textColor);
        if (textColors == null) {
            textColors = ContextCompat.getColorStateList(context, R.color.progress_bar_selector_text);
        }
        final boolean showText = a.getBoolean(R.styleable.CircleProgressBar_showText, true);
        a.recycle();

        initPaint();
        setMax(max);
        setMin(min);
        setProgressInternal(progress, false);
        setProgressWidth(progressWidth);
        setProgressColor(progressColor);
        setProgressBackground(progressBackground);
        setProgressType(progressType);
        setStartAngle(startAngle);
        setTextColors(textColors);
        setTextSize(textSize);
        setShowText(showText);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        //抗锯齿
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.FILL);
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
     * @return 进度是否改变
     */
    protected boolean setProgressInternal(int progress, boolean fromUser) {
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

    public int getProgressType() {
        return mProgressType;
    }

    /**
     * 设置进度条的类型
     *
     * @param progressType 进度条类型，包括圆形{@see cTYPE_CIRCLE}与弧形{@see TYPE_ARC}
     */
    public void setProgressType(int progressType) {
        if (this.mProgressType != progressType) {
            refreshProgressType(progressType);
        }
    }

    private void refreshProgressType(int progressType) {
        this.mProgressType = progressType;
        if (this.mProgressType == TYPE_ARC) {
            mMaxSweepAngle = SWEEP_ANGLE_ARC;
            mStartAngle = START_ANGLE;
        } else {
            mMaxSweepAngle = SWEEP_ANGLE_CIRCLE;
        }
        this.isChangeLayout = true;
        refreshProgressAngle();
        requestLayout();
    }

    /**
     * 刷新进度
     *
     * @param progress 进度（已经经过验证与当前进度不同）
     * @param fromUser 是否由用户触发
     */
    void refreshProgress(int progress, boolean fromUser) {
        this.mProgress = progress;
        refreshProgressAngle();
        onRefreshProgress(progress, fromUser);
        assumeLayout();
        invalidate();
    }

    /**
     * 进度刷新。当进度发生刷新时调用
     *
     * @param progress 已经刷新的进度
     * @param fromUser 是否由用户触发
     */
    public void onRefreshProgress(int progress, boolean fromUser) {

    }

    /**
     * 刷新进度扫过的角度
     */
    private void refreshProgressAngle() {
        if (mProgressType == TYPE_ARC) {
            int p = mMax - mMin;
            this.mProgressSweepAngle = p == 0 ? 0 : mMaxSweepAngle * (mProgress - mMin) / p;
        } else {
            this.mProgressSweepAngle = mMax == 0 ? 0 : mMaxSweepAngle * mProgress / mMax;
        }
    }

    private void assumeLayout() {
        int wantWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (wantWidth > 0) {
            CharSequence text = getProgressText();
            Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
            float spacingAdd = 0f, spacingMult = 1f;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                StaticLayout.Builder builder = StaticLayout.Builder.obtain(text,
                        0, text.length(), getTextPaint(), wantWidth)
                        //设置对齐方式
                        .setAlignment(alignment)
                        //设置行距参数。
                        .setLineSpacing(spacingAdd, spacingMult)
                        //设置是否包含字体上升和下降以外的额外空间
                        .setIncludePad(true)
                        //设置文本方向
                        .setTextDirection(android.text.TextDirectionHeuristics.FIRSTSTRONG_LTR)
                        .setMaxLines(4);
                mLayout = builder.build();
            } else {
                mLayout = new StaticLayout(text, getTextPaint(), wantWidth, alignment, spacingMult, spacingAdd, true);
            }
        }
    }

    /**
     * 获取进度文字
     * <p>
     * 本函数将进度格式化为文字
     *
     * @return 进度文字
     */
    protected CharSequence getProgressText() {
        float ratio = mMax == 0 ? 0 : (float) mProgress / mMax * 100;
        return String.format("%s%%", numberFormat(ratio));
    }

    protected String numberFormat(double num) {
        return decimalFormat.format(num);
    }

    public void setTextSize(float textSize) {
        if (textSize != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(textSize);
            assumeLayout();
        }
    }

    public ColorStateList getTextColors() {
        return mTextColors;
    }

    public void setTextColors(ColorStateList textColors) {
        this.mTextColors = textColors;
    }

    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        this.mShowText = showText;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public TextPaint getTextPaint() {
        return mTextPaint;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        if (mProgressType == TYPE_ARC) {
            this.mStartAngle = START_ANGLE;
        } else if (this.mStartAngle != startAngle) {
            this.mStartAngle = startAngle;
        }
    }

    protected void onLayoutProgress(int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int width = right - left - paddingLeft - getPaddingRight();
        int height = bottom - top - paddingTop - getPaddingBottom();
        float size;
        if (mProgressType == TYPE_ARC) {
            //因为圆弧的实际范围是一个长方形，所以给高度*1.2再判断
            if (width > height * 1.2f) {
                size = height * 1.2f - mProgressWidth;
            } else {
                size = width - mProgressWidth;
            }
            float arcHeight = size * 7 / 12;
            mProgressRectF.left = (width - size) / 2 + paddingLeft;
            mProgressRectF.top = (height - arcHeight) / 2 + paddingTop;
            mProgressRectF.right = mProgressRectF.left + size;
            mProgressRectF.bottom = mProgressRectF.top + size;
            mRadius = size / 2;
            mCentre.set(mProgressRectF.left + mRadius, mProgressRectF.top + mRadius);
        } else {
            if (width > height) {
                size = height - mProgressWidth;
            } else {
                size = width - mProgressWidth;
            }
            mProgressRectF.left = (width - size) / 2 + paddingLeft;
            mProgressRectF.top = (height - size) / 2 + paddingTop;
            mProgressRectF.right = mProgressRectF.left + size;
            mProgressRectF.bottom = mProgressRectF.top + size;
            mRadius = size / 2;
            mCentre.set(width / 2f + paddingLeft, height / 2f + paddingTop);
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
        if (mShowText) drawText(canvas);

        //test -->
//        mPaint.setColor(0x99FF0000);
//        mPaint.setStrokeWidth(1);
//        int width = getWidth();
//        int height = getHeight();
//        canvas.drawLine(0, mCentre.y, width, mCentre.y, mPaint);
//        canvas.drawLine(mCentre.x, 0, mCentre.x, height, mPaint);
        //<--test complete
    }

    /**
     * 绘制进度背景
     *
     * @param canvas 画布
     */
    protected void drawProgressBackground(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(mProgressBackground);

        if (mProgressType == TYPE_ARC) {
            canvas.drawArc(mProgressRectF, mStartAngle, mMaxSweepAngle, false, mPaint);
        } else {
            //绘制背景（绘制一个圆环）
            canvas.drawCircle(mCentre.x, mCentre.y, mRadius, mPaint);
        }
    }

    /**
     * 绘制进度
     *
     * @param canvas 画布
     */
    protected void drawProgress(Canvas canvas) {
        float angle = mProgressSweepAngle;
        if (angle > 0) {
            mPaint.setColor(mProgressColor);
            mPaint.setStyle(Paint.Style.STROKE);
            //绘制进度（绘制一条弧线）
            canvas.drawArc(mProgressRectF, mStartAngle, angle, false, mPaint);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    protected void drawText(Canvas canvas) {
        if (mTextColors != null) {
            mTextPaint.setColor(mTextColors.getColorForState(getDrawableState(), Color.BLACK));
        }
        if (mLayout == null) {
            assumeLayout();
        }
        Layout layout = mLayout;
        if (layout == null) {
            CharSequence text = getProgressText();
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float t = mCentre.y - fontMetrics.ascent - (fontMetrics.descent - fontMetrics.ascent) / 2;
            float l = mCentre.x - mTextPaint.measureText(text, 0, text.length()) / 2;
            canvas.drawText(text, 0, text.length(), l, t, mTextPaint);
        } else {
            canvas.save();
            canvas.translate(mCentre.x - (mLayout.getWidth() >> 1), mCentre.y - (mLayout.getHeight() >> 1));
            layout.draw(canvas);
            canvas.restore();
        }
    }
}
