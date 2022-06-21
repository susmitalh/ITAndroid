package com.locatocam.app.utility;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.State;

public class ViewPagerLayoutManager extends LinearLayoutManager {
    private static final String TAG = "ViewPagerLayoutManager";
    private OnChildAttachStateChangeListener mChildAttachStateChangeListener = new OnChildAttachStateChangeListener() {
        public void onChildViewAttachedToWindow(View view) {
            if (ViewPagerLayoutManager.this.mOnViewPagerListener != null && ViewPagerLayoutManager.this.getChildCount() == 1) {
                ViewPagerLayoutManager.this.mOnViewPagerListener.onInitComplete();
            }
        }

        public void onChildViewDetachedFromWindow(View view) {
            if (ViewPagerLayoutManager.this.mDrift >= 0) {
                if (ViewPagerLayoutManager.this.mOnViewPagerListener != null) {
                    ViewPagerLayoutManager.this.mOnViewPagerListener.onPageRelease(true, ViewPagerLayoutManager.this.getPosition(view));
                }
            } else if (ViewPagerLayoutManager.this.mOnViewPagerListener != null) {
                ViewPagerLayoutManager.this.mOnViewPagerListener.onPageRelease(false, ViewPagerLayoutManager.this.getPosition(view));
            }
        }
    };

    public int mDrift;
    public OnViewPagerListener mOnViewPagerListener;
    private PagerSnapHelper mPagerSnapHelper;
    private RecyclerView mRecyclerView;

    public ViewPagerLayoutManager(Context context, int i) {
        super(context, i, false);

        init();
    }


    private void init() {
        this.mPagerSnapHelper = new PagerSnapHelper();
    }

    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        recyclerView.setOnFlingListener(null);
        this.mPagerSnapHelper.attachToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        this.mRecyclerView.addOnChildAttachStateChangeListener(this.mChildAttachStateChangeListener);
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        super.onLayoutChildren(recycler, state);
    }

    public void onScrollStateChanged(int i) {
        boolean z = true;
        if (i == 0) {
            int position = getPosition(this.mPagerSnapHelper.findSnapView(this));
            if (this.mOnViewPagerListener != null && getChildCount() == 1) {
                OnViewPagerListener onViewPagerListener = this.mOnViewPagerListener;
                if (position != getItemCount() - 1) {
                    z = false;
                }
                onViewPagerListener.onPageSelected(position, z);
            }
        } else if (i == 1) {
            getPosition(this.mPagerSnapHelper.findSnapView(this));
        } else if (i == 2) {
            getPosition(this.mPagerSnapHelper.findSnapView(this));
        }
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        this.mDrift = i;
        return super.scrollVerticallyBy(i, recycler, state);
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        this.mDrift = i;
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    public void setOnViewPagerListener(OnViewPagerListener onViewPagerListener) {
        this.mOnViewPagerListener = onViewPagerListener;
    }
}
