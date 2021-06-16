package com.lookballs.pictureselector.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.lookballs.pictureselector.R;

/**
 * 可以设置最大高度的RecyclerView
 */
public class MaxHeightRecyclerView extends RecyclerView {

    private int mMaxHeight;

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView);
            mMaxHeight = arr.getLayoutDimension(R.styleable.MaxHeightRecyclerView_maxHeight, mMaxHeight);
            arr.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxHeight, View.MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置最大高度
     */
    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
        setMeasuredDimension(ScreenUtils.getScreenWidth(), mMaxHeight);
    }

}