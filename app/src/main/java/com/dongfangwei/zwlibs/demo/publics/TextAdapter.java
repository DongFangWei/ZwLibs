package com.dongfangwei.zwlibs.demo.publics;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dongfangwei.zwlibs.recycler.BaseAutoAdapter;
import com.dongfangwei.zwlibs.recycler.BaseViewHolder;
import com.dongfangwei.zwlibs.demo.R;

import java.util.List;

public class TextAdapter extends BaseAutoAdapter {
    private List<String> mData;

    public TextAdapter(Context context) {
        super(context);
    }

    public void setData(List<String> data) {
        if (data != null) {
            if (mData == null) {
                mData = data;
            } else {
                mData.addAll(data);
            }
        }
    }

    public void clearData() {
        if (mData != null) {
            mData.clear();
        }
    }

    @Override
    protected BaseViewHolder onCreateItemView(@NonNull ViewGroup parent, int type) {
        return new TextViewHolder(mContext, parent);
    }

    @Override
    protected void onBindItemView(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).setText(getItemViewData(position));
        }
    }

    @Override
    protected int getItemViewCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public String getItemViewData(int position) {
        return mData.get(position);
    }

    @Override
    protected int getItemType(int position) {
        return 0;
    }

    private static class TextViewHolder extends BaseViewHolder {

        public TextViewHolder(Context context, ViewGroup parent) {
            super(context, R.layout.item_main, parent);
        }

        public void setText(String text) {
            ((TextView) itemView).setText(text);
        }
    }
}
