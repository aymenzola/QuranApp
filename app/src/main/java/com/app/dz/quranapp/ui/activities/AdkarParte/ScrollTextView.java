package com.app.dz.quranapp.ui.activities.AdkarParte;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import java.util.LinkedList;

/**
 * ScrollTextView
 */
public class ScrollTextView extends androidx.appcompat.widget.AppCompatTextView {

    // scrolling feature
    private Scroller mSlr;

    // scroll speed
    private float mScrollSpeed = 200f;

    // the X offset when paused
    private int mXPaused = 0;

    // whether it's being paused
    private boolean mPaused = true;

    // messages
    private LinkedList<Message> mMessages;

    // current message
    private String mMessage;
    private int mRepeatTimes = 0;

    /*
     * constructor
     */
    public ScrollTextView(Context context) {
        this(context, null);
        // customize the TextView
        setSingleLine();
        setEllipsize(null);
        setVisibility(INVISIBLE);
    }

    /*
     * constructor
     */
    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
        // customize the TextView
        setSingleLine();
        setEllipsize(null);
        setVisibility(INVISIBLE);
    }

    /*
     * constructor
     */
    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // customize the TextView
        setSingleLine();
        setEllipsize(null);
        setVisibility(INVISIBLE);
    }

    /**
     * add message to messages pool
     * @param message String
     * @param repeatTimes int
     */
    public void addMessage(String message, int repeatTimes) {
        if (mMessages == null) {
            mMessages = new LinkedList<>();
        }
        mMessages.add(new Message(message, repeatTimes));
        if (mMessage == null) {
            mMessage = message;
            mRepeatTimes = repeatTimes;
        }
        startScroll();
    }

    public void addMessage(String message) {
        addMessage(message, 1);
    }

    /**
     * begin to scroll the text from the original position
     */
    public void startScroll() {
        if (mMessage == null)
            return;

        // set text
        if (getText() == null || !getText().equals(mMessage)) {
            setVisibility(INVISIBLE);
            setText(mMessage);
        }

        // begin from the very right side
        mXPaused = -1 * getWidth();
        // assume it's paused
        mPaused = true;
        resumeScroll();
    }

    /**
     * resume the scroll from the pausing point
     */
    public void resumeScroll() {

        if (!mPaused)
            return;

        // Do not know why it would not scroll sometimes
        // if setHorizontallyScrolling is called in constructor.
        setHorizontallyScrolling(true);

        // use LinearInterpolator for steady scrolling
        mSlr = new Scroller(this.getContext(), new LinearInterpolator());
        setScroller(mSlr);

        int scrollingLen = calculateScrollingLen();
        int distance = scrollingLen - (getWidth() + mXPaused);
        int duration = (int) (1000f * distance / mScrollSpeed);

        setVisibility(VISIBLE);
        mSlr.startScroll(mXPaused, 0, distance, 0, duration);
        invalidate();
        mPaused = false;
    }

    /**
     * calculate the scrolling length of the text in pixel
     *
     * @return the scrolling length in pixels
     */
    private int calculateScrollingLen() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        int scrollingLen = rect.width() + getWidth();
        rect = null;
        return scrollingLen;
    }

    /**
     * pause scrolling the text
     */
    public void pauseScroll() {
        if (null == mSlr)
            return;

        if (mPaused)
            return;

        mPaused = true;

        // abortAnimation sets the current X to be the final X,
        // and sets isFinished to be true
        // so current position shall be saved
        mXPaused = mSlr.getCurrX();

        mSlr.abortAnimation();
    }

    @Override
     /*
     * override the computeScroll to restart scrolling when finished so as that
     * the text is scrolled forever
     */
    public void computeScroll() {
        super.computeScroll();

        if (null == mSlr) return;

        if (mSlr.isFinished() && !mPaused) {
            if (--mRepeatTimes > 0) {
                startScroll();
                return;
            }
            if (mMessages != null && !mMessages.isEmpty()) {
                mMessage = mMessages.getFirst().getMessage();
                mRepeatTimes = mMessages.getFirst().getRepeatTimes();
                mMessages.removeFirst();
                startScroll();
                return;
            }
            mMessage = null;
            mRepeatTimes = 0;
        }
    }

    public float getScrollSpeed() {
        return mScrollSpeed;
    }

    public void setScrollSpeed(float scrollSpeed) {
        mScrollSpeed = scrollSpeed;
    }

    public boolean isPaused() {
        return mPaused;
    }

    class Message {

        private String mMessage;
        private int mRepeatTimes = 1;

        public Message(String message) {
            mMessage = message;
        }

        public Message(String message, int repeatTimes) {
            mMessage = message;
            mRepeatTimes = repeatTimes;
        }

        public String getMessage() {
            return mMessage;
        }

        public int getRepeatTimes() {
            return mRepeatTimes;
        }

    }

}