package com.morgan.design.android.service.notifcation;

import static com.morgan.design.Constants.NOTIFICATIONS_FULL;
import static com.morgan.design.Constants.NOTIFICATION_REMOVED;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNotZero;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.Logger;
import com.weatherslider.morgan.design.R;

public class WeatherNotificationControllerService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection {

	private static final String LOG_TAG = "WeatherNotificationControllerService";

	private final IBinder mBinder = new LocalBinder();

	private static Map<Integer, Boolean> SERVICE_IDS = new HashMap<Integer, Boolean>();
	static {
		SERVICE_IDS.put(R.string.weather_notification_service_1, false);
		SERVICE_IDS.put(R.string.weather_notification_service_2, false);
		SERVICE_IDS.put(R.string.weather_notification_service_3, false);
	}

	private final Map<Integer, BaseNotifcationService> boundServices = new HashMap<Integer, BaseNotifcationService>();

	protected BaseNotifcationService mBoundNotificationService1;
	protected BaseNotifcationService mBoundNotificationService2;
	protected BaseNotifcationService mBoundNotificationService3;

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
	@SuppressWarnings("rawtypes")
	public void onServiceConnected(final ComponentName name, final IBinder service) {
		Logger.d(LOG_TAG, "Binding to service, ComponentName=[%s]", name);

		if (name.toString().contains(WeatherNotificationService1.class.getSimpleName())) {
			this.mBoundNotificationService1 = ((WeatherNotificationService1.LocalBinder) service).getService();
			this.boundServices.put(R.string.weather_notification_service_1, this.mBoundNotificationService1);
			return;
		}
		if (name.toString().contains(WeatherNotificationService2.class.getSimpleName())) {
			this.mBoundNotificationService2 = ((WeatherNotificationService2.LocalBinder) service).getService();
			this.boundServices.put(R.string.weather_notification_service_2, this.mBoundNotificationService2);
			return;
		}
		if (name.toString().contains(WeatherNotificationService3.class.getSimpleName())) {
			this.mBoundNotificationService3 = ((WeatherNotificationService3.LocalBinder) service).getService();
			this.boundServices.put(R.string.weather_notification_service_3, this.mBoundNotificationService3);
			return;
		}

	}

	@Override
	public void onServiceDisconnected(final ComponentName name) {
		Logger.d(LOG_TAG, "Disconnected notification controller service");
		this.mBoundNotificationService1 = null;
		this.mBoundNotificationService2 = null;
		this.mBoundNotificationService3 = null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		bindService(new Intent(this, WeatherNotificationService1.class), this, Context.BIND_AUTO_CREATE);
		bindService(new Intent(this, WeatherNotificationService2.class), this, Context.BIND_AUTO_CREATE);
		bindService(new Intent(this, WeatherNotificationService3.class), this, Context.BIND_AUTO_CREATE);
	}

	// /////////////////////////////////////////////
	// ////////// Public methods ///////////////////
	// /////////////////////////////////////////////

	public int addWeatherNotification(final WeatherChoice weatherChoice, final YahooWeatherInfo weatherInfo) {

		// Get existing service, check not present
		final BaseNotifcationService existingService = this.boundServices.get(weatherChoice.getLastknownNotifcationId());
		if (null != existingService) {
			existingService.setWeatherInformation(weatherInfo);

			return weatherChoice.getLastknownNotifcationId();
		}
		// Try initiate another weather notification service
		else {
			for (final Entry<Integer, Boolean> service : SERVICE_IDS.entrySet()) {
				final Integer serviceId = service.getKey();
				final Boolean serviceIsActive = service.getValue();

				Logger.d(LOG_TAG, "Service ID [%s] is active [%s]", serviceId, serviceIsActive);

				if (!serviceIsActive) {
					final WeatherNotificationService notifcationService = this.boundServices.get(serviceId);
					notifcationService.setWeatherInformation(weatherInfo);

					Logger.d(LOG_TAG, "Service [%s] set to active", notifcationService.getClass().getSimpleName());
					service.setValue(true);

					return serviceId;
				}
			}
		}

		// No available notification services left
		sendBroadcast(new Intent(NOTIFICATIONS_FULL));
		return 0;

	}

	public void removeNotification(final int serviceId) {
		if (isNotZero(serviceId)) {
			final BaseNotifcationService baseNotifcationService = this.boundServices.get(serviceId);
			if (isNotNull(baseNotifcationService)) {
				baseNotifcationService.removeNotification();
				SERVICE_IDS.put(serviceId, false);
				sendBroadcast(new Intent(NOTIFICATION_REMOVED));
			}
		}
	}

	public void updatePreferences() {
		for (final Entry<Integer, BaseNotifcationService> entry : this.boundServices.entrySet()) {
			entry.getValue().updatePreferences();
		}
	}

	public int getMaxAllowedNotifications() {
		return this.boundServices.size();
	}

	public boolean notificationsAreFull() {
		int countActive = 0;
		for (final Entry<Integer, Boolean> service : SERVICE_IDS.entrySet()) {
			if (service.getValue()) {
				countActive++;
			}
		}
		return this.boundServices.size() == countActive;

	}

	class Notification {
		int id;
		int serviceId;
	}

}
