package com.example.vke.shop4stech.customLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;


public class SildingFinishLayout extends RelativeLayout implements
		OnTouchListener {

	private ViewGroup mParentView;

	private View touchView;

	private int mTouchSlop;

	private int downX;

	private int downY;

	private int tempX;

	private Scroller mScroller;

	private int viewWidth;

	private boolean isSilding;
	
	private OnSildingFinishListener onSildingFinishListener;
	private boolean isFinish;

	//手指向右滑动时的最小速度
	private static final int XSPEED_MIN = 200;

    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;

    public SildingFinishLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SildingFinishLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			mParentView = (ViewGroup) this.getParent();
			viewWidth = this.getWidth();
		}
	}

	public void setOnSildingFinishListener(OnSildingFinishListener onSildingFinishListener) {
		this.onSildingFinishListener = onSildingFinishListener;
	}

	public void setTouchView(View touchView) {
		this.touchView = touchView;
		touchView.setOnTouchListener(this);
	}

	public View getTouchView() {
		return touchView;
	}

	private void scrollRight() {
		final int delta = (viewWidth + mParentView.getScrollX());
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0,
				Math.abs(delta));
		postInvalidate();
	}

	private void scrollOrigin() {
		int delta = mParentView.getScrollX();
		mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate();
	}

	private boolean isTouchOnAbsListView() {
		return touchView instanceof AbsListView;
	}

	private boolean isTouchOnScrollView() {
		return touchView instanceof ScrollView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = tempX = (int) event.getRawX();
			downY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) event.getRawX();
			int deltaX = tempX - moveX;
			tempX = moveX;
			if (Math.abs(moveX - downX) > mTouchSlop
					&& Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
				isSilding = true;

				if (isTouchOnAbsListView()) {
					MotionEvent cancelEvent = MotionEvent.obtain(event);
					cancelEvent
							.setAction(MotionEvent.ACTION_CANCEL
									| (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					v.onTouchEvent(cancelEvent);
				}

			}

			if (moveX - downX >= 0 && isSilding) {
				mParentView.scrollBy(deltaX, 0);

				if (isTouchOnScrollView() || isTouchOnAbsListView()) {
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			int doneX = (int)event.getRawY();
            int xSpeed = getScrollVelocity();
            isSilding = false;
			if (mParentView.getScrollX() <= -viewWidth / 2 ) {
                if((xSpeed > XSPEED_MIN && doneX > downX)){
                    isFinish = true;
                    scrollRight();
                }
                else{
                    scrollOrigin();
                    isFinish = false;
                }
			} else {
				scrollOrigin();
				isFinish = false;
			}

			break;
		}

		if (isTouchOnScrollView() || isTouchOnAbsListView()) {
			return v.onTouchEvent(event);
		}

		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			if (mScroller.isFinished()) {

				if (onSildingFinishListener != null && isFinish) {
					onSildingFinishListener.onSildingFinish();
				}
			}
		}
	}

	public interface OnSildingFinishListener {
		public void onSildingFinish();
	}

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event montion event
     *
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }
}
