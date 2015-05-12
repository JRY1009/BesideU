package com.besideu.source.ctrl;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager {

	public ChildViewPager(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}

	private float mLastMotionX;
	String TAG = "@";

	private boolean flag = true;

	public boolean dispatchTouchEvent(MotionEvent ev) 
	{
		final float x = ev.getX();
		switch (ev.getAction()) 
		{
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);
			flag = true;
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (flag == true) 
			{
				if (x - mLastMotionX > 5 && getCurrentItem() == 0) 
				{
					flag = false;
					getParent().requestDisallowInterceptTouchEvent(false);
				}

				if (x - mLastMotionX < -5 && getCurrentItem() == getAdapter().getCount() - 1)
				{
					flag = false;
					getParent().requestDisallowInterceptTouchEvent(false);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

}
