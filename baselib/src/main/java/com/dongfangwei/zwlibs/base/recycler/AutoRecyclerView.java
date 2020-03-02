package com.dongfangwei.zwlibs.base.recycler;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dongfangwei.zwlibs.base.R;

/**
 * 自动加载的RecyclerView，配合{@link OnLoadListener}与{@link BaseAutoAdapter}使用实现自动加载
 */
public class AutoRecyclerView extends SwipeRefreshLayout {
    /* 控件状态 */
    public enum Status {
        /* 控件状态 */
        STATUS_NORMAL, STATUS_NULL, STATUS_ERROR
    }

    // 判断是否正在加载
    private boolean mIsLoading = false;
    // 开启或者关闭加载更多功能
    private boolean mEnableLoad = false;
    //是否加载完全部
    private boolean mIsLoadedAll = false;
    //刷新完成是否自动回到顶部
    private boolean mAutoToFirst = true;

    private Status mStatus = Status.STATUS_NORMAL;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private BaseAutoAdapter mAdapter;
    private OnLoadListener mOnLoadListener;

    private LoadErrorView mLoadErrorView;
    private CharSequence mLoadTextNull;
    private CharSequence mLoadTextError;
    private int mLoadTextColor;
    private Drawable mLoadImageNull;
    private Drawable mLoadImageError;
    private ColorStateList mLoadButtonTextColors;
    private Drawable mLoadButtonBackground;
    private AutoRecyclerView.OnScrollListener mOnScrollListener;

    public AutoRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public AutoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoRecyclerView);
        this.init(context, a);
        a.recycle();
    }

    private void init(Context context, TypedArray a) {
        mLoadTextNull = a.getString(R.styleable.AutoRecyclerView_loadTextNull);
        if (mLoadTextNull == null) {
            mLoadTextNull = context.getString(R.string.no_data);
        }
        mLoadTextError = a.getString(R.styleable.AutoRecyclerView_loadTextError);
        if (mLoadTextError == null) {
            mLoadTextError = context.getString(R.string.loaded_fail);
        }
        mLoadTextColor = a.getColor(R.styleable.AutoRecyclerView_loadTextColor, 0xFF666666);
        mLoadImageNull = a.getDrawable(R.styleable.AutoRecyclerView_loadImageNull);
        if (mLoadImageNull == null) {
            mLoadImageNull = ContextCompat.getDrawable(context, R.drawable.ic_load_error);
        }
        mLoadImageError = a.getDrawable(R.styleable.AutoRecyclerView_loadImageError);
        if (mLoadImageError == null) {
            mLoadImageError = ContextCompat.getDrawable(context, R.drawable.ic_load_error);
        }
        mLoadButtonTextColors = a.getColorStateList(R.styleable.AutoRecyclerView_loadButtonTextColor);
        mLoadButtonBackground = a.getDrawable(R.styleable.AutoRecyclerView_loadButtonBackground);
        int swipeRefreshColor = a.getColor(R.styleable.AutoRecyclerView_swipeRefreshColor, -1);
        if (swipeRefreshColor != -1) {
            setColorSchemeColors(swipeRefreshColor);
        }
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutParams(layoutParams);
        mRecyclerView.addOnScrollListener(onScrollListener);
        addView(mRecyclerView);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mLoadErrorView != null && mLoadErrorView.getVisibility() == VISIBLE) {
            this.mLoadErrorView.measure(View.MeasureSpec.makeMeasureSpec(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), View.MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mLoadErrorView != null && mLoadErrorView.getVisibility() == VISIBLE) {
            View child = this.mLoadErrorView;
            int childLeft = this.getPaddingLeft();
            int childTop = this.getPaddingTop();
            int childWidth = this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();
            int childHeight = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    /**
     * 设置滑动监听
     * @param onScrollListener 滑动监听
     */
    public void setOnScrollListener(AutoRecyclerView.OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (mEnableLoad && !mIsLoading && !mIsLoadedAll
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                    && mLayoutManager != null && recyclerView.getAdapter() != null
                    && mLayoutManager.findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 2) {
                onLoad();
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrolled(AutoRecyclerView.this, dx, dy);
            }
        }
    };

    private void onLoad() {
        mIsLoading = true;
        if (mOnLoadListener != null) {
            mOnLoadListener.onLoad();
        }
    }

    /**
     * 当数据加载完成时调用本方法，通知控件数据已加载完成
     * @see #onLoadComplete(boolean, boolean)
     */
    public void onLoadComplete(boolean isLoadedAll) {
        this.onLoadComplete(isLoadedAll, true);
    }

    /**
     * 当数据加载完成时调用本方法，通知控件数据已加载完成
     *
     * @param isLoadedAll      是否加载完所有数据
     * @param isRefreshAdapter 是否刷新适配器
     */
    public void onLoadComplete(boolean isLoadedAll, boolean isRefreshAdapter) {
        mIsLoading = false;
        mIsLoadedAll = isLoadedAll;

        if (mAdapter != null) {
            mAdapter.setLoadedAll(isLoadedAll);
            if (isRefreshAdapter) {
                mAdapter.notifyDataSetChanged();
            }
        }

        if (isRefreshing()) {
            if (mAutoToFirst) {
                this.mRecyclerView.scrollToPosition(0);
            }
            setRefreshing(false);
        }
    }

    /**
     * 状态变更
     * @param status 变更后的状态{@link Status}
     */
    public void changeStatus(Status status) {
        if (mStatus != status) {
            if (status == Status.STATUS_NORMAL) {
                showRecyclerView();
                hideLoadErrorView();
            } else {
                if (mAdapter == null || mAdapter.getItemCount() == 0) {
                    hideRecyclerView();
                    showLoadErrorView();
                    if (status == Status.STATUS_NULL) {
                        mLoadErrorView.setErrText(mLoadTextNull);
                        mLoadErrorView.setImageDrawable(mLoadImageNull);
                    } else {
                        mLoadErrorView.setErrText(mLoadTextError);
                        mLoadErrorView.setImageDrawable(mLoadImageError);
                    }
                }
            }
            mStatus = status;
        }
    }


    /************get set*************/

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    public void setAdapter(BaseAutoAdapter adapter) {
        if (adapter != null) {
            mAdapter = adapter;
            mAdapter.setShowLoadView(mEnableLoad);
            mRecyclerView.setAdapter(adapter);
        }
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.setEnabled(enableRefresh);
    }

    public boolean enableLoad() {
        return mEnableLoad;
    }

    public void setEnableLoad(boolean enableLoad) {
        if (this.mEnableLoad == enableLoad) return;

        this.mEnableLoad = enableLoad;
        if (mAdapter != null) {
            mAdapter.setShowLoadView(enableLoad);
        }
    }

    public boolean isAutoToFirst() {
        return mAutoToFirst;
    }

    public void setAutoToFirst(boolean mAutoToFirst) {
        this.mAutoToFirst = mAutoToFirst;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            this.mLayoutManager = (LinearLayoutManager) layoutManager;
        }
        mRecyclerView.setLayoutManager(layoutManager);

    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.mOnLoadListener = onLoadListener;
        this.setOnRefreshListener(onLoadListener);
        this.setEnableLoad(true);
    }

    private void showRecyclerView() {
        if (mRecyclerView.getVisibility() != VISIBLE) {
            mRecyclerView.setVisibility(VISIBLE);
        }
    }

    private void hideRecyclerView() {
        if (mRecyclerView.getVisibility() == VISIBLE) {
            mRecyclerView.setVisibility(GONE);
        }
    }

    private void initLoadErrorView() {
        if (mLoadErrorView == null) {
            mLoadErrorView = new LoadErrorView(getContext());
            mLoadErrorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mLoadErrorView.setErrTextColor(mLoadTextColor);
            mLoadErrorView.setBtnText(getContext().getString(R.string.load_again));
            if (mLoadButtonTextColors != null) {
                mLoadErrorView.setBtnTextColor(mLoadButtonTextColors);
            }
            if (mLoadButtonBackground != null) {
                mLoadErrorView.setBtnBackground(mLoadButtonBackground);
            }
            mLoadErrorView.setBtnOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnLoadListener != null) {
                        setRefreshing(true);
                        mOnLoadListener.onRefresh();
                    }
                }
            });
            this.addView(mLoadErrorView);
        }
    }

    private void showLoadErrorView() {
        if (mLoadErrorView == null) {
            initLoadErrorView();
        } else if (mLoadErrorView.getVisibility() != VISIBLE) {
            mLoadErrorView.setVisibility(VISIBLE);
        }
    }

    private void hideLoadErrorView() {
        if (mLoadErrorView != null && mLoadErrorView.getVisibility() == VISIBLE) {
            mLoadErrorView.setVisibility(GONE);
        }
    }

    public CharSequence getLoadTextNull() {
        return mLoadTextNull;
    }

    /**
     * 设置加载数据为空时的提示文字
     *
     * @param loadTextNull 加载数据为空时的提示文字
     */
    public void setLoadTextNull(CharSequence loadTextNull) {
        this.mLoadTextNull = loadTextNull;
        if (mLoadErrorView != null && mStatus == Status.STATUS_NULL) {
            mLoadErrorView.setErrText(loadTextNull);
        }
    }

    public CharSequence getLoadTextError() {
        return mLoadTextError;
    }

    /**
     * 设置加载出错时的提示文字
     *
     * @param loadTextError 加载出错时的提示文字
     */
    public void setLoadTextError(CharSequence loadTextError) {
        this.mLoadTextError = loadTextError;
        if (mLoadErrorView != null && mStatus == Status.STATUS_ERROR) {
            mLoadErrorView.setErrText(loadTextError);
        }
    }

    public Drawable getLoadImageNull() {
        return mLoadImageNull;
    }

    /**
     * 设置加载数据为空时提示图片的Resource
     *
     * @param loadImageNullResource 加载数据为空时提示图片的Resource
     */
    public void setLoadImageNullResource(@DrawableRes int loadImageNullResource) {
        if (-1 != loadImageNullResource) {
            this.mLoadImageNull = ContextCompat.getDrawable(getContext(), loadImageNullResource);
            if (mLoadErrorView != null) {
                mLoadErrorView.setImageDrawable(this.mLoadImageNull);
            }
        }
    }

    public Drawable getLoadImageError() {
        return mLoadImageError;
    }

    /**
     * 设置加载出错的提示图片的Resource
     *
     * @param loadImageErrorResource 加载出错的提示图片的Resource
     */
    public void setLoadImageErrorResource(int loadImageErrorResource) {
        if (-1 != loadImageErrorResource) {
            this.mLoadImageError = ContextCompat.getDrawable(getContext(), loadImageErrorResource);
            ;
            if (mLoadErrorView != null) {
                mLoadErrorView.setImageDrawable(mLoadImageError);
            }
        }
    }

    /**
     * 加载接口
     * {@link OnLoadListener#onRefresh} 刷新回调；当组件触发刷新条件时，回调此接口通知监听者
     * {@link OnLoadListener#onLoad()} 加载更多回调；当组件触发加载更多条件时，回调此接口通知监听者
     */
    public interface OnLoadListener extends OnRefreshListener {
        /**
         * 加载更多的回调接口
         */
        void onLoad();
    }

    public interface OnScrollListener {
        void onScrolled(@NonNull AutoRecyclerView recyclerView, int dx, int dy);
    }
}
