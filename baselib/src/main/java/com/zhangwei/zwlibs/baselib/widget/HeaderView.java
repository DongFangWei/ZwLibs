package com.zhangwei.zwlibs.baselib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.zhangwei.zwlibs.baselib.R;


/**
 * Created by 张巍 on 2019/5/15.
 */
public class HeaderView extends ViewGroup {
    private ImageView mBackView;
    private TextView mTitleView;
    private ImageView mMenuView;
    private int mDefaultSize;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDefaultSize = context.getResources().getDimensionPixelOffset(R.dimen.actionBarSize);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyleAttr, 0);
        boolean backShow = a.getBoolean(R.styleable.HeaderView_backShow, false);
        int backPadding = a.getDimensionPixelOffset(R.styleable.HeaderView_backPadding, -1);
        Drawable backIcon = a.getDrawable(R.styleable.HeaderView_backIcon);

        boolean menuShow = a.getBoolean(R.styleable.HeaderView_menuShow, false);
        int menuPadding = a.getDimensionPixelOffset(R.styleable.HeaderView_menuPadding, -1);
        Drawable menuIcon = a.getDrawable(R.styleable.HeaderView_menuIcon);

        CharSequence title = a.getText(R.styleable.HeaderView_title);
        ColorStateList textColor = a.getColorStateList(R.styleable.HeaderView_titleTextColor);
        final int textAppearanceRes = a.getResourceId(R.styleable.HeaderView_titleTextAppearance, -1);
        a.recycle();
        initTitle(context, title, textColor, textAppearanceRes);

        if (backShow) {
            if (backIcon == null) {
                backIcon = ContextCompat.getDrawable(context, R.drawable.selector_btn_back);
            }
            initBackView();
            setBackIcon(backIcon);
            if (backPadding != -1) {
                setBackPadding(backPadding);
            }
        }

        if (menuShow) {
            if (menuIcon == null) {
                menuIcon = ContextCompat.getDrawable(context, R.drawable.selector_btn_more_vert);
                if (menuPadding == -1)
                    menuPadding = context.getResources().getDimensionPixelOffset(R.dimen.spacing_least);
            }
            initMenuView();
            setMenuIcon(menuIcon);
            if (menuPadding != -1) {
                setMenuPadding(menuPadding);
            }
        }
    }

    private void initTitle(Context context, CharSequence text, ColorStateList textColor, int textAppearanceRes) {
        mTitleView = new TextView(context);
        mTitleView.setId(R.id.headerTitle);
        mTitleView.setGravity(Gravity.CENTER);
        if (textAppearanceRes != -1) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                mTitleView.setTextAppearance(textAppearanceRes);
            } else {
                mTitleView.setTextAppearance(getContext(), textAppearanceRes);
            }
        }

        if (textColor != null) {
            mTitleView.setTextColor(textColor);
        } else {
            mTitleView.setTextColor(Color.WHITE);
        }
        mTitleView.setTextSize(20);

        if (text != null) {
            mTitleView.setText(text);
        }
        addView(mTitleView);
    }


    private void initBackView() {
        if (mBackView == null) {
            mBackView = new ImageView(getContext());
            mBackView.setId(R.id.headerBack);
            addView(mBackView);
        }
    }

    public void showBackView(boolean show) {
        if (show) {
            initBackView();
            if (mBackView.getVisibility() != VISIBLE) {
                mBackView.setVisibility(VISIBLE);
            }
        } else if (mBackView != null && mBackView.getVisibility() == VISIBLE) {
            mBackView.setVisibility(GONE);
        }
    }

    public void setBackIcon(Drawable backIcon) {
        if (mBackView != null) {
            mBackView.setImageDrawable(backIcon);
        }
    }

    public void setBackIcon(@DrawableRes int resId) {
        if (mBackView != null) {
            mBackView.setImageResource(resId);
        }
    }

    public void setBackPadding(int padding) {
        if (mBackView != null) {
            mBackView.setPadding(padding, padding, padding, padding);
        }
    }

    private void initMenuView() {
        if (mMenuView == null) {
            mMenuView = new ImageView(getContext());
            mMenuView.setId(R.id.headerMenu);
            addView(mMenuView);
        }
    }

    public void showMenuView(boolean show) {
        if (show) {
            initMenuView();
            if (mMenuView.getVisibility() != VISIBLE) {
                mMenuView.setVisibility(VISIBLE);
            }
        } else if (mMenuView != null && mMenuView.getVisibility() == VISIBLE) {
            mMenuView.setVisibility(GONE);
        }
    }

    public void setMenuIcon(Drawable backIcon) {
        if (mMenuView != null) {
            mMenuView.setImageDrawable(backIcon);
        }
    }

    public void setMenuIcon(@DrawableRes int resId) {
        if (mMenuView != null) {
            mMenuView.setImageResource(resId);
        }
    }

    public void setMenuPadding(int padding) {
        if (mMenuView != null) {
            mMenuView.setPadding(padding, padding, padding, padding);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size, titleWidth;

        if (heightMode == MeasureSpec.EXACTLY) {
            size = Math.min(heightSize - getPaddingTop() - getPaddingBottom(), mDefaultSize);
        } else {
            size = mDefaultSize;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(size + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY);
        }

        int sizeMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        if (widthMode == MeasureSpec.EXACTLY) {
            titleWidth = widthSize - getPaddingLeft() - getPaddingRight();
            if (mBackView != null || mMenuView != null) {
                titleWidth -= size << 1;
            }
            int titleWidthMeasureSpec = MeasureSpec.makeMeasureSpec(titleWidth, MeasureSpec.EXACTLY);
            mTitleView.measure(titleWidthMeasureSpec, sizeMeasureSpec);
        } else {
            measureChild(mTitleView, widthMeasureSpec, heightMeasureSpec);
            titleWidth = mTitleView.getMeasuredWidth();
            int width = titleWidth + getPaddingLeft() + getPaddingRight();
            if (mBackView != null) {
                width += size;
            }
            if (mMenuView != null) {
                width += size;
            }
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        if (mBackView != null) {
            mBackView.measure(sizeMeasureSpec, sizeMeasureSpec);
        }
        if (mMenuView != null) {
            mMenuView.measure(sizeMeasureSpec, sizeMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = b - t - getPaddingTop() - getPaddingBottom();
        int left = getPaddingLeft();
        int top;
        int padding = getPaddingTop();
        if (mBackView != null) {
            top = ((height - mBackView.getMeasuredHeight()) >> 1) + padding;
            mBackView.layout(left, top, left = left + mBackView.getMeasuredWidth(),
                    top + mBackView.getMeasuredHeight());
        } else if (mMenuView != null) {
            left = left + mTitleView.getMeasuredHeight();
        }
        top = ((height - mTitleView.getMeasuredHeight()) >> 1) + padding;
        mTitleView.layout(left, top, left = left + mTitleView.getMeasuredWidth(),
                top + mTitleView.getMeasuredHeight());
        if (mMenuView != null) {
            top = ((height - mMenuView.getMeasuredHeight()) >> 1) + padding;
            mMenuView.layout(left, top, left + mMenuView.getMeasuredWidth(),
                    top + mMenuView.getMeasuredHeight());
        }
    }
}
