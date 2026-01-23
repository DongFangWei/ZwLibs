package com.dongfangwei.zwlibs.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dongfangwei.zwlibs.R;


/**
 * Created by 张巍 on 2019/5/15.
 */
public class HeaderView extends ViewGroup {
    // Enum for the "ellipsize" XML parameter.
    private static final int ELLIPSIZE_NOT_SET = -1;
    private static final int ELLIPSIZE_NONE = 0;
    private static final int ELLIPSIZE_START = 1;
    private static final int ELLIPSIZE_MIDDLE = 2;
    private static final int ELLIPSIZE_END = 3;
    private static final int ELLIPSIZE_MARQUEE = 4;

    protected ImageView mBackView;
    private int mBackPadding = -1;
    private Drawable mBackDrawable;
    protected TextView mTitleView;
    protected ImageView mMenuView;
    private int mMenuPadding = -1;
    private Drawable mMenuDrawable;
    protected final int mDefaultSize;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDefaultSize = context.getResources().getDimensionPixelOffset(R.dimen.actionBarSize);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyleAttr, R.style.Base_HeaderView);
        boolean backShow = a.getBoolean(R.styleable.HeaderView_backShow, false);
        int backPadding = a.getDimensionPixelOffset(R.styleable.HeaderView_backPadding, -1);
        Drawable backIcon = a.getDrawable(R.styleable.HeaderView_backIcon);

        boolean menuShow = a.getBoolean(R.styleable.HeaderView_menuShow, false);
        int menuPadding = a.getDimensionPixelOffset(R.styleable.HeaderView_menuPadding, -1);
        Drawable menuIcon = a.getDrawable(R.styleable.HeaderView_menuIcon);

        CharSequence title = a.getText(R.styleable.HeaderView_title);
        ColorStateList textColor = a.getColorStateList(R.styleable.HeaderView_titleTextColor);
        final int textAppearanceRes = a.getResourceId(R.styleable.HeaderView_titleTextAppearance, -1);
        int titleEllipsize = a.getInt(R.styleable.HeaderView_titleEllipsize, ELLIPSIZE_NOT_SET);
        a.recycle();
        initTitleView();
        if (title != null) {
            setTitle(title);
        }
        setTitleTextAppearance(textAppearanceRes);

        if (textColor != null) {
            setTitleColor(textColor);
        }

        if (titleEllipsize != -1) {
            switch (titleEllipsize) {
                case ELLIPSIZE_START:
                    setTitleEllipsize(TextUtils.TruncateAt.START);
                    break;
                case ELLIPSIZE_MIDDLE:
                    setTitleEllipsize(TextUtils.TruncateAt.MIDDLE);
                    break;
                case ELLIPSIZE_END:
                    setTitleEllipsize(TextUtils.TruncateAt.END);
                    break;
                case ELLIPSIZE_MARQUEE:
                    setTitleEllipsize(TextUtils.TruncateAt.MARQUEE);
                    break;
            }
        }

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
            }
            initMenuView();
            setMenuIcon(menuIcon);
            if (menuPadding != -1) {
                setMenuPadding(menuPadding);
            }
        }
    }

    protected void initTitleView() {
        mTitleView = new TextView(getContext());
        mTitleView.setId(R.id.headerTitle);
        mTitleView.setGravity(Gravity.CENTER);
        mTitleView.setSingleLine();
        mTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        addView(mTitleView);
    }

    public void setTitle(int titleRes) {
        mTitleView.setText(titleRes);
    }

    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitleColor(int textColor) {
        mTitleView.setTextColor(textColor);
    }

    public void setTitleColor(ColorStateList textColor) {
        mTitleView.setTextColor(textColor);
    }

    public void setTitleTextAppearance(int textAppearanceRes) {
        if (textAppearanceRes != -1) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                mTitleView.setTextAppearance(textAppearanceRes);
            } else {
                mTitleView.setTextAppearance(getContext(), textAppearanceRes);
            }
        }
    }

    public void setTitleEllipsize(TextUtils.TruncateAt where) {
        mTitleView.setEllipsize(where);
    }

    private void initBackView() {
        if (mBackView == null) {
            mBackView = new ImageView(getContext());
            mBackView.setId(R.id.headerBack);
            if (mBackPadding != -1) {
                mBackView.setPadding(mBackPadding, mBackPadding, mBackPadding, mBackPadding);
            }
            if (mBackDrawable != null) {
                mBackView.setImageDrawable(mBackDrawable);
            }
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
        mBackDrawable = backIcon;
    }

    public void setBackIcon(@DrawableRes int resId) {
        setBackIcon(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setBackPadding(int padding) {
        if (mBackPadding != padding) {
            if (mBackView != null) {
                mBackView.setPadding(padding, padding, padding, padding);
            }
            mBackPadding = padding;
        }
    }

    /**
     * 设置菜单点击事件
     * 菜单的id为{@link R.id#headerMenu}
     */
    private void initMenuView() {
        if (mMenuView == null) {
            mMenuView = new ImageView(getContext());
            mMenuView.setId(R.id.headerMenu);
            if (mMenuPadding != -1) {
                mMenuView.setPadding(mMenuPadding, mMenuPadding, mMenuPadding, mMenuPadding);
            }
            if (mMenuDrawable != null) {
                mMenuView.setImageDrawable(mMenuDrawable);
            }
            addView(mMenuView);
        }
    }

    /**
     * 菜单按钮是否显示
     *
     * @return true：显示，false：不显示
     */
    public boolean isShowMenuView() {
        return mMenuView != null && mMenuView.getVisibility() == VISIBLE;
    }

    public void setShowMenuView(boolean show) {
        if (show) {
            initMenuView();
            if (mMenuView.getVisibility() != VISIBLE) {
                mMenuView.setVisibility(VISIBLE);
            }
        } else if (isShowMenuView()) {
            mMenuView.setVisibility(GONE);
        }
    }

    public void setMenuIcon(Drawable menuIcon) {
        if (mMenuView != null) {
            mMenuView.setImageDrawable(menuIcon);
        }
        mMenuDrawable = menuIcon;
    }

    public void setMenuIcon(@DrawableRes int resId) {
        setMenuIcon(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setMenuPadding(int padding) {
        if (mMenuPadding != padding) {
            if (mMenuView != null) {
                mMenuView.setPadding(padding, padding, padding, padding);
            }
            mMenuPadding = padding;
        }
    }

    public void setBackOnClickListener(@Nullable OnClickListener clickListener) {
        if (mBackView != null) {
            mBackView.setOnClickListener(clickListener);
        }
    }

    public void setMenuOnClickListener(@Nullable OnClickListener clickListener) {
        if (mMenuView != null) {
            mMenuView.setOnClickListener(clickListener);
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
