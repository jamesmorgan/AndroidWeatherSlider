package com.morgan.design.android.service;

import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import static com.morgan.design.android.util.ObjectUtils.valueOrDefault;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.WeatherOverviewActivity;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.domain.types.OverviewMode;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.Wind;
import com.morgan.design.android.util.PreferenceUtils;
import com.weatherslider.morgan.design.R;

public class YahooWeatherNotifcationService extends Service {

	private static final String OPEN_WEATHER_VIEW = "com.morgan.design.android.action.OPEN_WEATHER_VIEW";

	private NotificationManager notificationManager;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int NOTIFICATION = R.string.weather_service_started;

	// This is the object that receives interactions from clients. See RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	private YahooWeatherInfo currentWeather;

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

	private void showNotification() {

		final Notification notification = new Notification();

		// Time stamp, set 0 to remove value from skin
		notification.when = 0;
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.DEFAULT_LIGHTS;
		notification.icon = IconFactory.getImageResourceFromCode(this.currentWeather.getCurrentCode());

		// Scrolling update text
		final String safeLocation = getSafeLocation();
		notification.tickerText = "Updated Weather Information, it is " + this.currentWeather.getCurrentText() + " in " + safeLocation;

		final PendingIntent pendingIntent = createIntent();;

		final String forcastText = this.currentWeather.getCurrentText() + ", " + safeLocation;
		notification.setLatestEventInfo(this, forcastText, getContent(), pendingIntent);

		this.notificationManager.notify(this.NOTIFICATION, notification);
	}

	private PendingIntent createIntent() {
		final OverviewMode overviewMode = PreferenceUtils.getOverviewMode(getApplicationContext());
		if (OverviewMode.OVERVIEW.equals(overviewMode)) {
			return createOpenOverviewActivity();
		}
		else if (OverviewMode.WEB.equals(overviewMode)) {
			return createOpenWebLinkPendingIntent();
		}
		return null;
	}

	private PendingIntent createOpenOverviewActivity() {
		final Intent notifyIntent = new Intent(OPEN_WEATHER_VIEW);
		notifyIntent.setClass(getApplicationContext(), WeatherOverviewActivity.class);
		return PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private String getContent() {
		final String temp =
				this.currentWeather.getCurrentTemp() + Temperature.withDegree(this.currentWeather.getTemperatureUnit().getAbrev());
		final String wind =
				this.currentWeather.getWindSpeed() + this.currentWeather.getWindSpeedUnit() + " ("
					+ Wind.fromDegreeToAbbreviation(this.currentWeather.getWindDirection()).toLowerCase() + ")";

		final String humidity = this.currentWeather.getHumidityPercentage() + "% (Humdity)";
		final String pressure = valueOrDefault(this.currentWeather.getPressure() + this.currentWeather.getPressureUnit(), "");

		return temp + " | " + wind + "  | " + humidity + " | " + pressure;
	}

	private String getSafeLocation() {
		final StringBuilder location = new StringBuilder();
		if (stringHasValue(this.currentWeather.getRegion())) {
			location.append(this.currentWeather.getRegion());
		}
		else if (stringHasValue(this.currentWeather.getCity())) {
			location.append(this.currentWeather.getCity());
		}

		return stringHasValue(this.currentWeather.getCountry())
				? location.append(", ").append(this.currentWeather.getCountry()).toString()
				: location.toString();
	}

	private PendingIntent createOpenWebLinkPendingIntent() {
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.currentWeather.getLink()));
		return PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public void updatePreferences() {
		if (null != this.currentWeather) {
			showNotification();
		}
	}

	public void setWeatherInformation(final YahooWeatherInfo currentWeather) {
		if (null != currentWeather) {
			((WeatherSliderApplication) getApplication()).setCurrentWeather(currentWeather);
			this.currentWeather = currentWeather;
			showNotification();
		}
		else {
			stopSelf();
		}
	}

}
