package com.morgan.design.android;

import static com.morgan.design.Constants.FORCAST_ENTRY;
import static com.morgan.design.Constants.NUMBER_OF_FORCASTS;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TabHost;

import com.morgan.design.Constants;
import com.morgan.design.android.SimpleGestureFilter.SimpleGestureListener;
import com.morgan.design.android.domain.ForcastEntry;

public class ForcastTabCreationActivity extends TabActivity implements SimpleGestureListener {

	private SimpleGestureFilter detector;
	private int currentIndex;
	private TabHost tabHost;
	private int tabCount;

	final List<ForcastEntry> entries = new ArrayList<ForcastEntry>();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.detector = new SimpleGestureFilter(this, this);
		this.detector.setEnabled(true);

		this.tabHost = getTabHost();
		TabHost.TabSpec spec;

		int numberOfForecasts = 0;
		final Intent intent = getIntent();
		if (null != intent) {
			if (intent.hasExtra(NUMBER_OF_FORCASTS)) {
				numberOfForecasts = intent.getIntExtra(NUMBER_OF_FORCASTS, 0);
			}

			for (int i = 0; i < numberOfForecasts; i++) {
				if (intent.hasExtra(FORCAST_ENTRY + i)) {
					final ForcastEntry forcastEntry = (ForcastEntry) intent.getSerializableExtra(FORCAST_ENTRY + i);
					this.entries.add(forcastEntry);

					if (isNotToday(forcastEntry)) {

						final Intent tabIntent = new Intent().setClass(this, ForecastOverviewTabActivity.class);
						tabIntent.putExtra(Constants.FORCAST_ENTRY, forcastEntry);
						spec =
								this.tabHost.newTabSpec(forcastEntry.getDayOfWeek().full())
									.setIndicator(forcastEntry.getDayOfWeek().full(), null)
									.setContent(tabIntent);

						this.tabHost.addTab(spec);
					}

				}
			}
		}

		this.currentIndex = 0;
		this.tabHost.setCurrentTab(this.currentIndex);
		this.tabCount = this.tabHost.getTabWidget().getTabCount();
	}

	private boolean isNotToday(final ForcastEntry forcastEntry) {
		return true;
	}

	// /////////////////////////////////////////////
	// ////////// Swipe Gestures ///////////////////
	// /////////////////////////////////////////////

	@Override
	public void onSwipe(final int direction) {
		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				if (0 == this.currentIndex) {
					finish();
				}
				this.tabHost.setCurrentTab(--this.currentIndex);
				break;
			case SimpleGestureFilter.SWIPE_LEFT:
				if (this.tabCount != this.currentIndex + 1) {
					this.tabHost.setCurrentTab(++this.currentIndex);
				}
				break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public void onDoubleTap() {
		// Do nothing at present
	}
}
