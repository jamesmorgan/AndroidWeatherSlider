package com.morgan.design.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.morgan.design.R;
import com.morgan.design.android.domain.YahooWeatherInfo;

public class YahooWeatherNotifcationService extends Service {

	private NotificationManager notificationManager;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int NOTIFICATION = R.string.weather_service_started;

	/**
	 * Class for clients to access. Because we know this service always runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		public YahooWeatherNotifcationService getService() {
			return YahooWeatherNotifcationService.this;
		}
	}

	public int getCurrentNotifcationId() {
		return this.NOTIFICATION;
	}

	@Override
	public void onCreate() {
		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Log.i("YahooWeatherNotifcationService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		this.notificationManager.cancel(this.NOTIFICATION);
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return this.mBinder;
	}

	// This is the object that receives interactions from clients. See RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	private YahooWeatherInfo currentWeather;

	private void showNotification() {

		final Notification notification = new Notification();

		// Time stamp, set 0 to remove value from skin
		notification.when = 0;
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.DEFAULT_LIGHTS;
		notification.icon = this.currentWeather.getCurrentCode();

		// Scrolling update text
		final String safeLocation = getSafeLocation();
		notification.tickerText =
				"Updated Weather Information, it is " + this.currentWeather.getCurrentText() + " in " + safeLocation + " "
					+ this.currentWeather.getCountry();

		final String forcastText = this.currentWeather.getCurrentText() + ", " + safeLocation;

		notification.setLatestEventInfo(this, forcastText, getContent(), createOpenWebLinkPendingIntent());

		// FIXME -> add option so if setting allows, open URL of current clicked weather in browser Or open Information tab.

		this.notificationManager.notify(this.NOTIFICATION, notification);

	}

	private String getContent() {
		final String temp = this.currentWeather.getCurrentTemp() + this.currentWeather.getTemperatureUnit();
		final String wind = this.currentWeather.getWindSpeed() + this.currentWeather.getWindSpeedUnit();
		final String contentText = temp + " | " + wind + "  | " + this.currentWeather.getCurrentDate();
		return contentText;
	}

	private String getSafeLocation() {
		if ("" != this.currentWeather.getCity()) {
			return this.currentWeather.getCity();
		}
		if ("" != this.currentWeather.getRegion()) {
			return this.currentWeather.getRegion();
		}
		return "";
	}

	private PendingIntent createOpenWebLinkPendingIntent() {
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.currentWeather.getLink()));
		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		return contentIntent;
	}

	public void setWeatherInformation(final YahooWeatherInfo currentWeather) {
		if (null != currentWeather) {
			this.currentWeather = currentWeather;
			showNotification();
		}
		else {
			stopSelf();
		}
	}
}
