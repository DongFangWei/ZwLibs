package com.zhangwei.zwlibs.baselib.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.zhangwei.zwlibs.baselib.R;

/**
 * Created by 张巍 on 2019/5/9.
 */
public class PasswordView extends EditText {
    //当前显示的是否是明文
    private boolean isPlaintext = false;
    //明文密文按钮是否按下
    private boolean isPressed = false;
    //显示密码时的图标
    private Drawable mShowDrawable;
    //隐藏密码时的图标
    private Drawable mHideDrawable;
    //明文密文按钮的位置
    private Rect mDrawableBound;
    //明文密文按钮的尺寸
    private int mDrawableSize;

    private int mDrawableWidth;
    private int mDrawableHeight;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PasswordView, defStyleAttr, 0);
        int inputType = a.getInt(R.styleable.PasswordView_passwordType, 0x00000081);
        mDrawableSize = a.getDimensionPixelOffset(R.styleable.PasswordView_drawableSize, context.getResources().getDimensionPixelOffset(R.dimen.icon_normal));
        Drawable showDrawable = a.getDrawable(R.styleable.PasswordView_showDrawable);
        Drawable hideDrawable = a.getDrawable(R.styleable.PasswordView_hideDrawable);
        a.recycle();
        setInputType(inputType);
        setTransformationMethod(PasswordTransformationMethod.getInstance());
        if (showDrawable == null) {
            showDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility);
        }
        if (hideDrawable == null) {
            hideDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off);
        }
        setShowDrawable(showDrawable);
        setHideDrawable(hideDrawable);
        mDrawableBound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //获取绘制文字所需的尺寸
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        int textSize = (int) (metrics.bottom - metrics.top);

        //计算明文密文按钮的宽高
        if (mDrawableSize == ViewGroup.LayoutParams.MATCH_PARENT) {
            mDrawableWidth = mDrawableHeight = heightSize - getPaddingTop() - getPaddingBottom();
        } else if (mDrawableSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mDrawableWidth = mShowDrawable.getIntrinsicWidth();
            mDrawableHeight = mShowDrawable.getIntrinsicHeight();
        } else {
            mDrawableWidth = mDrawableHeight = mDrawableSize;
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            int width = getCompoundPaddingLeft() + super.getCompoundPaddingRight() + Math.max(mDrawableWidth, textSize) + textSize;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int height = getCompoundPaddingTop() + getCompoundPaddingBottom() + Math.max(mDrawableHeight, textSize);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            int width = right - left;
            int height = bottom - top;
            mDrawableBound.top = (height - mDrawableHeight) >> 1;
            mDrawableBound.bottom = mDrawableBound.top + mDrawableHeight;
            mDrawableBound.right = width - getPaddingRight();
            mDrawableBound.left = mDrawableBound.right - mDrawableWidth;
            Log.d("PasswordEditText", mDrawableBound.toString());
            mShowDrawable.setBounds(mDrawableBound);
            mHideDrawable.setBounds(mDrawableBound);
        }
    }

    public void setShowDrawable(Drawable showDrawable) {
        this.mShowDrawable = showDrawable;
    }

    public void setHideDrawable(Drawable hideDrawable) {
        this.mHideDrawable = hideDrawable;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public int getCompoundPaddingRight() {
        return super.getCompoundPaddingRight() + mDrawableWidth + 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getScrollX(), getScrollY());
        if (isPlaintext) {
            mShowDrawable.draw(canvas);
        } else {
            mHideDrawable.draw(canvas);
        }
        canvas.restore();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = inShowBtn(event.getX(), event.getY());
                if (isPressed) return true;
                break;
            case MotionEvent.ACTION_UP:
                if (isPressed && inShowBtn(event.getX(), event.getY())) {
                    changeTextState();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean inShowBtn(float x, float y) {
        return x >= mDrawableBound.left && x <= mDrawableBound.right &&
                y >= mDrawableBound.top && y <= mDrawableBound.bottom;
    }

    /**
     * 改变文本的明密状态
     */
    private void changeTextState() {
        isPlaintext = !isPlaintext;
        if (isPlaintext) {
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        setSelection(getText().length());
    }
}
