package com.dongfangwei.zwlibs.base.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;

import com.dongfangwei.zwlibs.base.R;
import com.dongfangwei.zwlibs.base.widget.BadgeView;

import java.util.Locale;


/**
 * 导航栏的导航项
 * Created by dongfangwei on 2018/1/24.
 */

public class NavigationItem extends ViewGroup {
    private ImageView mIconV;
    private TextView mTextV;
    private BadgeView mBadgeV;
    private int mVerticalMargin;
    private int mIconPadding;
    private int mMaxBadgeNum;//badge的最大数值，默认99
    @ColorInt
    private int mBadgeColor;
    private ColorStateList mBadgeTextColor;

    public NavigationItem(Context context) {
        this(context, 0);
    }

    public NavigationItem(Context context, int badgeNum) {
        super(context);
        initView(context, -1, -1, null, -1, badgeNum);
    }

    public NavigationItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavigationItem, defStyleAttr, 0);
        final CharSequence text = a.getText(R.styleable.NavigationItem_android_text);
        Drawable drawable;
        final int id = a.getResourceId(R.styleable.NavigationItem_srcCompat, -1);
        if (id != -1) {
            drawable = AppCompatResources.getDrawable(context, id);
        } else {
            drawable = a.getDrawable(R.styleable.NavigationItem_android_src);
        }
        final int textAppearanceRes = a.getResourceId(R.styleable.NavigationItem_android_textAppearance, -1);
        ColorStateList textColor = a.getColorStateList(R.styleable.NavigationItem_android_textColor);
        final int textSize = a.getDimensionPixelSize(R.styleable.NavigationItem_android_textSize, -1);
        final int iconSize = a.getDimensionPixelSize(R.styleable.NavigationItem_iconSize, -1);
        final int badgeNum = a.getInteger(R.styleable.NavigationItem_badgeNum, 0);
        mIconPadding = a.getDimensionPixelOffset(R.styleable.NavigationItem_iconPadding, 0);
        mMaxBadgeNum = a.getInteger(R.styleable.NavigationItem_maxBadgeNum, 99);
        mBadgeColor = a.getColor(R.styleable.NavigationItem_badgeColor, -1);
        mBadgeTextColor = a.getColorStateList(R.styleable.NavigationItem_badgeTextColor);

        a.recycle();
        initView(context, textAppearanceRes, textSize, textColor, iconSize, badgeNum);
        setText(text);
        if (drawable != null) {
            setImageDrawable(drawable);
        }
    }

    private void initView(Context context, int textAppearanceRes, int textSize, ColorStateList textColor, int iconSize, int badgeNum) {
        float dp = context.getResources().getDisplayMetrics().density;
        if (iconSize == -1) {
            iconSize = (int) (dp * 24);
        }
        mVerticalMargin = (int) (8 * dp);
        mIconV = new ImageView(context);
        mIconV.setId(R.id.navigationIcon);
        LayoutParams iconParams = new LayoutParams(iconSize, iconSize);
        mIconV.setLayoutParams(iconParams);
        addView(mIconV);

        mTextV = new TextView(context);
        mTextV.setId(R.id.navigationText);
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextV.setLayoutParams(textParams);
        mTextV.setEms(5);
        mTextV.setGravity(Gravity.CENTER_HORIZONTAL);
        if (textAppearanceRes != -1) {
            if (textColor == null) {
                mTextV.setTextColor(new ColorStateList(new int[][]{{android.R.attr.state_selected}, {-android.R.attr.state_selected}}, new int[]{0xFFE4393C, 0xFF666666}));
            }
            if (textSize == -1) {
                mTextV.setTextSize(12);
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                mTextV.setTextAppearance(textAppearanceRes);
            } else {
                mTextV.setTextAppearance(getContext(), textAppearanceRes);
            }

            if (textColor != null) {
                mTextV.setTextColor(textColor);
            }

            if (textSize != -1) {
                mTextV.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        } else {
            if (textColor == null) {
                textColor = new ColorStateList(new int[][]{{android.R.attr.state_selected}, {-android.R.attr.state_selected}}, new int[]{0xFFE4393C, 0xFF666666});
            }
            mTextV.setTextColor(textColor);
            if (textSize == -1) {
                mTextV.setTextSize(12);
            } else {
                mTextV.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }
        addView(mTextV);
        setBadgeNum(badgeNum);
    }

    private void initBadgeView() {
        if (mBadgeV == null) {
            mBadgeV = new BadgeView(getContext());
            mBadgeV.setId(R.id.navigationBadge);
            LayoutParams badgeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mBadgeV.setLayoutParams(badgeParams);
            mBadgeV.setLines(1);

            if (mBadgeTextColor != null)
                mBadgeV.setTextColor(mBadgeTextColor);

            if (mBadgeColor != -1)
                mBadgeV.setBackgroundColor(mBadgeColor);

            addView(mBadgeV);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        measureChild(mIconV, widthMeasureSpec, heightMeasureSpec);
        measureChild(mTextV, widthMeasureSpec, heightMeasureSpec);
        if (mBadgeV != null) {
            measureChild(mBadgeV, widthMeasureSpec, heightMeasureSpec);
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.max(mIconV.getMeasuredWidth(), mTextV.getMeasuredWidth()) + getPaddingLeft() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
//            throw new RuntimeException("NavigationItem 的高度不能设为固定值");
            mVerticalMargin = (heightSize - mIconV.getMeasuredHeight() - mIconPadding
                    - mTextV.getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) >> 2;

        } else {
            height = mVerticalMargin + mIconV.getMeasuredHeight() + mIconPadding + mTextV.getMeasuredHeight() + mVerticalMargin + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = getPaddingTop();
        int width = r - l - getPaddingLeft() - getPaddingRight();

        int iconTop = top + mVerticalMargin + 1;
        int iconLeft = (width - mIconV.getMeasuredWidth()) / 2;
        int iconBottom = iconTop + mIconV.getMeasuredHeight();
        int iconRight = iconLeft + mIconV.getMeasuredWidth();
        mIconV.layout(iconLeft, iconTop, iconRight, iconBottom);

        int textLeft = (width - mTextV.getMeasuredWidth()) / 2;
        mTextV.layout(textLeft, iconBottom + mIconPadding, textLeft + mTextV.getMeasuredWidth(), iconBottom + mIconPadding + mTextV.getMeasuredHeight());

        if (mBadgeV != null) {
            int w = mBadgeV.getMeasuredWidth();
            int h = mBadgeV.getMeasuredHeight();
            int badgeLeft = iconRight - w / 2;
            int badgeTop = Math.max(iconTop - h / 2, top);
            mBadgeV.layout(badgeLeft, badgeTop, badgeLeft + w, badgeTop + h);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        mIconV.setImageDrawable(drawable);
    }

    public void setImageResource(int resId) {
        mIconV.setImageResource(resId);
    }

    public int getIconPadding() {
        return mIconPadding;
    }

    public void setIconPadding(int mIconPadding) {
        this.mIconPadding = mIconPadding;
        this.postInvalidate();
    }

    public void setText(CharSequence text) {
        if (text != null && text.length() != 0)
            mTextV.setText(text);
    }

    public void setText(int resid) {
        mTextV.setText(resid);
    }

    public void setTextColor(ColorStateList colors) {
        mTextV.setTextColor(colors);
    }

    public void setTextSize(float size) {
        mTextV.setTextSize(size);
    }

    public void setKey(String key) {
        this.setTag(key);
    }

    public int getMaxBadgeNum() {
        return mMaxBadgeNum;
    }

    public void setMaxBadgeNum(int mMaxBadgeNum) {
        this.mMaxBadgeNum = mMaxBadgeNum;
    }

    public void setBadgeNum(int num) {
        if (num > 0) {
            initBadgeView();
            if (mBadgeV.getVisibility() != View.VISIBLE) {
                mBadgeV.setVisibility(View.VISIBLE);
            }
            mBadgeV.setText(num > mMaxBadgeNum ? String.format(Locale.getDefault(), "%d+", mMaxBadgeNum) : String.valueOf(num));
        } else {
            hideBadge();
        }
    }

    public void setBadgeColor(int color) {
        mBadgeColor = color;
        if (mBadgeV != null) {
            mBadgeV.setBackgroundColor(color);
        }
    }

    public void setBadgeTextColor(int badgeTextColor) {
        setBadgeTextColor(ColorStateList.valueOf(badgeTextColor));
    }

    public void setBadgeTextColor(ColorStateList badgeTextColor) {
        mBadgeTextColor = badgeTextColor;
        if (mBadgeV != null) {
            mBadgeV.setTextColor(badgeTextColor);
        }
    }

    public boolean badgeShowing() {
        return mBadgeV != null && mBadgeV.getVisibility() == View.VISIBLE;
    }

    public void hideBadge() {
        if (badgeShowing()) {
            mBadgeV.setVisibility(View.GONE);
        }
    }

    public void setShow(boolean isShow) {
        if (isShow) {
            if (this.getVisibility() != VISIBLE) {
                this.setVisibility(VISIBLE);
            }
        } else {
            if (this.getVisibility() == VISIBLE) {
                this.setVisibility(GONE);
            }
        }
    }

    public void setSelected(boolean isSelected) {
        mIconV.setSelected(isSelected);
        mTextV.setSelected(isSelected);
    }
}
