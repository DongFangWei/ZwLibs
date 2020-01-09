package com.zhangwei.zwlibs.baselib.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Created by zhangwei on 2018/11/21.
 */

public class NavigationBarView extends LinearLayout implements View.OnClickListener {
    private View mSelectedView;
    private OnSelectedListener listener;

    public NavigationBarView(Context context) {
        this(context, null);
    }

    public NavigationBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof NavigationItemView) {
            super.addView(child, index, params);
            child.setOnClickListener(this);
        } else {
            throw new RuntimeException("NavigationBarView 不能有类型不是NavigationItem的子控件");//NavigationBarView can't have child view that are not NavigationItem.
        }
    }

    public void addItem(NavigationItemView navigationItem) {
        navigationItem.setOnClickListener(this);
        addView(navigationItem);
    }

    public NavigationItemView getNavigationItem(String key) {
        return this.findViewWithTag(key);
    }

    public void setSelectedItemAt(int index) {
        setSelectedItemAt(index, false);
    }

    public void setSelectedItemAt(int index, boolean enableListener) {
        View v = this.getChildAt(index);
        if (v != null) {
            if (enableListener)
                changeSelectView(v);
            else {
                if (mSelectedView != null)
                    mSelectedView.setSelected(false);
                v.setSelected(true);
                mSelectedView = v;
            }
        }
    }

    public void setItemBadgeNum(int index, int badgeNum) {
        View v = this.getChildAt(index);
        if (v != null) {
            ((NavigationItemView) v).setBadgeNum(badgeNum);
        }
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        changeSelectView(v);
    }

    private void changeSelectView(View view) {
        if (view != mSelectedView) {
            boolean isChange = listener == null || listener.onSelectedChanged(view, mSelectedView);

            if (isChange) {
                if (mSelectedView != null) mSelectedView.setSelected(false);
                view.setSelected(true);
                mSelectedView = view;
            }
        }
    }

    public interface OnSelectedListener {
        /**
         * 选中的View改变的监听
         *
         * @param newView 新选中的View
         * @param oldView 之前选中的View
         * @return 是否改变选中状态；返回true：将置newView为选中状态，取消oldView的选中状态；返回false：不改变任何View的状态
         */
        boolean onSelectedChanged(View newView, View oldView);
    }
}
