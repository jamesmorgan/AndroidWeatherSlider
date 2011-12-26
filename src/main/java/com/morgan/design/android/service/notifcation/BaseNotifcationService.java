package com.morgan.design.android.service.notifcation;

import static com.morgan.design.Constants.OPEN_WEATHER_OVERVIEW;
import static com.morgan.design.android.util.ObjectUtils.stringHasValue;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

import com.morgan.design.android.WeatherOverviewActivity;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.IconFactory;
import com.morgan.design.android.domain.types.OverviewMode;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.domain.types.Wind;
import com.morgan.design.android.domain.types.WindSpeed;
import com.morgan.design.android.util.DateUtils;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.Utils;

public abstract class BaseNotifcationService extends Service implements IWeatherNotificationService {

	private NotificationManager notificationManager;
	private YahooWeatherInfo currentWeather;

	private boolean isBound = false;
	private boolean isActive = false;

	// //////////////////////////////////////////////
	// ////////// Service methods ///////////////////
	// //////////////////////////////////////////////

	@Override
	public void onCreate() {
		this.isBound = true;
		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		this.isBound = false;
		this.notificationManager.cancel(getNotifcationId());
		stopForeground(true);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Log.i("BaseNotifcationService", "Received start id " + startId + ": " + intent);

		setForeground(true);

		return START_STICKY;
	}

	public abstract class LocalBinder<T extends BaseNotifcationService> extends Binder {
		abstract T getService();

		@Override
		public int hashCode() {
			return getNotifcationId();
		}
	}

	public abstract int getNotifcationId();

	public abstract LocalBinder<?> getLocalBinder();

	// /////////////////////////////////////////////
	// ////////// Public methods ///////////////////
	// /////////////////////////////////////////////

	@Override
	public void updatePreferences() {
		if (null != this.currentWeather) {
			showNotification();
		}
	}

	@Override
	public void setWeatherInformation(final YahooWeatherInfo currentWeather) {
		if (null != currentWeather) {
			this.isActive = true;
			this.currentWeather = currentWeather;
			showNotification();
		}
		else {
			this.isActive = false;
			stopSelf();
		}
	}

	@Override
	public void removeNotification() {
		this.notificationManager.cancel(getNotifcationId());
		this.isActive = false;
		stopForeground(true);
		stopSelf();
	}

	@Override
	public boolean isBound() {
		return this.isBound;
	}

	@Override
	public boolean isActive() {
		return this.isActive;
	}

	// /////////////////////////////////////////////
	// ////////// Helper methods ///////////////////
	// /////////////////////////////////////////////

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

		this.notificationManager.notify(getNotifcationId(), notification);
	}

	private PendingIntent createIntent() {
		final OverviewMode overviewMode = PreferenceUtils.getOverviewMode(getApplicationContext());
		if (OverviewMode.OVERVIEW.equals(overviewMode)) {
			return createOpenOverviewActivity();
		}
		return createOpenWebLinkPendingIntent();
	}

	private String getContent() {
		final String temp =
				this.currentWeather.getCurrentTemp() + Temperature.withDegree(Utils.abrev(this.currentWeather.getTemperatureUnit()));
		final String wind =
				WindSpeed.fromSpeedAndUnit(this, this.currentWeather.getWindSpeed(), this.currentWeather.getWindSpeedUnit()) + " ("
					+ Wind.fromDegreeToAbbreviation(this.currentWeather.getWindDirection()).toLowerCase() + ")";

		final String humidity = this.currentWeather.getHumidityPercentage() + "% (Humdity)";
		// final String pressure = valueOrDefault(this.currentWeather.getPressure() + this.currentWeather.getPressureUnit(), "");

		final String time = DateUtils.dateToTime(this.currentWeather.getCurrentDate());

		return temp + " | " + wind + "  | " + humidity + " | " + time;
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

	private PendingIntent createOpenOverviewActivity() {
		final Intent notifyIntent = new Intent(OPEN_WEATHER_OVERVIEW);
		// notifyIntent.putExtra(SERVICE_ID, getNotifcationId());
		notifyIntent.setClass(getApplicationContext(), WeatherOverviewActivity.class);
		return PendingIntent.getActivity(this, getNotifcationId(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private PendingIntent createOpenWebLinkPendingIntent() {
		final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.currentWeather.getLink()));
		return PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public int hashCode() {
		return getNotifcationId();
	}
}
