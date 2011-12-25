package com.morgan.design.android.service;

import static com.morgan.design.Constants.CONNECTIVITY_CHANGED;
import static com.morgan.design.Constants.FROM_INACTIVE_LOCATION;
import static com.morgan.design.Constants.LATEST_WEATHER_QUERY_COMPLETE;
import static com.morgan.design.Constants.NOTIFICATIONS_FULL;
import static com.morgan.design.Constants.NOTIFICATION_REMOVED;
import static com.morgan.design.Constants.PHONE_BOOT;
import static com.morgan.design.Constants.RELOAD_WEATHER;
import static com.morgan.design.Constants.WEATHER_ID;
import static com.morgan.design.android.util.ObjectUtils.isNotZero;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.YahooWeatherLookup;
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.notifcation.WeatherNotificationControllerService;
import com.morgan.design.android.tasks.OnAsyncCallback;
import com.morgan.design.android.util.HttpWeatherLookupFactory;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.TimeUtils;

public class StaticLookupService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection,
		OnAsyncCallback<YahooWeatherLookup> {

	private static final String LOG_TAG = "StaticLookupService";

	private ServiceUpdateBroadcaster serviceUpdate;
	private List<WeatherChoice> weatherChoice;

	private ConnectivityManager cnnxManager;
	protected WeatherNotificationControllerService mBoundNotificationControllerService;
	protected WeatherChoiceDao weatherDao;

	@Override
	public void onServiceConnected(final ComponentName className, final IBinder service) {
		this.mBoundNotificationControllerService = ((WeatherNotificationControllerService.LocalBinder) service).getService();
	}

	@Override
	public void onServiceDisconnected(final ComponentName className) {
		this.mBoundNotificationControllerService = null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.serviceUpdate = new ServiceUpdateBroadcasterImpl(this);
		this.weatherDao = new WeatherChoiceDao(getHelper());
		bindService(new Intent(this, WeatherNotificationControllerService.class), this, BIND_AUTO_CREATE);
		this.cnnxManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		this.weatherChoice = this.weatherDao.getActiveStaticLocations();

		if (fromReloadWeather(intent)) {
			Logger.d(LOG_TAG, "Loading weather data, triggered from alarm");
			reloadAll();
		}
		else if (fromPhoneBoot(intent)) {
			if (PreferenceUtils.shouldStartOnBoot(this)) {
				Logger.d(LOG_TAG, "Loading weather data, triggered from phone boot");
				reloadAll();
			}
		}
		else if (fromConnectivityChanged(intent)) {
			Logger.d(LOG_TAG, "Loading weather data, triggered from connectivity changed");
			reloadAll();
		}
		else if (fromInactiveLocation(intent)) {
			Logger.d(LOG_TAG, "Loading weather data, triggered from inacitve notification load");

			final int id = intent.getIntExtra(WEATHER_ID, 0);
			if (isNotZero(id)) {
				if (this.mBoundNotificationControllerService.notificationsAreFull()) {

					final WeatherChoice choice = this.weatherDao.getById(id);
					choice.setRoaming(false);
					this.weatherDao.update(choice);

					sendBroadcast(new Intent(NOTIFICATIONS_FULL));
					sendBroadcast(new Intent(NOTIFICATION_REMOVED));
				}
				else {
					final WeatherChoice choice = this.weatherDao.getById(id);
					choice.setRoaming(false);
					this.weatherDao.update(choice);
					getWeather(choice);
				}
			}
		}

		return START_STICKY;
	}

	private void reloadAll() {
		for (final WeatherChoice weatherChoice : this.weatherChoice) {
			if (weatherChoice.isActive() && !weatherChoice.isRoaming()) {
				getWeather(weatherChoice);
			}
		}
	}

	private void getWeather(final WeatherChoice weatherChoice) {
		new GetYahooWeatherInformationForWoeid(this.cnnxManager, weatherChoice, getTempMode(), this).execute();
	}

	@Override
	public void onPreLookup() {
		this.serviceUpdate.loading("Initalizing Weather Lookup");
	}

	@Override
	public void onPostLookup(final YahooWeatherLookup weatherLookup) {
		this.serviceUpdate.complete("Completed Weather Lookup");

		final YahooWeatherInfo weatherInfo = weatherLookup.getWeatherInfo();
		final WeatherChoice choice = weatherLookup.getWeatherChoice();

		if (null != weatherInfo) {
			choice.setActive(this.mBoundNotificationControllerService.addWeatherNotification(choice, weatherInfo));
			choice.successfullyQuery(weatherInfo);
		}
		else {
			Toast.makeText(
					this,
					String.format("Unable to get weather details at present, will try again in %s",
							TimeUtils.convertMinutesHumanReadableTime(PreferenceUtils.getPollingSchedule(this))), Toast.LENGTH_SHORT)
				.show();
			choice.failedQuery();
		}

		this.weatherDao.update(choice);
		this.weatherChoice = this.weatherDao.getActiveStaticLocations();

		sendBroadcast(new Intent(LATEST_WEATHER_QUERY_COMPLETE));
	}

	@Override
	public void onInitiateExecution() {
		this.serviceUpdate.onGoing("Running Weather Lookup");
	}

	protected Temperature getTempMode() {
		return PreferenceUtils.getTemperatureMode(getApplicationContext());
	}

	private boolean fromInactiveLocation(final Intent intent) {
		return null != intent && intent.hasExtra(FROM_INACTIVE_LOCATION) && intent.getBooleanExtra(FROM_INACTIVE_LOCATION, false);
	}

	private boolean fromReloadWeather(final Intent intent) {
		return null != intent && intent.hasExtra(RELOAD_WEATHER) && intent.getBooleanExtra(RELOAD_WEATHER, false);
	}

	private boolean fromConnectivityChanged(final Intent intent) {
		return null != intent && intent.hasExtra(CONNECTIVITY_CHANGED) && intent.getBooleanExtra(CONNECTIVITY_CHANGED, false);
	}

	private boolean fromPhoneBoot(final Intent intent) {
		return null != intent && intent.hasExtra(PHONE_BOOT) && intent.getBooleanExtra(PHONE_BOOT, false);
	}

	// //////////////////
	// Binding details //
	// //////////////////

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	// //////////////
	// Async Tasks //
	// //////////////

	class GetYahooWeatherInformationForWoeid extends AsyncTask<Void, Void, YahooWeatherLookup> {

		private final WeatherChoice weatherChoice;
		private final Temperature temperature;
		private final ConnectivityManager cnnxManager;
		private final OnAsyncCallback<YahooWeatherLookup> asyncCallback;

		public GetYahooWeatherInformationForWoeid(final ConnectivityManager cnnxManager, final WeatherChoice weatherChoice,
				final Temperature temperature, final OnAsyncCallback<YahooWeatherLookup> asyncCallback) {
			this.cnnxManager = cnnxManager;
			this.asyncCallback = asyncCallback;
			this.weatherChoice = weatherChoice;
			this.temperature = temperature;
		}

		@Override
		protected YahooWeatherLookup doInBackground(final Void... params) {
			this.asyncCallback.onInitiateExecution();
			return HttpWeatherLookupFactory.getForWeatherChoice(this.weatherChoice, this.temperature, this.cnnxManager);
		}

		@Override
		protected void onPreExecute() {
			this.asyncCallback.onPreLookup();
		}

		@Override
		protected void onPostExecute(final YahooWeatherLookup weatherInfo) {
			this.asyncCallback.onPostLookup(weatherInfo);
		}
	}

}
