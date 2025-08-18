package com.dongfangwei.zwlibs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dongfangwei.zwlibs.R;

/**
 * 带清除按钮的EditText
 */
public class ClearEditText extends EditText {
    /**
     * 清除文字按钮的图标
     */
    private Drawable mClearDrawable;
    /**
     * 清除文字按钮的位置
     */
    private Rect mDrawableBound;
    /**
     * 清除文字按钮的图标的宽
     */
    private int mDrawableWidth;
    /**
     * 清除文字按钮的图标的高
     */
    private int mDrawableHeight;

    /**
     * 清除文字按钮的图标的测量宽度
     */
    private int mDrawableMeasuredWidth;
    /**
     * 清除文字按钮的图标的测量高度
     */
    private int mDrawableMeasuredHeight;

    /**
     * 清除文字按钮的图标的Gravity
     * <p>
     * 可选值如下
     *
     * @see Gravity#TOP
     * @see Gravity#BOTTOM
     * @see Gravity#CENTER_VERTICAL
     * </p>
     */
    private int mDrawableGravity;

    /**
     * 清除文字按钮是否按下
     */
    private boolean isPressed = false;

    private OnClearTextListener onClearTextListener;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText, defStyleAttr, 0);
        Drawable clearDrawable = a.getDrawable(R.styleable.ClearEditText_clearDrawable);
        int defaultSize = (int) (24 * context.getResources().getDisplayMetrics().density);
        mDrawableWidth = a.getDimensionPixelOffset(R.styleable.ClearEditText_drawableWidth, defaultSize);
        mDrawableHeight = a.getDimensionPixelOffset(R.styleable.ClearEditText_drawableHeight, defaultSize);
        mDrawableGravity = a.getInt(R.styleable.ClearEditText_drawableGravity, Gravity.CENTER_HORIZONTAL);
        String disallowed = a.getString(R.styleable.ClearEditText_disallowed);
        a.recycle();
        if (clearDrawable == null) {
            clearDrawable = ContextCompat.getDrawable(context, R.drawable.ic_close);
        }
        setClearDrawable(clearDrawable);
        mDrawableBound = new Rect();
        if (disallowed != null && !disallowed.isEmpty()) {
            addFilter(new DisallowedInputFilter(disallowed));
        }
    }

    /**
     * 添加过滤器
     *
     * @param filter 过滤器
     */
    private void addFilter(InputFilter filter) {
        InputFilter[] oldFilters = getFilters();
        InputFilter[] newFilters;
        if (oldFilters != null && oldFilters.length > 0) {
            newFilters = new InputFilter[oldFilters.length + 1];
            newFilters[0] = filter;
            for (int i = 1, l = newFilters.length; i < l; i++) {
                newFilters[i] = oldFilters[i - 1];
            }
        } else {
            newFilters = new InputFilter[]{filter};
        }
        setFilters(newFilters);
    }

    public Drawable getClearDrawable() {
        return mClearDrawable;
    }

    public void setClearDrawable(Drawable clearDrawable) {
        this.mClearDrawable = clearDrawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //获取绘制文字所需的尺寸
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
//        int textSize = (int) (metrics.bottom - metrics.top);
        //计算明文密文按钮的宽高
        if (mDrawableHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            mDrawableMeasuredHeight = heightSize - getPaddingTop() - getPaddingBottom();
        } else if (mDrawableHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mDrawableMeasuredHeight = mClearDrawable.getIntrinsicHeight();
        } else {
            mDrawableMeasuredHeight = mDrawableHeight;
        }
        if (mDrawableWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            mDrawableMeasuredWidth = mDrawableMeasuredHeight * mClearDrawable.getIntrinsicWidth() / mClearDrawable.getIntrinsicHeight();
        } else if (mDrawableWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mDrawableMeasuredWidth = mClearDrawable.getIntrinsicWidth();
        } else {
            mDrawableMeasuredWidth = mDrawableWidth;
        }

//        if (widthMode == MeasureSpec.AT_MOST) {
//            int width = getCompoundPaddingLeft() + super.getCompoundPaddingRight() + Math.max(mDrawableMeasuredWidth, textSize) + textSize;
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//        }
//
//        if (heightMode == MeasureSpec.AT_MOST) {
//            int height = getCompoundPaddingTop() + getCompoundPaddingBottom() + Math.max(mDrawableMeasuredHeight, textSize);
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            int width = right - left;
            int height = bottom - top;
            switch (mDrawableGravity) {
                case Gravity.TOP:
                    mDrawableBound.top = getPaddingTop();
                    mDrawableBound.bottom = mDrawableBound.top + mDrawableMeasuredHeight;
                    break;
                case Gravity.BOTTOM:
                    mDrawableBound.bottom = height - getPaddingBottom();
                    mDrawableBound.top = mDrawableBound.bottom - mDrawableMeasuredHeight;
                    break;
                default:
                    mDrawableBound.top = (height - mDrawableMeasuredHeight) >> 1;
                    mDrawableBound.bottom = mDrawableBound.top + mDrawableMeasuredHeight;
                    break;
            }
            mDrawableBound.right = width - getPaddingRight();
            mDrawableBound.left = mDrawableBound.right - mDrawableMeasuredWidth;
            Log.d(getClass().getName(), mDrawableBound.toString());
            mClearDrawable.setBounds(mDrawableBound);
        }
    }

    @Override
    public int getCompoundPaddingRight() {
        if (isShowingClearDrawable()) {
            return super.getCompoundPaddingRight() + mDrawableMeasuredWidth + 10;
        } else {
            return super.getCompoundPaddingRight();
        }
    }

    @Override
    public int getCompoundPaddingTop() {
        if (isShowingClearDrawable()) {
            Layout layout = getLayout();
            if (layout == null) {
                return super.getCompoundPaddingTop() + (mDrawableMeasuredHeight >> 1);
            } else if (layout.getHeight() < mDrawableMeasuredHeight) {
                return super.getCompoundPaddingTop() + ((mDrawableMeasuredHeight - layout.getHeight()) >> 1);
            } else {
                return super.getCompoundPaddingTop();
            }
        } else {
            return super.getCompoundPaddingTop();
        }
    }

    @Override
    public int getCompoundPaddingBottom() {
        if (isShowingClearDrawable()) {
            Layout layout = getLayout();
            if (layout == null) {
                return super.getCompoundPaddingBottom() + (mDrawableMeasuredHeight >> 1);
            } else if (layout.getHeight() < mDrawableMeasuredHeight) {
                return super.getCompoundPaddingBottom() + ((mDrawableMeasuredHeight - layout.getHeight()) >> 1);
            } else {
                return super.getCompoundPaddingBottom();
            }
        } else {
            return super.getCompoundPaddingBottom();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShowingClearDrawable()) {
            canvas.save();
            canvas.translate(getScrollX(), getScrollY());
            mClearDrawable.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 当前是否显示清空图标
     *
     * @return true：显示，false：不显示
     */
    private boolean isShowingClearDrawable() {
        return !TextUtils.isEmpty(getText()) && isEnabled();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = inShowBtn(event.getX(), event.getY());
                if (isPressed && isShowingClearDrawable()) return true;
                break;
            case MotionEvent.ACTION_UP:
                if (isPressed && inShowBtn(event.getX(), event.getY())) {
                    clearText();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 位置是否在图标位置
     *
     * @param x x坐标
     * @param y y坐标
     * @return true：在图标位置，false：不在
     */
    private boolean inShowBtn(float x, float y) {
        return x >= mDrawableBound.left && x <= mDrawableBound.right &&
                y >= mDrawableBound.top && y <= mDrawableBound.bottom;
    }

    private void clearText() {
        setText(null);
        if (onClearTextListener != null) {
            onClearTextListener.onClearText(this);
        }
    }

    public OnClearTextListener getOnClearTextListener() {
        return onClearTextListener;
    }

    public void setOnClearTextListener(OnClearTextListener onClearTextListener) {
        this.onClearTextListener = onClearTextListener;
    }

    /**
     * 清除文字回调接口
     */
    public interface OnClearTextListener {

        /**
         * 清除文字回调方法
         *
         * @param editText 执行清除的EditText控件
         */
        void onClearText(EditText editText);
    }

    /**
     * 不允许输入过滤
     *
     * @author zhangwei
     * @date 2024/12/26 09:37
     */
    public class DisallowedInputFilter implements InputFilter {
        private final char[] mDisallowed;

        public DisallowedInputFilter(@NonNull String disallowed) {
            mDisallowed = disallowed.toCharArray();
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (checkChar(c)) {
                    builder.append(c);
                }
            }
            return builder;
        }

        /**
         * 检查字符是否允许输入
         *
         * @param c 要检查的字符
         * @return true：允许输入
         */
        private boolean checkChar(char c) {
            if (mDisallowed != null) {
                for (char disallowed : mDisallowed) {
                    if (disallowed == c) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
