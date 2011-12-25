package com.morgan.design.android.service.notifcation;

import static com.morgan.design.Constants.NOTIFICATIONS_FULL;
import static com.morgan.design.Constants.NOTIFICATION_REMOVED;

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
import com.morgan.design.android.dao.NotificationDao;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.orm.Notification;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.Logger;
import com.weatherslider.morgan.design.R;

public class WeatherNotificationControllerService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection {

	private static final String LOG_TAG = "WeatherNotificationControllerService";

	private final IBinder mBinder = new LocalBinder();

	private final Map<Integer, BaseNotifcationService> boundServices = new HashMap<Integer, BaseNotifcationService>();

	protected BaseNotifcationService mBoundNotificationService1;
	protected BaseNotifcationService mBoundNotificationService2;
	protected BaseNotifcationService mBoundNotificationService3;

	private NotificationDao notificationDao;
	private WeatherChoiceDao weatherDao;

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
		this.notificationDao = new NotificationDao(getHelper());
		this.weatherDao = new WeatherChoiceDao(getHelper());
		bindService(new Intent(this, WeatherNotificationService1.class), this, Context.BIND_AUTO_CREATE);
		bindService(new Intent(this, WeatherNotificationService2.class), this, Context.BIND_AUTO_CREATE);
		bindService(new Intent(this, WeatherNotificationService3.class), this, Context.BIND_AUTO_CREATE);
	}

	// /////////////////////////////////////////////
	// ////////// Public methods ///////////////////
	// /////////////////////////////////////////////

	public boolean addWeatherNotification(final WeatherChoice weatherChoice, final YahooWeatherInfo weatherInfo) {

		Notification notification = this.notificationDao.findNotificationForWeatherId(weatherChoice.getId());

		// Existing notification
		if (null != notification) {
			final BaseNotifcationService existingService = this.boundServices.get(notification.getServiceId());

			// Rebind -> update weather
			if (null != existingService) {
				existingService.setWeatherInformation(weatherInfo);
				return true;
			}
		}
		// New notification
		else {
			// Check available notifications
			if (!notificationsAreFull()) {

				notification = new Notification();
				notification.setFkWeatherChoiceId(weatherChoice.getId());

				// Find available service
				for (final BaseNotifcationService service : this.boundServices.values()) {
					if (!service.isActive()) {
						service.setWeatherInformation(weatherInfo);
						notification.setServiceId(service.getNotifcationId());
						this.notificationDao.addNotification(notification);
						break;
					}
				}
				return true;
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
		final Notification notification = this.notificationDao.findNotificationForWeatherId(weatherChoice.getId());
		if (null != notification) {
			final BaseNotifcationService service = this.boundServices.get(notification.getServiceId());
			if (null != service) {
				service.removeNotification();
			}
			weatherChoice.setActive(false);
			this.weatherDao.update(weatherChoice);
			this.notificationDao.delete(notification);
			sendBroadcast(new Intent(NOTIFICATION_REMOVED));
		}
	}

	public void updatePreferences() {
		for (final Entry<Integer, BaseNotifcationService> entry : this.boundServices.entrySet()) {
			entry.getValue().updatePreferences();
		}
	}

	public boolean notificationsAreFull() {
		// Check available notifications
		// TODO replace with hidden preference
		return this.notificationDao.getNumberOfNotifications() >= 3;
	}
}
