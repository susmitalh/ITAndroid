package com.locatocam.app.utility;

public interface OnViewPagerListener {
    void onInitComplete();

    void onPageRelease(boolean z, int i);

    void onPageSelected(int i, boolean z);
}
