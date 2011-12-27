package com.morgan.design.android.service;

import static com.morgan.design.Constants.FROM_FRESH_LOOKUP;
import static com.morgan.design.Constants.FROM_INACTIVE_LOCATION;
import static com.morgan.design.Constants.LOOPING_ALARM;
import static com.morgan.design.Constants.UPDATE_WEATHER_LIST;
import static com.morgan.design.Constants.WEATHER_ID;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNotZero;
import static com.morgan.design.android.util.ObjectUtils.isNull;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.morgan.design.android.broadcast.IServiceUpdateBroadcaster;
import com.morgan.design.android.broadcast.ReloadWeatherReciever;
import com.morgan.design.android.broadcast.ServiceUpdateBroadcasterImpl;
import com.morgan.design.android.dao.WeatherChoiceDao;
import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.domain.GeocodeResult;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.types.Temperature;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.service.notifcation.WeatherNotificationControllerService;
import com.morgan.design.android.tasks.GeocodeWOIEDDataTaskFromLocation;
import com.morgan.design.android.tasks.OnAsyncCallback;
import com.morgan.design.android.util.HttpWeatherLookupFactory;
import com.morgan.design.android.util.Logger;
import com.morgan.design.android.util.PreferenceUtils;
import com.morgan.design.android.util.TimeUtils;

public class RoamingLookupService extends OrmLiteBaseService<DatabaseHelper> implements OnAsyncCallback<YahooWeatherInfo>,
		ServiceConnection, ReloadWeatherReciever.OnReloadWeather {

	private static final String LOG_TAG = "RoamingLookupService";

	private BroadcastReceiver locationChangedBroadcastReciever;
	private OnAsyncCallback<GeocodeResult> onGeocodeDataCallback;

	private IServiceUpdateBroadcaster serviceUpdate;
	private ConnectivityManager cnnxManager;

	protected WeatherChoiceDao weatherDao;
	protected WeatherNotificationControllerService mBoundNotificationControllerService;

	private ReloadWeatherReciever reloadWeatherReciever;

	// ///////////////////////////////////
	// Service creation and destruction //
	// ///////////////////////////////////

	@Override
	public void onCreate() {
		super.onCreate();
		this.cnnxManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		this.serviceUpdate = new ServiceUpdateBroadcasterImpl(this);
		this.weatherDao = new WeatherChoiceDao(getHelper());
		this.reloadWeatherReciever = new ReloadWeatherReciever(this, this);

		bindService(new Intent(this, WeatherNotificationControllerService.class), this, BIND_AUTO_CREATE);
		sendBroadcast(new Intent(LOOPING_ALARM));

		registerForLocationChangedUpdates();

		this.onGeocodeDataCallback = new OnAsyncCallback<GeocodeResult>() {
			@Override
			public void onPostLookup(final GeocodeResult geocodeResult) {
				Logger.d(LOG_TAG, "onPostLookup -> GeocodeResult = %s", geocodeResult);
				if (null == geocodeResult) {
					RoamingLookupService.this.serviceUpdate.complete("Geocode Location Found");
				}
				else {
					RoamingLookupService.this.serviceUpdate.complete("Unable to fina Geocode Location");
					onLocationFound(geocodeResult);
				}
			}

			@Override
			public void onPreLookup() {
				RoamingLookupService.this.serviceUpdate.loading("Finding Geocode Location");
			}

			@Override
			public void onInitiateExecution() {
				RoamingLookupService.this.serviceUpdate.onGoing("Lookup Geocode Location");
			}
		};
	}

	@Override
	public void onReload() {
		Logger.d(LOG_TAG, "Alarm recieved, reloading roaming weathers");
		if (null == this.weatherChoice) {
			this.weatherChoice = this.weatherDao.getActiveRoamingLocation();
		}
		if (null != this.weatherChoice) {
			triggerGetGpsLocation();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterLocationChangedUpdates();
		unbindService(this);
		this.reloadWeatherReciever.unregister();
	}

	@Override
	public void onServiceConnected(final ComponentName className, final IBinder service) {
		this.mBoundNotificationControllerService = ((WeatherNotificationControllerService.LocalBinder) service).getService();
	}

	@Override
	public void onServiceDisconnected(final ComponentName className) {
		this.mBoundNotificationControllerService = null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);

		if (isNotNull(intent)) {
			if (intent.hasExtra(FROM_INACTIVE_LOCATION)) {
				final int id = intent.getIntExtra(WEATHER_ID, 0);
				if (isNotZero(id)) {
					Logger.d(LOG_TAG, "Initiating roaming weather for existing location, id=[%s]", id);
					initiateRoamingWeatherProcess(id);
				}
			}
			else if (intent.hasExtra(FROM_FRESH_LOOKUP)) {
				Logger.d(LOG_TAG, "Initiating roaming weather lookup for fresh location");
				initiateRoamingWeatherProcess();
			}
		}
		return START_STICKY;
	}

	// //////////////////////////////
	// publicly accessible methods //
	// //////////////////////////////

	private WeatherChoice weatherChoice;

	private GeocodeResult geocodeResult;

	public void initiateRoamingWeatherProcess() {
		this.weatherChoice = this.weatherDao.getActiveRoamingLocation();

		if (isNull(this.weatherChoice)) {
			this.weatherChoice = new WeatherChoice();
			this.weatherDao.create(this.weatherChoice);
		}

		this.weatherChoice.setRoaming(true);
		this.weatherDao.update(this.weatherChoice);
		triggerGetGpsLocation();
	}

	public void initiateRoamingWeatherProcess(final int weatherId) {
		if (null != this.weatherChoice) {
			this.weatherChoice.setRoaming(false);
			this.weatherDao.update(this.weatherChoice);
		}

		this.weatherChoice = this.weatherDao.getById(weatherId);
		this.weatherChoice.setRoaming(true);
		this.weatherDao.update(this.weatherChoice);
		triggerGetGpsLocation();

	}

	protected void onLocationFound(final GeocodeResult geocodeResult) {
		this.geocodeResult = geocodeResult;
		new GetYahooWeatherInformationTask(this.cnnxManager, geocodeResult, getTempMode(), this).execute();
	}

	@Override
	public void onPostLookup(final YahooWeatherInfo weather) {
		this.serviceUpdate.complete("Completed Weather Lookup");

		if (null != weather) {
			onSuccessfulLookup(weather);
		}
		else {
			onFailedLookup();
		}
	}

	private void onFailedLookup() {
		Toast.makeText(
				this,
				String.format("Unable to get weather details at present, will try again in %s",
						TimeUtils.convertMinutesHumanReadableTime(PreferenceUtils.getPollingSchedule(this))), Toast.LENGTH_SHORT).show();
		this.weatherChoice.setActive(false);
		this.weatherChoice.failedQuery();
		this.weatherDao.update(this.weatherChoice);

		sendBroadcast(new Intent(UPDATE_WEATHER_LIST));
	}

	private void onSuccessfulLookup(final YahooWeatherInfo weather) {

		// Set updated lat/long and woeid
		this.weatherChoice.setWoeid(this.geocodeResult.getWoeid());
		this.weatherChoice.setLatitude(this.geocodeResult.getLatitude());
		this.weatherChoice.setLongitude(this.geocodeResult.getLonditude());

		// Successful execution
		this.weatherChoice.successfullyQuery(weather);
		this.weatherChoice.setActive(this.mBoundNotificationControllerService.addWeatherNotification(this.weatherChoice, weather));

		this.weatherDao.update(this.weatherChoice);

		sendBroadcast(new Intent(UPDATE_WEATHER_LIST));
	}

	@Override
	public void onPreLookup() {
		this.serviceUpdate.loading("Initalizing Weather Lookup");

	}

	@Override
	public void onInitiateExecution() {
		this.serviceUpdate.onGoing("Running Weather Lookup");
	}

	protected Temperature getTempMode() {
		return PreferenceUtils.getTemperatureMode(getApplicationContext());
	}

	private void unregisterLocationChangedUpdates() {
		if (isNotNull(this.locationChangedBroadcastReciever)) {
			unregisterReceiver(this.locationChangedBroadcastReciever);
			this.locationChangedBroadcastReciever = null;
		}
	}

	private void registerForLocationChangedUpdates() {
		if (isNull(this.locationChangedBroadcastReciever)) {
			this.locationChangedBroadcastReciever = new BroadcastReceiver() {

				@Override
				public void onReceive(final Context context, final Intent intent) {

					final Bundle extras = intent.getExtras();

					if (null != extras) {
						boolean providersFound = false;
						Location location = null;
						if (intent.hasExtra(LocationLookupService.PROVIDERS_FOUND)) {
							providersFound = extras.getBoolean(LocationLookupService.PROVIDERS_FOUND);
						}
						if (intent.hasExtra(LocationLookupService.CURRENT_LOCAION)) {
							location = (Location) extras.getParcelable(LocationLookupService.CURRENT_LOCAION);

						}

						if (!providersFound) {
							Logger.d(LOG_TAG, "No location providers found, GPS and MOBILE are disabled");
						}
						else if (null != location && providersFound) {
							Logger.d(LOG_TAG, "Listened to location change lat=[%s], long=[%s]", location.getLatitude(),
									location.getLatitude());
							new GeocodeWOIEDDataTaskFromLocation(location, RoamingLookupService.this.onGeocodeDataCallback).execute();
						}
						else {
							Logger.d(LOG_TAG, "GPS location not found");
							onFailedLookup();
						}
					}
				}
			};
			registerReceiver(this.locationChangedBroadcastReciever,
					new IntentFilter(LocationLookupService.ROAMING_LOCATION_FOUND_BROADCAST));
		}
	}

	private void triggerGetGpsLocation() {
		Logger.d(LOG_TAG, "Triggering get GPS location");
		final Intent findLocationBroadcast = new Intent(LocationLookupService.GET_ROAMING_LOCATION_LOOKUP);
		findLocationBroadcast.putExtra(LocationLookupService.LOCATION_LOOKUP_TIMEOUT, LocationLookupService.DEFAULT_LOCATION_TIMEOUT);
		startService(findLocationBroadcast);
	}

	// //////////////////
	// Binding details //
	// //////////////////

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(final Intent intent) {
		return this.mBinder;
	}

	public class LocalBinder extends Binder {
		RoamingLookupService getService() {
			return RoamingLookupService.this;
		}
	}

	// //////////////
	// Async Tasks //
	// //////////////

	public class GetYahooWeatherInformationTask extends AsyncTask<Void, Void, YahooWeatherInfo> {

		private final Temperature temperature;
		private final ConnectivityManager cnnxManager;
		private final OnAsyncCallback<YahooWeatherInfo> asyncCallback;
		private final GeocodeResult result;

		public GetYahooWeatherInformationTask(final ConnectivityManager cnnxManager, final GeocodeResult result,
				final Temperature temperature, final OnAsyncCallback<YahooWeatherInfo> asyncCallback) {
			this.cnnxManager = cnnxManager;
			this.result = result;
			this.asyncCallback = asyncCallback;
			this.temperature = temperature;
		}

		@Override
		protected YahooWeatherInfo doInBackground(final Void... params) {
			this.asyncCallback.onInitiateExecution();
			return HttpWeatherLookupFactory.getForGeocodeResult(this.result, this.temperature, this.cnnxManager);
		}

		@Override
		protected void onPreExecute() {
			this.asyncCallback.onPreLookup();
		}

		@Override
		protected void onPostExecute(final YahooWeatherInfo weatherInfo) {
			this.asyncCallback.onPostLookup(weatherInfo);
		}
	}

}
