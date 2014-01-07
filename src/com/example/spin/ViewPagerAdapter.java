package com.example.spin;

import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.os.Parcelable;

public class ViewPagerAdapter extends PagerAdapter {
	
	Activity activity;
	int textArray[];

	 public ViewPagerAdapter(Activity activity, int[] textArray) {
		 this.textArray = textArray;
		 this.activity = activity;
	 }
	 
	 public Object instantiateItem(View collection, int position) {
		 View view = new View(activity);
		 view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				 LayoutParams.FILL_PARENT));
		 view.setBackgroundResource(textArray[position]);
		 ((ViewPager) collection).addView(view, 0);
		 return view;
	 }

	@Override
	public int getCount() {
		return textArray.length;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	 }
	
	@Override
	 public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);
	 }
	
	 @Override
	 public Parcelable saveState() {
		 return null;
	 }

}
