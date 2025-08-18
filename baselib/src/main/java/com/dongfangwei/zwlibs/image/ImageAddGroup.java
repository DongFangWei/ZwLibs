package com.dongfangwei.zwlibs.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dongfangwei.zwlibs.R;
import com.dongfangwei.zwlibs.glide.GlideApp;


/**
 * 可添加的图片组组件
 *
 * @author ZhangWei
 * @date Create in 2021-09-03 15:11
 */
public class ImageAddGroup extends ImageGroup {
    private final ImageView mAddView;
    private ImageSelectorTypeDialog mImageSelectorTypeDialog;
    private OnAddImageListener onAddImageListener;

    public ImageAddGroup(Context context) {
        this(context, null);
    }

    public ImageAddGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageAddGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.ImageGroupStyle_Add);
    }

    public ImageAddGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageAddGroup, defStyleAttr, defStyleRes);
        Drawable addViewIcon = a.getDrawable(R.styleable.ImageAddGroup_addViewIcon);
        Drawable addViewBackground = a.getDrawable(R.styleable.ImageAddGroup_addViewBackground);
        a.recycle();
        mAddView = new ImageView(context);
        mAddView.setId(R.id.imageAddBtn);
        mAddView.setScaleType(ImageView.ScaleType.CENTER);
        if (addViewIcon != null) {
            mAddView.setImageDrawable(addViewIcon);
        }
        if (addViewBackground != null) {
            mAddView.setBackground(addViewBackground);
        }
        mAddView.setOnClickListener(this);
        addView(mAddView);
    }

    @Override
    protected void addImageToView(Object image) {
        ImageView imageView = createChild();
        imageView.setTag(VIEW_TAG_DATA, image);
        int childCount = getChildCount();
        addView(imageView, childCount - 1);
        if (childCount >= getChildMaxCount()) {
            removeView(mAddView);
        }
        GlideApp.with(getContext())
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(getTransform())
                .into(imageView);
    }

    @Override
    public void clearImage() {
        super.clearImage();
        addView(mAddView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageAddBtn) {
            showImageSelectorTypeDialog();
        } else if (id == R.id.image_selector_type_camera_ll) {
            if (onAddImageListener != null) {
                onAddImageListener.onOpenCamera();
            }
            dismissImageSelectorTypeDialog();
        } else if (id == R.id.image_selector_type_photo_tv) {
            if (onAddImageListener != null) {
                onAddImageListener.onOpenAlbum();
            }
            dismissImageSelectorTypeDialog();
        } else if (id == R.id.dialog_cancel_btn) {
            dismissImageSelectorTypeDialog();
        } else {
            super.onClick(v);
        }
    }

    public void setOnAddImageListener(OnAddImageListener onAddImageListener) {
        this.onAddImageListener = onAddImageListener;
    }

    private void showImageSelectorTypeDialog() {
        if (mImageSelectorTypeDialog == null) {
            mImageSelectorTypeDialog = new ImageSelectorTypeDialog(getContext(), this);
            mImageSelectorTypeDialog.show();
        } else if (!mImageSelectorTypeDialog.isShowing()) {
            mImageSelectorTypeDialog.show();
        }
    }

    private void dismissImageSelectorTypeDialog() {
        if (mImageSelectorTypeDialog != null && mImageSelectorTypeDialog.isShowing()) {
            mImageSelectorTypeDialog.dismiss();
        }
    }

    /**
     * 添加图片监听
     */
    public interface OnAddImageListener {
        /**
         * 打开摄像头
         */
        void onOpenCamera();

        /**
         * 打开相册
         */
        void onOpenAlbum();
    }
}
