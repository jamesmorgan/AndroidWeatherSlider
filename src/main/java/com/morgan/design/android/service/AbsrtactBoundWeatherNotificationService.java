package com.morgan.design.android.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.dao.WoeidChoiceDao;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.Logger;

/**
 * Creates default bindings for {@link YahooWeatherNotifcationService}
 * 
 * @author James Edward Morgan
 */
public abstract class AbsrtactBoundWeatherNotificationService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection {

	private static final String LOG_TAG = "AbsrtactBoundWeatherService";

	protected YahooWeatherNotifcationService mBoundNotificationService;
	protected WoeidChoiceDao woeidChoiceDao;
	protected boolean mIsNotificatoionServiceBound = false;

	@Override
	public void onServiceConnected(final ComponentName className, final IBinder service) {
		Logger.d(LOG_TAG, "Successfully loaded weather details");
		this.mBoundNotificationService = ((YahooWeatherNotifcationService.LocalBinder) service).getService();
	}

	@Override
	public void onServiceDisconnected(final ComponentName className) {
		Logger.d(LOG_TAG, "Disconnected from weather service");
		this.mBoundNotificationService = null;
	}

	@Override
	public IBinder onBind(final Intent arg) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.woeidChoiceDao = new WoeidChoiceDao(getHelper());
		doBindService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	protected WeatherSliderApplication getToLevelApplication() {
		return ((WeatherSliderApplication) getApplication());
	}

	protected void doBindService() {
		bindService(new Intent(this, YahooWeatherNotifcationService.class), this, Context.BIND_AUTO_CREATE);
		this.mIsNotificatoionServiceBound = true;
	}

	protected void doUnbindService() {
		if (this.mIsNotificatoionServiceBound) {
			unbindService(this);
			this.mIsNotificatoionServiceBound = false;
		}
	}
}
