package com.morgan.design.android.service;

import static com.morgan.design.Constants.FAILED_LOOKUP;
import static com.morgan.design.Constants.FROM_INACTIVE_LOCATION;
import static com.morgan.design.Constants.LOOPING_ALARM;
import static com.morgan.design.Constants.UPDATE_WEATHER_LIST;
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
import com.morgan.design.RateMe;
import com.morgan.design.android.broadcast.IServiceUpdateBroadcaster;
import com.morgan.design.android.broadcast.ReloadWeatherReciever;
import com.morgan.design.android.broadcast.ReloadWeatherReciever.OnReloadWeather;
import com.morgan.design.android.broadcast.ServiceUpdateBroadcasterImpl;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.YahooWeatherLookup;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.tasks.OnAsyncCallback;
import com.morgan.design.android.util.HttpWeatherLookupFactory;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.TimeUtils;
import com.morgan.design.weatherslider.R;

public class StaticLookupService extends OrmLiteBaseService<DatabaseHelper> implements ServiceConnection, OnAsyncCallback<YahooWeatherLookup>, OnReloadWeather {

	private static final String LOG_TAG = "StaticLookupService";

	private IServiceUpdateBroadcaster serviceUpdate;
	private List<WeatherChoice> weatherChoice;

	private ConnectivityManager cnnxManager;
	protected WeatherNotificationControllerService mBoundNotificationControllerService;
	protected WeatherChoiceDao weatherDao;

	private ReloadWeatherReciever reloadWeatherReciever;

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
		this.reloadWeatherReciever = new ReloadWeatherReciever(this, this);
		sendBroadcast(new Intent(LOOPING_ALARM));
	}

	@Override
	public void onReload() {
		Logger.d(LOG_TAG, "Alarm recieved, reloading all static weathers");
		reloadAll();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(this);
		this.reloadWeatherReciever.unregister();
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		this.weatherChoice = this.weatherDao.getActiveStaticLocations();

		if (fromInactiveLocation(intent)) {
			Logger.d(LOG_TAG, "Loading weather data, triggered from inacitve notification load");

			final int id = intent.getIntExtra(WEATHER_ID, 0);
			if (isNotZero(id)) {

				final WeatherChoice choice = this.weatherDao.getById(id);
				if (null != choice) {
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
		this.serviceUpdate.loading(getString(R.string.service_update_initalizing_weather_lookup));
	}

	@Override
	public void onPostLookup(final YahooWeatherLookup weatherLookup) {
		this.serviceUpdate.complete(getString(R.string.service_update_completed_weather_lookup));

		if (null != weatherLookup) {

			final YahooWeatherInfo weatherInfo = weatherLookup.getWeatherInfo();
			final WeatherChoice choice = weatherLookup.getWeatherChoice();

			if (null == weatherInfo) {
				choice.failedQuery();
			}
			else if (weatherInfo.isError()) {
				choice.invalidQuery(weatherInfo.getWeatherError());
			}
			else {
				choice.setActive(this.mBoundNotificationControllerService.addWeatherNotification(choice, weatherInfo));
				choice.successfullyQuery(weatherInfo);
				RateMe.setSuccessIfRequired(this);
			}

			this.weatherDao.update(choice);
			this.weatherChoice = this.weatherDao.getActiveStaticLocations();
		}
		else {
			if (PreferenceUtils.reportErrorOnFailedLookup(this)) {
				Toast.makeText(
						this,
						String.format(getString(R.string.toast_unable_to_get_weather_details),
								TimeUtils.convertMinutesHumanReadableTime(PreferenceUtils.getPollingSchedule(this))), Toast.LENGTH_SHORT).show();
			}
		}
		sendBroadcast(new Intent(UPDATE_WEATHER_LIST).putExtra(FAILED_LOOKUP, null == weatherLookup));
	}

	@Override
	public void onInitiateExecution() {
		this.serviceUpdate.onGoing(getString(R.string.service_update_running_weather_lookup));
	}

	protected Temperature getTempMode() {
		return PreferenceUtils.getTemperatureMode(getApplicationContext());
	}

	private boolean fromInactiveLocation(final Intent intent) {
		return null != intent && intent.hasExtra(FROM_INACTIVE_LOCATION) && intent.getBooleanExtra(FROM_INACTIVE_LOCATION, false);
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

		public GetYahooWeatherInformationForWoeid(final ConnectivityManager cnnxManager, final WeatherChoice weatherChoice, final Temperature temperature,
				final OnAsyncCallback<YahooWeatherLookup> asyncCallback) {
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
