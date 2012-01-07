package com.morgan.design.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.weatherslider.morgan.design.R;

public class CopyOfMainPagerActivity extends Activity {

	private static int NUM_AWESOME_VIEWS = 20;

	private ViewPager contentPager;
	private Context cxt;

	private AwesomePagerAdapter awesomeAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view_pager);
		this.cxt = this;

		this.awesomeAdapter = new AwesomePagerAdapter();
		this.contentPager = (ViewPager) findViewById(R.id.viewpager);
		this.contentPager.setAdapter(this.awesomeAdapter);
	}

	private class AwesomePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return NUM_AWESOME_VIEWS;
		}

		/**
		 * Create the page for the given position. The adapter is responsible for adding the view to the container given here, although it
		 * only must ensure this is done by the time it returns from {@link #finishUpdate()}.
		 * 
		 * @param container The containing View in which the page will be shown.
		 * @param position The page position to be instantiated.
		 * @return Returns an Object representing the new page. This does not need to be a View, but can be some other container of the
		 *         page.
		 */
		@Override
		public Object instantiateItem(final View collection, final int position) {
			final TextView tv = new TextView(CopyOfMainPagerActivity.this.cxt);
			tv.setText("Bonjour PAUG " + position);
			tv.setTextColor(Color.WHITE);
			tv.setTextSize(30);

			((ViewPager) collection).addView(tv, 0);

			return tv;
		}

		/**
		 * Remove a page for the given position. The adapter is responsible for removing the view from its container, although it only must
		 * ensure this is done by the time it returns from {@link #finishUpdate()}.
		 * 
		 * @param container The containing View from which the page will be removed.
		 * @param position The page position to be removed.
		 * @param object The same object that was returned by {@link #instantiateItem(View, int)}.
		 */
		@Override
		public void destroyItem(final View collection, final int position, final Object view) {
			((ViewPager) collection).removeView((TextView) view);
		}

		@Override
		public boolean isViewFromObject(final View view, final Object object) {
			return view == ((TextView) object);
		}

		/**
		 * Called when the a change in the shown pages has been completed. At this point you must ensure that all of the pages have actually
		 * been added or removed from the container as appropriate.
		 * 
		 * @param container The containing View which is displaying this adapter's page views.
		 */
		@Override
		public void finishUpdate(final View arg0) {
		}

		@Override
		public void restoreState(final Parcelable arg0, final ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(final View arg0) {
		}

	}
}
