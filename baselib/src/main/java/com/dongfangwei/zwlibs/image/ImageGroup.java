package com.dongfangwei.zwlibs.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.dongfangwei.zwlibs.R;
import com.dongfangwei.zwlibs.glide.GlideApp;
import com.dongfangwei.zwlibs.glide.RoundedCornersTransformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片组组件
 *
 * @author ZhangWei
 * @date Create in 2021-09-03 10:42
 */
public class ImageGroup extends ViewGroup implements View.OnClickListener {
    protected static final int VIEW_TAG_DATA = 545435445;
    /* 间隔大小 */
    /**
     * 组件垂直间隔
     */
    private int mDividerPaddingVertical;
    /**
     * 组件水平间隔
     */
    private int mDividerPaddingHorizontal;
    /* 子组件的内边距 */
    /**
     * 子组件的内边距-Start
     */
    private int mChildPaddingStart;
    /**
     * 子组件的内边距-End
     */
    private int mChildPaddingEnd;
    /**
     * 子组件的内边距-Top
     */
    private int mChildPaddingTop;
    /**
     * 子组件的内边距-Bottom
     */
    private int mChildPaddingBottom;
    /**
     * 子组件的最大数量
     */
    private int mChildMaxCount;
    /**
     * 子组件的每行的最大数量
     */
    private int mLineMaxCount;

    private Drawable mChildBackground;

    private OnClickChildListener onClickChildListener;
    /**
     * 图片列表
     */
    final private List<Object> mImages = new ArrayList<>();

    private BitmapTransformation mTransform;

    public ImageGroup(Context context) {
        this(context, null);
    }

    public ImageGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.ImageGroupStyle);
    }

    public ImageGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        int dp_5 = (int) (context.getResources().getDisplayMetrics().density * 5);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageGroup, defStyleAttr, defStyleRes);
        int childPadding = a.getDimensionPixelSize(R.styleable.ImageGroup_childPadding, 0);
        int childPaddingStart = a.getDimensionPixelSize(R.styleable.ImageGroup_childPaddingStart, childPadding);
        int childPaddingEnd = a.getDimensionPixelSize(R.styleable.ImageGroup_childPaddingEnd, childPadding);
        int childPaddingTop = a.getDimensionPixelSize(R.styleable.ImageGroup_childPaddingTop, childPadding);
        int childPaddingBottom = a.getDimensionPixelSize(R.styleable.ImageGroup_childPaddingBottom, childPadding);

        int dividerPadding = a.getDimensionPixelSize(R.styleable.ImageGroup_dividerPadding, dp_5);
        int dividerPaddingVertical = a.getDimensionPixelSize(R.styleable.ImageGroup_dividerPaddingVertical, dividerPadding);
        int dividerPaddingHorizontal = a.getDimensionPixelSize(R.styleable.ImageGroup_dividerPaddingHorizontal, dividerPadding);

        Drawable childBackground = a.getDrawable(R.styleable.ImageGroup_childBackground);
        int lineMaxCount = a.getInt(R.styleable.ImageGroup_lineMaxCount, 3);
        int childMaxCount = a.getInt(R.styleable.ImageGroup_childMaxCount, lineMaxCount << 1);

        a.recycle();

        setChildPadding(childPaddingStart, childPaddingTop, childPaddingEnd, childPaddingBottom);
        setDividerPadding(dividerPaddingHorizontal, dividerPaddingVertical);
        if (childBackground != null) {
            setChildBackground(childBackground);
        }
        setLineMaxCount(lineMaxCount);
        setChildMaxCount(childMaxCount);
        setTransform(new RoundedCornersTransformation(dp_5));
    }

    private void setChildPadding(int childPaddingStart, int childPaddingTop,
                                 int childPaddingEnd, int childPaddingBottom) {
        this.mChildPaddingStart = childPaddingStart;
        this.mChildPaddingTop = childPaddingTop;
        this.mChildPaddingEnd = childPaddingEnd;
        this.mChildPaddingBottom = childPaddingBottom;
    }

    /**
     * 设置子组件之间的间隔
     *
     * @param dividerPaddingHorizontal 水平间隔大小
     * @param dividerPaddingVertical   垂直间隔大小
     */
    public void setDividerPadding(int dividerPaddingHorizontal, int dividerPaddingVertical) {
        this.mDividerPaddingHorizontal = dividerPaddingHorizontal;
        this.mDividerPaddingVertical = dividerPaddingVertical;
    }

    public int getChildMaxCount() {
        return mChildMaxCount;
    }

    public void setChildMaxCount(int childMaxCount) {
        this.mChildMaxCount = childMaxCount;
    }

    public void setLineMaxCount(int lineMaxCount) {
        this.mLineMaxCount = lineMaxCount;
    }

    public void setChildBackground(Drawable childBackground) {
        this.mChildBackground = childBackground;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specWidthMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalArgumentException("ImageGroup spec width mode can not is UNSPECIFIED!");
        }
        int childSize = (specWidthSize - getPaddingStart() - getPaddingEnd() - mDividerPaddingHorizontal * (mLineMaxCount - 1)) / mLineMaxCount;
        int childSizeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.measure(childSizeMeasureSpec, childSizeMeasureSpec);
        }

        int childLine = count / mLineMaxCount;
        if (count % mLineMaxCount != 0) {
            childLine++;
        }
        if (specHeightMode == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int childHeight = childLine > 0 ? (childSize + mDividerPaddingVertical) * childLine - mDividerPaddingVertical : 0;
            int height = childHeight + getPaddingTop() + getPaddingBottom();
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int count = getChildCount();
        int lineIndex = 1;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            if (lineIndex < mLineMaxCount) {
                left += child.getMeasuredWidth() + mDividerPaddingHorizontal;
                lineIndex++;
            } else {
                lineIndex = 1;
                left = getPaddingLeft();
                top += child.getMeasuredHeight() + mDividerPaddingVertical;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (onClickChildListener != null) {
            onClickChildListener.onClickChild(this, v);
        }
    }


    public void setTransform(BitmapTransformation transform) {
        this.mTransform = transform;
    }

    public BitmapTransformation getTransform() {
        return mTransform;
    }

    public void setOnClickChildListener(OnClickChildListener onClickChildListener) {
        this.onClickChildListener = onClickChildListener;
    }

    /**
     * 获取当前图片数量
     *
     * @return 当前图片数量
     */
    public int getImagesCount() {
        return mImages.size();
    }

    /**
     * 获取图片信息
     *
     * @return 图片信息列表
     */
    public List<Object> getImages() {
        return mImages;
    }


    /**
     * 设置图片
     *
     * @param images 图片列表
     */
    public <T> void setImages(List<T> images) {
        clearImage();
        addImages(images);
    }

    /**
     * 添加图片
     *
     * @param image 图片
     */
    public boolean addImage(Object image) {
        if (mImages.size() < mChildMaxCount && (image instanceof File || image instanceof String)) {
            mImages.add(image);
            addImageToView(image);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加图片（唯一，如果已经存在相同的图片将忽略）
     *
     * @param image 图片
     * @return true：成功添加
     */
    public boolean addImageUnique(Object image) {
        if (mImages.size() < mChildMaxCount
                && (image instanceof File || image instanceof String)
                && checkImageUnique(image)) {
            mImages.add(image);
            addImageToView(image);
            return true;
        } else {
            return false;
        }
    }

    /**
     * （批量）添加图片
     *
     * @param images 图片列表
     * @return true：成功添加
     */
    public <T> boolean addImages(List<T> images) {
        if (images != null && mImages.size() < mChildMaxCount) {
            int addCount = Math.min(mChildMaxCount - mImages.size(), images.size());
            for (int i = 0; i < addCount; i++) {
                Object image = images.get(i);
                if (image instanceof File || image instanceof String) {
                    mImages.add(image);
                    addImageToView(image);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * （批量）添加图片（唯一，如果已经存在相同的图片将忽略）
     *
     * @param images 图片列表
     * @return true：成功添加
     */
    public <T> boolean addImagesUnique(List<T> images) {
        if (images != null && mImages.size() < mChildMaxCount) {
            int addCount = Math.min(mChildMaxCount - mImages.size(), images.size());
            for (int i = 0; i < addCount; i++) {
                Object image = images.get(i);
                if ((image instanceof File || image instanceof String) && checkImageUnique(image)) {
                    mImages.add(image);
                    addImageToView(image);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断图片是否已经存在
     *
     * @param image 图片
     * @return true：图片不存在，false：图片已存在
     */
    private boolean checkImageUnique(Object image) {
        if (image != null && !mImages.isEmpty()) {
            for (Object img : mImages) {
                if (img.equals(image)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 清空图片
     */
    public void clearImage() {
        mImages.clear();
        removeAllViews();
    }

    /**
     * 添加图片组件
     *
     * @param image 图片文件
     */
    protected void addImageToView(Object image) {
        ImageView imageView = createChild();
        imageView.setTag(VIEW_TAG_DATA, image);
        addView(imageView);
        GlideApp.with(getContext())
                .load(image)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(mTransform)
                .into(imageView);
    }

    /**
     * 创建子组件
     *
     * @return 子组件
     */
    protected final ImageView createChild() {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setPadding(mChildPaddingStart, mChildPaddingTop, mChildPaddingEnd, mChildPaddingBottom);
        if (mChildBackground != null) {
            imageView.setBackground(mChildBackground.getConstantState().newDrawable(getResources()));
        }
        imageView.setOnClickListener(this);
        return imageView;
    }

    public interface OnClickChildListener {
        void onClickChild(ImageGroup group, View selectedChild);
    }
}
