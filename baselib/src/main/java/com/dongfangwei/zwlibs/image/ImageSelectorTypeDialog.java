package com.dongfangwei.zwlibs.image;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.dongfangwei.zwlibs.R;


/**
 * 对话框 - 图片选择器类型
 *
 * @author zhangwei
 * @date 2025/8/18 11:23
 */
public class ImageSelectorTypeDialog extends Dialog {
    private final View.OnClickListener onClickListener;

    public ImageSelectorTypeDialog(@NonNull Context context, View.OnClickListener onClickListener) {
        super(context, R.style.Dialog);
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            View parent = window.getDecorView();
            parent.setPadding(0, parent.getPaddingTop(), 0, parent.getPaddingBottom());
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.BOTTOM;
            window.setWindowAnimations(R.style.Animation_Popup_Bottom);
        }
        setContentView(R.layout.dialog_image_selector);
        findViewById(R.id.image_selector_type_camera_ll).setOnClickListener(onClickListener);
        findViewById(R.id.image_selector_type_photo_tv).setOnClickListener(onClickListener);
        findViewById(R.id.dialog_cancel_btn).setOnClickListener(onClickListener);
    }
}
