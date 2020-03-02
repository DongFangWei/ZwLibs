package com.dongfangwei.zwlibs.base.recycler;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.dongfangwei.zwlibs.base.R;


public abstract class BaseAutoAdapter extends BaseAdapter<BaseViewHolder> implements View.OnClickListener {
    protected static final int ITEM_TYPE_LOAD = -999;
    private boolean mIsLoadedAll = false;
    private boolean mIsShowLoadView = true;

    @StringRes
    private int mLoadedRes = R.string.loaded;
    @StringRes
    private int mLoadingRes = R.string.loading;

    protected BaseAutoAdapter(Context context) {
        super(context);
    }

    public BaseAutoAdapter(Context context, boolean isShowLoadView) {
        super(context);
        this.mIsShowLoadView = isShowLoadView;
    }

    @NonNull
    @Override
    public final BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        if (type == ITEM_TYPE_LOAD) {
            return createLoadHolder();
        } else {
            return onCreateItemView(parent, type);
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
        if (viewHolder instanceof LoadHolder) {
            LoadHolder loadHolder = (LoadHolder) viewHolder;
            if (mIsLoadedAll) {
                loadHolder.hideProgressBar();
                loadHolder.setText(mLoadedRes);
            } else {
                loadHolder.setText(mLoadingRes);
                loadHolder.showProgressBar();
            }
        } else {
            onBindItemView(viewHolder, position);
        }
    }

    @Override
    public final int getItemCount() {
        int count = getItemViewCount();
        return mIsShowLoadView && count > 0 ? count + 1 : count;
    }

    @Override
    public final int getItemViewType(int position) {
        if (mIsShowLoadView && position == getItemCount() - 1) {
            return ITEM_TYPE_LOAD;
        } else {
            return getItemType(position);
        }
    }

    protected abstract BaseViewHolder onCreateItemView(@NonNull ViewGroup parent, int type);

    protected abstract void onBindItemView(@NonNull BaseViewHolder holder, int position);

    protected abstract int getItemViewCount();

    protected abstract int getItemType(int position);

    public boolean isLoadedAll() {
        return mIsLoadedAll;
    }

    public void setLoadedAll(boolean isLoadedAll) {
        this.mIsLoadedAll = isLoadedAll;
    }

    public boolean isShowLoadView() {
        return mIsShowLoadView;
    }

    public void setShowLoadView(boolean isShowLoadView) {
        this.mIsShowLoadView = isShowLoadView;
    }

    public void setLoadedRes(int loadedRes) {
        if (this.mLoadedRes != loadedRes)
            this.mLoadedRes = loadedRes;
    }

    public void setLoadingRes(int loadingRes) {
        if (this.mLoadingRes != loadingRes)
            this.mLoadingRes = loadingRes;
    }

    public void setOnItemClickListener(BaseAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private LoadHolder createLoadHolder() {
        int dp_10 = (int) (mContext.getResources().getDisplayMetrics().density * 10);
        LinearLayout linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setPadding(0, dp_10, 0, dp_10);
        linearLayout.setGravity(Gravity.CENTER);
        return new LoadHolder(linearLayout);
    }

    public class LoadHolder extends BaseViewHolder {
        private TextView mTextView;
        private ProgressBar mProgressBar;

        private LoadHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            Context context = itemView.getContext();
            float dp = context.getResources().getDisplayMetrics().density;
            int barSize = (int) (dp * 36);
            mProgressBar = new ProgressBar(context);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(barSize, barSize);
            mProgressBar.setLayoutParams(barParams);

            mTextView = new TextView(context);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mTextView.setLayoutParams(textParams);
            mTextView.setLines(1);
            int dp_5 = (int) (5 * dp);
            mTextView.setPadding(dp_5, 0, dp_5, 0);
            itemView.addView(mProgressBar);
            itemView.addView(mTextView);
        }

        public void setText(int textRes) {
            mTextView.setText(textRes);
        }

        public void setText(CharSequence text) {
            mTextView.setText(text);
        }

        void showProgressBar() {
            if (mProgressBar.getVisibility() != View.VISIBLE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        void hideProgressBar() {
            if (mProgressBar.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }
}
