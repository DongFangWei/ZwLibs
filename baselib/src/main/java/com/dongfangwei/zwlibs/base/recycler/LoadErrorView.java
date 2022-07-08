package com.dongfangwei.zwlibs.base.recycler;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.dongfangwei.zwlibs.base.R;

public class LoadErrorView extends ViewGroup {
    private ImageView mImageView;
    private TextView mTextView;
    private Button mButton;
    private int mMaxImgSize;
    private int mBtnMarginTop;

    public LoadErrorView(Context context) {
        this(context, null);
    }

    public LoadErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadErrorView, defStyleAttr, 0);
        final CharSequence errText = a.getText(R.styleable.LoadErrorView_errText);
        final CharSequence btnText = a.getText(R.styleable.LoadErrorView_btnText);
        Drawable drawable;
        final int id = a.getResourceId(R.styleable.LoadErrorView_srcCompat, -1);
        if (id != -1) {
            drawable = AppCompatResources.getDrawable(context, id);
        } else {
            drawable = a.getDrawable(R.styleable.LoadErrorView_android_src);
        }
        a.recycle();
        float dp = getResources().getDisplayMetrics().density;

        mBtnMarginTop = (int) (16 * dp);

        mImageView = new ImageView(context);
        mMaxImgSize = (int) (128 * dp);
        LayoutParams imgParams = new LayoutParams((int) (192 * dp), mMaxImgSize);
        mImageView.setLayoutParams(imgParams);
        if (drawable != null)
            mImageView.setImageDrawable(drawable);

        mTextView = new TextView(context);
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextView.setLayoutParams(textParams);
        setErrText(errText);

        mButton = new Button(context);
        LayoutParams btnParams = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) (32 * dp));
        mButton.setLayoutParams(btnParams);
        mButton.setTextColor(0xFF9A9A9A);
        mButton.setTextSize(14);
        mButton.setPadding(0, 0, 0, 0);
        Drawable btnBgDrawable = getBtnBackgroundDrawable((int) dp, (int) (dp * 6));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mButton.setBackground(btnBgDrawable);
        } else {
            mButton.setBackgroundDrawable(btnBgDrawable);
        }
        setBtnText(btnText);

        addView(mImageView);
        addView(mTextView);
        addView(mButton);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        int imgSize = mMaxImgSize;

        measureChild(mTextView, widthMeasureSpec, heightMeasureSpec);
        if (mButton.getVisibility() != GONE) {
            measureChild(mButton, widthMeasureSpec, heightMeasureSpec);
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            if (widthSize < mMaxImgSize) {
                imgSize = widthSize;
            }
            width = widthSize;
        } else {
            width = Math.max(Math.max(mMaxImgSize, mTextView.getMeasuredWidth()), mButton.getMeasuredWidth()) + getPaddingLeft() + getPaddingBottom();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            int imgHeight = heightSize - mTextView.getMeasuredHeight() - mBtnMarginTop - mButton.getMeasuredHeight();
            if (imgHeight < mMaxImgSize) imgSize = Math.min(imgSize, imgHeight);
            height = heightSize;
        } else {
            height = getPaddingTop() + getPaddingBottom() + mMaxImgSize + mTextView.getMeasuredHeight() + mBtnMarginTop + mButton.getMeasuredHeight();
        }

        if (imgSize != mMaxImgSize) {
            int imgMeasureSpec = MeasureSpec.makeMeasureSpec(imgSize, MeasureSpec.EXACTLY);
            mImageView.measure(imgMeasureSpec, imgMeasureSpec);
        } else {
            measureChild(mImageView, widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = l + getPaddingLeft();
        int top = t + getPaddingTop();
        int right = r + getPaddingRight();
        int bottom = b + getPaddingBottom();
        int width = right - left;
        int height = bottom - top;
        int childHeight = mImageView.getMeasuredHeight() + mTextView.getMeasuredHeight() + mBtnMarginTop + mButton.getMeasuredHeight();
        top = top + (height - childHeight) / 2;
        int childLeft = left + (width - mImageView.getMeasuredWidth()) / 2;
        mImageView.layout(childLeft, top, childLeft + mImageView.getMeasuredWidth(), (top = top + mImageView.getMeasuredHeight()));
        childLeft = left + (width - mTextView.getMeasuredWidth()) / 2;
        mTextView.layout(childLeft, top, childLeft + mTextView.getMeasuredWidth(), (top = top + mTextView.getMeasuredHeight()));
        if (mButton.getVisibility()==VISIBLE) {
            childLeft = left + (width - mButton.getMeasuredWidth()) / 2;
            top += mBtnMarginTop;
            mButton.layout(childLeft, top, childLeft + mButton.getMeasuredWidth(), top + mButton.getMeasuredHeight());
        }
    }

    public int getBtnMarginTop() {
        return mBtnMarginTop;
    }

    public void setBtnMarginTop(int btnMarginTop) {
        this.mBtnMarginTop = btnMarginTop;
    }

    public void setErrText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setErrTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setImageDrawable(Drawable image) {
        mImageView.setImageDrawable(image);
    }

    public void setImageBitmap(Bitmap image) {
        mImageView.setImageBitmap(image);
    }

    public void setImageResource(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setBtnText(CharSequence text) {
        mButton.setText(text);
    }

    public void setBtnTextColor(ColorStateList colors) {
        mButton.setTextColor(colors);
    }

    public void setBtnBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mButton.setBackground(background);
        } else {
            mButton.setBackgroundDrawable(background);
        }
    }

    /**
     * 设置按钮的可见性状态
     *
     * @param visibility 可见性状态
     */
    public void setBtnVisibility(int visibility) {
        mButton.setVisibility(visibility);
    }

    public void setBtnOnClickListener(OnClickListener clickListener) {
        mButton.setOnClickListener(clickListener);
    }

    private StateListDrawable getBtnBackgroundDrawable(int stroke, int cornerRadius) {
        StateListDrawable drawable = new StateListDrawable();
        GradientDrawable pressedDrawable = new GradientDrawable();
        pressedDrawable.setStroke(stroke, 0xFF8A8A8A);
        pressedDrawable.setColor(0x338A8A8A);
        pressedDrawable.setCornerRadius(cornerRadius);
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        GradientDrawable unpressedDrawable = new GradientDrawable();
        unpressedDrawable.setStroke(stroke, 0xFF9A9A9A);
        unpressedDrawable.setCornerRadius(cornerRadius);
        drawable.addState(new int[]{-android.R.attr.state_pressed}, unpressedDrawable);
        return drawable;
    }
}
