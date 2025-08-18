package com.dongfangwei.zwlibs.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.dongfangwei.zwlibs.R;


/**
 * Created by zhangwei on 2017/12/18.
 */

public class NumberView extends ViewGroup implements View.OnClickListener {
    /**
     * 默认最大值
     */
    private static final int DEFAULT_MAX_NUMBER = 99;
    /**
     * 默认最小值
     */
    private static final int DEFAULT_MIN_NUMBER = 1;

    /**
     * 最大数值
     */
    private int mMaxNumber = DEFAULT_MAX_NUMBER;
    /**
     * 最小数值
     */
    private int mMinNumber = DEFAULT_MIN_NUMBER;
    /**
     * 当前数值
     */
    private int mNumber;

    /**
     * 按钮 - 数值增加
     */
    private final ImageView mAddBtn;
    /**
     * 按钮 - 数值减小
     */
    private final ImageView mMinusBtn;
    /**
     * 输入框 - 数值
     */
    private final EditText mNumEt;
    /**
     * 按钮最小尺寸
     */
    private final int mBtnMinSize;
    private OnNumberChangedListener mListener;

    public NumberView(Context context) {
        this(context, null);
    }

    public NumberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setFocusableInTouchMode(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberView, defStyleAttr, 0);

        int addBackgroundRes = a.getResourceId(R.styleable.NumberView_addBtnBackground, R.drawable.shape_bg_number_view);
        int addIconRes = a.getResourceId(R.styleable.NumberView_addBtnIcon, R.drawable.selector_btn_add);
        int minusBackgroundRes = a.getResourceId(R.styleable.NumberView_minusBtnBackground, R.drawable.shape_bg_number_view);
        int minusIcon = a.getResourceId(R.styleable.NumberView_minusBtnIcon, R.drawable.selector_btn_minus);

        int editBackgroundRes = a.getResourceId(R.styleable.NumberView_editBackground, R.drawable.shape_bg_number_view);
        float textSize = a.getDimension(R.styleable.NumberView_android_textSize, -1);
        ColorStateList textColor = a.getColorStateList(R.styleable.NumberView_android_textColor);
        boolean enable = a.getBoolean(R.styleable.NumberView_editEnable, true);
        int max = a.getInteger(R.styleable.NumberView_maxNum, DEFAULT_MAX_NUMBER);
        int min = a.getInteger(R.styleable.NumberView_minNum, DEFAULT_MIN_NUMBER);
        int num = a.getInteger(R.styleable.NumberView_num, DEFAULT_MIN_NUMBER);

        a.recycle();

        mBtnMinSize = (int) (24 * context.getResources().getDisplayMetrics().density);

        mAddBtn = new ImageView(context);
        mAddBtn.setId(R.id.addBtn);
        mAddBtn.setBackgroundResource(addBackgroundRes);
        mAddBtn.setImageResource(addIconRes);

        mMinusBtn = new ImageView(context);
        mMinusBtn.setId(R.id.minusBtn);
        mMinusBtn.setBackgroundResource(minusBackgroundRes);
        mMinusBtn.setImageResource(minusIcon);

        mNumEt = new EditText(context);
        mNumEt.setId(R.id.numEt);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mNumEt.setLayoutParams(params);
        mNumEt.setPadding(0, 0, 0, 0);
        mNumEt.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_NORMAL);
        mNumEt.setGravity(Gravity.CENTER);
        mNumEt.setSingleLine();
        mNumEt.setBackgroundResource(editBackgroundRes);

        if (textSize == -1) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        setTextColor(textColor == null ? ColorStateList.valueOf(0xff000000) : textColor);
        setEditEnable(enable);


        setMaxNumber(max);
        setMinNumber(min);

        if (num < min) {
            num = min;
        } else if (num > max) {
            num = max;
        }
        setNumber(num);


        mMinusBtn.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        this.addView(mMinusBtn);
        this.addView(mNumEt);
        this.addView(mAddBtn);

        mNumEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int n = Integer.parseInt(s.toString());
                    if (n > mMaxNumber) {
                        mNumber = mMaxNumber;
                        s.clear();
                        s.append(String.valueOf(mNumber));
                    } else if (n < mMinNumber) {
                        mNumber = mMinNumber;
                        s.clear();
                        s.append(String.valueOf(mNumber));
                    } else {
                        mNumber = n;
                    }
                    refreshAddBtnEnable();
                    refreshMinusBtnEnable();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setTextColor(ColorStateList color) {
        mNumEt.setTextColor(color);
    }

    public void setTextColor(int color) {
        mNumEt.setTextColor(color);
    }

    private void setTextSize(int unit, float size) {
        mNumEt.setTextSize(unit, size);
    }

    public void setTextSize(float textSize) {
        mNumEt.setTextSize(textSize);
    }

    public void setEditEnable(boolean enable) {
        mNumEt.setEnabled(enable);
    }

    public int getMaxNumber() {
        return mMaxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        if (maxNumber < 1) this.mMaxNumber = 999;
        else this.mMaxNumber = maxNumber;

        mNumEt.setMaxLines(String.valueOf(this.mMaxNumber).length());

        refreshAddBtnEnable();
    }

    public int getMinNumber() {
        return mMinNumber;
    }

    public void setMinNumber(int minNumber) {
        this.mMinNumber = Math.max(minNumber, 1);
        refreshMinusBtnEnable();
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int num) {
        setNumber(num, false);
    }

    public void setNumber(int num, boolean isListener) {
        if (num != this.mNumber) {
            if (num > mMaxNumber) {
                this.mNumber = mMaxNumber;
            } else {
                this.mNumber = Math.max(num, mMinNumber);
            }
            numberChanged(isListener);
        }
    }

    public void setOnNumberChangedListener(OnNumberChangedListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height, btnWidth, etWidth;
        int childWidthMeasureSpec, childHeightMeasureSpec;
        float dp_8 = getContext().getResources().getDisplayMetrics().density * 8;

        if (specHeightMode == MeasureSpec.EXACTLY) {
            height = specHeightSize;
        } else {
            int etHeight = (int) (mNumEt.getTextSize() + dp_8);
            height = Math.max(etHeight, mBtnMinSize);
        }


        btnWidth = Math.min(height, mBtnMinSize * 2);
        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(btnWidth, MeasureSpec.EXACTLY);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        mAddBtn.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        mMinusBtn.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        if (specWidthMode == MeasureSpec.EXACTLY) {
            etWidth = specWidthSize - btnWidth * 2;
            if (etWidth < 0) {
                etWidth = 0;
            }
            width = specWidthSize;
        } else {
            etWidth = (int) (mNumEt.getTextSize() * 0.58 * mNumEt.getText().length() + dp_8);
            if (etWidth < height * 2) {
                etWidth = height * 2;
            }
            width = btnWidth * 2 + etWidth;
        }
        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(etWidth, MeasureSpec.EXACTLY);
        mNumEt.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = mMinusBtn.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        int left = 0;
        mMinusBtn.layout(left, 0, (left += width), height);
        mNumEt.layout(left - 1, 0, (left += mNumEt.getMeasuredWidth()) + 1, height);
        mAddBtn.layout(left, 0, left + width, height);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.addBtn) {
            addNum();
        } else if (id == R.id.minusBtn) {
            minusNum();
        }
    }

    private void addNum() {
        if (mNumber < mMaxNumber) {
            mNumber++;
            numberChanged(true);
        }
    }

    private void minusNum() {
        if (mNumber > mMinNumber) {
            mNumber--;
            numberChanged(true);
        }
    }

    private void numberChanged(boolean isListener) {
        mNumEt.setText(String.valueOf(mNumber));
        if (mNumEt.hasFocus()) {
            hintKeyBoard();
            mNumEt.clearFocus();
        }
        refreshMinusBtnEnable();
        refreshAddBtnEnable();

        if (isListener && mListener != null) {
            mListener.onNumberChanged(this, mNumber);
        }

    }

    public interface OnNumberChangedListener {
        void onNumberChanged(NumberView v, int num);
    }

    private void refreshMinusBtnEnable() {
        mMinusBtn.setEnabled(mNumber > mMinNumber);
    }

    private void refreshAddBtnEnable() {
        mAddBtn.setEnabled(mNumber < mMaxNumber);
    }

    private void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if (imm.isActive()) {
            //拿到view的token 不为空
            if (mNumEt.getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(mNumEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
