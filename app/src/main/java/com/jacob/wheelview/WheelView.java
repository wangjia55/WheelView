package com.jacob.wheelview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob-wj on 2015/4/10.
 */
public class WheelView extends ScrollView {

    private Paint mPaintBorder;

    private LinearLayout mLinearContainer;

    private int mItemHeight;

    private int mItemWidth;
    private List<String> mItems = new ArrayList<>();

    private int mOffset;

    private int mSelection;

    private int mDisplayCount;
    private int[] mBorderHeight = new int[2];

    public static final int DEFAULT_OFFSET = 2;

    public static final long DELAY = 20;

    public static final int TEXT_SIZE_NORMAL = 20;
    public static final int TEXT_SIZE_SELECTED = 24;

    private int mInitY;

    private AutoScrollRunnable mAutoRunnable = new AutoScrollRunnable();

    public static final int COLOR_NORMAL = Color.parseColor("#FFADADAD");
    public static final int COLOR_SELECT = Color.parseColor("#FF929AFF");
    private OnWheelPickerListener mPickerListener;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder.setDither(true);
        mPaintBorder.setStrokeWidth(dpToPx(1));
        mPaintBorder.setColor(COLOR_SELECT);

        mOffset = DEFAULT_OFFSET;
        mDisplayCount = mOffset * 2 + 1;

        this.setVerticalScrollBarEnabled(false);
        mLinearContainer = new LinearLayout(getContext());
        mLinearContainer.setOrientation(LinearLayout.VERTICAL);
        addView(mLinearContainer);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mItemWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshSelectedUI(t);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                int left = (int) (mItemWidth / 6f);
                int right = (int) (mItemWidth * 5 / 6f);
                canvas.drawLine(left, mBorderHeight[0], right, mBorderHeight[0], mPaintBorder);
                canvas.drawLine(left, mBorderHeight[1], right, mBorderHeight[1], mPaintBorder);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };
        super.setBackgroundDrawable(background);
    }


    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * 外部传入的item数据，
     * 注意：这里需要在头部和尾部额外增加offset个空余位置
     */
    public void setItems(List<String> listItem) {
        mItems.clear();
        mItems.addAll(listItem);
        for (int i = 0; i < mOffset; i++) {
            mItems.add(0, "");
            mItems.add("");
        }
        addItemView();
        refreshSelectedUI(0);
    }

    private void refreshSelectedUI(int y) {
        int position = y / mItemHeight + mOffset;
        int remain = y % mItemHeight;

        if (remain > mItemHeight / 2) {
            position = position + 1;
        }

        int size = mLinearContainer.getChildCount();
        for (int i = 0; i < size; i++) {
            View textView = mLinearContainer.getChildAt(i);
            if (textView instanceof TextView) {
                if (i == position) {
                    ((TextView) textView).setTextColor(COLOR_SELECT);
                    ((TextView) textView).setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SELECTED);
                } else {
                    ((TextView) textView).setTextColor(COLOR_NORMAL);
                    ((TextView) textView).setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_NORMAL);
                }
            }

        }
    }

    /**
     * she
     */
    public void setSelection(int selection) {
        final int index = selection;
        this.mSelection = selection+mOffset;
        this.post(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(0, mItemHeight * index);
            }
        });
    }

    public void setOffset(int offset) {
        this.mOffset = offset;
        this.mDisplayCount = mOffset * 2 + 1;
    }

    /**
     * 添加所有的item到布局中
     */
    private void addItemView() {
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            TextView textView = createView(mItems.get(i));
            mLinearContainer.addView(textView);
        }

        mBorderHeight[0] = mItemHeight * mOffset;
        mBorderHeight[1] = mItemHeight * (mOffset + 1);
    }

    private TextView createView(String title) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        textView.setText(title);
        textView.setTextColor(COLOR_NORMAL);
        int padding = dpToPx(15);
        textView.setPadding(padding, padding, padding, padding);
        if (mItemHeight == 0) {
            mItemHeight = measureItemHeight(textView);
            mLinearContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mItemHeight * mDisplayCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width,
                    mItemHeight * mDisplayCount));
        }
        return textView;
    }

    private int measureItemHeight(View view) {
        int childWidthSpec;
        int childHeightSpec;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);

        int height = params.height;
        if (height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view.getMeasuredHeight();
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                startScrollAtPosition();
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void startScrollAtPosition() {
        mInitY = getScrollY();
        postDelayed(mAutoRunnable, DELAY);
    }

    private class AutoScrollRunnable implements Runnable {
        int tempY;

        @Override
        public void run() {
            tempY = getScrollY();
            if (tempY - mInitY == 0) {
                int remain = tempY % mItemHeight;
                int position = tempY / mItemHeight;
                if (remain == 0) {
                    mSelection = position+mOffset;
                    onCallBack();
                }else{
                    if(remain>mItemHeight/2){
                        mSelection = position+mOffset+1;
                        WheelView.this.smoothScrollTo(0, (position + 1) * mItemHeight);
                        onCallBack();
                    }else{
                        mSelection = position+mOffset;
                        WheelView.this.smoothScrollTo(0, position * mItemHeight);
                        onCallBack();
                    }
                }
            } else {
                mInitY = getScrollY();
                postDelayed(this, DELAY);
            }
        }
    }

    private void onCallBack() {
        if (mPickerListener != null) {
            mPickerListener.wheelSelect(mSelection-mOffset, mItems.get(mSelection));
        }
    }


    public interface OnWheelPickerListener {
        void wheelSelect(int position, String content);
    }

    public void setOnWheelPickerListener(OnWheelPickerListener listener) {
        this.mPickerListener = listener;
    }
}

