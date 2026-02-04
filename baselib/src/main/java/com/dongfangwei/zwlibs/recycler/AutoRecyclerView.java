package com.dongfangwei.zwlibs.recycler;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dongfangwei.zwlibs.R;

/**
 * 自动加载的RecyclerView，配合{@link OnLoadListener}与{@link BaseAutoAdapter}使用实现自动加载
 */
public class AutoRecyclerView extends SwipeRefreshLayout {
    /* 加载状态 */
    /**
     * 空闲
     *
     * @see #onLoadComplete(boolean)
     */
    protected static final int LOAD_STATE_IDLE = 0;
    /**
     * 加载中
     *
     * @see #isRefreshing()
     * @see #onLoad()
     */
    protected static final int LOAD_STATE_LOADING = 1;
    /**
     * 加载完成（已经加载完了全部数据）
     *
     * @see #onLoadComplete(boolean)
     */
    protected static final int LOAD_STATE_LOADED = 2;
    /**
     * 加载出错（没有成功加载数据，并不是没有数据可加载）
     *
     * @see #onLoadError()
     */
    protected static final int LOAD_STATE_ERROR = 4;

    /**
     * 开启或者关闭加载更多功能
     */
    private boolean mEnableLoad = false;
    /**
     * 当前加载状态
     */
    private int mLoadState = LOAD_STATE_IDLE;
    /**
     * 刷新完成是否自动回到顶部
     */
    private boolean mAutoToFirst = true;

    /**
     * 显示内容的RecyclerView
     */
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    /**
     * 内容适配器
     */
    private BaseAutoAdapter mAdapter;
    /**
     * 加载回调监听
     */
    private OnLoadListener mOnLoadListener;

    /**
     * 加载出错提示组件
     */
    private LoadErrorView mLoadErrorView;
    /**
     * 加载提示文本 - 没有数据
     */
    private CharSequence mLoadTextNull;
    /**
     * 加载提示文本 - 加载出错
     */
    private CharSequence mLoadTextError;
    /**
     * 加载提示文本的颜色
     */
    private int mLoadTextColor;
    /**
     * 加载提示文本的上边距
     */
    private int mLoadTextMarginTop;
    /**
     * 加载提示图片 - 没有数据
     */
    private Drawable mLoadImageNull;
    /**
     * 加载提示图片 - 加载出错
     */
    private Drawable mLoadImageError;
    /**
     * 加载按钮的文本颜色
     */
    private ColorStateList mLoadButtonTextColors;
    /**
     * 加载按钮的背景图
     */
    private Drawable mLoadButtonBackground;
    /**
     * 加载按钮的显示样式
     */
    private int mLoadButtonVisibility;
    /**
     * 加载按钮的上边距
     */
    private int mLoadButtonMarginTop;
    /**
     * 滚动监听
     */
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

    /**
     * 初始化
     *
     * @param context 上下文
     * @param a       样式
     */
    protected void init(Context context, TypedArray a) {
        mLoadTextNull = a.getString(R.styleable.AutoRecyclerView_loadTextNull);
        if (mLoadTextNull == null) {
            mLoadTextNull = context.getString(R.string.no_data);
        }
        mLoadTextError = a.getString(R.styleable.AutoRecyclerView_loadTextError);
        if (mLoadTextError == null) {
            mLoadTextError = context.getString(R.string.loaded_fail);
        }
        mLoadTextColor = a.getColor(R.styleable.AutoRecyclerView_loadTextColor, 0xFF666666);
        mLoadTextMarginTop = a.getDimensionPixelOffset(R.styleable.AutoRecyclerView_loadTextMarginTop, -1);
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
        mLoadButtonMarginTop = a.getDimensionPixelOffset(R.styleable.AutoRecyclerView_loadButtonMarginTop, -1);
        int loadButtonVisibility = a.getInteger(R.styleable.AutoRecyclerView_loadButtonVisibility, 0);
        int swipeRefreshColor = a.getColor(R.styleable.AutoRecyclerView_swipeRefreshColor, -1);
        if (swipeRefreshColor != -1) {
            setColorSchemeColors(swipeRefreshColor);
        }
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutParams(layoutParams);
        mRecyclerView.addOnScrollListener(mOnScrollRvListener);
        setLoadButtonVisibility(loadButtonVisibility);
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
     *
     * @param onScrollListener 滑动监听
     */
    public void setOnScrollListener(AutoRecyclerView.OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    /**
     * RecyclerView滚动监听
     */
    private final RecyclerView.OnScrollListener mOnScrollRvListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (mEnableLoad && mLoadState == LOAD_STATE_IDLE
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                    && mLayoutManager != null && recyclerView.getAdapter() != null
                    && mLayoutManager.findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 2) {
                onLoad();
            }

            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(AutoRecyclerView.this, newState);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrolled(AutoRecyclerView.this, dx, dy);
            }
        }
    };

    /**
     * RecyclerView滚动到顶部
     */
    public void scrollToTop() {
        if (this.mRecyclerView != null) {
            this.mRecyclerView.scrollToPosition(0);
        }
    }

    /**
     * 加载数据
     */
    private void onLoad() {
        mLoadState = LOAD_STATE_LOADING;
        if (mOnLoadListener != null) {
            mOnLoadListener.onLoad();
        }
    }

    /**
     * 当数据加载完成时调用本方法，通知控件数据已加载完成
     * 控件将刷新显示内容
     *
     * @param isLoadedAll 是否加载完所有数据
     */
    public void onLoadComplete(boolean isLoadedAll) {
        mLoadState = isLoadedAll ? LOAD_STATE_LOADED : LOAD_STATE_IDLE;

        onChangeStatus();

        if (isRefreshing()) {
            if (mAutoToFirst) {
                this.mRecyclerView.scrollToPosition(0);
            }
            setRefreshing(false);
        }
    }

    /**
     * 当数据加载出错试调用本方法
     */
    public void onLoadError() {
        mLoadState = LOAD_STATE_ERROR;
        onChangeStatus();
        if (isRefreshing()) {
            setRefreshing(false);
        }
    }

    /**
     * 根据状态变更显示
     */
    protected void onChangeStatus() {
        if (isNullData()) {
            hideRecyclerView();
            showLoadErrorView();
            changLoadErrorView();
        } else {
            showRecyclerView();
            hideLoadErrorView();
            mAdapter.setLoadedState(mLoadState);
        }
    }

    /**
     * 改变加载错误信息显示组件
     */
    protected void changLoadErrorView() {
        if (mLoadState == LOAD_STATE_ERROR) {
            mLoadErrorView.setErrText(mLoadTextError);
            mLoadErrorView.setImageDrawable(mLoadImageError);
        } else {
            mLoadErrorView.setErrText(mLoadTextNull);
            mLoadErrorView.setImageDrawable(mLoadImageNull);
        }
    }

    /**
     * 当前是否没有数据
     *
     * @return 是：true
     */
    protected boolean isNullData() {
        return mAdapter == null || mAdapter.isEmptyData();
    }

    /************get set*************/

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    public void setAdapter(BaseAutoAdapter adapter) {
        if (adapter != null) {
            mAdapter = adapter;
            mAdapter.setShowLoadView(mEnableLoad);
            mAdapter.setOnReloadListener(() -> {
                onLoad();
                return true;
            });
            mRecyclerView.setAdapter(adapter);
        }
    }

    /**
     * 设置刷新功能的启用状态
     *
     * @param enableRefresh 启用刷新：true，禁用刷新：false
     */
    public void setEnableRefresh(boolean enableRefresh) {
        this.setEnabled(enableRefresh);
    }

    /**
     * 获取加载功能的启用状态
     *
     * @return 启用：true
     */
    public boolean enableLoad() {
        return mEnableLoad;
    }

    /**
     * 设置加载功能的启用状态
     *
     * @param enableLoad 启用加载：true，禁用刷新：false
     */
    public void setEnableLoad(boolean enableLoad) {
        if (this.mEnableLoad == enableLoad) return;

        this.mEnableLoad = enableLoad;
        if (mAdapter != null) {
            mAdapter.setShowLoadView(enableLoad);
        }
    }

    public int getLoadState() {
        return mLoadState;
    }

    protected void setLoadState(int mLoadState) {
        this.mLoadState = mLoadState;
    }

    /**
     * 是否自动回到顶部
     */
    public boolean isAutoToFirst() {
        return mAutoToFirst;
    }

    /**
     * 设置加载完是否自动回到顶部
     *
     * @param autoToFirst 自动回到顶部
     */
    public void setAutoToFirst(boolean autoToFirst) {
        this.mAutoToFirst = autoToFirst;
    }

    /**
     * 设置RecyclerView的LayoutManager
     *
     * @param layoutManager 布局管理者
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            this.mLayoutManager = (LinearLayoutManager) layoutManager;
        }
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 设置加载监听
     *
     * @param onLoadListener 加载监听
     */
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.mOnLoadListener = onLoadListener;
        this.setOnRefreshListener(onLoadListener);
        this.setEnableLoad(true);
    }

    /**
     * 显示RecyclerView
     */
    protected void showRecyclerView() {
        if (mRecyclerView.getVisibility() != VISIBLE) {
            mRecyclerView.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏RecyclerView
     */
    protected void hideRecyclerView() {
        if (mRecyclerView.getVisibility() == VISIBLE) {
            mRecyclerView.setVisibility(GONE);
        }
    }

    /**
     * 初始化加载错误提示View
     */
    private void initLoadErrorView() {
        if (mLoadErrorView == null) {
            mLoadErrorView = new LoadErrorView(getContext());
            mLoadErrorView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mLoadErrorView.setErrTextColor(mLoadTextColor);
            if (mLoadTextMarginTop != -1) {
                mLoadErrorView.setTextMarginTop(mLoadTextMarginTop);
            }
            mLoadErrorView.setBtnText(getContext().getString(R.string.load_again));
            if (mLoadButtonTextColors != null) {
                mLoadErrorView.setBtnTextColor(mLoadButtonTextColors);
            }
            if (mLoadButtonBackground != null) {
                mLoadErrorView.setBtnBackground(mLoadButtonBackground);
            }
            if (mLoadButtonMarginTop != -1) {
                mLoadErrorView.setBtnMarginTop(mLoadButtonMarginTop);
            }
            mLoadErrorView.setBtnVisibility(mLoadButtonVisibility);
            mLoadErrorView.setBtnOnClickListener(v -> {
                if (mOnLoadListener != null) {
                    setRefreshing(true);
                    mOnLoadListener.onRefresh();
                }
            });
            this.addView(mLoadErrorView);
        }
    }

    /**
     * 显示加载提示view
     */
    protected void showLoadErrorView() {
        if (mLoadErrorView == null) {
            initLoadErrorView();
        } else if (mLoadErrorView.getVisibility() != VISIBLE) {
            mLoadErrorView.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏加载提示view
     */
    protected void hideLoadErrorView() {
        if (mLoadErrorView != null && mLoadErrorView.getVisibility() == VISIBLE) {
            mLoadErrorView.setVisibility(GONE);
        }
    }

    /**
     * 获取加载数据为空时的提示文字
     */
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
        if (mLoadErrorView != null && isNullData()) {
            mLoadErrorView.setErrText(loadTextNull);
        }
    }

    /**
     * 获取加载出错时的提示文字
     */
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
        if (mLoadErrorView != null && mLoadState == LOAD_STATE_ERROR) {
            mLoadErrorView.setErrText(loadTextError);
        }
    }

    /**
     * 获取数据为空时的提示图标
     */
    public Drawable getLoadImageNull() {
        return mLoadImageNull;
    }

    /**
     * 设置加载提示文本的上边距
     *
     * @param loadTextMarginTop 加载提示文本的上边距
     */
    public void setLoadTextMarginTop(int loadTextMarginTop) {
        if (this.mLoadTextMarginTop != loadTextMarginTop) {
            this.mLoadTextMarginTop = loadTextMarginTop;
            if (mLoadErrorView != null) {
                mLoadErrorView.setTextMarginTop(loadTextMarginTop);
            }
        }
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

    /**
     * 获取加载出错时的提示图标
     */
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
            if (mLoadErrorView != null) {
                mLoadErrorView.setImageDrawable(mLoadImageError);
            }
        }
    }

    /**
     * 获取加载按钮的背景图
     */
    public Drawable getLoadButtonBackground() {
        return mLoadButtonBackground;
    }

    /**
     * 设置加载按钮的背景图
     *
     * @param loadButtonBackground 背景图
     */
    public void setLoadButtonBackground(Drawable loadButtonBackground) {
        this.mLoadButtonBackground = loadButtonBackground;
        if (mLoadErrorView != null) {
            mLoadErrorView.setBtnBackground(loadButtonBackground);
        }
    }

    /**
     * 设置加载按钮的上边距
     *
     * @param loadButtonMarginTop 加载按钮的上边距
     */
    public void setLoadButtonMarginTop(int loadButtonMarginTop) {
        if (this.mLoadButtonMarginTop != loadButtonMarginTop) {
            this.mLoadButtonMarginTop = loadButtonMarginTop;
            if (mLoadErrorView != null) {
                mLoadErrorView.setBtnMarginTop(loadButtonMarginTop);
            }
        }
    }

    /**
     * 设置加载出错（包含数据为空）时，加载按钮的可见性状态
     *
     * @param loadButtonVisibility 可见性状态
     */
    public void setLoadButtonVisibility(int loadButtonVisibility) {
        if (this.mLoadButtonVisibility != loadButtonVisibility) {
            this.mLoadButtonVisibility = loadButtonVisibility;
            if (mLoadErrorView != null) {
                mLoadErrorView.setBtnVisibility(loadButtonVisibility);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //通知父层ViewGroup不要拦截点击事件
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 加载回调监听
     * <p>
     * {@link OnLoadListener#onRefresh()} 刷新回调；当组件触发刷新条件时，回调此接口通知监听者
     * <p>
     * {@link OnLoadListener#onLoad()} 加载更多回调；当组件触发加载更多条件时，回调此接口通知监听者
     */
    public interface OnLoadListener extends OnRefreshListener {
        /**
         * 加载更多的回调接口
         */
        void onLoad();
    }

    /**
     * 滚动监听
     */
    public interface OnScrollListener {
        void onScrolled(@NonNull AutoRecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(@NonNull AutoRecyclerView recyclerView, int newState);
    }
}
