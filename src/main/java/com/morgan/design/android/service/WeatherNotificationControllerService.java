package com.morgan.design.android.service;

import static com.morgan.design.Constants.NOTIFICATIONS_FULL;
import static com.morgan.design.Constants.NOTIFICATION_ID;
import static com.morgan.design.Constants.OPEN_WEATHER_OVERVIEW;
import static com.morgan.design.Constants.UPDATE_WEATHER_LIST;
import static com.morgan.design.android.util.ObjectUtils.stringHasValue;

import java.util.Map.Entry;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.Logger;
import com.morgan.design.WeatherSliderApplication;
import com.morgan.design.android.WeatherOverviewActivity;
import com.morgan.design.android.dao.NotificationDao;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.dao.orm.WeatherNotification;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.OverviewMode;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.Wind;
import com.morgan.design.android.domain.types.WindSpeed;
import com.morgan.design.android.factory.IconFactory;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.Utils;
import com.morgan.design.weatherslider.R;

public class WeatherNotificationControllerService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection {

	public static final int MAX_NUMBER_OF_NOTIFICATIONS = 3;

	private static final String LOG_TAG = "WeatherNotificationControllerService";

	private final IBinder mBinder = new LocalBinder();

	private NotificationDao notificationDao;
	private WeatherChoiceDao weatherDao;
	private NotificationManager notificationManager;

	private WeatherSliderApplication applicaiton;

	private static final String TICKER_TEXT = "Updated Weather Information, it is %s in %s";

	@Override
	public IBinder onBind(final Intent intent) {
		return this.mBinder;
	}

	public class LocalBinder extends Binder {
		public WeatherNotificationControllerService getService() {
			return WeatherNotificationControllerService.this;
		}
	}

	@Override
	public void onServiceConnected(final ComponentName name, final IBinder service) {
		Logger.d(LOG_TAG, "Binding to service, ComponentName=[%s]", name);
	}

	@Override
	public void onServiceDisconnected(final ComponentName name) {
		Logger.d(LOG_TAG, "Disconnected notification controller service");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.notificationDao = new NotificationDao(getHelper());
		this.weatherDao = new WeatherChoiceDao(getHelper());
		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.applicaiton = WeatherSliderApplication.locate(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// /////////////////////////////////////////////
	// ////////// Public methods ///////////////////
	// /////////////////////////////////////////////

	public boolean addWeatherNotification(final WeatherChoice weatherChoice, final YahooWeatherInfo weatherInfo) {

		WeatherNotification notification = this.notificationDao.findNotificationForWeatherId(weatherChoice.getId());

		// Existing notification
		if (null != notification) {
			return displayNotifcation(notification.getServiceId(), weatherInfo);
		}
		// New notification
		else {

			// Check available notifications
			if (!notificationsAreFull()) {

				notification = new WeatherNotification();
				notification.setFkWeatherChoiceId(weatherChoice.getId());

				// Find available service
				for (final Entry<Integer, YahooWeatherInfo> entry : this.applicaiton.getWeathers().entrySet()) {
					if (null == entry.getValue()) {
						notification.setServiceId(entry.getKey());
						this.notificationDao.addNotification(notification);
						return displayNotifcation(notification.getServiceId(), weatherInfo);
					}
				}

			}
			else {
				// No notification available
				sendBroadcast(new Intent(NOTIFICATIONS_FULL));
				return false;
			}
		}
		return false;
	}

	public void removeNotification(final WeatherChoice weatherChoice) {
		final WeatherNotification notification = this.notificationDao.findNotificationForWeatherId(weatherChoice.getId());

		if (null != notification) {

			// Remove notification
			this.notificationManager.cancel(notification.getServiceId());

			// Clean map
			this.applicaiton.setWeather(notification.getServiceId(), null);

			// Delete reference
			this.notificationDao.delete(notification);

			// Update choice
			weatherChoice.setActive(false);
			this.weatherDao.update(weatherChoice);

			// Broadcast update
			sendBroadcast(new Intent(UPDATE_WEATHER_LIST));
		}
	}

	public void updatePreferences() {
		for (final Entry<Integer, YahooWeatherInfo> entry : this.applicaiton.getWeathers().entrySet()) {
			if (null != entry.getValue()) {
				displayNotifcation(entry.getKey(), entry.getValue());
			}
		}
	}

	public boolean notificationsAreFull() {
		return this.notificationDao.getNumberOfNotifications() >= MAX_NUMBER_OF_NOTIFICATIONS;
	}

	public void verboseKillAll() {
		if (null != this.notificationManager) {
			this.notificationManager.cancelAll();
		}
		this.applicaiton.clearAll();
		this.notificationDao.clearAll();
	}

	private boolean displayNotifcation(final int serviceId, final YahooWeatherInfo weatherInfo) {
		if (PreferenceUtils.shouldUseNewNotificationLayout(this)) {
			return showCustomNotification(serviceId, weatherInfo);
		}
		return showNotification(serviceId, weatherInfo);
	}

	private boolean showCustomNotification(int serviceId, YahooWeatherInfo weatherInfo) {
		if (null != weatherInfo) {
			this.applicaiton.setWeather(serviceId, weatherInfo);

			// Cancel existing notification, giving impression of textual updates on new one, if disabled, simple re-use
			// existing notification
			if (PreferenceUtils.enabledNotifcationTickerText(this)) {
				this.notificationManager.cancel(serviceId);
			}

			// Scrolling update text
			CharSequence safeLocation = getSafeLocation(weatherInfo);
			final CharSequence forcastText = weatherInfo.getCurrentText() + ", " + safeLocation;

			long when = System.currentTimeMillis();
			Notification notification = new Notification();
			notification.tickerText = String.format(TICKER_TEXT, weatherInfo.getCurrentText(), safeLocation);
			notification.when = when;
			notification.icon = IconFactory.getImageResourceFromCode(weatherInfo.getCurrentCode());
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.DEFAULT_LIGHTS;

			RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_status_bar_notification_layout);
			contentView.setImageViewResource(R.id.content_icon, IconFactory.getImageResourceFromCode(weatherInfo.getCurrentCode()));
			contentView.setTextViewText(R.id.content_title, forcastText);
			contentView.setTextViewText(R.id.content_text, getContent(weatherInfo, true));
			contentView.setTextViewText(R.id.content_temperature,
					weatherInfo.getCurrentTemp() + Temperature.withDegree(Utils.abrev(weatherInfo.getTemperatureUnit())));
			notification.contentView = contentView;

			PendingIntent pendingContentIntent = createIntent(serviceId, weatherInfo);
			notification.contentIntent = pendingContentIntent;

			this.notificationManager.notify(serviceId, notification);
			return true;
		}
		return false;
	}

	private boolean showNotification(final int serviceId, final YahooWeatherInfo weatherInfo) {
		if (null != weatherInfo) {
			this.applicaiton.setWeather(serviceId, weatherInfo);

			// Cancel existing notification, giving impression of textual updates on new one, if disabled, simple re-use
			// existing notification
			if (PreferenceUtils.enabledNotifcationTickerText(this)) {
				this.notificationManager.cancel(serviceId);
			}

			final String safeLocation = getSafeLocation(weatherInfo);
			final String forcastText = weatherInfo.getCurrentText() + ", " + safeLocation;

			final Notification.Builder builder = new Notification.Builder(this);
			builder.setOngoing(true);
			builder.setContentTitle(forcastText);
			builder.setContentText(getContent(weatherInfo, false));
			builder.setTicker(String.format(TICKER_TEXT, weatherInfo.getCurrentText(), safeLocation));
			builder.setDefaults(Notification.DEFAULT_LIGHTS);
			// Time stamp, set 0 to remove value from skin
			builder.setWhen(System.currentTimeMillis());
			builder.setSmallIcon(IconFactory.getImageResourceFromCode(weatherInfo.getCurrentCode()));
			builder.setContentIntent(createIntent(serviceId, weatherInfo));

			this.notificationManager.notify(serviceId, builder.getNotification());
			return true;
		}
		return false;
	}

	private PendingIntent createIntent(final int serviceId, final YahooWeatherInfo weatherInfo) {
		final OverviewMode overviewMode = PreferenceUtils.getOverviewMode(getApplicationContext());
		if (OverviewMode.OVERVIEW.equals(overviewMode)) {
			// Internal Overview Screen Intent
			final Intent notifyIntent = new Intent(OPEN_WEATHER_OVERVIEW);
			notifyIntent.putExtra(NOTIFICATION_ID, serviceId);
			notifyIntent.setClass(getApplicationContext(), WeatherOverviewActivity.class);
			return PendingIntent.getActivity(this, serviceId, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		}
		// Web Overview Intent
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(weatherInfo.getLink()));
		return PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private String getContent(final YahooWeatherInfo weatherInfo, boolean switchTempForPreasure) {
		final String wind = WindSpeed.fromSpeedAndUnit(this, weatherInfo.getWindSpeed(), weatherInfo.getWindSpeedUnit()) + " ("
				+ Wind.fromDegreeToAbbreviation(weatherInfo.getWindDirection()).toLowerCase() + ")";
		final String humidity = weatherInfo.getHumidityPercentage() + "% (Humdity)";

		if (switchTempForPreasure) {
			final String pressure = weatherInfo.getPressure() + weatherInfo.getPressureUnit();
			return wind + "  | " + humidity + " | " + pressure;
		}

		final String temp = weatherInfo.getCurrentTemp() + Temperature.withDegree(Utils.abrev(weatherInfo.getTemperatureUnit()));
		return temp + " | " + wind + "  | " + humidity;
	}

	private String getSafeLocation(final YahooWeatherInfo weatherInfo) {
		final StringBuilder location = new StringBuilder();
		if (stringHasValue(weatherInfo.getRegion())) {
			location.append(weatherInfo.getRegion());
		}
		else if (stringHasValue(weatherInfo.getCity())) {
			location.append(weatherInfo.getCity());
		}
		return stringHasValue(weatherInfo.getCountry()) ? location.append(", ").append(weatherInfo.getCountry()).toString() : location.toString();
	}

}
