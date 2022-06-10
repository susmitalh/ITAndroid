package com.locatocam.app.views.createrolls.audio_trimmer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.locatocam.app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Stack;

public class SoundRangeBar extends View{
    private static final String TAG = SoundRangeBar.class.getSimpleName();

    //    private float mStart = 0;
    private float mMax = 15;
    private float mRange = 0;

    private Bitmap mThumbBmp;

    private Paint mPaint = new Paint();
    private RectF mBarRect;
    private RectF mRangeRect = new RectF();
    private float mBarMaxHeight;
    private float mBarWidth;
    private float mThumbWidth;
    private float mGap;
    /**
     * 记录thumb的位置
     */
    private float mThumbStart;
    private int mSlop;

    private int mRangeColor;
    private int mBgColor;

    private String mMaxText;

    private Float[] mBarHeights;

    public SoundRangeBar(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public SoundRangeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public SoundRangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public SoundRangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SoundRangeBar, defStyleAttr, defStyleRes);
        mMax = attributes.getInt(R.styleable.SoundRangeBar_sr_max, 15);
        float start = attributes.getFloat(R.styleable.SoundRangeBar_sr_start, 0);
        mRange = attributes.getFloat(R.styleable.SoundRangeBar_sr_range, 5);
        mRangeColor = attributes.getColor(R.styleable.SoundRangeBar_sr_rangeColor, ContextCompat.getColor(context, R.color.colorRange));
        mBgColor = attributes.getColor(R.styleable.SoundRangeBar_sr_bgColor, ContextCompat.getColor(context, android.R.color.white));
        attributes.recycle();

//        mThumb = ContextCompat.getDrawable(context, R.mipmap.ic_range_thumb);
        mThumbBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_range_thumb);
        mBarMaxHeight = DimensionUtils.dip2px(context, 48);
        mBarWidth = DimensionUtils.dip2px(context, 4);
        mGap = DimensionUtils.dip2px(context, 2);
        mBarRect = new RectF(0, 0, mBarWidth, mBarMaxHeight);

        mSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mMaxText = getTimeText(mMax);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        mThumbWidth = getThumbWidth();
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = (int) (widthSize - mThumbWidth);
                break;
            case MeasureSpec.AT_MOST:
                width = (int) (getContext().getResources().getDisplayMetrics().widthPixels - mThumbWidth);
                break;
            case MeasureSpec.UNSPECIFIED:
                width = DimensionUtils.dip2px(getContext(), 200);
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                height = DimensionUtils.dip2px(getContext(), 48);
                break;
        }
        height += mThumbBmp.getHeight();

        width += mThumbWidth;
        setMeasuredDimension(width, height);

        int barCount = (int) (width / (mBarWidth + mGap));
        mBarHeights = generateData(barCount);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawThumb(canvas);
        //drawText(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (checkThumbTouch(event)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                mThumbStart = event.getX();
                Log.d(TAG, "thumb:" + event.getX());
                float x = event.getX();
                x -= mThumbWidth / 2;//让手指位于thumb的中点
                if (x >= 0 && x <= getRangeBarWidth() - getRangeWidth()) {
                    moveThumb(x);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(event);
    }

    OnRangeListener mOnRangeListener;

    public void setOnRangeListener(OnRangeListener listener) {
        mOnRangeListener = listener;
    }

    public float getMax() {
        return mMax;
    }

    public void setMax(float mMax) {
        this.mMax = mMax;
    }

    public float getRange() {
        return mRange;
    }

    public void setRange(float mRange) {
        this.mRange = mRange;
        invalidate();
    }

    private void drawBackground(Canvas canvas) {
        int width = (int) (getRangeBarWidth());
        int barCount = (int) (width / (mBarWidth + mGap));

        mPaint.setColor(mBgColor);
        float start = mThumbWidth / 2;
        for (int i = 0; i < barCount; ++i) {
            mBarRect.left = start + i * (mBarWidth + mGap);
            mBarRect.right = mBarRect.left + mBarWidth;
            float barHeight = mBarHeights[i];
            float delta = mBarMaxHeight - barHeight;
            mBarRect.top = delta / 2;
            mBarRect.bottom = mBarMaxHeight - (delta / 2);
            float rx = mBarRect.width() / 2;
            float ry = mBarRect.width() / 2;

            mRangeRect.left = mThumbStart + mThumbWidth / 2 - mBarWidth / 2;
            mRangeRect.right = mRangeRect.left + getRangeWidth() + mBarWidth / 2;

            if (mBarRect.left >= mRangeRect.left && mBarRect.right <= mRangeRect.right) {
                mPaint.setColor(mRangeColor);
            } else {
                mPaint.setColor(mBgColor);
            }
            canvas.drawRoundRect(mBarRect, rx, ry, mPaint);
        }
    }

    private void drawThumb(Canvas canvas) {
        canvas.drawBitmap(mThumbBmp, mThumbStart, mBarMaxHeight, null);
    }

    private void drawText(Canvas canvas) {
        mPaint.setColor(mBgColor);
        float textSize = DimensionUtils.dip2px(getContext(), 10);
        mPaint.setTextSize(textSize);
        float x = getMeasuredWidth() - mThumbWidth / 2;
        float y = (mBarMaxHeight - textSize - 5) / 2;
        canvas.drawText(mMaxText, x, y, mPaint);

        canvas.drawText(getCurrText(), 0, y, mPaint);
    }

    private int getThumbWidth() {
        return mThumbBmp.getWidth() + DimensionUtils.dip2px(getContext(), 4);
    }

    private String getTimeText(float sec) {
        long timestamp = (long) (sec + 0.5) * 1000;
        Date date = new Date();
        date.setTime(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return sdf.format(date);
    }

    private String getCurrText() {
        float x = 0;
        float thumb = mThumbStart - mThumbWidth / 2;
        if (thumb > 0) {
            x = mMax * thumb / getRangeBarWidth();
        }
        if (mOnRangeListener != null) {
            mOnRangeListener.onRangeStart((long) x, (long) mMax);
        }
        return getTimeText(x);
    }

    private Float[] generateData(int barCount) {
        Random random = new Random();
        Stack<Float> barHeights = new Stack<>();
        for (int i = 0; i < barCount; ++i) {
            float barHeight = random.nextInt((int) mBarMaxHeight * 2 / 3) + mBarMaxHeight / 3;
            if (barHeights.isEmpty()) {
                barHeights.push(barHeight);
                continue;
            }
            if (barHeight - barHeights.peek().floatValue() < DimensionUtils.dip2px(getContext(), 1)) {
                barHeight = random.nextInt((int) mBarMaxHeight * 2 / 3) + mBarMaxHeight / 3;
            }
            barHeights.push(barHeight);
        }
        Float[] data = new Float[]{};
        return barHeights.toArray(data);
    }

    private boolean checkThumbTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (y > mBarMaxHeight && y <= getMeasuredHeight()) {
            if (x >= 0 && x <= getMeasuredWidth()) {
                return true;
            }
        }
        return false;
    }

    private void moveThumb(float x) {
        float delta = x - mThumbStart;
        if (Math.abs(delta) >= mSlop) {
            if (delta > 0) {
                //left
            } else {
                //right
            }
        }
        mThumbStart += delta;
        invalidate();
    }

    public float getRangeWidth() {
        float barWidth = getRangeBarWidth();
        float x = mRange * barWidth / mMax;
        return x;
    }

    private float getRangeBarWidth() {
        return getMeasuredWidth() - mThumbWidth;
    }

    public interface OnRangeListener {
        void onRangeStart(long start, long max);
    }
}
