package com.dongfangwei.zwlibs.base.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 标记View，可以显示数字用来表示未读消息
 */
public class BadgeView extends TextView {
    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextSize(10);
        setTextColor(0xFFFFFFFF);
        setBackgroundColor(0xFFED0000);
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        this.setSize(this.getTextSize());
    }

    private void setSize(float textSize) {
        int h = (int) (textSize / 2f);
        int v = (int) (textSize / 10f);
        setPadding(h, v, h, v);
        setBackgroundRadius(textSize);
    }

    @Override
    public void setBackground(Drawable background) {
        if (background instanceof GradientDrawable) {
            super.setBackground(background);
        } else {
            throw new RuntimeException("背景的Drawable类型必须是GradientDrawable");
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        Drawable background = getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(color);
        } else {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(color);
            drawable.setCornerRadius(this.getTextSize());
            background = drawable;
        }
        setBackground(background);
    }

    private void setBackgroundRadius(float radius) {
        Drawable background = getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setCornerRadius(radius);
        }
    }
}
