package com.dongfangwei.zwlibs.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public static final int KEY_POSITION = -998;

    public BaseViewHolder(Context context, @LayoutRes int resId, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(resId, parent, false));
    }

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected Context getContext() {
        return this.itemView.getContext();
    }

    public void setPosition(int position) {
        this.itemView.setTag(KEY_POSITION, position);
    }

    protected void setPosition(int position, @NonNull View view) {
        view.setTag(KEY_POSITION, position);
    }

    public void setOnClickListener(@NonNull View.OnClickListener listener) {
        this.itemView.setOnClickListener(listener);
    }

    protected final <T extends View> T findViewById(@IdRes int viewRes) {
        return this.itemView.findViewById(viewRes);
    }

//    public static int getViewPosition(View view) {
//        if (view != null) {
//            Object o = view.getTag(KEY_POSITION);
//            if (o != null) {
//                return (int) o;
//            }
//        }
//        return -1;
//    }
}
