package com.zhangwei.zwlibs.baselib.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.zhangwei.zwlibs.baselib.R;


/**
 * Created by 张巍 on 2019/5/15.
 */
public class HeaderView extends ViewGroup {
    private ImageView mBackView;
    private TextView mTitleView;
    private ImageView mMenuView;
    private float mDefaultSize;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDefaultSize = context.getResources().getDimension(R.dimen.actionBarSize);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyleAttr, 0);
        boolean backShow = a.getBoolean(R.styleable.HeaderView_backShow, false);
        int backPadding = a.getDimensionPixelOffset(R.styleable.HeaderView_backPadding, 0);
        Drawable backIcon = a.getDrawable(R.styleable.HeaderView_backIcon);

        boolean menuShow = a.getBoolean(R.styleable.HeaderView_menuShow, false);
        int menuPadding = a.getDimensionPixelOffset(R.styleable.HeaderView_menuPadding, 0);
        Drawable menuIcon = a.getDrawable(R.styleable.HeaderView_menuIcon);

        CharSequence title = a.getText(R.styleable.HeaderView_title);
        ColorStateList textColor = a.getColorStateList(R.styleable.HeaderView_titleTextColor);
        final int textAppearanceRes = a.getResourceId(R.styleable.HeaderView_titleTextAppearance, -1);
        a.recycle();
        initTitle(context, title, textColor, textAppearanceRes);

        if (backShow) {
            initBackView();
            setBackIcon(backIcon);
            setBackPadding(backPadding);
        }

        if (menuShow) {
            initMenuView();
            setMenuIcon(menuIcon);
            setMenuPadding(menuPadding);
        }
    }

    private void initTitle(Context context, CharSequence text, ColorStateList textColor, int textAppearanceRes) {
        mTitleView = new TextView(context);
        mTitleView.setId(R.id.headerTitle);
        if (textAppearanceRes != -1) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                mTitleView.setTextAppearance(textAppearanceRes);
            } else {
                mTitleView.setTextAppearance(getContext(), textAppearanceRes);
            }
        }

        if (textColor != null) {
            mTitleView.setTextColor(textColor);
        }

        if (text != null) {
            mTitleView.setText(text);
        }
    }


    private void initBackView() {
        if (mBackView == null) {
            mBackView = new ImageView(getContext());
            mBackView.setId(R.id.headerBack);
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

        int sizeMeasureSpec;
        int size;
        int titleWidthMeasureSpec;
        int width;
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
            size = height - getPaddingTop() - getPaddingBottom();
            sizeMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        } else {
            size = (int) mDefaultSize;
            height = size + getPaddingTop() + getPaddingBottom();
            sizeMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        }
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            width = widthSize;
            titleWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width - getPaddingLeft() - getPaddingRight() - size - size, MeasureSpec.AT_MOST);
        } else {

        }

        if (mBackView != null) {
            mBackView.measure(sizeMeasureSpec, sizeMeasureSpec);
        }
        if (mMenuView != null) {
            mMenuView.measure(sizeMeasureSpec, sizeMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
