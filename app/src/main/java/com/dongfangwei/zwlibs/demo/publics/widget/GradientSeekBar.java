package com.dongfangwei.zwlibs.demo.publics.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.dongfangwei.zwlibs.demo.R;
import com.dongfangwei.zwlibs.widget.CircleSeekBar;


public class GradientSeekBar extends CircleSeekBar {
    private SweepGradient mSweepGradient;
    private Matrix mMatrix = new Matrix();
    private RelativeSizeSpan mSizeSpan;
    //用于文字格式化的String，
    private String mTextFormat;
    private float mScaleTextSize;
    private int bgCircleSize1;
    private int bgCircleSize2;
    private int bgCircleSize3;

    public GradientSeekBar(Context context) {
        this(context, null);
    }

    public GradientSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSizeSpan = new RelativeSizeSpan(0.5f);
        mTextFormat = context.getString(R.string.progress_text_format);
        mScaleTextSize = context.getResources().getDisplayMetrics().density * 10;
    }

    private void initSweepGradient() {
        mSweepGradient = new SweepGradient(mCentre.x, mCentre.y, 0xFF4E73FF, 0xFF4AC5FE);
    }

    @Override
    public void setProgressType(int progressType) {
        //过滤掉圆弧类型
        if (progressType == TYPE_CIRCLE) {
            super.setProgressType(progressType);
        }
    }

    @Override
    protected CharSequence getProgressText() {
        float ratio = getMax() == 0 ? 0 : (float) getProgress() / getMax() * 100;
        String text = String.format(mTextFormat, numberFormat(ratio));
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(mSizeSpan, text.indexOf('%'), text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }


    @Override
    protected void onLayoutProgress(int left, int top, int right, int bottom) {
        super.onLayoutProgress(left, top, right, bottom);
        //(getProgressWidth()/2)/(r/180*PI)
        float offsetAngle = (float) (getProgressWidth() * 90 / mRadius / Math.PI);
        mMatrix.setRotate(mStartAngle - offsetAngle, mCentre.x, mCentre.y);
        initSweepGradient();
        mSweepGradient.setLocalMatrix(mMatrix);

        int size = getPaddingLeft() / 12;
        bgCircleSize1 = (size << 1) + size;
        bgCircleSize2 = bgCircleSize1 + size;
        bgCircleSize3 = bgCircleSize2 + size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawProgressNumber(canvas);
        super.onDraw(canvas);
    }

    private void drawBackground(Canvas canvas) {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        float r = mRadius + bgCircleSize1 - ((bgCircleSize1 - getProgressWidth()) / 2);
        paint.setColor(0x12BFBFBF);
        paint.setStrokeWidth(bgCircleSize1);
        canvas.drawCircle(mCentre.x, mCentre.y, r, paint);

        r += bgCircleSize2 - (bgCircleSize2 - bgCircleSize1 >> 1);
        paint.setColor(0x22BFBFBF);
        paint.setStrokeWidth(bgCircleSize2);
        canvas.drawCircle(mCentre.x, mCentre.y, r, paint);

        r += bgCircleSize3 - (bgCircleSize3 - bgCircleSize2 >> 1);
        paint.setColor(0x32BFBFBF);
        paint.setStrokeWidth(bgCircleSize3);
        canvas.drawCircle(mCentre.x, mCentre.y, r, paint);


    }

    private void drawProgressNumber(Canvas canvas) {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFAFAFAF);
        paint.setStrokeWidth(1);
        paint.setTextSize(mScaleTextSize);
        canvas.save();
        String text = "25";
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float y = mCentre.y - fontMetrics.ascent - (fontMetrics.descent - fontMetrics.ascent) / 2;
        float x = mProgressRectF.right - getProgressWidth() - paint.measureText(text);
        canvas.drawText(text, x, y, paint);

        text = "75";
        x = mProgressRectF.left + getProgressWidth();
        canvas.drawText(text, x, y, paint);

        text = "50";
        x = mCentre.x - paint.measureText(text) / 2;
        y = mProgressRectF.bottom - getProgressWidth();
        canvas.drawText(text, x, y, paint);

        text = "100";
        x = mCentre.x - paint.measureText(text) / 2;
        y = mProgressRectF.top + getProgressWidth() - fontMetrics.top;
        canvas.drawText(text, x, y, paint);
        canvas.restore();
    }

    @Override
    protected void drawProgress(Canvas canvas) {
        Paint paint = getPaint();
        paint.setShader(mSweepGradient);
        super.drawProgress(canvas);
        paint.setShader(null);
    }
}
