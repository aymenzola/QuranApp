package com.app.dz.quranapp.ui.activities.AdkarParte.AdkarDetailsParte;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Scroller;
import android.widget.TextView;
public class ScrollingTextView extends androidx.appcompat.widget.AppCompatTextView {

    // scrolling speed in pixels per second
    private static final float SPEED = 50f;

    private Scroller mScroller; // Add this line

    public ScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setHorizontallyScrolling(true);
        mScroller = new Scroller(context, null, true); // Modify this line
        setScroller(mScroller); // Modify this line
    }

    // Other methods...

    @Override
    public void computeScroll() {
        if (mScroller != null) { // Modify this line
            if (mScroller.isFinished() && getLineCount() > 0) { // Modify this line
                int scrollX = -getWidth();
                int scrollY = getLineBounds(0, null);
                int scrollDelta = (int) (getLayout().getLineRight(0) - scrollX);
                scrollDelta += SPEED; // the scrolling speed
                mScroller.startScroll(scrollX, scrollY, scrollDelta, 0, (int) (1000 * scrollDelta / SPEED)); // Modify this line
            }
            super.computeScroll();
        }
    }
}