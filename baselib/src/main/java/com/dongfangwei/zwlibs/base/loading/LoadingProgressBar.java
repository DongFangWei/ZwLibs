package com.dongfangwei.zwlibs.base.loading;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.dongfangwei.zwlibs.base.R;

/**
 * 表示加载中状态的进度条对话框
 */
public class LoadingProgressBar extends Dialog {
    public LoadingProgressBar(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.setContentView(new ProgressBar(context));
        this.setCanceledOnTouchOutside(false);
    }
}
