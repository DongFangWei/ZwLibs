package com.dongfangwei.zwlibs.base.recycler;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by IceMa on 2019/9/25.
 */
public abstract class BaseAdapter<T extends BaseViewHolder> extends RecyclerView.Adapter<T>
        implements View.OnClickListener {
    protected Context mContext;
    protected OnItemClickListener mOnItemClickListener;

    public BaseAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public abstract Object getItemViewData(int position);

    @Override
    public void onClick(View v) {
        try {
            int position = (int) v.getTag(BaseViewHolder.KEY_POSITION);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, position, getItemViewData(position));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * Item的点击监听
     */
    public interface OnItemClickListener {
        /**
         * 点击回调
         *
         * @param view     被点击的view
         * @param position 被点击的view的下标
         * @param data     被点击的view绑定的数据
         */
        void onItemClick(View view, int position, Object data);
    }
}
